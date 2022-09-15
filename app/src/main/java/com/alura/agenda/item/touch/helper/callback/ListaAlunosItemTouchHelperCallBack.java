package com.alura.agenda.item.touch.helper.callback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alura.agenda.R;
import com.alura.agenda.dao.AlunoDao;
import com.alura.agenda.dao.TelefoneDao;
import com.alura.agenda.model.Aluno;
import com.alura.agenda.model.Telefone;
import com.alura.agenda.model.TipoTelefone;
import com.alura.agenda.task.BuscarTodosTelefonesFutureTask;
import com.alura.agenda.task.InserirAlunoTelefoneThread;
import com.alura.agenda.task.RemoverAlunoThread;
import com.alura.agenda.ui.adapter.recyclerview.ListaAlunosAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ListaAlunosItemTouchHelperCallBack extends ItemTouchHelper.Callback {

    private final AlunoDao alunoDao;
    private final List<Aluno> listaAlunos;
    private final ListaAlunosAdapter adapter;
    private final TelefoneDao telefoneDao;
    private final ColorDrawable swipeBackground = new ColorDrawable(Color.parseColor("#DC143C"));
    private final Context context;
    private final RecyclerView idRecyclerView;
    private Telefone telefoneFixoRemover;
    private Telefone telefoneCelularRemover;
    private int posicaoTelefoneFixo;
    private int posicaoTelefoneCelular;
    private List<Telefone> telefones;

    public ListaAlunosItemTouchHelperCallBack(AlunoDao alunoDao,
                                              List<Aluno> listaAlunos,
                                              ListaAlunosAdapter adapter,
                                              TelefoneDao telefoneDao,
                                              Context context,
                                              RecyclerView idRecyclerView) {
        this.alunoDao = alunoDao;
        this.listaAlunos = listaAlunos;
        this.adapter = adapter;
        this.telefoneDao = telefoneDao;
        this.context = context;
        this.idRecyclerView = idRecyclerView;
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        int marcacoesDeslizes = makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);

        return makeMovementFlags(0, marcacoesDeslizes);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {

        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        int posicaoRemover = viewHolder.getAdapterPosition();
        Aluno alunoRemover = listaAlunos.get(posicaoRemover);

        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        BuscarTodosTelefonesFutureTask task = new BuscarTodosTelefonesFutureTask(telefoneDao, alunoRemover.getId());
        Future<List<Telefone>> future = threadPool.submit(task);

        try {
            telefones = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        for (Telefone telefone : telefones) {

            if (telefone.getTipo() == TipoTelefone.FIXO) {


                posicaoTelefoneFixo = telefones.indexOf(telefone);
                telefoneFixoRemover = telefone;

            } else {

                posicaoTelefoneCelular = telefones.indexOf(telefone);
                telefoneCelularRemover = telefone;


            }
        }
        new RemoverAlunoThread(alunoDao, alunoRemover, adapter, idRecyclerView, posicaoRemover);

        threadPool.shutdown();
        Snackbar.make(viewHolder.itemView, R.string.apagar, Snackbar.LENGTH_SHORT).setAction(R.string.desfazer_alteração, new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new InserirAlunoTelefoneThread(alunoDao, telefoneDao, alunoRemover, telefoneFixoRemover, telefoneCelularRemover, adapter, posicaoTelefoneFixo, posicaoTelefoneCelular, idRecyclerView, posicaoRemover);
            }
        }).show();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX,
                            float dY,
                            int actionState,
                            boolean isCurrentlyActive) {


        View itemView = pegarItemView(viewHolder);
        Drawable iconeLixeiraDeletar = capturarIconeLixeira();
        int iconeMargin = calcularAlturaDoDrawable(itemView, iconeLixeiraDeletar);

        //dx -> É a quantidade de deslocamento no sentido (horizontal) feita pelo o usuário
        //dy -> É a quantidade de deslocamento no sentido (vertical) feita pelo o usuário
        if (dX > 0) {


            desenharRetanguloSentidoPositivoEixoX((int) dX, itemView);
            centralizarIconeLixeira(itemView, iconeLixeiraDeletar, iconeMargin);


        } else {

            desenharRetanguloSentidoNegativoEixoX((int) dX, itemView);
            centralizarIcone(itemView, iconeLixeiraDeletar, iconeMargin);


        }
        desenharRetangulo(c, swipeBackground);
        salvarDesenhoRetangulo(c);

        if (dX > 0) {
            definirAreaMaximaDesenhoRetanguloAEsquerda(c, (int) dX, itemView);
        } else {

            definirAreaMaximaDesenhoRetanguloADireita(c, (int) dX, itemView);
        }


        desenharRetangulo(c, Objects.requireNonNull(iconeLixeiraDeletar));
        restaurar(c);


        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    }

    private void restaurar(@NonNull Canvas c) {
        //essa chamada equilibra uma chamada anterior para save()
        //e é chamada para remover todos os modificadores no estado da matriz/clipe desde
        //a última chamada de salvamento
        c.restore();
    }

    private void definirAreaMaximaDesenhoRetanguloADireita(@NonNull Canvas c, int dX, View itemView) {
        c.clipRect(itemView.getRight() +
                        dX,
                itemView.getTop(),
                itemView.getRight(),
                itemView.getBottom());
    }

    private void definirAreaMaximaDesenhoRetanguloAEsquerda(@NonNull Canvas c, int dX, View itemView) {

        //aqui permite uma visão pai para recortar a tela
        // para definir claramente a área máxima de desenho dos seus filhos
        c.clipRect(itemView.getLeft()
                , itemView.getTop()
                , dX,
                itemView.getBottom());
    }

    private void salvarDesenhoRetangulo(@NonNull Canvas c) {
        //agora vamos salvar o desenho
        c.save();
    }

    private void desenharRetangulo(@NonNull Canvas c, Drawable swipeBackground) {
        //desenhar o icone da lixeira deletar
        //(c) a tela que o recyclerview está desenhando seu filhos
        swipeBackground.draw(c);
    }

    private void centralizarIcone(View itemView, Drawable iconeLixeiraDeletar, int iconeMargin) {
        //icone  deletar
        //colocamos o iconeMargin para centralizar o icone da lixeira
        Objects.requireNonNull(iconeLixeiraDeletar).setBounds(itemView.getRight()
                        - iconeMargin - iconeLixeiraDeletar.getIntrinsicWidth(),
                itemView.getTop() + iconeMargin,
                itemView.getRight() - iconeMargin,
                itemView.getBottom() - iconeMargin);
    }

    private void desenharRetanguloSentidoNegativoEixoX(int dX, View itemView) {
        //aqui vamos agora no sentido negativo no eixo x / y
        swipeBackground.setBounds(
                itemView.getRight() +
                        dX,
                itemView.getTop(),
                itemView.getRight(),
                itemView.getBottom());
    }

    private void centralizarIconeLixeira(View itemView, Drawable iconeLixeiraDeletar, int iconeMargin) {
        //icone  deletar
        //colocamos o iconeMargin para centralizar o icone da lixeira
        Objects.requireNonNull(iconeLixeiraDeletar).setBounds(itemView.getLeft() +
                        iconeMargin,
                itemView.getTop() +
                        iconeMargin,
                itemView.getLeft() + iconeMargin +
                        iconeLixeiraDeletar.getIntrinsicWidth(),
                itemView.getBottom() - iconeMargin);
    }

    private void desenharRetanguloSentidoPositivoEixoX(int dX, View itemView) {
        //setBounds - .Vai especificar um RETANGULO delimitador para o drawable
        // Aqui o drawable vai desenhar quando seu método draw() for chamado.
        swipeBackground.setBounds(
                itemView.getLeft(),
                itemView.getTop(),
                dX,
                itemView.getBottom());
    }

    private int calcularAlturaDoDrawable(View itemView, Drawable iconeLixeiraDeletar) {
        //getHeight() -  estou pegando a altura do itemview que é um viewholder
        //getIntrinsicHeight() - altura no qual o drawable gostaria de ser dispostos
        //dividindo por 2 vai dar no (centro)
        return (itemView.getHeight()
                - Objects.requireNonNull(iconeLixeiraDeletar).getIntrinsicHeight()) / 2;

    }

    @Nullable
    private Drawable capturarIconeLixeira() {
        //pegar o icone da lixeira colocar dentro da variável drawable
        return ContextCompat.getDrawable(context,
                R.drawable.ic_lixeira_remover);

    }

    @NonNull
    private View pegarItemView(@NonNull RecyclerView.ViewHolder viewHolder) {

        //pegando item da view
        return viewHolder.itemView;

    }
}

