package com.bank.mswallet.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de respuesta para un monedero.
 */
@Getter
@Setter
public class WalletResponse {

    private String id;
    private String documentType;
    private String documentNumber;
    private String phoneNumber;
    private String imei;
    private String email;
    private String linkedDebitCardId;
}
