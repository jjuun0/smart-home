import json
import boto3


# 지문인식 로그 DB 에 True라고 추가된다면(지문인식이 성공한다면)
# rpi 에 캠을 통해 사진을 찍어 달라는 요청을 한다.

def lambda_handler(event, context):
    # TODO implement
    client = boto3.client('iot-data', region_name='ap-northeast-2')

    print(event)

    id = event['Records'][0]['dynamodb']['NewImage']['Id']['N']
    name = event['Records'][0]['dynamodb']['NewImage']['Name']['S']
    correct = event['Records'][0]['dynamodb']['NewImage']['Correct']['BOOL']

    if correct is True:
        # Change topic, qos and payload
        response = client.publish(
            topic='test/1',
            qos=1,
            payload=json.dumps({"Id": id, "Name": name, "Correct": correct})
        )

    # return {
    #     'statusCode': 200,
    #     'body': json.dumps('Hello from Lambda!')
    # }
