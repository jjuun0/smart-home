import json
import boto3
from boto3.dynamodb.conditions import Key

dynamodb = boto3.resource('dynamodb')


def lambda_handler(event, context):
    table = dynamodb.Table('FingerPrint')
    client = boto3.client('iot-data', region_name='ap-northeast-2')

    process, id, name = event["Process"], event['ID'], event['Name']

    if process == 'Enroll':

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
        if id_response['ScannedCount'] > 0:
            allow = "False"
            message = "Already existed id"

        elif name_response['ScannedCount'] > 0:
            allow = "False"
            message = "Already existed name"

        else:
            allow = "True"
            message = "Allow to enroll"




    else:  # process = 'Delete'
        db_response = table.query(
            KeyConditionExpression=Key('ID').eq(id) & Key('Name').eq(name)
            # FilterConditionExpression=Key('Name').gr(name)
        )

        print("Delete db response : ", db_response)

        if db_response['ScannedCount'] > 0:
            allow = "True"
            message = "Allow to delete"

        else:
            allow = "False"
            message = "id, name are not in DB"

    response = client.publish(
        topic='aws/fingerprint/db/scan',
        qos=1,
        payload=json.dumps({"Allow": allow, "Message": message})
    )

    print("topic publish result: ", response)

    return


