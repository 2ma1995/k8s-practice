package ordersystem.ordersystem.common.config;

import ordersystem.ordersystem.common.auth.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableMethodSecurity //PreAuthorize사용시 해당 어노테이션 선언 필요.
public class SecurityConfig {
    private final JwtAuthFilter authFilter;
    public SecurityConfig(JwtAuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
//                spring security에서 cors 정책 지정.
                .cors(c -> c.configurationSource(corsConfiguration()))
                .csrf(AbstractHttpConfigurer::disable) //csrf(보안공격 중 하나) 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) //http basic 보안방식 비활성화
//                세션로그인 방식 사용하지 않는다는 것을 의미.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                token을 검증하고, token을 통해 Authentication객체 생성
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class) //Username... -> Authentication객체가 있는지 검사하는 클래스
//                .authenticated() : 모든 요청에 대해 Authentication 객체가 생성되기를 요구
                .authorizeHttpRequests(a -> a.requestMatchers("/member/create","/member/doLogin","member/refresh-token","/product/list").permitAll().anyRequest().authenticated())
                .build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() { // 메서드 이름 변경
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://www.2ma.shop"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder makePassword() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
