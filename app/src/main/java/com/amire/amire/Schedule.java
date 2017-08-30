package com.amire.amire;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.amire.amire.AmireControl;

public class Schedule extends ActionBarActivity {
    public static String myString;
    ListView schedulelist;
    ArrayAdapter<String> adapter;
    Button add;
    ArrayList<String> routine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);
        add = (Button) findViewById(R.id.addbutton);
         routine = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.activity_device_list,R.id.textView,routine);
        schedulelist.setAdapter(adapter);

//        for(Integer i = 0;i<10;i++){
//            HashMap<Integer,Integer> myMap = new HashMap<>();
//            myMap.put(i+1,i+2);
//            AmireControl.TimerDetails.add(myMap);
//        }

//        for(Integer i = 0;i<10;i++){
//            Log.d("timer details =" , String.valueOf(TimerDetails.get(i).keySet()));
//
//            Set set = TimerDetails.get(i).entrySet();
//            Iterator iam = set.iterator();
//            while(iam.hasNext()) {
//                Map.Entry me = (Map.Entry) iam.next();
//                Log.d("Key",String.valueOf(me.getKey()));
//                Log.d("Value",String.valueOf(me.getValue()));
//
//            }
//        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(),Settings.class);
                startActivityForResult(myIntent,0);
            }
        });
    }



}
