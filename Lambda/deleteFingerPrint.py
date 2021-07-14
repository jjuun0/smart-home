import json
import boto3

dynamodb = boto3.resource('dynamodb')


def lambda_handler(event, context):
    print(event)
    id, name = event['ID'], event['Name']

    table = dynamodb.Table('FingerPrint')

    db_response = table.delete_item(
        Key={
            'ID': id,
            'Name': name
        }
    )

    return db_response