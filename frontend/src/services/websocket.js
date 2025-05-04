import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'

class WebSocketService {
  constructor() {
    this.socket = null
    this.stompClient = null
    this.subscriptions = new Map()
  }

  connect() {
    this.socket = new SockJS('/ws')
    this.stompClient = Stomp.over(this.socket)
    this.stompClient.connect({}, this.onConnect.bind(this), this.onError.bind(this))
  }

  onConnect() {
    console.log('WebSocket connected')
    this.subscriptions.forEach((callback, topic) => {
      this.subscribe(topic, callback)
    })
  }

  onError(error) {
    console.error('WebSocket error:', error)
  }

  subscribe(topic, callback) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.subscribe(topic, callback)
    } else {
      this.subscriptions.set(topic, callback)
    }
  }

  unsubscribe(topic) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.unsubscribe(topic)
    }
    this.subscriptions.delete(topic)
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect()
    }
    if (this.socket) {
      this.socket.close()
    }
  }
}

export default new WebSocketService() 