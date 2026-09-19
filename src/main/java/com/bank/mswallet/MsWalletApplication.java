package com.bank.mswallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Microservicio de monedero movil Yanki.
 */
@EnableMongoAuditing
@SpringBootApplication
public class MsWalletApplication {

    /**
     * Inicio de la aplicacion.
     *
     * @param args argumentos de inicio
     */
    public static void main(String[] args) {
        SpringApplication.run(MsWalletApplication.class, args);
    }
}
