package com.alura.agenda.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(indices = {@Index("alunoId")}, foreignKeys = @ForeignKey(
        entity = Aluno.class,
        parentColumns = "id",
        childColumns = "alunoId",
        onDelete = CASCADE,
        onUpdate = CASCADE))
public class Telefone implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private TipoTelefone tipo;
    private String numero;
    private int alunoId;

    public Telefone() {
    }

    @Ignore
    public Telefone(TipoTelefone tipo, String numero) {
        this.tipo = tipo;
        this.numero = numero;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoTelefone getTipo() {
        return tipo;
    }

    public void setTipo(TipoTelefone tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(int alunoId) {
        this.alunoId = alunoId;
    }

}

