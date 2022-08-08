package com.alura.agenda.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.alura.agenda.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class ListaAlunosActivity extends AppCompatActivity {


    ActivityResultLauncher<Intent> intentActivityResultLauncherSalvar = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            Toast.makeText(ListaAlunosActivity.this, "SALVAR", Toast.LENGTH_SHORT).show();

        }
    });


    public static final String TITULO_APPBAR = "Lista";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);
        setTitle(TITULO_APPBAR);

        ExtendedFloatingActionButton idBotaoAdicionarAlunoFab = findViewById(R.id.lista_alunos_adicionar_aluno_fab);
        idBotaoAdicionarAlunoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent vaiParaFormularioCadastroAlunosActivity = new Intent(ListaAlunosActivity.this,
                        FormularioCadastroAlunosActivity.class);
                intentActivityResultLauncherSalvar.launch(vaiParaFormularioCadastroAlunosActivity);

            }
        });


    }
}