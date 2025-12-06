package com.cydrag.fridgeapp.controller;

import com.cydrag.fridgeapp.config.SecurityConfig;
import com.cydrag.fridgeapp.dto.request.LoginRequest;
import com.cydrag.fridgeapp.security.AuthTokenFilter;
import com.cydrag.fridgeapp.security.CookieFactory;
import com.cydrag.fridgeapp.security.JwtUtils;
import com.cydrag.fridgeapp.security.UserDetailsServiceImpl;
import com.cydrag.fridgeapp.service.AuthService;
import com.cydrag.fridgeapp.service.RefreshTokenService;
import com.cydrag.fridgeapp.service.model.AuthResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private RefreshTokenService refreshTokenService;
    @MockitoBean
    private CookieFactory cookieFactory;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private AuthTokenFilter authTokenFilter;

    @Test
    void login_ShouldReturnAuthResponseAndCookie_WhenCredentialsValid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResult authResult = new AuthResult("access-token-123", "refresh-token-xyz", 3600L);
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "refresh-token-xyz").build();

        when(authService.login(request.getEmail(), request.getPassword())).thenReturn(authResult);
        when(cookieFactory.createRefreshTokenCookie(anyString())).thenReturn(cookie);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("invalid-email");
        request.setPassword("123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().is(HttpStatus.UNPROCESSABLE_CONTENT.value()));
    }
}
