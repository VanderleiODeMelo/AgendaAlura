package com.alura.agenda.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;

@Entity
public class Aluno implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nome;
    private String email;
    private Calendar momentoCadastro = Calendar.getInstance();

    public Aluno() {
    }

    @Ignore
    public Aluno(String nome, String email) {
        this.nome = nome;
        this.email = email;

    }

    public Calendar getMomentoCadastro() {
        return momentoCadastro;
    }

    public void setMomentoCadastro(Calendar momentoCadastro) {
        this.momentoCadastro = momentoCadastro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean temIdValido() {
        return id > 0;
    }


}
