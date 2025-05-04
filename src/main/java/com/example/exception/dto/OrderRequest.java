package com.example.exception.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String market;
    private String side;
    private String volume;
    private String price;
    private String ordType = "limit";

    @Override
    public String toString() {
        return String.format("market=%s&side=%s&volume=%s&price=%s&ord_type=%s",
                market, side, volume, price, ordType);
    }
} 