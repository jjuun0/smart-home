# smart-home
- 지문인식 센서 라이브러리 참고 링크  
  - https://learn.adafruit.com/adafruit-optical-fingerprint-sensor?view=all  

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

- 여태까지 한것 정리  
![2021 04 29](https://user-images.githubusercontent.com/66052461/116555111-f2193b00-a936-11eb-8a8b-5a9c9f30b5ed.png)  

# 2021.05.05 ~ 2021.05.06  
- 어플에서 DynamoDB 값 출력해보기  
  1. AWS API Gateway 를 이용해 url로 요청을 보내면 DynamoDB 테이블의 데이터를 읽어온다.  
    - mapping tamplet 을 이용하여 서버에게 요청한 후 서버가 응답하는 형태를 우리가 바꿀 수 있다.  
    ![People_get](https://user-images.githubusercontent.com/66052461/117308528-94e43300-aebc-11eb-83ce-2fc90ed3bbab.png)  
  2. Retrofit 을 이용하여 API 를 쉽게 사용해 어플에서도 DynamoDB 테이블의 값을 볼 수 있었다.  
    - Retrofit : Java 의 HttpClient 의 라이브러리, 네트워크로 부터 전달된 데이터를 우리 프로그램에서 필요한 형태의 객체로 받을 수 있다.  
    ![get_rest_api_app](https://user-images.githubusercontent.com/66052461/117308968-f73d3380-aebc-11eb-8d0b-c95823d09b4b.png)  
- 해야 할 것  
  - Delete, Post(add), Update 도 해봐야함.(rest api, app)  

# 2021.05.07
- Flask 사용해 DynamoDB 테이블 정보를 가져와봤다.
  - 테이블 전체를 가져오는 scan, 내가 원하는 항목만 가져오는 query 방법 사용.
  ![image](https://user-images.githubusercontent.com/66052461/117457008-ca078880-af83-11eb-8ac4-bfe45fc2680e.png)
- 해야 할 것
  - 내가 서버가 될 수 있는 방법? 찾아봐야함.

# 2021.05.09
- 내 버킷에 entered, allowed 폴더를 만들어서 출입자와 허가된 사람의 이미지를 따로 저장한다.
- main.py 를 실행하면 2초뒤 캠으로 캡쳐하고 이 이미지를 s3/entered 에 저장하고, Log 테이블에 기록을 하며, 다른 인물일 경우 이메일로 알림을 보낸다.
- entered 폴더 안에 파일 이름은 1.png, 2.png 이런식으로 저장 -> 출입자가 찍히는 시간으로 저장
- 해야 할것
  - 어플에서 people db에 추가할 수 있도록 하자.

# 2021.05.10
- android 에서 retrofit 을 이용해 Post 도 해봤다.
- 해야할 것
  - android 에서 S3 로 이미지 파일을 올리는 방법을 찾아봐야함.

# 2021.05.13  
- android 의 갤러리 사진을 선택하면 S3 버킷으로 파일을 올리는 것을 성공했다.  
  - 갤러리의 선택한 사진을 핸드폰에 띄우는 동시에 파일을 S3로 전송해 허가된 사용자 DB 사진 폴더(allowed)에 저장한다.  
  
- 해야할 것  
  - s3 에 저장뿐만 아니라, dynamodb에도 저장을 해야한다. 저번에 dynamodb 에 항목을 추가하는것을 해보았는데 지금 추가가 잘 안되서 수정중임..  
    -  @SerializedName("body") 이것을 해결해 봐야한다.  

# 2021.05.14  
- 어제 못한 dynamodb에 항목을 추가하는것을 못했는데, 제대로 수정하여 추가가 된다.  
  - @SerializedName("body") 부분을 People 부분에 코딩이 되어있는데 이를 지움.  
- 지난번에 한 api gateway 에서 POST 부분에서 요청을 하면 응답으로 아무것도 리턴이 안되었는데 이를 까먹었었음.. 시간을 날림..  
  - 나중에 구글링을 통하여 깨달아 코드를 수정할 필요가 없다는것을 알았음.   
- 앱에서 사용자가 이름을 입력을 하고 버튼을 누르면, 그 이름으로 People 테이블에 추가가 되고, S3/allowed 에도 사진이 저장됨. 
 
- 해야할 것  
  - 허가된 사용자로 추가가 되었을때 collection 에는 추가가 따로 안되는데 이를 어떻게 추가할지 고민해봐야함.  

# 2021.05.16  
- 한 것들 정리  
![SmartHome_](https://user-images.githubusercontent.com/66052461/118383866-c4bdd400-b63c-11eb-84cf-5ae5f8e24e01.png)


# 2021.05.18  
- 어플  
  - People 조회 기능 추가.  
  - GUI 추가 (버튼을 눌러서 다른 액티비티 호출하고, 버튼을 누르면 db에 추가가 되거나, db를 조회할 수 있음)  
  - MainActivity, SavePeopleActivity, GetPeopleActivity 액티비티 클래스 추가   
  - 초기 어플 화면  
  ![main](https://user-images.githubusercontent.com/66052461/118621983-d9979480-b801-11eb-9361-a419377da4c7.png)  
  - 추가  
  ![add](https://user-images.githubusercontent.com/66052461/118622017-e0bea280-b801-11eb-8eb3-4c73fc89f168.png)  
  - 조회  
  ![get](https://user-images.githubusercontent.com/66052461/118622021-e1efcf80-b801-11eb-9c06-b1215d1f00d8.png)  

- 해야할 것  
  -  현재는 db의 이름만 가져와 화면에 보여주는데, 나중에는 사진까지 보여줄 필요가 있어보임.  
  -  S3 에 저장된 이미지를 다운받아야 하는거 같은데, -> 조회 버튼을 누르면 사람 사진 / 이름이 같이 보여질수 있으면 좋을듯.  

# 2021.05.19
- main.py : 실행시킬때마다 collection 을 새로 만들고 s3 버킷의 allowed 폴더에 있는 사진들을 collection에 추가함
  - 마지막에 만들었던 collection 까지 지워준다.  

# 2021.05.23  
- aws api gateway에서 삭제부분을 만들었다. -> lambda 함수(delete_item_dynamodb_s3) 호출  
  - lambda 함수(delete_item_dynamodb_s3) : 요청 url에 포함된 이름의 데이터를 삭제한다. (dynamodb 에서 item 을 삭제, S3/allowed 안의 이미지 삭제)  
![image](https://user-images.githubusercontent.com/66052461/119261085-b3faf880-bc10-11eb-8490-94ab48c1087c.png)  

# 2021.05.24  
- lambda 함수 수정 : 요청한후 응답을 true, false로 나타내 리턴하게끔 수정  
- 어플  
  - 조회시 radio 형식으로 People 에 저장되어있는 테이블을 보여줌  
  - 이름을 선택하면 s3 에서 이미지들을 다운받아 이미지뷰에 보여줌  
  - 이름 선택후 삭제하면 s3, dynamodb 모두 삭제  
  ![image](https://user-images.githubusercontent.com/66052461/119349385-16b7c700-bcd9-11eb-941f-3790a1822aa2.png)  
- 해야할 것  
  - lambda 함수가 리턴한 값을 어플에서 못가져옴.  
  ![image](https://user-images.githubusercontent.com/66052461/119349912-b1180a80-bcd9-11eb-8938-8e31e2001fbe.png)  

# 2021.05.28  
- 앱 UI 업데이트  
  - (수정전)
    - 메인 화면에서 허가된 인물 추가 기능  
    - 가상 에뮬레이터 이용  
  - (수정후)  
    - '관리'에 들어가서 추가할 수 있도록 바꿈  
    - layout 깔끔하게 변경  
    - 실제 안드로이드 단말에서 테스트 함  
    - 메인 화면  
    ![Screenshot_2021-05-28-18-50-17](https://user-images.githubusercontent.com/66052461/119966279-1de71980-bfe6-11eb-8a4b-76b940136c54.png)  
    - 관리 화면  
    ![Screenshot_2021-05-28-18-50-28](https://user-images.githubusercontent.com/66052461/119966432-45d67d00-bfe6-11eb-837c-8889b5d5adde.png)  
    - 로그 화면  
    ![Screenshot_2021-05-28-18-50-40](https://user-images.githubusercontent.com/66052461/119966531-5be43d80-bfe6-11eb-9f72-a6b855b15a71.png)  
- LOG 테이블 조회 기능 추가  
  - (문제점) : 현재 시간순으로 정렬되어 있지 않음  
- app notification 기능 추가  
  - Firebase Cloud Messaging(FCM) 으로 알림 보내게끔 추가  
  ![notification](https://user-images.githubusercontent.com/66052461/119988892-88f21980-c001-11eb-8e39-5d8ba629ce1e.png)  
  
  
# 2021.05.30  
- 여태 한 것 정리 : 회의 준비  
![SmartHome_2021 05 30](https://user-images.githubusercontent.com/66052461/120106701-19af2d80-c199-11eb-8323-efbd376433cb.png)  

# 2021.06.01  
- Log 테이블 조회 방식 변경 - Correct(출입자와 허가자의 데이터가 일치하는지) 값 true or false 로 조회 가능  
  - True 버튼을 누르면  
  ![true_log](https://user-images.githubusercontent.com/66052461/120315981-e47d1980-c317-11eb-8e8c-85d963aaf232.png)  
  - False 버튼을 누르면  
  ![false_log](https://user-images.githubusercontent.com/66052461/120315985-e5ae4680-c317-11eb-93dd-fd913c192b9d.png)  
 
 
# 2021.06.05  
- s3 버킷에 동영상 하나를 업로드하여 안드로이드에서 스트리밍으로 영상을 볼 수 있는지 확인했다.  
  - 동영상을 url 로 가져오는데 이때 읽어오는데 시간이 좀 걸린다.  
  ![video stream](https://user-images.githubusercontent.com/66052461/120881466-2fdf4280-c60c-11eb-8446-44ba789d7dc3.png)  

# 2021.06.10  
- AWS IoT Core 를 rpi 를 등록하고, paho-mqtt 를 이용해 pub / sub 를 구축하였다.  
- 지문인식DB 의 Correct 가 True 라고 저장되면 rpi 에서 true 값을 받도록 하였다.  
  - 후에 rpi 에서 받은 값중 Name이나 Id에 해당하는 값으로 얼굴 인식을 진행할 예정이다.  
  ![aws_iot_rpi_connected](https://user-images.githubusercontent.com/66052461/121523190-e25e3d80-ca30-11eb-94a6-34688fa0fadf.PNG)  
- 또한 라즈베리파이에서 캡쳐한 사진을 S3 에 업로드 하는것 까지 진행했다.  
  ![s3](https://user-images.githubusercontent.com/66052461/121523193-e38f6a80-ca30-11eb-9fbc-0c08ffe846d5.png)  
- DB 데이터 정리  
  ![DB](https://user-images.githubusercontent.com/66052461/121488405-04929400-ca0e-11eb-97e3-6c1d5eea0dc5.png)  

# 2021.06.11  
- 라즈베리파이에서 캡쳐한 파일 이름을 'jun_2021-06-11--16-45-22.jpg' 로 저장했다. -> 이름_시간(년-월-일--시-분-초) 형식  
- 이후에 s3/entered 폴더에 업로드가 되면 face_rekognition 함수(aws lambda)가 실행된다.  
  - lambda : face_rekognition  
    - s3 에 저장된 파일 이름에서 사람 이름을 가져와 source 파일경로를 알아내어 target 파일과 얼굴 비교를 한다.  
    - 또한 결과 값을 db 에 저장했다.(시간, 이름, 일치여부, 유사도 를 저장한다.)  
    ![db_add](https://user-images.githubusercontent.com/66052461/121651483-a4672500-cad5-11eb-9fe6-0446c3bc6da5.PNG)  
    
# 2021.06.12  
-  Arduino MKR WiFi 1010 과 AWS IoT Core 에 연결하여 값을 서로 주고 받음.  
  - 아두이노 : "arduino/incoming" 구독중, AWS : "arduino/outgoing" 구독중인 상태  
  - aws 에서 "arduino/incoming" 주제로 아래와 같이 요청을 보냄  
    ![aws_iot_request_led_state](https://user-images.githubusercontent.com/66052461/121765752-3c254b80-cb88-11eb-805d-bef33c456324.png)  
  - 아두이노의 serial monitor 에서 payload 를 확인  
    ![arduino_aws_iot_connect_2](https://user-images.githubusercontent.com/66052461/121765751-3c254b80-cb88-11eb-9984-f9af98926ac9.png)  
  - 아두이노는 "arduino/incoming" 주제를 구독중이므로 aws 에서 보낸 payload 를 decode 해서 led state 를 변경함. (OFF -> ON)  
  - 또한 "arduino/outgoing" 주제에 led state 를 보냄. -> aws 는 "arduino/outgoing" 를 구독중이므로 아래와 같이 아두이노 에서 보낸 payload 를 읽을수 있다.  
    ![arduino_aws_iot_connect_1](https://user-images.githubusercontent.com/66052461/121765750-3af41e80-cb88-11eb-9743-704c1f04dd5e.png)  

# 2021.06.17-18  
- 아두이노의 지문인식 센서를 활용하여 데이터를 보냄.(json 형식으로 바꿔서)      
  ![mqtt_fingerprint_1](https://user-images.githubusercontent.com/66052461/122499846-990f7e80-d02c-11eb-94b2-4d7e167d8596.png)  
- 지문인식 결과 에러 값인 1. 지문이 인식되지 않았다는 에러 2. Unknown error 일 경우는 데이터 전송을 하지 않는다.    
  ![mqtt_fingerprint_2](https://user-images.githubusercontent.com/66052461/122499848-99a81500-d02c-11eb-928d-a96a323d63ec.png)  

# 2021.06.21  
- Date 항목을 추가함(WiFi.getTime() 함수를 이용)  
- aws iot 규칙을 만들어서(AddFingerPrintLogRule) FingerPrintLog 에 기록하게함.  
  - lambda 함수(AddFingerPrintLog)  
    - 아두이노에서 'arduino/outgoing' 주제로 지문인식 결과(json 형태)를 보낸다.  
    - 이 payload 를 Correct, Date, Confidence, ID, Message 로 나누어서 기록함.    
  ![add_fingerprintlog_db](https://user-images.githubusercontent.com/66052461/122713134-2c041f00-d2a0-11eb-98ed-55db51a480e8.png)  
  
# 2021.06.22  
- FingerPrint 등록 과정 수행  
  - 아두이노 시리얼 모니터에서 이름을 입력 받음 -> FingerPrint 에 있는지 체크함. 있으면 종료 없다면 DB 에 저장  
    - 'arduino/fingerprint/enroll' 주제로 이름을 보내고 lambda 함수 내에서 DB 에 query 로 같은 이름이 있는지 체크함.  
    ![arduino_fingerprint_enroll____topic](https://user-images.githubusercontent.com/66052461/122866440-e65c5a80-d362-11eb-93fc-09d12e06d604.png)  
    - 'aws/fingerprint/enroll' 주제로 같은 이름이 있다면 등록 거부, 이름이 없다면 등록 허가 payload 를 보냄.  
    ![image](https://user-images.githubusercontent.com/66052461/122866770-7d291700-d363-11eb-81d0-203a8bbd8ddb.png)  
    - 아두이노에서 등록 허가 된다면 지문을 등록함.  
    - 성공적으로 지문이 등록 된다면 DB 에 등록하라고 'arduino/fingerprint/db' 주제로 이름과 ID 값을 보냄.  
    ![arduino_fingerprint_db____topic](https://user-images.githubusercontent.com/66052461/122866446-e8261e00-d362-11eb-97cb-4e33015a6a02.png)  
  - 성공적으로 등록이 된 시리얼 모니터 화면  
  ![serial monitor_enroll_success png](https://user-images.githubusercontent.com/66052461/122866179-7fd73c80-d362-11eb-85be-99fc497e34f1.jpg)  
  - 실패 : 이미 이름이 등록된 경우  
  ![serial monitor_enroll_fail](https://user-images.githubusercontent.com/66052461/122866196-85cd1d80-d362-11eb-9556-5812b7f3323d.png)  
  
# 2021.06.23  
- FingerPrint 등록 과정 수정 : id 값을 사용자가 입력하는 방식으로 수정  
  - id, name 을 입력받고 DB 에서 조회를 하여 일치하는 값이 없다면 등록 허가  
  - id 값이 이미 DB 에 존재하는 경우  
  ![existed_id](https://user-images.githubusercontent.com/66052461/123077320-e9d00e80-d454-11eb-9cb0-f732cd709917.png)  
  - name 값이 이미 DB 에 존재하는 경우  
  ![existed_name](https://user-images.githubusercontent.com/66052461/123077323-ea68a500-d454-11eb-9ba1-fc01ead834f6.png)  

# 2021.06.25  
- publish 하는 public(주제 수정) + 지문 삭제 구현
  - 등록   
    - arduino/fingerprint/db/scan : 아두이노가 aws DB 에서 id, name 정보 조회하라고 publish 하는 주제  
    - aws/fingerprint/db/scan : aws 가 DB 에서 조회 결과를 아두이노에게 알려주는 주제  
    - arduino/fingerprint/db/enroll : 아두이노에서 지문등록이 완료가 되어 aws DB 에 id, name 을 등록하라고 publish 하는 주제  
    
  - 삭제 : 시리얼 모니터에서 사용자가 id, name 을 입력하면 DB 에서 조회를 하고 데이터가 존재한다면 아두이노 지문과 aws DB 데이터를 삭제한다.  
    - arduino/fingerprint/db/scan : 아두이노가 aws DB 에서 id, name 정보 조회하라고 publish 하는 주제   
    ![delete_1](https://user-images.githubusercontent.com/66052461/123407611-1caa0c00-d5e7-11eb-968b-6bee230c2c62.png)  
    - aws/fingerprint/db/scan : aws 가 DB 에서 조회 결과를 아두이노에게 알려주는 주제  
    ![delete_2](https://user-images.githubusercontent.com/66052461/123407615-1d42a280-d5e7-11eb-876f-fff0aa8c9092.png)  
    - arduino/fingerprint/db/delete : 아두이노에서 지문삭제가 완료가 되어 aws DB 에도 삭제하라고 publish 하는 주제  
    ![delete_3](https://user-images.githubusercontent.com/66052461/123407617-1ddb3900-d5e7-11eb-9f27-efaa0d744bdd.png)  

- 또한 람다 함수도 수정 : scan lambda 에서 등록, 삭제 과정에서 서로 리턴해주는 정보가 달라서 현재 과정이 어떤 과정인지 알려주는것을 추가.
  - "Process" : "Enroll" or "Delete"
  
# 2021.06.26  
- app 에서 FingerPrint DB, Log 를 조회 구현.  
  - 앞에서 face recognition 부분과 동일하게 진행했고, 반복되는 코드를 줄였다.  

- 해야 할 것  
  - 지문인식실패시 핸드폰으로 알림오게끔 추가.  
  - 지문인식 하면 바로 얼굴인식 하도록 동작하게끔 수정.  
  - 한이음 계정을 이용해서 여태 해왔던것 해야함.  

# 2021.06.28  
- 지문 비교 실패시 알림이 오게끔 추가  
- 안드로이드 앱 코드 수정 : UI 변경, 패키지화  

# 2021.06.29
- 한이음 계정으로 프로젝트 옮김.

# 2021.07 ~ 07.14
- 한이음 aws 계정 엑세스키 유출 사건
- 팀원이 엑세스키 포함된 코드를 깃허브에 퍼블릭으로 저장해 유출되었다
- EC2 의 인스턴스를 많이 생성하려는 시도가 있었다.
- 하지만 한이음 측에서 aws 계정을 빌려줄때 제한을 걸어놔서 앞서 말한 시도가 성공은 하지 못했다.
- 주의 필요함.
- 람다 함수 업로드


# 2021.07.23
- 회의를 통한 부족한점 찾기.
  - 프로젝트 주제 연관성 찾기
  - 앱 UI 수정
  - 관리자 모드 추가: 집 밖에서 문을 열어줄수 있게 제어
  - 얼굴로그 false 조회시 출입자 얼굴 확인 가능하도록 추가

# 2021.07.26
- 얼굴 DB false 조회시 출입자 얼굴 확인 기능 추가

# 2021.07.27  
- 관리자 모드 추가  
  - 알고리즘  
    1. 어플에서 관리자로 들어가서 예를 누른다. -> DB 에 시간과 허가했다는 True 데이터가 기록된다.  
    2. 트리거로 람다 함수 실행 -> rpi 에게 mqtt 방식으로 문을 열어주라는(모터 코드 실행) 명령을 내림.  
    3. 기존 gpio 라이브러리는 서보모터가 떨림 -> pigpio 라이브러리 사용해 떨림 방지   
  - 개선  
    - 관리자로 들어갈때 비밀번호 입력하라는 문구 떠야할듯.   


# 2021.07.31  
- cctv 에서 녹화된 영상 확인  
  - 날짜별로 폴더로 만들어져있고 해당 폴더에는 해당 날짜에 찍힌 동영상 파일들이 저장되어 있다.  
  - https://github.com/jjuun0/smart-home/issues/28 (어플 캡쳐 화면)  
  - api gateway 로 cctv 폴더, 파일에 접근하여 폴더 파일 이름을 가져와 사용자에게 알려준다.  
  - 파일을 클릭하면 url 로 비디오를 열어 재생하는 알고리즘.  

# 2021.08.02  
- 관리자 모드 액티비티 들어가기전에 비밀번호를 물어보고 접근 가능하도록 수정  
  - 문을 강제로 열어주는 행동임으로 한번더 비밀번호를 물어봐야함.  
  - https://github.com/jjuun0/smart-home/issues/29  

# 2021.08.03
- Face DB 조회후, 항목을 선택하고 삭제할때 바로 삭제가 되었는데, 이를 한번 더 삭제하겠냐고 알려주는 경고창 생성  
 - https://github.com/jjuun0/smart-home/issues/30    

# 2021.08.31  
- 모니터 화면에 도어락 인증 절차를 출력하는것을 추가  
  - 인증 결과를 3초간 띄워준다. mqtt 통신을 통해 true, false 값만 띄워준다.   
  - open_door 람다 함수 변경  
    - 기존에는 얼굴 인증이 통과된 결과만 값을 전송하였는데, 실패도 결과를 전송함.  
    - 모니터 화면에 얼굴 인증 절차를 띄워야하기 때문이다.  
