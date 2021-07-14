import json
import firebase_admin
from firebase_admin import messaging
from firebase_admin import credentials
import boto3

# 지문 비교, 얼굴 비교 결과 일치하지 않는다면 핸드폰으로 알림 전송

# fcm - pushNotification
credential_file_path = './notificationtest-447df-firebase-adminsdk-sqpol-ba288ad00a.json'


def send_fcm_notify_notification(title, body):
    cred = credentials.Certificate(credential_file_path)
    try:
        push_service = firebase_admin.get_app()
    except:
        push_service = firebase_admin.initialize_app(cred)

    message = messaging.Message(
        data={
            'title': title,
            'body': body
        },
        topic='notify')

    response = messaging.send(message)

    print('notify response: ', response)


def lambda_handler(event, context):
    print('event: ', event)

    table = event['Records'][0]["eventSourceARN"].split('/')[1]

    print('table: ', table)

    if event['Records'][0]['dynamodb']['NewImage']['Correct']['S'] == "False":
        if table == 'FingerPrintLog':
            title = '지문 인증 실패'
            body = event['Records'][0]['dynamodb']['NewImage']['Message']['S']
        else:
            title = '얼굴 인증 실패'
            body = "허가되지 않는 사람이 들어오려 합니다"

        send_fcm_notify_notification(title, body)

    return

