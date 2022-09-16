package com.alura.agenda.ui.activity;

import static com.alura.agenda.constantes.ConstantesActivitys.CHAVE_ALUNO;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alura.agenda.R;
import com.alura.agenda.dao.AlunoDao;
import com.alura.agenda.dao.TelefoneDao;
import com.alura.agenda.database.AgendaDataBase;
import com.alura.agenda.model.Aluno;
import com.alura.agenda.model.Telefone;
import com.alura.agenda.model.TipoTelefone;
import com.alura.agenda.task.BuscarTodosTelefonesFutureTask;
import com.alura.agenda.task.EditarAlunoETelefonesThread;
import com.alura.agenda.task.SalvarAlunoThread;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FormularioCadastroAlunosActivity extends AppCompatActivity {

    public static final String TITULO_APPBAR_SALVAR = "Formulário cadastro aluno";
    public static final String TITULO_APPBAR_EDITAR = "Formulário edita aluno";

    private EditText idCampoNome;
    private EditText idCampoEmail;
    private EditText idCampoTelefoneFixo;
    private EditText idCampoTelefoneCelular;
    private Aluno aluno;
    private List<Telefone> listaTelefones;
    private TelefoneDao telefoneDao;
    private AlunoDao alunoDao;
    private Telefone telefoneFixo;
    private Telefone telefoneCelular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_cadastro_alunos);

        alunoDao = AgendaDataBase.getInstance(FormularioCadastroAlunosActivity.this).alunoDao();
        telefoneDao = AgendaDataBase.getInstance(this).telefoneDao();

        inicializarCamposIds();
        configurarBotaoVoltar();
        preencherInformacoes();
    }

    private void preencherInformacoes() {
        Intent dadosAluno = getIntent();
        if (dadosAluno.hasExtra(CHAVE_ALUNO)) {

            aluno = (Aluno) dadosAluno.getSerializableExtra(CHAVE_ALUNO);
            if (aluno != null) {
                setTitle(TITULO_APPBAR_EDITAR);

                idCampoNome.setText(aluno.getNome());
                idCampoEmail.setText(aluno.getEmail());
                preencherCamposTelefones(aluno);
            }
        } else {
            setTitle(TITULO_APPBAR_SALVAR);

            aluno = new Aluno();
        }
    }

    private void preencherCamposTelefones(Aluno aluno) {

        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        BuscarTodosTelefonesFutureTask task = new BuscarTodosTelefonesFutureTask(telefoneDao, aluno.getId());
        Future<List<Telefone>> future = threadPool.submit(task);

        try {
            listaTelefones = future.get();
            for (Telefone telefone : listaTelefones) {

                if (telefone != null) {

                    if (telefone.getTipo() == TipoTelefone.FIXO) {

                        idCampoTelefoneFixo.setText(telefone.getNumero());

                    } else {

                        idCampoTelefoneCelular.setText(telefone.getNumero());
                    }
                }

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();


    }

    private void configurarBotaoVoltar() {

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.formulario_cadastro_alunos_menus_opcoes, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        criarTelefone();

        int itemIdMenuSalvar = item.getItemId();

        finalizarFormulario(itemIdMenuSalvar);

        return super.onOptionsItemSelected(item);
    }

    private void finalizarFormulario(int itemIdMenuSalvar) {

        if (ehMenuSalvar(itemIdMenuSalvar)) {

            criarAluno();

            if (temAluno()) {

                editarAluno();

            } else {

                salvarAluno();

            }
            finish();
        }
    }

    private void salvarAluno() {
        new SalvarAlunoThread(aluno, alunoDao, telefoneFixo, telefoneCelular, telefoneDao);
    }

    private void editarAluno() {

        new EditarAlunoETelefonesThread(aluno, telefoneDao, alunoDao, telefoneCelular, listaTelefones, telefoneFixo);
    }

    private void criarTelefone() {

        String numeroFixo = idCampoTelefoneFixo.getText().toString();
        String numeroCelular = idCampoTelefoneCelular.getText().toString();

        telefoneFixo = new Telefone(TipoTelefone.FIXO, numeroFixo);
        telefoneCelular = new Telefone(TipoTelefone.CELULAR, numeroCelular);
    }

    private boolean temAluno() {
        return aluno.temIdValido();
    }

    private boolean ehMenuSalvar(int itemIdMenuSalvar) {
        return itemIdMenuSalvar == R.id.menu_salvar;
    }

    @NonNull
    private void criarAluno() {

        String nome = idCampoNome.getText().toString();
        String email = idCampoEmail.getText().toString();

        aluno.setNome(nome);
        aluno.setEmail(email);
    }

    private void inicializarCamposIds() {

        idCampoNome = findViewById(R.id.formulario_cadastro_alunos_campo_nome);
        idCampoEmail = findViewById(R.id.formulario_cadastro_alunos_campo_email);
        idCampoTelefoneFixo = findViewById(R.id.formulario_cadastro_alunos_campo_telefone_fixo);
        idCampoTelefoneCelular = findViewById(R.id.formulario_cadastro_alunos_campo_telefone_celular);
    }
}