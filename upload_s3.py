import boto3
s3 = boto3.resource('s3')
for bucket in s3.buckets.all():
    print(bucket.name)

s3.meta.client.upload_file('C:/Users/jjuun/Desktop/character/dog.PNG', 'junfirstbucket', 'kangkangju.png')