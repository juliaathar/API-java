package com.senai.apivsconnect.controllers;

import com.senai.apivsconnect.dtos.UsuarioDto;
import com.senai.apivsconnect.models.UsuarioModel;
import com.senai.apivsconnect.repositories.UsuarioRepository;
import com.senai.apivsconnect.services.FileUploadServices;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/usuarios", produces = {"application/json"})
public class UsuarioController {
    @Autowired //injeção de dependencia
    UsuarioRepository usuarioRepository;

    @Autowired
    FileUploadServices fileUploadServices;

    @GetMapping
    public ResponseEntity<List<UsuarioModel>> listarUsuario() {
        //retorna uma resposta de requisicao onde o status code for 200(OK) listando todos os usuarios
        return ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.findAll());
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<Object> exibirUsuario(@PathVariable(value = "idUsuario") UUID id) {
        Optional<UsuarioModel> usuarioBuscado = usuarioRepository.findById(id);

        if (usuarioBuscado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        return ResponseEntity.status(HttpStatus.OK).body(usuarioBuscado.get());

    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> cadastrarUsuario(@ModelAttribute @Valid UsuarioDto usuarioDto) {
        if (usuarioRepository.findByEmail(usuarioDto.email()) != null) {
            //não pode cadastrar
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Esse email já está cadastrado");
        }

        UsuarioModel usuario = new UsuarioModel();

        BeanUtils.copyProperties(usuarioDto, usuario);

        String urlImagem;

        try {
            urlImagem = fileUploadServices.FazerUpload(usuarioDto.imagem());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        usuario.setUrl_img(urlImagem);

        String senhaCriptografada = new BCryptPasswordEncoder().encode(usuarioDto.senha());

        usuario.setSenha(senhaCriptografada);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRepository.save(usuario));
    }

    @PutMapping(value = "/{idUsuario}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> editarUsuario(@PathVariable(value = "idUsuario") UUID id, @ModelAttribute @Valid UsuarioDto usuarioDto) {
        Optional<UsuarioModel> usuarioBuscado = usuarioRepository.findById(id);

        if (usuarioBuscado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }


        UsuarioModel usuario = usuarioBuscado.get();
        BeanUtils.copyProperties(usuarioDto, usuario);

        String urlImagem;

        try {
            urlImagem = fileUploadServices.FazerUpload(usuarioDto.imagem());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        usuario.setUrl_img(urlImagem);

        String senhaCriptografada = new BCryptPasswordEncoder().encode(usuarioDto.senha());

        usuario.setSenha(senhaCriptografada);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRepository.save(usuario));
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Object> deletarUsuario(@PathVariable(value = "idUsuario") UUID id) {
        Optional<UsuarioModel> usuarioBuscado = usuarioRepository.findById(id);

        if (usuarioBuscado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        usuarioRepository.delete(usuarioBuscado.get());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuário deletado!");
    }

}
