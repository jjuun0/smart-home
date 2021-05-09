import boto3


def create_collection(collection_id):
    """ collection 을 만드는 함수 """
    client = boto3.client('rekognition')

    #Create a collection
    print('Creating collection: ' + collection_id)
    response = client.create_collection(CollectionId=collection_id)
    print('Collection ARN: ' + response['CollectionArn'])
    print('Status code: ' + str(response['StatusCode']))
    print('Done...')


def main():
    collection_id = 'allowed_collection'
    """
        Creating collection: allowed_collection
        Collection ARN: aws:rekognition:ap-northeast-2:154320029444:collection/allowed_collection
    """


    # "test_collection"
    create_collection(collection_id)


if __name__ == "__main__":
    main()
