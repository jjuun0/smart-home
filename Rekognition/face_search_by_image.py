import boto3
""" S3의 이미지 파일을 가지고 collection 에서 얼굴을 찾는 모듈 """


def s3_search_collection(bucket, collectionId, fileName):
    threshold = 70
    maxFaces = 2
    correct = None
    similarity = 0
    client = boto3.client('rekognition')

    response = client.search_faces_by_image(CollectionId=collectionId,
                                            Image={'S3Object': {'Bucket': bucket, 'Name': fileName}},
                                            FaceMatchThreshold=threshold,
                                            MaxFaces=maxFaces)
    if response['FaceMatches']:
        correct = True
        # 여러명을 리턴했을때 어떻게 할지 생각해봐야함
        # 현재는 가장 유사도가 높은값만 저장
        # similarity = int(response['FaceMatches']['Similarity'])
        faceMatches = response['FaceMatches']
        for match in faceMatches:
            if similarity < match['Similarity']:
                similarity = int(match['Similarity'])
                match_image_id = match['Face']['ExternalImageId']
    else:
        match_image_id = fileName
        correct = False

    return response, match_image_id, correct, similarity



if __name__ == "__main__":
    my_bucket = 'junfirstbucket'
    my_collectionId = 'allowed_collection'
    my_fileName = 'entered/jun3.png'  # collection 에 없는 이미지를 사용 (단, S3에 업로드된 파일이어야함)

    my_response = s3_search_collection(my_bucket, my_collectionId, my_fileName)

    print(my_response)

    faceMatches = my_response['FaceMatches']
    print('Matching faces')
    for match in faceMatches:
        print('FaceId:' + match['Face']['FaceId'])
        print('Similarity: ' + "{:.2f}".format(match['Similarity']) + "%")
        print

"""
{'SearchedFaceBoundingBox': {'Width': 0.5414453744888306, 'Height': 0.7818819880485535, 'Left': 0.18827341496944427, 'Top': 0.09703899919986725}, 'SearchedFaceConfidence': 99.9996337890625, 'FaceMatches': [{'Similarity': 99.98916625976562, 'Face': {'FaceId': 'da57d9bf-dbe6-4b04-adaf-d3e07b5cdf7c', 'BoundingBox': {'Width': 0.25540798902511597, 'Height': 0.40029600262641907, 'Left': 0.1248370036482811, 'Top': 0.23155799508094788}, 'ImageId': '19d8de6c-b7c7-330d-b7e4-44363fe84d81', 'ExternalImageId': 'test4.jpg', 'Confidence': 99.99919891357422}}], 'FaceModelVersion': '5.0', 'ResponseMetadata': {'RequestId': '42e42611-54db-40cf-94e4-d270bc68dd6b', 'HTTPStatusCode': 200, 'HTTPHeaders': {'content-type': 'application/x-amz-json-1.1', 'date': 'Wed, 28 Apr 2021 13:42:28 GMT', 'x-amzn-requestid': '42e42611-54db-40cf-94e4-d270bc68dd6b', 'content-length': '545', 'connection': 'keep-alive'}, 'RetryAttempts': 0}}
Matching faces
FaceId:da57d9bf-dbe6-4b04-adaf-d3e07b5cdf7c
Similarity: 99.99%
"""