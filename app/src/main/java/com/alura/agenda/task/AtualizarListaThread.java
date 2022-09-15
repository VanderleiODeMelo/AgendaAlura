package com.alura.agenda.task;

import androidx.recyclerview.widget.RecyclerView;

import com.alura.agenda.model.Aluno;
import com.alura.agenda.ui.adapter.recyclerview.ListaAlunosAdapter;

import java.util.List;

public class AtualizarListaThread implements Runnable {
    private final RecyclerView idRecyclerView;
    private final ListaAlunosAdapter adapter;
    private final List<Aluno> list;

    public AtualizarListaThread(RecyclerView idRecyclerView, ListaAlunosAdapter adapter, List<Aluno> list) {
        this.idRecyclerView = idRecyclerView;
        this.adapter = adapter;
        this.list = list;
        new Thread(this).start();
    }

    @Override
    public void run() {

        idRecyclerView.post(() -> adapter.atualizarListaAluno(list));
    }
}
