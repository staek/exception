package com.example.exception.service;

import com.example.exception.config.UpbitConfig;
import com.example.exception.dto.OrderRequest;
import com.example.exception.dto.OrderResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpbitService {

    private final RestTemplate restTemplate;
    private final UpbitConfig upbitConfig;

    private String createToken(String query) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("access_key", upbitConfig.getAccessKey());
        claims.put("nonce", UUID.randomUUID().toString());
        if (query != null) {
            claims.put("query", query);
        }

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, upbitConfig.getSecretKey().getBytes())
                .compact();
    }

    public Map<String, Object> getMarketInfo() {
        String url = UriComponentsBuilder.fromHttpUrl(upbitConfig.getBaseUrl())
                .path("/market/all")
                .build()
                .toString();
        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> getCurrentPrice(String markets) {
        String url = UriComponentsBuilder.fromHttpUrl(upbitConfig.getBaseUrl())
                .path("/ticker")
                .queryParam("markets", markets)
                .build()
                .toString();
        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> getOrderbook(String markets) {
        String url = UriComponentsBuilder.fromHttpUrl(upbitConfig.getBaseUrl())
                .path("/orderbook")
                .queryParam("markets", markets)
                .build()
                .toString();
        return restTemplate.getForObject(url, Map.class);
    }

    public OrderResponse placeOrder(OrderRequest orderRequest) {
        String url = UriComponentsBuilder.fromHttpUrl(upbitConfig.getBaseUrl())
                .path("/orders")
                .build()
                .toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(createToken(orderRequest.toString()));

        HttpEntity<OrderRequest> request = new HttpEntity<>(orderRequest, headers);
        return restTemplate.postForObject(url, request, OrderResponse.class);
    }

    public Map<String, Object> getBalance() {
        String url = UriComponentsBuilder.fromHttpUrl(upbitConfig.getBaseUrl())
                .path("/accounts")
                .build()
                .toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(createToken(null));

        HttpEntity<?> request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, request, Map.class).getBody();
    }

    public Map<String, Object> cancelOrder(String uuid) {
        String url = UriComponentsBuilder.fromHttpUrl(upbitConfig.getBaseUrl())
                .path("/order")
                .build()
                .toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(createToken("uuid=" + uuid));

        Map<String, String> body = new HashMap<>();
        body.put("uuid", uuid);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, request, Map.class).getBody();
    }
} 