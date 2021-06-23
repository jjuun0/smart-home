import json
import boto3
from boto3.dynamodb.conditions import Key

dynamodb = boto3.resource('dynamodb')


def lambda_handler(event, context):
    id, name = event['ID'], event['Name']

    table = dynamodb.Table('FingerPrint')

    id_response = table.query(
        KeyConditionExpression=Key('ID').eq(id)
    )

    print("id scan result: ", id_response)

    name_response = table.query(
        IndexName='Name-index',
        KeyConditionExpression=Key('Name').eq(name)
    )

    print("name scan result: ", name_response)

    # topic publish
    client = boto3.client('iot-data', region_name='ap-northeast-2')

    if id_response['ScannedCount'] != 0:
        enroll = "False"
        message = "Already existed id"

    elif name_response['ScannedCount'] != 0:
        enroll = "False"
        message = "Already existed name"

    else:
        enroll = "True"
        message = "Allow to enroll"

    response = client.publish(
        topic='aws/fingerprint/enroll',
        qos=1,
        payload=json.dumps({"Enroll": enroll, "Message": message})
    )

    print("topic publish result: ", response)


