import com.example.producttracking.ProductTrackingApplication;
import com.example.producttracking.config.SecurityConfig;
import com.example.producttracking.controller.AuthController;
import com.example.producttracking.dto.LoginRequest;
import com.example.producttracking.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = ProductTrackingApplication.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    public void whenLoginSuccess_thenReturnToken() throws Exception {
        // Hazırlık
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password");

        String token = "sample-token";
        when(authService.login(eq("john@example.com"), eq("password"))).thenReturn(token);

        // Test: /api/auth/login endpoint'ine POST isteği gönderiliyor
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    public void whenLoginFailure_thenUnauthorized() throws Exception {
        // Hazırlık
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("wrongpassword");

        when(authService.login(eq("john@example.com"), eq("wrongpassword")))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Test: Yanlış şifreyle login denendiğinde UNAUTHORIZED (401) dönmeli
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials."));
    }

    @Test
    public void whenLogoutSuccess_thenReturnSuccessMessage() throws Exception {
        // Hazırlık: logout sırasında bir exception fırlatılmaz, dolayısıyla doğrudan başarılı işlem gerçekleşir.
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");

        // Test: /api/auth/logout endpoint'ine POST isteği gönderiliyor
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful."));
    }

    @Test
    public void whenLogoutFailure_thenBadRequest() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");

        doThrow(new RuntimeException("Logout failed"))
        .when(authService).logout(eq("john@example.com"));

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Logout failed."));
    }
}