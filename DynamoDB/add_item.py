from pprint import pprint
import boto3


def add_people(name, image_name, url, dynamodb=None):
    """ People 테이블에 '사람 이름', '이미지 이름', '이미지 url' 을 추가하는 함수 """
    if not dynamodb:
        dynamodb = boto3.resource('dynamodb', endpoint_url="http://dynamodb.ap-northeast-2.amazonaws.com")

    table = dynamodb.Table('People')
    response = table.put_item(
        Item={
            'Name': name,
            'Image_Name': image_name,
            'Image_url': url
        }
    )
    return response


def add_log(date, image_name, correct, similarity, dynamodb=None):
    """ Log 테이블에 추가하는 함수 """
    if not dynamodb:
        dynamodb = boto3.resource('dynamodb', endpoint_url="http://dynamodb.ap-northeast-2.amazonaws.com")

    table = dynamodb.Table('Log')
    response = table.put_item(
        Item={
            'Date': date,
            'Image_Name': image_name,
            'Correct': correct,
            'Similarity': similarity
        }
    )
    return response




# image-url : 'https://' + bucket + '.S3.' + region + '.' + 'amazonaws.com/' + image_name
if __name__ == '__main__':
    people_resp = add_people('JunHyoung', 'jun2.png', 'https://junfirstbucket.s3.ap-northeast-2.amazonaws.com/jun2.png', 0)
    print("Add people succeeded:")
    pprint(people_resp)
    '''
    Add people succeeded:
    {'ResponseMetadata': {'HTTPHeaders': {'content-length': '2',
                                      'content-type': 'application/x-amz-json-1.0',
                                      'date': 'Wed, 28 Apr 2021 12:16:24 GMT',
                                      'x-amz-crc32': '2745614147',
                                      'x-amzn-requestid': 'PSS10JRR2568934PE5LBAKVTMBVV4KQNSO5AEMVJF66Q9ASUAAJG'},
                      'HTTPStatusCode': 200,
                      'RequestId': 'PSS10JRR2568934PE5LBAKVTMBVV4KQNSO5AEMVJF66Q9ASUAAJG',
                      'RetryAttempts': 0}}
    '''
