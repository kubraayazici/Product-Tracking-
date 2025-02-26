package com.example.producttracking.config;

import com.example.producttracking.model.User;
import com.example.producttracking.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecretKey jwtSecret;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(SecretKey jwtSecret, UserRepository userRepository) {
        this.jwtSecret = jwtSecret;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // Token'ı doğrula, burada Jwts.parserBuilder() gibi yöntemler kullanabilirsiniz.
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // Token geçerliyse, kullanıcı kimliğini alın.
                String email = claims.getSubject();

                Optional<User> userOptional = userRepository.findByEmail(email);
                if (userOptional.isEmpty() || !userOptional.get().getIsActive()) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User is logged out or inactive");
                    return;
                }
                // Kullanıcının yetkilerini de çekip Authentication nesnesi oluşturun.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ex) {
                // Token doğrulama başarısızsa, hata mesajı yazdırın ve isteği reddedin.
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalid");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}