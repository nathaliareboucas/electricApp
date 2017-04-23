package br.com.electricapp.electricapp.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Created by Nathalia on 15/04/2017.
 */
public class Leitura {

    private Long id;
    private String ultimaLeitura;
    private String proximaLeitura;
    private BigDecimal valorUltimaLeitura;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUltimaLeitura() {
        return ultimaLeitura;
    }

    public void setUltimaLeitura(String ultimaLeitura) {
        this.ultimaLeitura = ultimaLeitura;
    }

    public String getProximaLeitura() {
        return proximaLeitura;
    }

    public void setProximaLeitura(String proximaLeitura) {
        this.proximaLeitura = proximaLeitura;
    }

    public BigDecimal getValorUltimaLeitura() {
        return valorUltimaLeitura;
    }

    public void setValorUltimaLeitura(BigDecimal valorUltimaLeitura) {
        this.valorUltimaLeitura = valorUltimaLeitura;
    }
}
