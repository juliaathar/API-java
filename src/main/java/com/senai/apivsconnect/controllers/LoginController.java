package com.senai.apivsconnect.controllers; // Declaração do pacote da classe

import com.senai.apivsconnect.dtos.LoginDto; // Importa a classe LoginDto para lidar com os dados de login
import com.senai.apivsconnect.dtos.TokenDto; // Importa a classe TokenDto para representar o token de autenticação
import com.senai.apivsconnect.models.UsuarioModel; // Importa a classe UsuarioModel que representa um modelo de usuário
import com.senai.apivsconnect.services.TokenService; // Importa a classe TokenService para gerar tokens de autenticação
import jakarta.validation.Valid; // Importa a anotação @Valid para validação dos dados
import org.springframework.beans.factory.annotation.Autowired; // Importa a anotação @Autowired para injeção de dependências
import org.springframework.http.HttpStatus; // Importa a classe HttpStatus para lidar com códigos de status HTTP
import org.springframework.http.ResponseEntity; // Importa a classe ResponseEntity para representar uma resposta HTTP
import org.springframework.security.authentication.AuthenticationManager; // Importa a classe AuthenticationManager para autenticar usuários
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Importa a classe UsernamePasswordAuthenticationToken para representar um token de autenticação com nome de usuário e senha
import org.springframework.web.bind.annotation.PostMapping; // Importa a anotação @PostMapping para mapear uma requisição HTTP POST
import org.springframework.web.bind.annotation.RequestBody; // Importa a anotação @RequestBody para indicar que os dados devem ser lidos do corpo da requisição

public class LoginController { // Declaração da classe LoginController

    @Autowired // Injeta o AuthenticationManager para autenticação de usuários
    private AuthenticationManager authenticationManager;

    @Autowired // Injeta o TokenService para geração de tokens de autenticação
    private TokenService tokenService;

    @PostMapping("/login") // Mapeia o método para lidar com requisições POST na rota "/login"
    public ResponseEntity<Object> login(@RequestBody @Valid LoginDto dados) { // Declaração do método login com anotações

        // Cria um objeto UsernamePasswordAuthenticationToken com email e senha dos dados da requisição
        var usernamePassword = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        // Autentica o usuário usando o AuthenticationManager e o token criado
        var auth = authenticationManager.authenticate(usernamePassword);

        // Gera um token de autenticação com base no usuário autenticado
        var token = tokenService.gerarToken((UsuarioModel) auth.getPrincipal());

        // Retorna uma resposta HTTP com status 200 (OK) e o token no corpo da resposta, encapsulado em um objeto TokenDto
        return ResponseEntity.status(HttpStatus.OK).body(new TokenDto(token));
    }
}
