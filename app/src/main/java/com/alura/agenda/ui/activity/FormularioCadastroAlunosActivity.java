package com.alura.agenda.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alura.agenda.R;
import com.alura.agenda.model.Aluno;

public class FormularioCadastroAlunosActivity extends AppCompatActivity {

    public static final String TITULO_APPBAR = "Formulario cadastro aluno";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_cadastro_alunos);
        setTitle(TITULO_APPBAR);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.formulario_cadastro_alunos_menus_opcoes, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemIdMenuSalvar = item.getItemId();

        if (itemIdMenuSalvar == R.id.menu_salvar) {

            EditText idCampoNome = findViewById(R.id.formulario_cadastro_alunos_campo_nome);
            EditText idCampoEmail = findViewById(R.id.formulario_cadastro_alunos_campo_email);
            EditText idCampoTelefone = findViewById(R.id.formulario_cadastro_alunos_campo_telefone);

            String nome = idCampoNome.getText().toString();
            String email = idCampoEmail.getText().toString();
            String telefone = idCampoTelefone.getText().toString();

            Aluno aluno = new Aluno();
            aluno.setNome(nome);
            aluno.setEmail(email);
            aluno.setTelefone(telefone);
            finish();


        }


        return super.onOptionsItemSelected(item);
    }
}