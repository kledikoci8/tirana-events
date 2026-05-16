package com.tirana.events.dto;

import lombok.Data;

@Data
public class VerifyTicketRequest {
    private String qrCode;
    private String nfcData;
    private String scannerId;
}
