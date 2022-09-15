package com.alura.agenda.task;

import androidx.recyclerview.widget.RecyclerView;

import com.alura.agenda.dao.AlunoDao;
import com.alura.agenda.model.Aluno;
import com.alura.agenda.ui.adapter.recyclerview.ListaAlunosAdapter;

public class RemoverAlunoThread implements Runnable {
    private final AlunoDao alunoDao;
    private final Aluno alunoRemover;
    private final ListaAlunosAdapter adapter;
    private final RecyclerView idRecyclerView;
    private final int posicaoRemover;

    public RemoverAlunoThread(AlunoDao alunoDao,
                              Aluno alunoRemover,
                              ListaAlunosAdapter adapter,
                              RecyclerView idRecyclerView,
                              int posicaoRemover) {
        this.alunoDao = alunoDao;
        this.alunoRemover = alunoRemover;
        this.adapter = adapter;
        this.idRecyclerView = idRecyclerView;
        this.posicaoRemover = posicaoRemover;
        new Thread(this).start();
    }

    @Override
    public void run() {

        alunoDao.remover(alunoRemover);
        idRecyclerView.post(() -> adapter.removerAluno(posicaoRemover));

    }
}
