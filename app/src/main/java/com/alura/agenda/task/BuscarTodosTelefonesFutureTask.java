package com.alura.agenda.task;

import com.alura.agenda.dao.TelefoneDao;
import com.alura.agenda.model.Telefone;

import java.util.List;
import java.util.concurrent.Callable;

public class BuscarTodosTelefonesFutureTask implements Callable<List<Telefone>> {

    private final TelefoneDao telefoneDao;
    private final int alunoId;

    public BuscarTodosTelefonesFutureTask(TelefoneDao telefoneDao, int alunoId) {
        this.telefoneDao = telefoneDao;
        this.alunoId = alunoId;
    }

    @Override
    public List<Telefone> call() {

        return telefoneDao.buscarTodosTelefones(alunoId);
    }
}
