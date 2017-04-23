package br.com.electricapp.electricapp.model;

/**
 * Created by Nathalia on 20/04/2017.
 */
public class Usuario {

    private Long id;
    private String endpoint;
    private String senha;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
