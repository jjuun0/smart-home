import time, json, ssl
import paho.mqtt.client as mqtt
import cv2
from upload_s3 import upload_file

ENDPOINT = 'a1ljg0dz6w3bas-ats.iot.ap-northeast-2.amazonaws.com'
THING_NAME = 'rpi'


def on_connect(mqttc, obj, flags, rc):
    if rc == 0:  # success connect
        print('connected!!')
        mqttc.subscribe('test/1', qos=0)  # subscribe


def on_message(mqttc, obj, msg):
    if msg.topic == 'test/1':
        payload = msg.payload.decode('utf-8')
        j = json.loads(payload)
        print(j)
        capture_camera()
        upload_file(j['Name'], 'entered.jpg')


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

mqtt_client.tls_set('./certs/AmazonRootCA1.pem', certfile='./certs/db30eb1bc5-certificate.pem.crt',
                    keyfile='./certs/db30eb1bc5-private.pem.key', tls_version=ssl.PROTOCOL_TLSv1_2, ciphers=None)
mqtt_client.connect(ENDPOINT, port=8883)
mqtt_client.loop_start()  # threaded network loop

'''
while True:
	# 어쩌구 저쩌구 해야할 일
	time.sleep(0.1)
'''

# payload = json.dumps({'action': 'test'})
# mqtt_client.publish('test/1', payload, qos=1)



