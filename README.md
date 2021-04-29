# smart-home

# 2021.04.24
- opencv 를 이용한 face detection 을 통해, 내 s3 버켓에 이미지 파일을 업로드함.  

- 로컬 파일에 우선 이미지 파일을 업로드하고, 그 이미지 파일을 s3에 업로드

# 2021.04.25
- 내 얼굴 이미지와, 손흥민 이미지에서 얼굴 부분만 잘라내어 one-shot learning 할 이미지를 만듦.  
- 위의 이미지로 image augmentation 을 통해 이미지를 좌우 반전, 기울이기, 노이즈 추가해 데이터셋을 증강시킴(데이터 셋마다 50장).  

- Siamese Network 를 통해 학습시키고, 평가했다.  
  - Train : 매번 학습시키는데 오래걸려서, 모델의 파라미터를 저장.
  - Test : 아이유 이미지를 추가했다. (테스트 데이터 셋 : 각 데이터 셋마다 20장)
  - 코드 관리 :Network, Loss, Dataset, Train, Test 모듈로 나누어 코드를 분리하여 디버깅 하기 쉽게 저장함.
  - train_result : 테스트한 결과 이미지 + 모델이 학습하는데 loss 값 그래프 이미지 있음. 
  - ~~추후에 분리수거(일반 플라스틱 vs 투명 플라스틱) 에도 사용해보자.~~

# 2021.04.26
- Siamese Network
  - 이번엔 분리수거를 해보려고 종이, 비닐, 플라스틱, 캔 의 이미지를 학습시켜 평가해봤는데 성능이 좋지 않음.  
  - 비슷한거도 값이 높게 나와 수정이 필요함.  
  
- aws rekognition 데모를 이용해봤다. 성능이 좋아서 이를 얼굴 비교하는데 사용해보려고 했다.  
  - 얼굴 비교 : s3 이미지 파일(허가된 사람, 사전에 등록한 사람 사진)을 다운받고, 출입자를 캠으로 찍어 얼굴을 크롭한 사진과 비교한다.  
  - ~~추후에 해야할 일~~
    - ~~true : 현재 시간과 누구가 들어왔는지를 dynamo db 에 저장을 한다.~~  
    - ~~false : 들어오지 못한 출입자(targetFile)의 이미지를 s3에 따로 저장할지. 고민해보자.~~    

# 2021.04.27  
- 개발서를 읽어보니, collection 을 통해 얼굴 비교가 가능하다.(test_collection)
- collection 에 S3 에 있는 이미지 파일들을(jun_1, son) 추가해주고, S3 의 입력 이미지(jun_2)를 비교해보니 99.9 퍼센트 동일인물이 있다고 나옴, face-id 도 출력됨.  

# 2021.04.28  
- dynamodb 에 People, Log 테이블을 만듦  
  - People : 이 테이블에 등록을 해야 허가된 사람이라고 판단.  
    ![people_table](https://user-images.githubusercontent.com/66052461/116424194-457a8300-a87c-11eb-92be-9e4494d612ae.png)  
    - 사람 이름 / 사진 이름 / 이미지 url
    - DB에 이미지 파일을 저장하는데 최적화된 도구가 아니기 때문에 s3에 이미지 자체를 저장하고 이미지 주소를 DB에 저장한다.
  - Log : 출입자의 신원을 확인하기 위한 테이블.  
    ![log_table](https://user-images.githubusercontent.com/66052461/116424251-52977200-a87c-11eb-897f-67c32b2a0ce5.png)  
    - 시간 / 일치 여부 : true / 일치한 이미지 이름 / 유사도
    - 시간 / 일치 여부 : false / input 이미지 이름 / 유사도(0)
    - correct : 일치 여부(true, false)를 정렬키로 설정해도 좋을거 같음.  
    - 현재 input 이미지로 collection 과 비교해 유사도를 판단하는데, 이때 여러명이 리턴될수도 있는데 유사도가 가장 높은 값을 가진 사람을 db에 저장하도록 함.

# 2021.04.29  
- lambda 함수를 써봄. (testFunction)   
  - 출입할때 이미지를 받아와서 db(People table's collection)에 얼굴의 유사도를 판단하여 db(Log table)에 기록을 하게 된다. 
  - 이때 false로 저장된다면, 이메일로 알람을 전송해준다.
  - **유의사항, IAM 말고, 내 lambda 함수(testFunction) - 구성 - 권한 - role(lambda-dynamodb-role) 설정해줘야한다. (AWSLambdaDynamoDBExecutionRole : CloudWatch 에 로그를 남길수 있게해주는 권한)**  
- db 에 저장된 값을 가져와봄.  
  ![lambda_function_log](https://user-images.githubusercontent.com/66052461/116548431-2c7eda00-a92f-11eb-85fe-a496fc465556.PNG)  
  - 이는 CloudWatch 에 기록된 로그를 가져온 것이다. json 형태로 리턴해준다.  
  - db 를 트리거로 해두고, db 에 값이 저장되면, lambda 함수가 동작한다.  
  - 이메일 전송  
    - 주제 : my-topic  
    - 메일을 보내기전에 이메일 인증이 필요하다. aws sns - 구독에서 설정해야한다.   

- 해야할 일  
  - 로컬 파일의 이미지로 테스트 해보고 있는데 캠에서 캡쳐한 이미지를 가져와야함.
  - 현재 s3, 같은 버킷(junfirstbucket)에 이미지가 저장됨.  
    - 등록된 사람들의 이미지를 따로 저장하고, 출입자의 이미지를 따로 저장해야할거 같다. 
    - (버킷을 서로 다른곳에 저장하던지, 폴더를 따로 만들어 저장해야함.)  
  - 파일 정리. (현재 뒤죽박죽임 정리가 필요함.)
