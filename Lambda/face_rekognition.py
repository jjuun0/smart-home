import json
import boto3

# s3/entered 에 이미지가 추가되면(출입자의 사진) 얼굴 비교를 하여 결과값을 DB 에 저장한다.

s3 = boto3.resource('s3')
dynamodb = boto3.resource('dynamodb')


def lambda_handler(event, context):
    print('event: ', event)

    bucket = event['Records'][0]['s3']['bucket']['name']
    targetImage = event['Records'][0]['s3']['object']['key']
    sourceImage = 'face/allowed/' + targetImage.split('/')[-1].split('_')[0] + '.jpg'
    # sourceImage = 'allowed/jun.jpg'

    print('bucket: ', bucket)
    print('target: ', targetImage)
    print('entered: ', sourceImage)

    client = boto3.client('rekognition')

    faceComparison = client.compare_faces(
        SourceImage={'S3Object': {'Bucket': bucket, 'Name': str(sourceImage)}},
        TargetImage={'S3Object': {'Bucket': bucket, 'Name': str(targetImage)}}
    )

    print('faceComparison: ', faceComparison)

    if faceComparison['FaceMatches']:  # 얼굴 비교 값이 일치할때
        result = {
            'Similarity': int(faceComparison['FaceMatches'][0]['Similarity']),
            'Confidence': int(faceComparison['FaceMatches'][0]['Face']['Confidence'])
        }
        similarity = int(faceComparison['FaceMatches'][0]['Similarity'])
        confidence = int(faceComparison['FaceMatches'][0]['Face']['Confidence'])
        correct = 'True'


    else:  # 얼굴 비교 값이 일치하지 않을때
        result = "sourceImage and targetImage don't match each other"
        correct = 'False'
        similarity = 0

    name, date = targetImage.split('/')[-1].split('_')
    date = date.split('.')[0]

    table = dynamodb.Table('FaceLog')
    db_response = table.put_item(
        Item={
            'Date': date,
            'Name': name,
            'Correct': correct,
            'Similarity': similarity
        }
    )

    print('db_response: ', db_response)

    return

