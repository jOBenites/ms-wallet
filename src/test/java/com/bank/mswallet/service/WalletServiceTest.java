package com.bank.mswallet.service;

import com.bank.mswallet.event.WalletEventProducer;
import com.bank.mswallet.model.Wallet;
import com.bank.mswallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletEventProducer walletEventProducer;

    @InjectMocks
    private WalletService walletService;

    @Test
    void registerWallet_success() {
        Wallet wallet = new Wallet("DNI", "12345678", "999111222", "device-1", "juan@test.com");
        wallet.setId("wallet-1");
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(wallet));

        StepVerifier.create(walletService.registerWallet("DNI", "12345678", "999111222", "device-1", "juan@test.com"))
                .assertNext(result -> org.junit.jupiter.api.Assertions.assertEquals("999111222", result.getPhoneNumber()))
                .verifyComplete();
    }

    @Test
    void sendPayment_validAmount_publishesEvent() {
        Wallet source = new Wallet("DNI", "11111111", "999111222", "device-1", "one@test.com");
        source.setId("wallet-1");
        Wallet target = new Wallet("DNI", "22222222", "999333444", "device-2", "two@test.com");
        target.setId("wallet-2");
        when(walletRepository.findByPhoneNumber("999111222")).thenReturn(Mono.just(source));
        when(walletRepository.findByPhoneNumber("999333444")).thenReturn(Mono.just(target));

        StepVerifier.create(walletService.sendPayment("999111222", "999333444", new BigDecimal("55.00")))
                .assertNext(result -> org.junit.jupiter.api.Assertions.assertFalse(result.isBlank()))
                .verifyComplete();

        verify(walletEventProducer).publishPaymentSent(any(), any(), any(), any());
    }

    @Test
    void findById_returnsWallet() {
        Wallet wallet = new Wallet("DNI", "12345678", "999111222", "device-1", "juan@test.com");
        when(walletRepository.findById("wallet-1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(walletService.findById("wallet-1"))
                .assertNext(result -> assertEquals("999111222", result.getPhoneNumber()))
                .verifyComplete();
    }

    @Test
    void findAll_returnsWallets() {
        Wallet wallet = new Wallet("DNI", "12345678", "999111222", "device-1", "juan@test.com");
        when(walletRepository.findAll()).thenReturn(reactor.core.publisher.Flux.just(wallet));

        StepVerifier.create(walletService.findAll())
                .assertNext(result -> assertEquals("12345678", result.getDocumentNumber()))
                .verifyComplete();
    }

    @Test
    void update_existingWallet_savesChanges() {
        Wallet wallet = new Wallet("DNI", "12345678", "999111222", "device-1", "old@test.com");
        when(walletRepository.findById("wallet-1")).thenReturn(Mono.just(wallet));
        when(walletRepository.save(wallet)).thenReturn(Mono.just(wallet));

        StepVerifier.create(walletService.update("wallet-1", "new@test.com", "device-2"))
                .assertNext(result -> {
                    assertEquals("new@test.com", result.getEmail());
                    assertEquals("device-2", result.getImei());
                })
                .verifyComplete();
    }

    @Test
    void delete_existingWallet_returnsTrue() {
        when(walletRepository.existsById("wallet-1")).thenReturn(Mono.just(true));
        when(walletRepository.deleteById("wallet-1")).thenReturn(Mono.empty());

        StepVerifier.create(walletService.delete("wallet-1"))
                .assertNext(assertTrueValue -> assertTrue(assertTrueValue))
                .verifyComplete();
    }

    @Test
    void delete_missingWallet_returnsFalse() {
        when(walletRepository.existsById("wallet-1")).thenReturn(Mono.just(false));

        StepVerifier.create(walletService.delete("wallet-1"))
                .assertNext(assertFalseValue -> assertFalse(assertFalseValue))
                .verifyComplete();
    }
}
