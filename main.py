import boto3
from Rekognition import collection_add_file
from Rekognition import face_search_by_image
from DynamoDB import add_item
import cam_capture
import cv2
import datetime
bucket = 'junfirstbucket'  # 업로드할 S3 버킷
# filename = '0.png'
collectionId = 'allowed_collection'

s3 = boto3.client('s3')

# 현재 시간
nowDatetime = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')

def get_file_count(s3):
    """ s3/entered 폴더안의 파일 개수를 리턴함 """
    bucket_files = []
    result = s3.list_objects_v2(Bucket=bucket, Prefix="entered/")
    for item in result['Contents']:
        files = item['Key']
        bucket_files.append(files)
    return len(bucket_files)


# filename = str(get_file_count(s3)) + '.png'
filename = nowDatetime + '.png'

cam_capture.capture('unknown.png')

# upload_file(로컬에서 올릴 파일이름, S3 버킷 이름, 버킷에 저장될 파일 이름)
s3.upload_file("unknown.png", bucket, 'entered/'+filename, ExtraArgs={'ACL': 'public-read'})

response, match_image_id, correct, similarity = face_search_by_image.s3_search_collection(bucket, collectionId, filename)


add_item.add_log(nowDatetime, match_image_id, correct, similarity, dynamodb=0)
