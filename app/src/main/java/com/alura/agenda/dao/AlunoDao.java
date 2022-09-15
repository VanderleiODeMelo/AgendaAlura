package com.alura.agenda.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.alura.agenda.model.Aluno;

import java.util.List;

@Dao
public interface AlunoDao {


    @Insert
    Long salvar(Aluno aluno);

    @Query("SELECT * FROM Aluno")
    List<Aluno> buscarTodosAlunos();

    @Update
    void editar(Aluno aluno);

    @Delete
    void remover(Aluno aluno);

    @Insert
    void inserir(Aluno aluno);
}
