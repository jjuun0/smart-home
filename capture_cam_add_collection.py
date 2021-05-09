import boto3
from Rekognition import collection_add_file, collection_list_face
import cam_capture
from DynamoDB import add_item


s3 = boto3.client('s3')
name = 'omr'
filename = name + '.png'
cam_capture.capture(filename)


# 업로드할 파일의 이름
local_filepath = filename

# 업로드할 S3 버킷
bucket_name = 'junfirstbucket'

allowed_folder = 'allowed/' + filename

collectionId = 'allowed_collection'

# upload_file(로컬에서 올릴 파일이름, S3 버킷 이름, 버킷에 저장될 파일 이름)
s3.upload_file(local_filepath, bucket_name, allowed_folder, ExtraArgs={'ACL': 'public-read'})

add_item.add_people(name, filename, filename, 0)

collection_add_file.add_a_face_to_collection(bucket_name, allowed_folder, filename, collectionId)

collection_list_face.list_faces_in_collection(collectionId)
