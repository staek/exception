import json
import asyncio
import websockets
from typing import List, Callable

class UpbitWebSocket:
    def __init__(self):
        self.ws_url = "wss://api.upbit.com/websocket/v1"
        self.callbacks = {}

    async def connect(self):
        """웹소켓 연결"""
        self.ws = await websockets.connect(self.ws_url)
        return self.ws

    async def subscribe(self, markets: List[str], channels: List[str]):
        """구독 설정"""
        subscribe_fmt = [
            {"ticket": "UNIQUE_TICKET"},
            {
                "type": "ticker",
                "codes": markets,
                "isOnlyRealtime": True
            }
        ]
        await self.ws.send(json.dumps(subscribe_fmt))

    async def listen(self, callback: Callable):
        """실시간 데이터 수신"""
        while True:
            try:
                data = await self.ws.recv()
                data = json.loads(data)
                await callback(data)
            except Exception as e:
                print(f"Error in websocket connection: {e}")
                break

    async def close(self):
        """웹소켓 연결 종료"""
        if hasattr(self, 'ws'):
            await self.ws.close()

async def example_callback(data):
    """예시 콜백 함수"""
    print(f"Received data: {data}")

async def main():
    """메인 실행 함수"""
    ws_client = UpbitWebSocket()
    await ws_client.connect()
    
    # BTC와 ETH 시세 구독
    markets = ["KRW-BTC", "KRW-ETH"]
    await ws_client.subscribe(markets, ["ticker"])
    
    try:
        await ws_client.listen(example_callback)
    except KeyboardInterrupt:
        await ws_client.close()

if __name__ == "__main__":
    asyncio.run(main()) 