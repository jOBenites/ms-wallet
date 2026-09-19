package com.bank.mswallet.controller;

import com.bank.mswallet.dto.LinkDebitCardRequest;
import com.bank.mswallet.dto.WalletPaymentRequest;
import com.bank.mswallet.dto.WalletRequest;
import com.bank.mswallet.dto.WalletResponse;
import com.bank.mswallet.model.Wallet;
import com.bank.mswallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador REST reactivo para monederos Yanki.
 */
@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * Registra un monedero.
     *
     * @param request datos del monedero
     * @return Mono con el monedero creado
     */
    @PostMapping
    public Mono<ResponseEntity<WalletResponse>> create(@RequestBody WalletRequest request) {
        return walletService.registerWallet(request.getDocumentType(), request.getDocumentNumber(),
                        request.getPhoneNumber(), request.getImei(), request.getEmail())
                .map(this::toResponse)
                .map(wallet -> ResponseEntity.status(HttpStatus.CREATED).body(wallet));
    }

    /**
     * Busca un monedero por ID.
     *
     * @param id identificador del monedero
     * @return Mono con respuesta o 404
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<WalletResponse>> getById(@PathVariable String id) {
        return walletService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Lista todos los monederos.
     *
     * @return Flux con los monederos
     */
    @GetMapping
    public Flux<WalletResponse> getAll() {
        return walletService.findAll().map(this::toResponse);
    }

    /**
     * Actualiza correo e identificador del dispositivo.
     *
     * @param id identificador del monedero
     * @param request datos actualizables
     * @return Mono con respuesta o 404
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<WalletResponse>> update(@PathVariable String id,
                                                        @RequestBody WalletRequest request) {
        return walletService.update(id, request.getEmail(), request.getImei())
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Elimina un monedero.
     *
     * @param id identificador del monedero
     * @return Mono con 204 o 404
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return walletService.delete(id)
                .map(deleted -> deleted
                        ? ResponseEntity.noContent().<Void>build()
                        : ResponseEntity.notFound().build());
    }

    /**
     * Asocia una tarjeta de debito a un monedero.
     *
     * @param id identificador del monedero
     * @param request tarjeta que se asociara
     * @return Mono con el monedero actualizado
     */
    @PostMapping("/{id}/debit-card")
    public Mono<ResponseEntity<WalletResponse>> linkDebitCard(@PathVariable String id,
                                                               @RequestBody LinkDebitCardRequest request) {
        return walletService.linkDebitCard(id, request.getDebitCardId())
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    /**
     * Envía un pago entre dos celulares.
     *
     * @param request datos del pago
     * @return Mono con el identificador del pago
     */
    @PostMapping("/payments")
    public Mono<ResponseEntity<String>> sendPayment(@RequestBody WalletPaymentRequest request) {
        return walletService.sendPayment(request.getSourcePhoneNumber(), request.getTargetPhoneNumber(),
                        request.getAmount())
                .map(paymentId -> ResponseEntity.status(HttpStatus.CREATED).body(paymentId));
    }

    private WalletResponse toResponse(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setDocumentType(wallet.getDocumentType());
        response.setDocumentNumber(wallet.getDocumentNumber());
        response.setPhoneNumber(wallet.getPhoneNumber());
        response.setImei(wallet.getImei());
        response.setEmail(wallet.getEmail());
        response.setLinkedDebitCardId(wallet.getLinkedDebitCardId());
        return response;
    }
}
