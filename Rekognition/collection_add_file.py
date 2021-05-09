import boto3


def add_a_face_to_collection(bucket, photo_path, photo_name, collection_id):
    """ bucket 의 photo_path 에서 photo_name 를 collection 에 추가하는 함수"""
    client = boto3.client('rekognition')
    response = client.index_faces(CollectionId=collection_id,
                                  Image={'S3Object': {'Bucket': bucket, 'Name': photo_path}},
                                  ExternalImageId=photo_name,
                                  MaxFaces=1,  # 인덱싱되는 얼굴 수를 1로 제한
                                  QualityFilter="AUTO",
                                  DetectionAttributes=['ALL'])
    print('Results for ' + photo_name)
    print('Faces indexed:')
    for faceRecord in response['FaceRecords']:
        print(' Face ID: ' + faceRecord['Face']['FaceId'])
        print(' Location: {}'.format(faceRecord['Face']['BoundingBox']))

    print('Faces not indexed:')
    for unindexedFace in response['UnindexedFaces']:
        print(' Location: {}'.format(unindexedFace['FaceDetail']['BoundingBox']))
        print(' Reasons:')
        for reason in unindexedFace['Reasons']:
            print(' ' + reason)

    return len(response['FaceRecords'])



def main():
    bucket = 'junfirstbucket'
    collection_id = 'allowed_collection'
    my_photo_path = 'allowed/son.png'
    my_photo_name = 'son.png'

    indexed_faces_count = add_a_face_to_collection(bucket, my_photo_path, my_photo_name, collection_id)
    print("Faces indexed count: " + str(indexed_faces_count))


if __name__ == "__main__":
    main()
