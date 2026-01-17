package com.example.ai_kanban;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = { FreeMarkerAutoConfiguration.class })
@MapperScan("com.example.ai_kanban.domain.mapper")
@EnableCaching
public class AiKanbanApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiKanbanApplication.class, args);
    }

}
