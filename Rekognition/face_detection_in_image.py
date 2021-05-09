import boto3
import json


def detect_faces(photo, bucket):
    """ 사진에 얼굴을 탐지해주는 함수 """

    client = boto3.client('rekognition')
    response = client.detect_faces(Image={'S3Object': {'Bucket': bucket, 'Name': photo}}, Attributes=['ALL'])
    print('Detected faces for ' + photo)
    for faceDetail in response['FaceDetails']:
        print('The detected face is between ' + str(faceDetail['AgeRange']['Low'])
              + ' and ' + str(faceDetail['AgeRange']['High']) + ' years old')
        print('Here are the other attributes:')
        print(json.dumps(faceDetail, indent=4, sort_keys=True))
    return len(response['FaceDetails'])


def main():
    photo = 'jun2.png'
    bucket = 'junfirstbucket'
    face_count = detect_faces(photo, bucket)
    print("Faces detected: " + str(face_count))


if __name__ == "__main__":
    main()

    """
    Matching faces
    FaceId:da57d9bf-dbe6-4b04-adaf-d3e07b5cdf7c
    Similarity: 99.99%
    """
