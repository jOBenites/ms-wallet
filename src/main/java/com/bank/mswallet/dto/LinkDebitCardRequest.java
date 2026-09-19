package com.bank.mswallet.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para asociar un monedero a una tarjeta de debito.
 */
@Getter
@Setter
public class LinkDebitCardRequest {

    private String debitCardId;
}
