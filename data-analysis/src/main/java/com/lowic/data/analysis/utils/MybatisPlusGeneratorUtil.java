package com.lowic.data.analysis.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @author Lowic
 */
public class MybatisPlusGeneratorUtil {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://119.91.192.36:3308/try?characterEncoding=utf-8&serverTimezone=GMT%2B8", "root", "111111")
                .globalConfig(builder -> {
                    builder.author("lowic") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .outputDir("E://"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.lowic.data.analysis") // 设置父包名
                            // .moduleName("system") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "E://")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("sd_ad_rp") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
