package com.example.modelo;

public class Usuario {

    private String nome;
    private int colorNumber;

    public Usuario(String nome, int colorNumber) {
        this.nome = nome;
        this.colorNumber = colorNumber;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getColorNumber() {
        return colorNumber;
    }

    public void setColorNumber(int colorNumber) {
        this.colorNumber = colorNumber;
    }

}
