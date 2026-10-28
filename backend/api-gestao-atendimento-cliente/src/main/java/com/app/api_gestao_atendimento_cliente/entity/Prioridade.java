package com.app.api_gestao_atendimento_cliente.entity;

public enum Prioridade {

    ALTA(1),
    MEDIA(2),
    BAIXA(3);

    private final int nivel;

    Prioridade(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }

}
