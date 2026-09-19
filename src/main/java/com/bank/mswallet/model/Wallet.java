package com.bank.mswallet.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Representa un monedero movil del tipo Yanki.
 */
@Getter
@Setter
@NoArgsConstructor
@Document(collection = "wallet")
public class Wallet {

    @Id
    private String id;

    private String documentType;

    private String documentNumber;

    @Indexed(unique = true)
    private String phoneNumber;

    private String imei;

    private String email;

    @Indexed
    private String linkedDebitCardId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Constructor completo.
     *
     * @param documentType tipo de documento
     * @param documentNumber numero de documento
     * @param phoneNumber numero de celular
     * @param imei identificador de dispositivo
     * @param email correo electronico
     */
    public Wallet(String documentType, String documentNumber, String phoneNumber, String imei, String email) {
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.phoneNumber = phoneNumber;
        this.imei = imei;
        this.email = email;
    }
}
