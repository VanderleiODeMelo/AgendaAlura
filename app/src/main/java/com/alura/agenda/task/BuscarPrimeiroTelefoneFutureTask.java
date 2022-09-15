package com.alura.agenda.task;

import com.alura.agenda.dao.TelefoneDao;
import com.alura.agenda.model.Telefone;

import java.util.concurrent.Callable;

public class BuscarPrimeiroTelefoneFutureTask implements Callable<Telefone> {

    private final TelefoneDao telefoneDao;
    private final int alunoId;

    public BuscarPrimeiroTelefoneFutureTask(TelefoneDao telefoneDao, int alunoId) {
        this.telefoneDao = telefoneDao;
        this.alunoId = alunoId;
    }


    @Override
    public Telefone call() {

        return telefoneDao.buscarPrimeiroTelefoneAluno(alunoId);
    }
}
