package com.alura.agenda.database;

import static com.alura.agenda.model.TipoTelefone.CELULAR;
import static com.alura.agenda.model.TipoTelefone.FIXO;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.alura.agenda.conversor.ConversorCalendar;
import com.alura.agenda.conversor.ConversorTipoTelefone;
import com.alura.agenda.dao.AlunoDao;
import com.alura.agenda.dao.TelefoneDao;
import com.alura.agenda.model.Aluno;
import com.alura.agenda.model.Telefone;

@Database(entities = {Aluno.class, Telefone.class}, version = 6, exportSchema = false)
@TypeConverters({ConversorCalendar.class, ConversorTipoTelefone.class})
public abstract class AgendaDataBase extends RoomDatabase {

    public static final String NOME_DO_BANCO_DE_DADOS = "agenda.db";
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE Aluno ADD COLUMN `momentocadastro` INTEGER");
        }
    };
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {


            //1°criar uma nova tabela
            database.execSQL("CREATE TABLE IF NOT EXISTS `Aluno_novo`" +
                    " (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " `nome` TEXT," +
                    " `email` TEXT," +
                    "`telefone` TEXT," +
                    " `momentoCadastro` INTEGER)");


            //2°copiar os dados da tabela antiga para a nova
            database.execSQL("INSERT INTO Aluno_novo(id,nome,email,telefone,momentoCadastro)" +
                    " SELECT id,nome,email,telefone,momentoCadastro FROM Aluno");

            //3°apagar a atbela antiga
            database.execSQL("DROP TABLE Aluno");

            //4°renomear a atbela nova com o nome da antiga tabela
            database.execSQL("ALTER TABLE Aluno_novo RENAME TO Aluno");


        }
    };
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            //1°criar uma nova tabela
            database.execSQL("CREATE TABLE IF NOT EXISTS `Aluno_novo` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " `nome` TEXT," +
                    " `email` TEXT," +
                    " `telefoneFixo` TEXT, " +
                    "`momentoCadastro` INTEGER)");


            //2°copiar os dados para nova tabela
            database.execSQL("INSERT INTO Aluno_novo(id,nome,email,telefoneFixo,momentoCadastro) SELECT " +
                    "id,nome,email,telefone,momentoCadastro FROM Aluno");

            //3°apagar a tabela antiga
            database.execSQL("DROP TABLE Aluno");

            //4°renomear a tabela nova com o nome da antiga
            database.execSQL("ALTER TABLE Aluno_novo RENAME TO Aluno");


        }
    };
    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE Aluno ADD COLUMN  `telefoneCelular` TEXT");
        }
    };
    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            //1°criar uma tabela nova
            database.execSQL("CREATE TABLE IF NOT EXISTS `Aluno_novo`" +
                    " (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " `nome` TEXT," +
                    " `email` TEXT," +
                    " `momentoCadastro` INTEGER)");

            //2°criar a tabela de telefone
            database.execSQL("CREATE TABLE IF NOT EXISTS `Telefone`" +
                    " (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " `tipo` TEXT," +
                    " `numero` TEXT," +
                    " `alunoId` INTEGER NOT NULL, FOREIGN KEY(`alunoId`) REFERENCES " +
                    "`Aluno`(`id`)" +
                    " ON UPDATE CASCADE" +
                    " ON DELETE CASCADE )");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_Telefone_alunoId` ON `Telefone` (`alunoId`)");

            //3° copiar os dados dos alunos da tabela (((antiga))) para a (((nova))) tabela
            database.execSQL("INSERT INTO Aluno_novo(id,nome,email,momentoCadastro) " +
                    "SELECT id,nome,email,momentoCadastro FROM Aluno");


            //4°copiar os dados de telefone da tabela antiga aluno para a tabela nova telefone
            Cursor cursor = database.query("SELECT id,telefoneFixo,telefoneCelular FROM Aluno");

            if (cursor != null) {


                if (cursor.moveToFirst()) {
                    do {

                        @SuppressLint("Range") String alunoId = cursor.getString(cursor.getColumnIndex("id"));
                        @SuppressLint("Range") String telefoneFixo = cursor.getString(cursor.getColumnIndex("telefoneFixo"));
                        @SuppressLint("Range") String telefoneCelular = cursor.getString(cursor.getColumnIndex("telefoneCelular"));

                        if (telefoneFixo != null) {
                            //inserir telefone fixo
                            database.execSQL("INSERT INTO Telefone(tipo,numero,alunoId)" +
                                    "VALUES('" + FIXO + "', '" + telefoneFixo + "'," + alunoId + ")");

                        }
                        if (telefoneCelular != null) {

                            database.execSQL("INSERT INTO Telefone(tipo,numero,alunoId)" +
                                    "VALUES('" + CELULAR + "','" + telefoneCelular + "'," + alunoId + ")");
                        }
                    } while (cursor.moveToNext());
                }
            }
            //5°apagar a tabela aluno velha
            database.execSQL("DROP TABLE IF EXISTS Aluno");

            //6°renomear a tabela nova com o nome da tabela antiga
            database.execSQL("ALTER TABLE Aluno_novo RENAME TO Aluno");


        }
    };
    public static final Migration[] MIGRATIONS = {
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6};
    private static AgendaDataBase agendaDataBase;

    public abstract AlunoDao alunoDao();

    public abstract TelefoneDao telefoneDao();

    public static AgendaDataBase getInstance(Context context) {


        if (agendaDataBase == null) {

            return agendaDataBase = Room.databaseBuilder(context, AgendaDataBase.class, NOME_DO_BANCO_DE_DADOS)

                    .addMigrations(MIGRATIONS)
                    .build();
        }
        return agendaDataBase;


    }


}
