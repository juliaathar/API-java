package com.senai.apivsconnect.services; // Declaração do pacote da classe

import com.auth0.jwt.JWT; // Importa a classe JWT do Auth0 para lidar com tokens JWT
import com.auth0.jwt.algorithms.Algorithm; // Importa a classe Algorithm do Auth0 para escolher o algoritmo de assinatura JWT
import com.auth0.jwt.exceptions.JWTCreationException; // Importa a exceção JWTCreationException do Auth0 para lidar com erros na criação de tokens JWT
import com.senai.apivsconnect.models.UsuarioModel; // Importa a classe UsuarioModel para acessar informações do usuário
import org.springframework.beans.factory.annotation.Value; // Importa a anotação @Value do Spring para acessar valores das propriedades
import org.springframework.stereotype.Service; // Importa a anotação @Service do Spring para marcar a classe como um serviço

import java.time.Instant; // Importa a classe Instant para lidar com data/hora
import java.time.LocalDateTime; // Importa a classe LocalDateTime para lidar com data/hora
import java.time.ZoneOffset; // Importa a classe ZoneOffset para definir o fuso horário

@Service // Marca a classe como um serviço
public class TokenService {
    @Value("${api.security.token.secret}") // Injeta o valor da propriedade "api.security.token.secret"
    private String secret;

    public String gerarToken(UsuarioModel usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret); // Cria um algoritmo de assinatura com a chave secreta

            String token = JWT.create()
                    .withIssuer("api-vsconnect") // Define o emissor do token
                    .withSubject(usuario.getEmail()) // Define o assunto do token (neste caso, o email do usuário)
                    .withClaim("nomeUsuario", usuario.getNome()) // Define um claim personalizado (nomeUsuario)
                    .withExpiresAt(gerarValidadeToken()) // Define a data de expiração do token
                    .sign(algoritmo); // Assina o token com o algoritmo escolhido

            return token; // Retorna o token gerado
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro na criação do token: ", exception); // Lança uma exceção em caso de erro na criação do token
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret); // Cria um algoritmo de verificação com a chave secreta

            return JWT.require(algoritmo)
                    .withIssuer("api-vsconnect") // Define o emissor esperado do token
                    .build()
                    .verify(token)
                    .getSubject(); // Verifica o token e retorna o assunto (neste caso, o email do usuário)
        } catch () {
        }
    }

    public Instant gerarValidadeToken() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")); // Gera a data de validade do token (2 horas a partir do momento atual, com fuso horário -03:00)
    }
}
