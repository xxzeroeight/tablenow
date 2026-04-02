package com.tablenow.tablenow.global.config;

import com.tablenow.tablenow.global.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig
{
    private final JwtFilter jwtFilter;

    /**
     * BCrypt 해싱을 사용하는 {@link PasswordEncoder} 빈 등록.
     * <p>
     * 단방향 암호화로 복호화 불가.
     *
     * @return {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 애플리케이션 시작 시 적용된 Security 필터 체인 목록을 콘솔에 출력하는 {@link CommandLineRunner} 빈 등록.
     * <p>
     * 각 필터의 순서(현재/전체)와 클래스명을 출력하며, 필터 구성 확인 용도로 사용.
     * 운영 환경에서는 제거 권장.
     *
     * @param debugFilterChain 출력 대상 {@link SecurityFilterChain}
     * @return 필터 목록을 콘솔에 출력하는 {@link CommandLineRunner}
     */
    @Bean
    public CommandLineRunner debugFilterChain(SecurityFilterChain debugFilterChain) {
        return args -> {
            int filterSize = debugFilterChain.getFilters().size();

            List<String> filterNames = IntStream.range(0, filterSize)
                    .mapToObj(idx -> String.format("\t[%s/%s] %s", idx + 1, filterSize,
                            debugFilterChain.getFilters().get(idx).getClass()))
                    .toList();

            System.out.println("현재 적용된 필터 체인 목록:");
            filterNames.forEach(System.out::println);
        };
    }

    /**
     * Security 필터 체인 설정.
     * <ul>
     *   <li>CSRF 비활성화</li>
     *   <li>세션 Stateless (JWT 사용)</li>
     *   <li>HTTP Basic 비활성화</li>
     *   <li>모든 요청 허용 (추후 수정 예정)</li>
     * </ul>
     *
     * @param http {@link HttpSecurity}
     * @return {@link SecurityFilterChain}
     * @throws Exception 설정 중 오류 발생 시
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF 설정
            .csrf(AbstractHttpConfigurer::disable)
            // 2. 세션 설정
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 3. Http Basic Authentication 설정
            .httpBasic(AbstractHttpConfigurer::disable)
            // 4. JwtFilter 설정
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            // 5. HTTP 요청 권한 설정
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/signup", "/api/auth/reissue").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                    .anyRequest().authenticated()
            )
            // 6. 예외 처리 설정
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((req, resp, e) ->
                            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            );

        return http.build();
    }
}
