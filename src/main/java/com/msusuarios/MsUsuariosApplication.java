package com.msusuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling; // 👈 Import necesario

@SpringBootApplication
@EnableDiscoveryClient         // 👈 Habilita registro en Eureka
@EnableFeignClients            // 👈 Habilita uso de FeignClient
@EnableScheduling              // 👈 Habilita tareas programadas como @Scheduled
public class MsUsuariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsUsuariosApplication.class, args);
    }
}
