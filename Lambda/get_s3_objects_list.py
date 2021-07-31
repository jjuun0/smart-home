import json
import boto3


def lambda_handler(event, context):
    # print(event)

    bucket = 'smarthome-2021'
    client = boto3.client('s3')

    # get folders
    if not event:
        prefix = 'cctv/'  # Make sure you provide / in the end

        result = client.list_objects(Bucket=bucket, Prefix=prefix, Delimiter='/')
        folder_list = []
        for o in result.get('CommonPrefixes'):
            print('sub folder : ', o.get('Prefix'))
            path = o.get('Prefix')
            name = path.split('/')[1]
            folder_list.append(name)

        return folder_list

    # get files
    else:
        date = event['date']
        prefix = 'cctv/' + date + '/'
        response = client.list_objects(Bucket=bucket, Prefix=prefix)

        file_list = []
        for obj in response.get('Contents')[1:]:
            path = obj.get('Key')
            name = path.split('/')[-1].split('.')[0]
            file_list.append(name)
        return file_list

        # return json.dumps(files, default=str)

