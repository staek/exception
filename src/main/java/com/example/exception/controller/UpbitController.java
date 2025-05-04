package com.example.exception.controller;

import com.example.exception.dto.OrderRequest;
import com.example.exception.dto.OrderResponse;
import com.example.exception.service.UpbitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/upbit")
@RequiredArgsConstructor
public class UpbitController {

    private final UpbitService upbitService;

    @GetMapping("/markets")
    public ResponseEntity<Map<String, Object>> getMarketInfo() {
        return ResponseEntity.ok(upbitService.getMarketInfo());
    }

    @GetMapping("/ticker")
    public ResponseEntity<Map<String, Object>> getCurrentPrice(@RequestParam String markets) {
        return ResponseEntity.ok(upbitService.getCurrentPrice(markets));
    }

    @GetMapping("/orderbook")
    public ResponseEntity<Map<String, Object>> getOrderbook(@RequestParam String markets) {
        return ResponseEntity.ok(upbitService.getOrderbook(markets));
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(upbitService.placeOrder(orderRequest));
    }

    @GetMapping("/accounts")
    public ResponseEntity<Map<String, Object>> getBalance() {
        return ResponseEntity.ok(upbitService.getBalance());
    }

    @DeleteMapping("/order")
    public ResponseEntity<Map<String, Object>> cancelOrder(@RequestParam String uuid) {
        return ResponseEntity.ok(upbitService.cancelOrder(uuid));
    }
} 