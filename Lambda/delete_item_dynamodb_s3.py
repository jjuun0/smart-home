import json
import boto3


# 어플에서 allowed 데이터(허가된 사)를 삭제하는 버튼을 누르면
# s3/allowed 에 저장되어있는 이미지 삭제 + DB 에 저장되어 있는 데이터 삭제

# trigger : api gateway

def lambda_handler(event, context):
    print('event: ', event)
    # dynamodb
    dynamodb = boto3.resource('dynamodb')

    table = dynamodb.Table('People')

    response1 = table.delete_item(
        Key={
            "Name": event['Key']['Name']['S']
        }
    )

    # s3
    s3 = boto3.resource('s3')
    response2 = s3.Object('junfirstbucket', 'allowed/' + event['Key']['Name']['S'] + '.jpg').delete()

    print(response1)
    print(response2)

    dynamodb_result = False
    s3_result = False

    if response1["ResponseMetadata"]["HTTPStatusCode"] == 200:
        dynamodb_result = True
    if response2["ResponseMetadata"]["HTTPStatusCode"] == 200:
        s3_result = True

    return {
        "DynamoDB delete": dynamodb_result,
        "S3 delete": s3_result
    }

