package com.ridehub.paymentservice.dto.response;

public final class GatewayResponse {

    private GatewayResponse(){}

    public static final String SUCCESS_CODE = "00";
    public static final String FAILED_CODE = "01";
    public static final String PROCESSING_CODE = "02";

    public static final String SUCCESS_MESSAGE = "Payment Successful";
    public static final String FAILED_MESSAGE = "Payment Failed";
    public static final String PROCESSING_MESSAGE = "Payment Processing";
}