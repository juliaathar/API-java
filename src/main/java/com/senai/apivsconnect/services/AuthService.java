package com.senai.apivsconnect.services; // Declaração do pacote da classe

import com.senai.apivsconnect.repositories.UsuarioRepository; // Importa a classe UsuarioRepository para acessar os dados do usuário
import org.springframework.beans.factory.annotation.Autowired; // Importa a anotação @Autowired para injeção de dependências
import org.springframework.security.core.userdetails.UserDetails; // Importa a interface UserDetails do Spring Security
import org.springframework.security.core.userdetails.UserDetailsService; // Importa a interface UserDetailsService do Spring Security
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Importa a exceção UsernameNotFoundException do Spring Security
import org.springframework.stereotype.Service; // Importa a anotação @Service para marcar a classe como um serviço

@Service // Marca a classe como um serviço
public class AuthService implements UserDetailsService { // Declaração da classe AuthService que implementa UserDetailsService

    @Autowired // Injeta o UsuarioRepository para acessar os dados dos usuários
    UsuarioRepository usuarioRepository;

    @Override // Sobrescreve o método da interface UserDetailsService
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByEmail(username); // Busca o usuário pelo email

        if (usuario == null){
            throw new UsernameNotFoundException("Usuário não encontrado"); // Lança uma exceção se o usuário não for encontrado
        }

        // Retorna um objeto UserDetails (neste caso, retorna nulo, mas normalmente você retornaria os detalhes do usuário)
        return null;
    }
}
