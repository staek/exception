# Upbit Exchange System

업비트 거래소 API를 활용한 거래 시스템입니다.

## 설치 방법

1. 필요한 패키지 설치:
```bash
pip install -r requirements.txt
```

2. 환경 변수 설정:
- `.env` 파일을 생성하고 다음 내용을 입력하세요:
```
UPBIT_ACCESS_KEY=your_access_key_here
UPBIT_SECRET_KEY=your_secret_key_here
```

## 주요 기능

1. 시장 정보 조회
2. 현재가 정보 조회
3. 호가 정보 조회
4. 주문 실행
5. 계좌 잔고 조회
6. 주문 취소
7. 실시간 시세 조회 (WebSocket)

## 사용 예시

```python
from upbit_exchange import UpbitExchange

# 거래소 인스턴스 생성
exchange = UpbitExchange()

# 현재가 조회
markets = "KRW-BTC"
current_price = exchange.get_current_price(markets)
print(current_price)

# 주문 실행
order = exchange.place_order(
    market="KRW-BTC",
    side="bid",
    volume="0.001",
    price="50000000"
)
print(order)
```

## WebSocket 사용 예시

```python
from upbit_websocket import UpbitWebSocket
import asyncio

async def main():
    ws_client = UpbitWebSocket()
    await ws_client.connect()
    
    markets = ["KRW-BTC", "KRW-ETH"]
    await ws_client.subscribe(markets, ["ticker"])
    
    try:
        await ws_client.listen(lambda x: print(x))
    except KeyboardInterrupt:
        await ws_client.close()

if __name__ == "__main__":
    asyncio.run(main())
```

## 주의사항

- API 키는 절대로 공개되지 않도록 주의하세요.
- 실제 거래 전에 충분한 테스트를 진행하세요.
- 거래 수수료와 슬리피지를 고려하여 거래를 진행하세요. 