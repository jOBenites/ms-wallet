package com.bank.mswallet.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Productor de eventos del monedero móvil.
 */
@Component
@RequiredArgsConstructor
public class WalletEventProducer {

    private static final Logger log = LoggerFactory.getLogger(WalletEventProducer.class);
    private static final String LINKED_TOPIC = "bank.wallet.linked-to-debitcard";
    private static final String PAYMENT_SENT_TOPIC = "bank.wallet.payment-sent";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publica evento de asociación de monedero a tarjeta de debito.
     *
     * @param walletId identificador del monedero
     * @param phoneNumber numero de celular
     * @param debitCardId identificador de la tarjeta
     */
    public void publishLinkedToDebitCard(String walletId, String phoneNumber, String debitCardId) {
        Map<String, Object> payload = Map.of(
                "walletId", walletId,
                "phoneNumber", phoneNumber,
                "debitCardId", debitCardId,
                "occurredAt", LocalDateTime.now()
        );
        kafkaTemplate.send(LINKED_TOPIC, walletId, payload);
        log.info("Evento bank.wallet.linked-to-debitcard publicado para wallet {}", walletId);
    }

    /**
     * Publica el envío de pago por celular.
     *
     * @param paymentId identificador del pago
     * @param sourcePhoneNumber origen
     * @param targetPhoneNumber destino
     * @param amount monto
     */
    public void publishPaymentSent(String paymentId, String sourcePhoneNumber, String targetPhoneNumber, BigDecimal amount) {
        Map<String, Object> payload = Map.of(
                "paymentId", paymentId,
                "sourcePhoneNumber", sourcePhoneNumber,
                "targetPhoneNumber", targetPhoneNumber,
                "amount", amount,
                "occurredAt", LocalDateTime.now()
        );
        kafkaTemplate.send(PAYMENT_SENT_TOPIC, paymentId, payload);
        log.info("Evento bank.wallet.payment-sent publicado para pago {}", paymentId);
    }
}
