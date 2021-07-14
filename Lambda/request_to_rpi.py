import json
import boto3
from boto3.dynamodb.conditions import Key


# 지문인식 로그 DB 에 True라고 추가된다면(지문인식이 성공한다면)
# rpi 에 캠을 통해 사진을 찍어 달라는 요청을 한다.

def lambda_handler(event, context):
    # TODO implement
    client = boto3.client('iot-data', region_name='ap-northeast-2')

    print(event)
    # id = event['ID']
    # correct = event['Correct']

    id = event['Records'][0]['dynamodb']['NewImage']['ID']['N']
    correct = event['Records'][0]['dynamodb']['NewImage']['Correct']['S']

    if correct == "True":
        dynamodb = boto3.resource('dynamodb')
        table = dynamodb.Table('FingerPrint')

        id_response = table.query(
            KeyConditionExpression=Key('ID').eq(int(id))
        )

        print('id_response: ', id_response)
        name = id_response['Items'][0]['Name']
        print('ID: ', id)
        print('Name: ', name)

        # Change topic, qos and payload
        publish_response = client.publish(
            topic='aws/facecompare/photographing',
            qos=1,
            payload=json.dumps({"ID": id, "Name": name, "Correct": correct})
        )

        print('publish response: ', publish_response)

    # return {
    #     'statusCode': 200,
    #     'body': json.dumps('Hello from Lambda!')
    # }
    return



