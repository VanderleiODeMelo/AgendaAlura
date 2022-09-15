package com.alura.agenda.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataUtil {


    public static String formatarData(Calendar momentoCadastro) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(momentoCadastro.getTime());
    }
}
