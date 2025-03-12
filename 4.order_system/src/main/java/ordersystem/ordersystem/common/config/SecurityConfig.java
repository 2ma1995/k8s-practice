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
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter authFilter; // 변수명 통일

    public SecurityConfig(JwtAuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/member/create", "/member/doLogin", "/product/list", "/member/refresh-token")
                        .permitAll().anyRequest().authenticated())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class) // 필터 변경
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
