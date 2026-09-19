package com.bank.mswallet.service;

import com.bank.mswallet.event.WalletEventProducer;
import com.bank.mswallet.model.Wallet;
import com.bank.mswallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Servicio reactivo para el monedero móvil Yanki.
 */
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletEventProducer walletEventProducer;

    /**
     * Registra un monedero para un usuario no bancario.
     *
     * @param documentType tipo de documento
     * @param documentNumber documento
     * @param phoneNumber celular
     * @param imei dispositivo
     * @param email correo
     * @return Mono con el monedero creado
     */
    public Mono<Wallet> registerWallet(String documentType, String documentNumber, String phoneNumber,
                                      String imei, String email) {
        if (documentType == null || documentType.isBlank()) {
            return Mono.error(new IllegalArgumentException("El tipo de documento es obligatorio"));
        }
        if (documentNumber == null || documentNumber.isBlank()) {
            return Mono.error(new IllegalArgumentException("El documento es obligatorio"));
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return Mono.error(new IllegalArgumentException("El celular es obligatorio"));
        }
        Wallet wallet = new Wallet(documentType, documentNumber, phoneNumber, imei, email);
        return walletRepository.save(wallet);
    }

    /**
     * Asocia un monedero a una tarjeta de debito.
     *
     * @param walletId identificador del monedero
     * @param debitCardId identificador de la tarjeta
     * @return Mono con el monedero actualizado
     */
    public Mono<Wallet> linkDebitCard(String walletId, String debitCardId) {
        if (debitCardId == null || debitCardId.isBlank()) {
            return Mono.error(new IllegalArgumentException("La tarjeta es obligatoria"));
        }
        return walletRepository.findById(walletId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Monedero no encontrado")))
                .flatMap(wallet -> {
                    wallet.setLinkedDebitCardId(debitCardId);
                    return walletRepository.save(wallet)
                            .doOnNext(saved -> walletEventProducer.publishLinkedToDebitCard(
                                    saved.getId(), saved.getPhoneNumber(), debitCardId));
                });
    }

    /**
     * Busca un monedero por ID.
     *
     * @param walletId identificador del monedero
     * @return Mono con el monedero o vacio
     */
    public Mono<Wallet> findById(String walletId) {
        return walletRepository.findById(walletId);
    }

    /**
     * Lista todos los monederos.
     *
     * @return Flux con los monederos
     */
    public Flux<Wallet> findAll() {
        return walletRepository.findAll();
    }

    /**
     * Actualiza los datos de contacto de un monedero.
     *
     * @param walletId identificador del monedero
     * @param email nuevo correo
     * @param imei nuevo identificador de dispositivo
     * @return Mono con el monedero actualizado o vacio
     */
    public Mono<Wallet> update(String walletId, String email, String imei) {
        return walletRepository.findById(walletId)
                .flatMap(wallet -> {
                    wallet.setEmail(email);
                    wallet.setImei(imei);
                    return walletRepository.save(wallet);
                });
    }

    /**
     * Elimina un monedero.
     *
     * @param walletId identificador del monedero
     * @return Mono true si se elimino
     */
    public Mono<Boolean> delete(String walletId) {
        return walletRepository.existsById(walletId)
                .flatMap(exists -> {
                    if (exists) {
                        return walletRepository.deleteById(walletId).then(Mono.just(true));
                    }
                    return Mono.just(false);
                });
    }

    /**
     * Envía un pago entre dos celulares.
     *
     * @param sourcePhoneNumber origen
     * @param targetPhoneNumber destino
     * @param amount monto
     * @return Mono con el identificador del pago generado
     */
    public Mono<String> sendPayment(String sourcePhoneNumber, String targetPhoneNumber, BigDecimal amount) {
        if (sourcePhoneNumber == null || sourcePhoneNumber.isBlank()) {
            return Mono.error(new IllegalArgumentException("El origen es obligatorio"));
        }
        if (targetPhoneNumber == null || targetPhoneNumber.isBlank()) {
            return Mono.error(new IllegalArgumentException("El destino es obligatorio"));
        }
        if (amount == null || amount.signum() <= 0) {
            return Mono.error(new IllegalArgumentException("El monto debe ser mayor a cero"));
        }
        return walletRepository.findByPhoneNumber(sourcePhoneNumber)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Monedero origen no encontrado")))
                .flatMap(source -> walletRepository.findByPhoneNumber(targetPhoneNumber)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Monedero destino no encontrado")))
                        .map(target -> {
                            String paymentId = UUID.randomUUID().toString();
                            walletEventProducer.publishPaymentSent(
                                    paymentId, source.getPhoneNumber(), target.getPhoneNumber(), amount);
                            return paymentId;
                        }));
    }
}
