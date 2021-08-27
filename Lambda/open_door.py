import json
import boto3


def lambda_handler(event, context):
    # TODO implement
    client = boto3.client('iot-data', region_name='ap-northeast-2')

    print('event', event)

    correct = event['Records'][0]['dynamodb']['NewImage']['Correct']['S']
    if correct == "True":
        # Change topic, qos and payload
        publish_response = client.publish(
            topic='aws/motor',
            qos=1,
            payload=json.dumps({"Door open": correct})
        )

        print('publish response: ', publish_response)

    return
