package com.senai.apivsconnect.controllers; // Declaração do pacote da classe

import com.senai.apivsconnect.dtos.UsuarioDto; // Importa a classe UsuarioDto para lidar com os dados do usuário
import com.senai.apivsconnect.models.UsuarioModel; // Importa a classe UsuarioModel que representa um modelo de usuário
import com.senai.apivsconnect.repositories.UsuarioRepository; // Importa a classe UsuarioRepository para acessar dados do usuário
import com.senai.apivsconnect.services.FileUploadServices; // Importa a classe FileUploadServices para lidar com o upload de arquivos
import jakarta.validation.Valid; // Importa a anotação @Valid para validação dos dados
import org.springframework.beans.BeanUtils; // Importa a classe BeanUtils para copiar propriedades entre objetos
import org.springframework.beans.factory.annotation.Autowired; // Importa a anotação @Autowired para injeção de dependências
import org.springframework.http.HttpStatus; // Importa a classe HttpStatus para lidar com códigos de status HTTP
import org.springframework.http.MediaType; // Importa a classe MediaType para definir o tipo de mídia da requisição
import org.springframework.http.ResponseEntity; // Importa a classe ResponseEntity para representar uma resposta HTTP
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Importa a classe BCryptPasswordEncoder para criptografar senhas
import org.springframework.stereotype.Repository; // Importa a anotação @Repository
import org.springframework.web.bind.annotation.*; // Importa as anotações para mapear requisições HTTP

import java.io.IOException; // Importa a classe IOException para lidar com exceções de entrada/saída
import java.util.List; // Importa a classe List para lidar com coleções de usuários
import java.util.Optional; // Importa a classe Optional para lidar com possíveis resultados
import java.util.UUID; // Importa a classe UUID para representar identificadores únicos

@RestController // Marca a classe como um controlador REST
@RequestMapping(value = "/usuarios", produces = {"application/json"})
// Define a rota base e o tipo de mídia da resposta
public class UsuarioController { // Declaração da classe UsuarioController

    @Autowired // Injeta o UsuarioRepository para acessar os dados dos usuários
    UsuarioRepository usuarioRepository;

    @Autowired // Injeta o FileUploadServices para realizar o upload de arquivos
    FileUploadServices fileUploadServices;

    @GetMapping // Mapeia a rota padrão (listar todos os usuários)
    public ResponseEntity<List<UsuarioModel>> listarUsuario() {
        // Retorna uma resposta com status 200 (OK) listando todos os usuários
        return ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.findAll());
    }

    @GetMapping("/{idUsuario}") // Mapeia a rota para exibir um usuário pelo ID
    public ResponseEntity<Object> exibirUsuario(@PathVariable(value = "idUsuario") UUID id) {
        Optional<UsuarioModel> usuarioBuscado = usuarioRepository.findById(id);

        if (usuarioBuscado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        return ResponseEntity.status(HttpStatus.OK).body(usuarioBuscado.get());
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    // Mapeia a rota para cadastrar um usuário com suporte a upload de arquivos
    public ResponseEntity<Object> cadastrarUsuario(@ModelAttribute @Valid UsuarioDto usuarioDto) {
        if (usuarioRepository.findByEmail(usuarioDto.email()) != null) {
            // Verifica se o email já está cadastrado e retorna uma resposta de erro
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Esse email já está cadastrado");
        }

        UsuarioModel usuario = new UsuarioModel();

        BeanUtils.copyProperties(usuarioDto, usuario); // Copia as propriedades do DTO para o modelo de usuário

        String urlImagem;

        try {
            urlImagem = fileUploadServices.FazerUpload(usuarioDto.imagem()); // Realiza o upload da imagem
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        usuario.setUrl_img(urlImagem); // Define a URL da imagem no modelo de usuário

        String senhaCriptografada = new BCryptPasswordEncoder().encode(usuarioDto.senha()); // Criptografa a senha

        usuario.setSenha(senhaCriptografada); // Define a senha criptografada no modelo de usuário

        // Retorna uma resposta com status 201 (Created) e o usuário cadastrado no corpo da resposta
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRepository.save(usuario));
    }

    @PutMapping(value = "/{idUsuario}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    // Mapeia a rota para editar um usuário com suporte a upload de arquivos
    public ResponseEntity<Object> editarUsuario(@PathVariable(value = "idUsuario") UUID id, @ModelAttribute @Valid UsuarioDto usuarioDto) {
        Optional<UsuarioModel> usuarioBuscado = usuarioRepository.findById(id);

        if (usuarioBuscado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        UsuarioModel usuario = usuarioBuscado.get();
        BeanUtils.copyProperties(usuarioDto, usuario); // Copia as propriedades do DTO para o modelo de usuário

        String urlImagem;

        try {
            urlImagem = fileUploadServices.FazerUpload(usuarioDto.imagem()); // Realiza o upload da imagem
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        usuario.setUrl_img(urlImagem); // Define a URL da imagem no modelo de usuário

        String senhaCriptografada = new BCryptPasswordEncoder().encode(usuarioDto.senha()); // Criptografa a senha

        usuario.setSenha(senhaCriptografada); // Define a senha criptografada no modelo de usuário

        // Retorna uma resposta com status 201 (Created) e o usuário editado no corpo da resposta
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRepository.save(usuario));
    }

    @DeleteMapping("/{idUsuario}") // Mapeia a rota para deletar um usuário pelo ID
    public ResponseEntity<Object> deletarUsuario(@PathVariable(value = "idUsuario") UUID id) {
        Optional<UsuarioModel> usuarioBuscado = usuarioRepository.findById(id);

        if (usuarioBuscado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        usuarioRepository.delete(usuarioBuscado.get());

        // Retorna uma resposta com status 204 (No Content) indicando que o usuário foi deletado
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuário deletado!");
    }
}
