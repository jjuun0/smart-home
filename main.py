import boto3
from Rekognition import collection_add_file
from Rekognition import face_search_by_image, collection_create, collection_delete
from DynamoDB import add_item
import cam_capture
import datetime


def add_collection(s3, bucket, collectionId):
    """ s3/allowed 폴더안의 파일을 collection에 추가함 """
    bucket_files = []
    # filename_list = []
    # name_list = []
    result = s3.list_objects_v2(Bucket=bucket, Prefix="allowed/")

    for item in result['Contents'][1:]:
        files = item['Key']
        # print(files)
        filename = files.split('/')[-1]
        name = filename.split('.')[0]
        collection_add_file.add_a_face_to_collection(bucket, 'allowed/' + filename, name, collectionId)
        # filename_list.append(filename)
        # name_list.append(name)
        bucket_files.append(files)

        return bucket_files

def main():
    bucket = 'junfirstbucket'  # 업로드할 S3 버킷
    my_collection = 'collection'

    s3 = boto3.client('s3')

    collection_create.create_collection(my_collection)  # collection 에 있는 사진과 출입자의 사진과 비교한다.

    add_collection(s3, bucket, my_collection)  # collection 에 허가된 인물들 추가

    cam_capture.capture('entered.jpg')  # webcam 에서 출입자의 사진을 찍음

    nowDatetime = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')  # 현재 시간
    filename = nowDatetime + '.jpg'  # 파일 이름은 사진이 찍인 시간으로 저장

    # upload_file(로컬에서 올릴 파일이름, S3 버킷 이름, 버킷에 저장될 파일 이름)
    s3.upload_file("entered.jpg", bucket, 'entered/'+filename, ExtraArgs={'ACL': 'public-read'})

    # response, match_image_id, correct, similarity = face_search_by_image.s3_search_collection(bucket, collectionId, filename)
    response, match_image_id, correct, similarity = face_search_by_image.s3_search_collection(bucket, my_collection, filename)

    add_item.add_log(nowDatetime, match_image_id, correct, similarity, dynamodb=0)  # Log 테이블에 얼굴 비교 결과 저장

    collection_delete.delete_collection(my_collection)  # delete collection


if __name__ == '__main__':
    main()
