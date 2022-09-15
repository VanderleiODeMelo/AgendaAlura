package com.alura.agenda.task;

import com.alura.agenda.dao.AlunoDao;
import com.alura.agenda.model.Aluno;

import java.util.List;
import java.util.concurrent.Callable;

public class BuscarTodosAlunosFutureTask implements Callable<List<Aluno>> {

    private final AlunoDao alunoDao;

    public BuscarTodosAlunosFutureTask(AlunoDao alunoDao) {
        this.alunoDao = alunoDao;
    }

    @Override
    public List<Aluno> call() {

        return alunoDao.buscarTodosAlunos();
    }
}
