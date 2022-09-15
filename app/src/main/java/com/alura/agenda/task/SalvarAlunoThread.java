package com.alura.agenda.task;

import com.alura.agenda.dao.AlunoDao;
import com.alura.agenda.dao.TelefoneDao;
import com.alura.agenda.model.Aluno;
import com.alura.agenda.model.Telefone;

public class SalvarAlunoThread implements Runnable {

    private final Aluno aluno;
    private final AlunoDao alunoDao;
    private final Telefone telefoneFixo;
    private final Telefone telefoneCelular;
    private final TelefoneDao telefoneDao;

    public SalvarAlunoThread(Aluno aluno,
                             AlunoDao alunoDao,
                             Telefone telefoneFixo,
                             Telefone telefoneCelular,
                             TelefoneDao telefoneDao) {
        this.aluno = aluno;
        this.alunoDao = alunoDao;
        this.telefoneFixo = telefoneFixo;
        this.telefoneCelular = telefoneCelular;
        this.telefoneDao = telefoneDao;
        new Thread(this).start();

    }

    @Override
    public void run() {

        int alunoId = alunoDao.salvar(aluno).intValue();
        vinculaAlunoComTelefone(alunoId, telefoneFixo, telefoneCelular);
        telefoneDao.salvar(telefoneFixo, telefoneCelular);

    }

    private void vinculaAlunoComTelefone(int alunoId, Telefone... telefones) {

        for (Telefone telefone : telefones) {

            telefone.setAlunoId(alunoId);

        }
    }
}
