package com.example.exception.dto;

import lombok.Data;

@Data
public class OrderResponse {
    private String uuid;
    private String side;
    private String ordType;
    private String price;
    private String state;
    private String market;
    private String createdAt;
    private String volume;
    private String remainingVolume;
    private String reservedFee;
    private String remainingFee;
    private String paidFee;
    private String locked;
    private String executedVolume;
    private String tradesCount;
} 