package com.alura.agenda.ui.activity;

import static com.alura.agenda.constantes.ConstantesActivitys.CHAVE_ALUNO;
import static com.alura.agenda.constantes.ConstantesActivitys.CHAVE_POSICAO;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alura.agenda.R;
import com.alura.agenda.dao.AlunoDao;
import com.alura.agenda.dao.TelefoneDao;
import com.alura.agenda.database.AgendaDataBase;
import com.alura.agenda.item.touch.helper.callback.ListaAlunosItemTouchHelperCallBack;
import com.alura.agenda.model.Aluno;
import com.alura.agenda.task.AtualizarListaThread;
import com.alura.agenda.task.BuscarTodosAlunosFutureTask;
import com.alura.agenda.ui.adapter.recyclerview.ListaAlunosAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ListaAlunosActivity extends AppCompatActivity {

    public static final String TITULO_APPBAR = "Lista";
    private ListaAlunosAdapter adapter;
    private TelefoneDao telefoneDao;
    private List<Aluno> alunoList;
    private RecyclerView idRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);
        setTitle(TITULO_APPBAR);
        telefoneDao = AgendaDataBase.getInstance(this).telefoneDao();
        configurarFab();
        configurarRecyclerView();


    }

    private void configurarRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        idRecyclerView = findViewById(R.id.lista_alunos_recyclerview);
        idRecyclerView.setHasFixedSize(true);
        idRecyclerView.setLayoutManager(linearLayoutManager);
        configurarAdapter(idRecyclerView);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void configurarAdapter(RecyclerView idRecyclerView) {

        AlunoDao alunoDao = AgendaDataBase.getInstance(this).alunoDao();
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        BuscarTodosAlunosFutureTask task = new BuscarTodosAlunosFutureTask(alunoDao);
        Future<List<Aluno>> future = threadPool.submit(task);

        try {
            alunoList = future.get();
            new Thread(() -> {

                adapter = new ListaAlunosAdapter(alunoList, ListaAlunosActivity.this);
                adapter.notifyDataSetChanged();
                idRecyclerView.post(() -> {
                    idRecyclerView.setAdapter(adapter);
                    configurarClickPorItemListener();
                    configurarItemTouchHelperCallBack(idRecyclerView, alunoDao, alunoList);
                });
            }).start();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
    }

    private void configurarItemTouchHelperCallBack(RecyclerView idRecyclerView, AlunoDao alunoDao, List<Aluno> listaAlunos) {

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ListaAlunosItemTouchHelperCallBack(alunoDao,
                listaAlunos, adapter, telefoneDao, this, idRecyclerView));
        itemTouchHelper.attachToRecyclerView(idRecyclerView);
    }

    private void configurarClickPorItemListener() {

        adapter.setOnItemClickListener((aluno, posicao) -> {

            Intent vaiParaFormularioCadastroAlunoactivityComAluno = new Intent(ListaAlunosActivity.this, FormularioCadastroAlunosActivity.class);

            vaiParaFormularioCadastroAlunoactivityComAluno.putExtra(CHAVE_ALUNO, aluno);
            vaiParaFormularioCadastroAlunoactivityComAluno.putExtra(CHAVE_POSICAO, posicao);
            startActivity(new Intent(vaiParaFormularioCadastroAlunoactivityComAluno));
        });
    }

    private void configurarFab() {
        ExtendedFloatingActionButton idBotaoAdicionarAlunoFab = findViewById(R.id.lista_alunos_adicionar_aluno_fab);
        idBotaoAdicionarAlunoFab.setOnClickListener(view -> {

            Intent vaiParaFormularioCadastroAlunosActivity = new Intent(ListaAlunosActivity.this,
                    FormularioCadastroAlunosActivity.class);
            startActivity(vaiParaFormularioCadastroAlunosActivity);

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        AlunoDao alunoDao = AgendaDataBase.getInstance(this).alunoDao();
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        BuscarTodosAlunosFutureTask task = new BuscarTodosAlunosFutureTask(alunoDao);
        Future<List<Aluno>> future = threadPool.submit(task);

        try {
            List<Aluno> list = future.get();

            new AtualizarListaThread(idRecyclerView, adapter, list);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        idRecyclerView.setAdapter(adapter);
        threadPool.shutdown();
    }
}