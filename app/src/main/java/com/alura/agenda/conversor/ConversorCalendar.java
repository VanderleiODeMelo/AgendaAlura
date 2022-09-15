package com.alura.agenda.conversor;

import androidx.room.TypeConverter;

import java.util.Calendar;

public class ConversorCalendar {

    @TypeConverter
    public Long paraLong(Calendar valor) {

        return valor.getTimeInMillis();

    }

    @TypeConverter
    public Calendar paraCalendar(Long valor) {
        Calendar momentoCadastro = Calendar.getInstance();

        if (valor != null) {

            momentoCadastro.setTimeInMillis(valor);
        }
        return momentoCadastro;

    }

}
