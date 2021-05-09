import boto3
from botocore.exceptions import ClientError

def describe_collection(collection_id):
    """ collection 을 설명해주는 함수 """
    print('Attempting to describe collection ' + collection_id)
    client=boto3.client('rekognition')

    try:
        response=client.describe_collection(CollectionId=collection_id)
        print("Collection Arn: " + response['CollectionARN'])
        print("Face Count: " + str(response['FaceCount']))
        print("Face Model Version: " + response['FaceModelVersion'])
        print("Timestamp: " + str(response['CreationTimestamp']))

    except ClientError as e:
        if e.response['Error']['Code'] == 'ResourceNotFoundException':
            print('The collection ' + collection_id + ' was not found ')
        else:
            print('Error other than Not Found occurred: ' + e.response['Error']['Message'])

    print('Done...')


def main():
    collection_id = 'test_collection'
    describe_collection(collection_id)


if __name__ == "__main__":
    main()
