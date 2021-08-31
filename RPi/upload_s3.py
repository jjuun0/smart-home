import boto3
import datetime

def upload_file(name, file_path):

    bucket = 'smarthome-2021'

    access_key_id = 'AKIA6I7XS3WJMUSV2UAE'
    access_secret_key = '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'


    s3_resource = boto3.resource( 
                    's3', 
                    aws_access_key_id=access_key_id, 
                    aws_secret_access_key=access_secret_key, 
                    region_name='ap-northeast-2', 
    ) 

    # get image file 
    image = open(file_path, 'rb')
    
    nowDatetime = datetime.datetime.now().strftime('%Y-%m-%d--%H-%M-%S')

    # save image to S3 bucket as public 
    s3_resource.Bucket(bucket).put_object(Body=image, Key='face/entered/' + name + '_' + nowDatetime + '.jpg', ACL='public-read') 

    print('complete upload')

# time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
# print(time)
