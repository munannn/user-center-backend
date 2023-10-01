package com.luo.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.oas.annotations.EnableOpenApi;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author 木南
 */
//@Configuration
//@EnableOpenApi
//@Profile("dev")
public class SwaggerConfig{

//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                //这里一定要标注你控制器的位置
//                .apis(RequestHandlerSelectors.basePackage("com.luo.usercenter.controller"))
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("用户中心")
//                .description("用户中心接口文档")
//                .termsOfServiceUrl("https://github/munannn")
//                .contact(new Contact("luohanzhi", "https://github/munannn", "2087609705@qq.com"))
//                .version("1.0")
//                .build();
//    }
}