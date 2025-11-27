package com.example.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


// Bean pool : bean객체들을 담아놈
// Spring Application의 환경 설정의 일부를 담당하는 클래스

/**
 * Spring Security 관련 설정을 담당하는 설정 클래스.
 *
 * <p>비밀번호 암호화를 위해 {@link BCryptPasswordEncoder}를 빈(Bean)으로 등록한다.
 * BCrypt는 강력한 해시 알고리즘을 제공하여 사용자 비밀번호를 안전하게 저장할 수 있게 한다.</p>
 *
 * <p>{@code @Configuration}을 사용하여 스프링이 해당 클래스를 설정 클래스로 인식하도록 한다.</p>
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
