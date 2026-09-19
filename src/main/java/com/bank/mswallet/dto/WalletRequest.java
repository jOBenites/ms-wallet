package com.bank.mswallet.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para registrar o actualizar un monedero.
 */
@Getter
@Setter
public class WalletRequest {

    private String documentType;
    private String documentNumber;
    private String phoneNumber;
    private String imei;
    private String email;
}
