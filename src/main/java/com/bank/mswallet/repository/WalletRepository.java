package com.bank.mswallet.repository;

import com.bank.mswallet.model.Wallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para monederos.
 */
public interface WalletRepository extends ReactiveMongoRepository<Wallet, String> {

    /**
     * Busca un monedero por numero de celular.
     *
     * @param phoneNumber numero de telefono
     * @return Mono con el monedero o vacio
     */
    Mono<Wallet> findByPhoneNumber(String phoneNumber);
}
