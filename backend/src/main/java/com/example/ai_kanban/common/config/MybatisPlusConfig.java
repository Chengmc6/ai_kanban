package com.example.ai_kanban.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.ai_kanban.common.handlers.ObjectFieldHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MetaObjectHandler metaObjectHandler(){
        return new ObjectFieldHandler();
    }
}
