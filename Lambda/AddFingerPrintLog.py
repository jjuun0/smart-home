import json
import boto3

dynamodb = boto3.resource('dynamodb')


def lambda_handler(event, context):
    print(event)
    correct, id, confidence, message, date = event.values()

    table = dynamodb.Table('FingerPrintLog')

    db_response = table.put_item(
        Item={
            'Correct': correct,
            'ID': id,
            'Confidence': confidence,
            'Message': message,
            'Date': date
        }
    )

    return db_response

