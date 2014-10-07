package com.mrzon.churchhub.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class DataManager {
    private Context context;

    public DataManager(Context baseContext) {
        this.context = baseContext;
    }

    public List<Denomination> getDenominationList() {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("DENOMINATION", -1);

        return null;
    }

}
