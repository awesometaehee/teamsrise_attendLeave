package com.of.attendLeave.infra.config;

import com.of.attendLeave.modules.user.RequestUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(a -> a.requestMatchers("/actuator/**").permitAll().anyRequest().authenticated())
                .addFilterBefore(internalJwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    OncePerRequestFilter internalJwtFilter() {
        return new OncePerRequestFilter() {
            final PublicKey publicKey = loadPublicKey("/keys/internal-jwt-public.pem");

            @Override
            protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
                String auth = req.getHeader("Authorization");
                if (auth == null || !auth.startsWith("Bearer ")) {
                    res.sendError(401, "Missing internal token");
                    return;
                }
                String token = auth.substring(7);
                try {
                    Jws<Claims> jws = Jwts.parserBuilder()
                            .setSigningKey(publicKey)
                            .requireIssuer("gw.internal")
                            .build()
                            .parseClaimsJws(token);

                    Claims c = jws.getBody();
                    if (!"attn.internal".equals(c.getAudience())) {
                        res.sendError(401, "Invalid audience");
                        return;
                    }
                    @SuppressWarnings("unchecked")
                    var roles = (List<String>) c.getOrDefault("roles", List.of());
                    var auths = roles.stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                            .toList();

                    var user = new RequestUser(
                            c.getSubject(),
                            c.get("company_idx", Integer.class),
                            c.get("tid", String.class),
                            c.get("deptCode", String.class),
                            roles
                    );

                    var authentication = new UsernamePasswordAuthenticationToken(user, "N/A", auths);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    chain.doFilter(req, res);
                } catch (JwtException e) {
                    res.sendError(401, "Invalid internal token");
                }
            }
        };
    }

    private PublicKey loadPublicKey(String classpathPem) {
        try {
            String pub = new String(Objects.requireNonNull(
                    this.getClass().getResourceAsStream(classpathPem)).readAllBytes());
            pub = pub.replaceAll("-----\\w+ PUBLIC KEY-----", "").replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(pub);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(new X509EncodedKeySpec(der));
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOriginPatterns(List.of("http://localhost:*", "https://localhost:*"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        c.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }

    /**
     * 역할 계층 (상위 권한이 하위 권한을 포함)
     * @return
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
                ROLE_SUPER_CORP > ROLE_SUPER
                ROLE_SUPER > ROLE_CORP
                ROLE_CORP > ROLE_NONE
                """);
    }

    /**
     * 메서드 보안
     * @param roleHierarchy
     * @return
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        var handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }
}
