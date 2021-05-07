from flask import Flask  # 서버 구현을 위한 Flask 객체 import
from flask_restx import Api, Resource  # Api 구현을 위한 Api 객체 import
from flask_cors import CORS
from flask import Flask, jsonify, request
import boto3

app = Flask(__name__)  # Flask 객체 선언, 파라미터로 어플리케이션 패키지의 이름을 넣어줌.
api = Api(app)  # Flask 객체에 Api 객체 등록

# Constant variable with path prefix
BASE_ROUTE = "/people"
TABLE = 'People'

# @app.route(BASE_ROUTE, methods=['GET'])
# def list_songs():
#     return jsonify(message="hello world")
client = boto3.client('dynamodb', region_name='ap-northeast-2')
dynamodb = boto3.resource('dynamodb', region_name='ap-northeast-2')

@app.route('/people')
def get_table():
    table = dynamodb.Table('People')
    allData = table.scan()
    return jsonify(allData['Items'])


@app.route('/people/<Name>', methods=['GET'])
def get_Item(Name):
    client = boto3.client('dynamodb', region_name='ap-northeast-2')
    item = client.get_item(TableName=TABLE, Key={
        'Name': {
            'S': Name
        }
    })
    return jsonify(item['Item'])



if __name__ == '__main__':
    app.run(debug = True)
