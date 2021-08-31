import time, json, ssl
import paho.mqtt.client as mqtt
import cv2

from upload_s3 import upload_file
from pigpio_motor import open_door
from lcd_keypad import print_result


ENDPOINT = 'a2qwvgpe5h2bne-ats.iot.ap-northeast-2.amazonaws.com'
THING_NAME = 'RPI'

def on_connect(mqttc, obj, flags, rc):
    if rc == 0: # success connect
        print('connected!!')
        mqttc.subscribe('arduino/fingerprint/log/enroll', qos=0)
        mqttc.subscribe('aws/facecompare/photographing', qos=0) # subscribe
        mqttc.subscribe('aws/motor', qos=0) 

def on_message(mqttc, obj, msg):
    if msg.topic == 'arduino/fingerprint/log/enroll':
        payload = msg.payload.decode('utf-8')
        j = json.loads(payload)
#         print(j)
        result_msg = 'fingerprint\n' + j['Correct']
        print_result(result_msg)
        
    elif msg.topic == 'aws/facecompare/photographing':
        payload = msg.payload.decode('utf-8')
        j = json.loads(payload)
#         print(j)
        capture_camera()
        upload_file(j['Name'], 'entered.jpg')
        
    elif msg.topic == 'aws/motor':
        payload = msg.payload.decode('utf-8')
        j = json.loads(payload)
#         print(j)
        result_msg = j['face_result']
        print_result('face\n' + result_msg)
        if result_msg == 'True':
            open_door()
        

def capture_camera():
    print("Soon caputure!! Don't close eyes")
#     cap = cv2.VideoCapture(cv2.CAP_V4L2)
    cap = cv2.VideoCapture(0)
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
    
    grabbed, frame = cap.read()
#     frame = cv2.flip(frame, 0)
    cv2.imwrite('entered.jpg', frame)
    cap.release()
    cv2.destroyAllWindows()

mqtt_client = mqtt.Client(client_id=THING_NAME)
mqtt_client.on_connect = on_connect
mqtt_client.on_message = on_message

mqtt_client.tls_set('./certs/AmazonRootCA1.pem', certfile='./certs/b68fb51d7de7a25f1c16a67abd1b7572e80d41ba404503d188b32c4328a788fd-certificate.pem.crt',
    keyfile='./certs/b68fb51d7de7a25f1c16a67abd1b7572e80d41ba404503d188b32c4328a788fd-private.pem.key', tls_version=ssl.PROTOCOL_TLSv1_2, ciphers=None)
mqtt_client.connect(ENDPOINT, port=8883)
mqtt_client.loop_start() # threaded network loop

'''
while True:
	# 어쩌구 저쩌구 해야할 일
	time.sleep(0.1)
'''


# payload = json.dumps({'action': 'test'})
# mqtt_client.publish('test/1', payload, qos=1)


