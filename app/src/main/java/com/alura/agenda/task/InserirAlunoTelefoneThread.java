package com.alura.agenda.task;

import androidx.recyclerview.widget.RecyclerView;

import com.alura.agenda.dao.AlunoDao;
import com.alura.agenda.dao.TelefoneDao;
import com.alura.agenda.model.Aluno;
import com.alura.agenda.model.Telefone;
import com.alura.agenda.ui.adapter.recyclerview.ListaAlunosAdapter;

public class InserirAlunoTelefoneThread implements Runnable {


    private final AlunoDao alunoDao;
    private final TelefoneDao telefoneDao;
    private final Aluno alunoRemover;
    private final Telefone telefoneFixoRemover;
    private final Telefone telefoneCelularRemover;
    private final ListaAlunosAdapter adapter;
    private final int posicaoTelefoneFixo;
    private final int posicaoTelefoneCelular;
    private final RecyclerView idRecyclerView;
    private final int posicaoRemover;

    public InserirAlunoTelefoneThread(AlunoDao alunoDao,
                                      TelefoneDao telefoneDao,
                                      Aluno alunoRemover,
                                      Telefone telefoneFixoRemover,
                                      Telefone telefoneCelularRemover,
                                      ListaAlunosAdapter adapter,
                                      int posicaoTelefoneFixo,
                                      int posicaoTelefoneCelular,
                                      RecyclerView idRecyclerView,
                                      int posicaoRemover) {


        this.alunoDao = alunoDao;
        this.telefoneDao = telefoneDao;
        this.alunoRemover = alunoRemover;
        this.telefoneFixoRemover = telefoneFixoRemover;
        this.telefoneCelularRemover = telefoneCelularRemover;
        this.adapter = adapter;
        this.posicaoTelefoneFixo = posicaoTelefoneFixo;
        this.posicaoTelefoneCelular = posicaoTelefoneCelular;
        this.idRecyclerView = idRecyclerView;
        this.posicaoRemover = posicaoRemover;
        new Thread(this).start();
    }

    @Override
    public void run() {

        alunoDao.inserir(alunoRemover);
        telefoneDao.inserir(telefoneFixoRemover);
        telefoneDao.inserir(telefoneCelularRemover);

        idRecyclerView.post(() -> {

            adapter.inserirAluno(posicaoRemover, alunoRemover);
            adapter.inserirTelefone(posicaoTelefoneFixo, telefoneFixoRemover);
            adapter.inserirTelefone(posicaoTelefoneCelular, telefoneCelularRemover);
        });


    }

}
