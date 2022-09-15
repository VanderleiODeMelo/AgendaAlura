package com.alura.agenda.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.alura.agenda.model.Telefone;

import java.util.List;

@Dao
public interface TelefoneDao {


    @Query("SELECT * FROM Telefone WHERE alunoId =:alunoId LIMIT 1")
    Telefone buscarPrimeiroTelefoneAluno(int alunoId);

    @Query("SELECT * FROM Telefone WHERE alunoId =:alunoId")
    List<Telefone> buscarTodosTelefones(int alunoId);

    @Insert
    void salvar(Telefone... telefones);

    @Insert(onConflict = REPLACE)
    void editar(Telefone... telefones);


    @Insert
    void inserir(Telefone telefone);

}
