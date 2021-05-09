import boto3
from Rekognition import collection_add_file
import cam_capture


s3 = boto3.client('s3')

cam_capture.capture('omr.png')

filename = 'son.png'

# 업로드할 파일의 이름
local_filepath = '../img/original/allow/' + filename

# 업로드할 S3 버킷
bucket_name = 'junfirstbucket'
save_bucket_folder = 'allowed/' + filename

collection = 'allowed_collection'

# upload_file(로컬에서 올릴 파일이름, S3 버킷 이름, 버킷에 저장될 파일 이름)
s3.upload_file(local_filepath, bucket_name, save_bucket_folder, ExtraArgs={'ACL': 'public-read'})

collection_add_file.add_a_face_to_collection(bucket_name, save_bucket_folder, filename, collection)
