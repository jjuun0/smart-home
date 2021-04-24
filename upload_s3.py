from face_detect import detect
import boto3
import cv2
import os


def get_recent_file_name():
    """ 이미지 폴더의 가장 최신의 파일이름을 가져와 새로운 파일이름을 정의하는 함수 """

    files_path = "img/"  # 파일들이 들어있는 폴더
    file_name_and_time_list = []

    # 해당 경로에 있는 파일들의 생성시간을 함께 리스트로 넣어줌.
    for f_name in os.listdir(f"{files_path}"):
        written_time = os.path.getctime(f"{files_path}{f_name}")
        file_name_and_time_list.append((f_name, written_time))

    # 생성시간 역순으로 정렬하고,
    sorted_file_lst = sorted(file_name_and_time_list, key=lambda x: x[1], reverse=True)
    # 가장 앞의 파일이름을 넣어준다.
    recent_file = sorted_file_lst[0][0]
    recent_file = recent_file.split('.')
    recent_file_number = int(recent_file[0][4:])

    # 나중에는 시간별로 파일이름을 지정하면 좋을듯함
    recent_file_name = 'test' + str(recent_file_number + 1) + '.jpg'
    return recent_file_name


s3 = boto3.client('s3')
# for bucket in s3.buckets.all():
#     print(bucket.name)

img = detect()

# img = cv2.imread('img/test.jpg')
# cv2.imshow('test', img)

filename = get_recent_file_name()

# img 폴더에 이미지 저장
cv2.imwrite(os.path.join('img/', filename), img)

# 업로드할 파일의 이름
filepath = 'img/' + filename

# 업로드할 S3 버킷
bucket_name = 'junfirstbucket'

# upload_file(로컬에서 올릴 파일이름, S3 버킷 이름, 버킷에 저장될 파일 이름)
s3.upload_file(filepath, bucket_name, filename, ExtraArgs={'ACL': 'public-read'})

