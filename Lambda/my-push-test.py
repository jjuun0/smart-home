import json
import firebase_admin
from firebase_admin import messaging
from firebase_admin import credentials
import boto3

# 얼굴인식이 실패하면 핸드폰으로 알림 전송

# fcm - pushNotification
credential_file_path = './pushnotification-d2530-firebase-adminsdk-yc3hw-432e810aa0.json'


def send_fcm_notify_notification():
    cred = credentials.Certificate(credential_file_path)
    try:
        push_service = firebase_admin.get_app()
    except:
        push_service = firebase_admin.initialize_app(cred)
    topic = "notify"
    message = messaging.Message(
        data={
            'title': "얼굴인식 실패",
            'body': "허가되지 않는 사람이 들어오려 합니다."
        },
        topic=topic)
    response = messaging.send(message)
    print(response)


def lambda_handler(event, context):
    if event['Records'][0]['dynamodb']['NewImage']['Correct']['BOOL'] is False:
        send_fcm_notify_notification()
    return