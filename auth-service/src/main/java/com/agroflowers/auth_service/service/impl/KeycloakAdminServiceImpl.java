package com.agroflowers.auth_service.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.agroflowers.auth_service.dto.CreateUserByAdminDto;
import com.agroflowers.auth_service.dto.LoginRequestDto;
import com.agroflowers.auth_service.dto.RegisterRequestDto;
import com.agroflowers.auth_service.dto.TokenResponseDto;
import com.agroflowers.auth_service.service.KeycloakAdminService;

import reactor.core.publisher.Mono;

@Service
public class KeycloakAdminServiceImpl implements KeycloakAdminService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakAdminServiceImpl.class);

    private final WebClient keycloakWebClient;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-client-id}")
    private String adminClientId;

    @Value("${keycloak.admin-client-secret}")
    private String adminClientSecret;

    @Value("${keycloak.login-client-id}")
    private String loginClientId;

    @Value("${keycloak.login-client-secret}")
    private String loginClientSecret;

    public KeycloakAdminServiceImpl(WebClient keycloakWebClient) {
        this.keycloakWebClient = keycloakWebClient;
    }

    @Override
    public void registerUser(RegisterRequestDto request) {
        String adminToken = getAdminAccessToken();
        String userId = createUserInKeycloak(
                adminToken,
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName(),
                request.password()
        );

        assignRealmRole(adminToken, userId, "OPERADOR");
        sendVerifyEmail(adminToken, userId);
    }

    @Override
    public void createUserByAdmin(CreateUserByAdminDto request) {
        String adminToken = getAdminAccessToken();
        String userId = createUserInKeycloak(
                adminToken,
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName(),
                request.password()
        );

        assignRealmRole(adminToken, userId, request.role());
        sendVerifyEmail(adminToken, userId);
    }

    @Override
    public TokenResponseDto login(LoginRequestDto request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", loginClientId);
        form.add("client_secret", loginClientSecret);
        form.add("username", request.username());
        form.add("password", request.password());

        Map<String, Object> response;

        try {
            response = keycloakWebClient.post()
                    .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(form))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException.Unauthorized | WebClientResponseException.BadRequest ex) {
            throw new IllegalStateException("Usuario o contrasena incorrectos");
        }

        if (response == null) {
            throw new IllegalStateException("No se pudo iniciar sesion");
        }

        return new TokenResponseDto(
                (String) response.get("access_token"),
                (String) response.get("refresh_token"),
                Long.valueOf(String.valueOf(response.get("expires_in"))),
                (String) response.get("token_type")
        );
    }

    private String getAdminAccessToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", adminClientId);
        form.add("client_secret", adminClientSecret);

        Map<String, Object> response = keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> Mono.error(
                        new IllegalStateException("El servicio de autenticacion no esta configurado correctamente")
                ))
                .bodyToMono(Map.class)
                .block();

        if (response == null || response.get("access_token") == null) {
            throw new IllegalStateException("No se pudo obtener autorizacion administrativa de Keycloak");
        }

        return (String) response.get("access_token");
    }

    private String createUserInKeycloak(
            String adminToken,
            String username,
            String email,
            String firstName,
            String lastName,
            String password
    ) {
        Map<String, Object> credentials = Map.of(
                "type", "password",
                "value", password,
                "temporary", false
        );

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("username", username);
        payload.put("email", email);
        payload.put("firstName", firstName);
        payload.put("lastName", lastName);
        payload.put("enabled", true);
        payload.put("emailVerified", false);
        payload.put("requiredActions", List.of("VERIFY_EMAIL"));
        payload.put("credentials", List.of(credentials));

        ResponseEntity<Void> response;

        try {
            response = keycloakWebClient.post()
                    .uri("/admin/realms/{realm}/users", realm)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.Conflict ex) {
            throw new IllegalStateException("Ya existe un usuario con ese username o correo");
        }

        if (response == null) {
            throw new IllegalStateException("No se pudo crear el usuario en Keycloak");
        }

        String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);

        if (location == null) {
            throw new IllegalStateException("No se pudo crear el usuario en Keycloak");
        }

        return location.substring(location.lastIndexOf('/') + 1);
    }

    @SuppressWarnings("unchecked")
    private void assignRealmRole(String adminToken, String userId, String roleName) {
        Map<String, Object> role = keycloakWebClient.get()
                .uri("/admin/realms/{realm}/roles/{roleName}", realm, roleName)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (role == null || role.get("id") == null) {
            throw new IllegalStateException("El rol " + roleName + " no existe en el realm de Keycloak");
        }

        List<Map<String, Object>> body = List.of(Map.of("id", role.get("id"), "name", roleName));

        keycloakWebClient.post()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", realm, userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void sendVerifyEmail(String adminToken, String userId) {
        try {
            keycloakWebClient.put()
                    .uri("/admin/realms/{realm}/users/{userId}/send-verify-email", realm, userId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException ex) {
            log.warn("No se pudo enviar el correo de verificacion al usuario {}: {}", userId, ex.getMessage());
        }
    }
}
