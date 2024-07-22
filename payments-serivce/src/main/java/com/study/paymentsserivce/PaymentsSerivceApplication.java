package com.study.paymentsserivce;

import com.study.paymentsserivce.config.AxonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AxonConfig.class})
public class PaymentsSerivceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentsSerivceApplication.class, args);
    }

}
