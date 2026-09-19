package com.bank.mswallet.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para enviar un pago entre monederos.
 */
@Getter
@Setter
public class WalletPaymentRequest {

    private String sourcePhoneNumber;
    private String targetPhoneNumber;
    private BigDecimal amount;
}
