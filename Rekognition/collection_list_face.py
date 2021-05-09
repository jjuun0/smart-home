import boto3


def list_faces_in_collection(collection_id):
    """ collection 에 담겨져 있는 얼굴 사진을 리스트로 보여주는 함수 """
    maxResults = 2
    faces_count = 0
    tokens = True
    client = boto3.client('rekognition')
    response = client.list_faces(CollectionId=collection_id, MaxResults=maxResults)
    print('Faces in collection ' + collection_id)

    while tokens:
        faces = response['Faces']
        for face in faces:
            print(face)
            faces_count += 1
        if 'NextToken' in response:
            nextToken = response['NextToken']
            response = client.list_faces(CollectionId=collection_id, NextToken=nextToken,MaxResults=maxResults)
        else:
            tokens = False
    return faces_count


def main():
    # collection_id = 'test_collection'
    collection_id = 'allowed_collection'
    faces_count = list_faces_in_collection(collection_id)
    print("faces count: " + str(faces_count))


if __name__ == "__main__":
    main()

    """
    Faces in collection test_collection
    {'FaceId': 'da57d9bf-dbe6-4b04-adaf-d3e07b5cdf7c', 'BoundingBox': {'Width': 0.25540798902511597, 'Height': 0.40029600262641907, 'Left': 0.1248370036482811, 'Top': 0.23155799508094788}, 'ImageId': '19d8de6c-b7c7-330d-b7e4-44363fe84d81', 'ExternalImageId': 'test4.jpg', 'Confidence': 99.99919891357422}
    {'FaceId': 'f3616bf9-03d3-4cd7-941d-5928c120966c', 'BoundingBox': {'Width': 0.20499500632286072, 'Height': 0.30493101477622986, 'Left': 0.3963479995727539, 'Top': 0.1595979928970337}, 'ImageId': '3020c243-e2bd-3ae3-9394-cbcc20311c3f', 'ExternalImageId': 'son.png', 'Confidence': 99.99930572509766}
    faces count: 2
    """
