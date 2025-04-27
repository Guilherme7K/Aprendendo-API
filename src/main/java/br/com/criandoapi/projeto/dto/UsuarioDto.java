package br.com.criandoapi.projeto.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UsuarioDto {
    private String nome;
    private String email;
    private String senha;

    public UsuarioDto(String nome, String email, String senha){
        super();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }
}
