package com.amire.amire;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.AppCompatPopupWindow;
import android.widget.Button;
import android.widget.TextView;


public class Settings extends ActionBarActivity {

    Button addtoset;
    Number data, time;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        addtoset= (Button) findViewById(R.id.button5);
    }
}
