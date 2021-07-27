import time, json, ssl
import paho.mqtt.client as mqtt
import cv2
from upload_s3 import upload_file
from pigpio_motor import open_door

ENDPOINT = 'a2qwvgpe5h2bne-ats.iot.ap-northeast-2.amazonaws.com'
THING_NAME = 'RPI'

def on_connect(mqttc, obj, flags, rc):
    if rc == 0: # success connect
        print('connected!!')
        mqttc.subscribe('aws/facecompare/photographing', qos=0) # subscribe

def on_message(mqttc, obj, msg):
    if msg.topic == 'aws/facecompare/photographing':
        payload = msg.payload.decode('utf-8')
        j = json.loads(payload)
        print(j)
        capture_camera()
        upload_file(j['Name'], 'entered.jpg')
    elif msg.topic == 'aws/motor':
        payload = msg.payload.decode('utf-8')
        j = json.loads(payload)
        print(j)
        open_door()
        
def capture_camera():
    print("Soon caputure!! Don't close eyes")
    cap = cv2.VideoCapture(cv2.CAP_V4L2)
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
    
    grabbed, frame = cap.read()
    frame = cv2.flip(frame, 0)
    cv2.imwrite('entered.jpg', frame)
    cap.release()
    cv2.destroyAllWindows()

mqtt_client = mqtt.Client(client_id=THING_NAME)
mqtt_client.on_connect = on_connect
mqtt_client.on_message = on_message

mqtt_client.tls_set('', certfile='',
    keyfile='', tls_version=ssl.PROTOCOL_TLSv1_2, ciphers=None)
mqtt_client.connect(ENDPOINT, port=8883)
mqtt_client.loop_start() # threaded network loop


# payload = json.dumps({'action': 'test'})
# mqtt_client.publish('test/1', payload, qos=1)



