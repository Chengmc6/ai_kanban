package com.example.ai_kanban;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodeGenerator {

    // 数据库配置
    private static final String URL = "jdbc:mysql://localhost:3306/ai_kanban?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    // 作者
    private static final String AUTHOR = "高明";

    // 项目路径
    private static final String PROJECT_PATH = System.getProperty("user.dir");

    public static void main(String[] args) {

        FastAutoGenerator.create(URL, USERNAME, PASSWORD)

                // 全局配置
                .globalConfig(builder -> builder
                        .author(AUTHOR)
                        .outputDir(PROJECT_PATH + "/src/main/java")
                        .dateType(DateType.TIME_PACK)
                        .disableOpenDir()
                )

                // 包配置
                .packageConfig(builder -> builder
                        .parent("com.example.ai_kanban")
                        .entity("domain.entity")
                        .mapper("domain.mapper")
                        .service("domain.service")
                        .serviceImpl("domain.service.impl")
                        .controller("controller")
                        .pathInfo(Collections.singletonMap(
                                OutputFile.xml,
                                PROJECT_PATH + "/src/main/resources/mapper/"
                        ))
                )

                // 模板配置（你可以在这里扩展 DTO/VO/Converter）
                .templateConfig(builder -> {
                    // 默认模板即可，不需要额外配置
                })

                // 策略配置
                .strategyConfig(builder -> {

                    // ⭐ 自动读取所有表（已过滤）
                    List<String> tables = getAllTables(URL, USERNAME, PASSWORD);
                    builder.addInclude(tables);

                    // Entity 策略
                    builder.entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .enableChainModel()
                            .enableFileOverride()
                            .formatFileName("%sEntity");

                    // Mapper 策略
                    builder.mapperBuilder()
                            .enableBaseResultMap()
                            .enableBaseColumnList()
                            .enableFileOverride()
                            .formatMapperFileName("%sMapper")
                            .formatXmlFileName("%sMapper");

                    // Service 策略
                    builder.serviceBuilder()
                            .enableFileOverride()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl");

                    // Controller 策略
                    builder.controllerBuilder()
                            .enableRestStyle()
                            .enableHyphenStyle()
                            .enableFileOverride()
                            .formatFileName("%sController");
                })

                // ⭐ 使用 Freemarker 模板引擎（关键）
                .templateEngine(new FreemarkerTemplateEngine())

                .execute();
    }

    /**
     * 自动读取数据库所有表名，并过滤不需要生成的表
     */
    public static List<String> getAllTables(String url, String username, String password) {
        List<String> tables = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()")) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String table = rs.getString("table_name");

                // 自动过滤不想生成的表
                if (table.startsWith("flyway") ||
                        table.startsWith("sys_") ||
                        table.startsWith("t_") ||
                        table.equalsIgnoreCase("flyway_schema_history")) {
                    continue;
                }

                tables.add(table);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tables;
    }
}
