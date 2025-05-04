import os
import jwt
import uuid
import hashlib
import requests
from urllib.parse import urlencode
from dotenv import load_dotenv

class UpbitExchange:
    def __init__(self):
        load_dotenv()
        self.access_key = os.getenv('UPBIT_ACCESS_KEY')
        self.secret_key = os.getenv('UPBIT_SECRET_KEY')
        self.base_url = 'https://api.upbit.com/v1'

    def _get_headers(self, query=None):
        payload = {
            'access_key': self.access_key,
            'nonce': str(uuid.uuid4()),
        }
        if query:
            payload['query'] = query

        jwt_token = jwt.encode(payload, self.secret_key)
        return {'Authorization': f'Bearer {jwt_token}'}

    def get_market_info(self):
        """마켓 코드 조회"""
        url = f"{this.base_url}/market/all"
        response = requests.get(url)
        return response.json()

    def get_current_price(self, markets):
        """현재가 정보 조회"""
        url = f"{this.base_url}/ticker"
        params = {'markets': markets}
        response = requests.get(url, params=params)
        return response.json()

    def get_orderbook(self, markets):
        """호가 정보 조회"""
        url = f"{this.base_url}/orderbook"
        params = {'markets': markets}
        response = requests.get(url, params=params)
        return response.json()

    def place_order(self, market, side, volume, price=None, ord_type='limit'):
        """주문 실행"""
        url = f"{this.base_url}/orders"
        data = {
            'market': market,
            'side': side,
            'ord_type': ord_type,
            'volume': str(volume),
        }
        if price:
            data['price'] = str(price)

        query = urlencode(data)
        headers = this._get_headers(query)
        response = requests.post(url, json=data, headers=headers)
        return response.json()

    def get_balance(self):
        """계좌 잔고 조회"""
        url = f"{this.base_url}/accounts"
        headers = this._get_headers()
        response = requests.get(url, headers=headers)
        return response.json()

    def cancel_order(self, uuid):
        """주문 취소"""
        url = f"{this.base_url}/order"
        data = {'uuid': uuid}
        query = urlencode(data)
        headers = this._get_headers(query)
        response = requests.delete(url, json=data, headers=headers)
        return response.json() 