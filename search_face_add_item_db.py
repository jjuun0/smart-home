from DynamoDB import add_item
from Rekognition import face_search_by_image
import datetime

bucket = 'junfirstbucket'
collectionId = 'test_collection'
fileName = 'iu1.png'
correct = None
similarity = 0

nowDatetime = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
# print(nowDatetime)  # 2015-04-19 12:11:32

response = face_search_by_image.s3_search_collection(bucket, collectionId, fileName)

print(response)

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

add_item.add_log(nowDatetime, match_image_id, correct, similarity, dynamodb=0)
