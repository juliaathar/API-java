package com.senai.apivsconnect.models;

public enum TipoModel {
    ADMIN("admin"),
    DESENVOLVEDOR("dev"),
    CLIENTE("cliente");

    private String tipo;

    //construtor da classe
    TipoModel(String tipo){
        this.tipo = tipo;
    }

    public String getRole(){
        return tipo;
    }
}
