import boto3
import io
import os
"""
: s3 이미지 파일(허가된 사람, 사전에 등록한 사람 사진)을 다운받고, 출입자를 캠으로 찍어 얼굴을 크롭한 사진과 비교한다. 

sourceFile : s3 에 저장되어 있는 얼굴 사진들. (사전 등록한 얼굴 사진) -> 이 사진들의 사람만이 출입 가능
targetFile : 캠으로 찍힌 사진(출입자의 사진)
둘을 비교해서 95퍼센트 이상의 유사도를 가지고 있다면 true, 아니면 false 를 리턴해줌.(result)

추가로 해야할 것
result(true) 라면 현재 시간과 누구가 들어왔는지를 dynamo db 에 저장을 한다.
result(false) 라면 들어오지 못한 출입자(targetFile)의 이미지를 s3에 따로 저장할지. 고민해보자.
"""
if __name__ == "__main__":
    # s3 file download to local folder (허가된 사람의 사진 파일을 다운)
    # s3 = boto3.resource('s3')
    # s3.Bucket('junfirstbucket').download_file('test4.jpg', 'local_test4.jpg')

    # 파일이 저장된 경로
    file_path = 'img/'
    files_name = os.listdir(file_path)
    print(files_name)

    # 출입자의 사진 파일 -> 캠으로 받아와서 크롭한 사진을 불러와야함.
    # 현재는 테스트를 위해 로컬 파일에서 가져옴.
    targetFile = '../jun0.png'
    people = None
    imageTarget = open(targetFile, 'rb')

    client = boto3.client('rekognition')

    for file_name in files_name:
        sourceFile = file_path + file_name
        result = False

        imageSource = open(sourceFile, 'rb')
        imageTarget = open(targetFile, 'rb')

        # aws rekognition 을 이용해서 SimilarityThreshold 보다 높아야지 저장.
        response = client.compare_faces(SimilarityThreshold=70,
                                        SourceImage={'Bytes': imageSource.read()},
                                        TargetImage={'Bytes': imageTarget.read()})
        # if response['FaceMatches']:
        for faceMatch in response['FaceMatches']:
            if faceMatch['Similarity'] > 94:
                print('source image and target image are same')
                result = True
                people = sourceFile  # 누가 들어왔는지를 저장
                break

        # 찾지 못했다면.
        # else:
        #     print('different')

        imageSource.close()
        imageTarget.close()

    # 찾지 못했다면.
    # if result is False:
        # targetFile 을 s3에 업로드 할지 말지,, 출입 못한 사람.

    print(result, people)

