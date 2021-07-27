import json
import boto3


def lambda_handler(event, context):
    # TODO implement

    print(event)
    allow = event['Records'][0]['dynamodb']['NewImage']['Allow']['S']
    time = event['Records'][0]['dynamodb']['NewImage']['Time']['S']

    if allow == 'True':
        print('allow:', allow)
        print('time:', time)

        client = boto3.client('iot-data', region_name='ap-northeast-2')

        publish_response = client.publish(
            topic='aws/motor',
            qos=1,
            payload=json.dumps({"Allow": allow, "Time": time})
        )
        print(publish_response)

    return {
        'statusCode': 200,
        'body': json.dumps('Hello from Lambda!')
    }
