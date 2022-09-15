package com.alura.agenda.ui.adapter.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alura.agenda.R;
import com.alura.agenda.dao.TelefoneDao;
import com.alura.agenda.database.AgendaDataBase;
import com.alura.agenda.model.Aluno;
import com.alura.agenda.model.Telefone;
import com.alura.agenda.task.BuscarPrimeiroTelefoneFutureTask;
import com.alura.agenda.util.DataUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ListaAlunosAdapter extends RecyclerView.Adapter<ListaAlunosAdapter.AlunoViewHolder> {

    private final List<Aluno> lista;
    private final Context context;
    private final List<Telefone> listaTelefone;
    private OnItemClickListener onItemClickListener;


    public ListaAlunosAdapter(List<Aluno> lista, Context context) {
        this.lista = lista;
        this.context = context;

        listaTelefone = new ArrayList<>();

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ListaAlunosAdapter.AlunoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View viewCriada = LayoutInflater.from(context).inflate(R.layout.item_aluno, parent, false);
        return new AlunoViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaAlunosAdapter.AlunoViewHolder holder, int position) {

        Aluno aluno = lista.get(position);
        holder.vincular(aluno);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void atualizarListaAluno(List<Aluno> lista) {
        this.lista.clear();
        this.lista.addAll(lista);
        notifyDataSetChanged();

    }

    public void removerAluno(int posicao) {


        lista.remove(posicao);
        notifyItemRemoved(posicao);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void inserirAluno(int posicao, Aluno aluno) {
        this.lista.add(posicao, aluno);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void inserirTelefone(int posicao, Telefone telefone) {

        this.listaTelefone.add(posicao, telefone);
        notifyDataSetChanged();
    }

    public class AlunoViewHolder extends RecyclerView.ViewHolder {

        private final TextView idCampoNome;
        private final TextView idCampoTelefone;
        private Aluno aluno;

        public AlunoViewHolder(@NonNull View itemView) {
            super(itemView);

            idCampoNome = itemView.findViewById(R.id.item_aluno_campo_nome);
            idCampoTelefone = itemView.findViewById(R.id.item_aluno_campo_telefone);

            itemView.setOnClickListener(view -> onItemClickListener.onItemClick(aluno, getAdapterPosition()));
        }

        @SuppressLint("SetTextI18n")
        public void vincular(Aluno aluno) {

            this.aluno = aluno;
            idCampoNome.setText(aluno.getNome() + " - " + DataUtil.formatarData(Calendar.getInstance()));
            TelefoneDao telefoneDao = AgendaDataBase.getInstance(context).telefoneDao();

            ExecutorService threadPool = Executors.newFixedThreadPool(2);
            BuscarPrimeiroTelefoneFutureTask task = new BuscarPrimeiroTelefoneFutureTask(telefoneDao, aluno.getId());
            Future<Telefone> future = threadPool.submit(task);

            try {
                Telefone primeiroTelefone = future.get();

                idCampoTelefone.setText(primeiroTelefone.getNumero());

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            threadPool.shutdown();
        }
    }

    public interface OnItemClickListener {

        void onItemClick(Aluno aluno, int posicao);

    }
}
