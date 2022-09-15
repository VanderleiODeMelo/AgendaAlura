package com.alura.agenda.task;

import com.alura.agenda.dao.AlunoDao;
import com.alura.agenda.dao.TelefoneDao;
import com.alura.agenda.model.Aluno;
import com.alura.agenda.model.Telefone;
import com.alura.agenda.model.TipoTelefone;

import java.util.List;

public class EditarAlunoETelefonesThread implements Runnable {


    private final Aluno aluno;
    private final TelefoneDao telefoneDao;
    private final AlunoDao alunoDao;
    private final Telefone telefoneCelular;
    private final List<Telefone> listaTelefones;
    private final Telefone telefoneFixo;

    public EditarAlunoETelefonesThread(Aluno aluno,
                                       TelefoneDao telefoneDao,
                                       AlunoDao alunoDao,
                                       Telefone telefoneCelular,
                                       List<Telefone> listaTelefones,
                                       Telefone telefoneFixo) {
        this.aluno = aluno;
        this.telefoneDao = telefoneDao;
        this.alunoDao = alunoDao;
        this.telefoneCelular = telefoneCelular;
        this.listaTelefones = listaTelefones;
        this.telefoneFixo = telefoneFixo;
        new Thread(this).start();

    }

    @Override
    public void run() {

        alunoDao.editar(aluno);
        vinculaAlunoComTelefone(aluno.getId(), telefoneFixo, telefoneCelular);
        atualizarIdsTelefones();
        telefoneDao.editar(telefoneFixo, telefoneCelular);
    }

    private void atualizarIdsTelefones() {
        for (Telefone telefone : listaTelefones) {

            if (telefone.getTipo() == TipoTelefone.FIXO) {

                telefoneFixo.setId(telefone.getId());

            } else {

                telefoneCelular.setId(telefone.getId());
            }

        }
    }

    private void vinculaAlunoComTelefone(int alunoId, Telefone... telefones) {

        for (Telefone telefone : telefones) {


            telefone.setAlunoId(alunoId);

        }

    }


}
