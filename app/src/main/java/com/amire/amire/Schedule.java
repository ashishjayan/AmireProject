package com.amire.amire;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Schedule extends ActionBarActivity {
    public static String myString;

    ListView schedulelist;
    ArrayAdapter<String> adapter;
    EditText order, channel, duration;
    Button add, start;
    ArrayList<String> routine;
    public static ArrayList<HashMap<Integer,Integer>> TimerDetails;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);
        add = (Button) findViewById(R.id.addbutton);
        start=(Button) findViewById(R.id.Continue);
        schedulelist = (ListView) findViewById(R.id.routine);
        order = (EditText)findViewById(R.id.order);
        channel = (EditText)findViewById(R.id.channelnumber);
        duration = (EditText)findViewById(R.id.duration);
        routine = new ArrayList<String>();

        adapter= new ArrayAdapter<String>(Schedule.this,android.R.layout.simple_list_item_multiple_choice,routine);
        TimerDetails = new ArrayList<HashMap<Integer, Integer>>();


//        for(Integer i = 0;i<10;i++){
//            HashMap<Integer,Integer> myMap = new HashMap<>();
//            myMap.put(i+1,i+2);
//            AmireControl.TimerDetails.add(myMap);
//        }
//
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
                routine.add(order.getText().toString());
                order.setText("");
                adapter.notifyDataSetChanged();
                //Beginning of end soft keybaord
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                //end of end soft keyboard

            }
        });

        schedulelist.setAdapter(adapter);

        start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                Intent myIntent = new Intent(view.getContext(),DeviceList.class);
                startActivity(myIntent);
            }
        });
    }



}
