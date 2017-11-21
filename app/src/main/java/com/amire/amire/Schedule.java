package com.amire.amire;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;


public class Schedule extends ActionBarActivity {
    public static String myString;

    ListView schedulelist;
    ArrayAdapter<String> adapter;
   // EditText  duration;
    String channel;
    Spinner spinner;
    NumberPicker scrollwheel;
    ArrayAdapter<CharSequence> channeladapter;
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
//        order = (EditText)findViewById(R.id.order);
        spinner= (Spinner) findViewById(R.id.channelnumber);
        //channel = (EditText)findViewById(R.id.channelnumber);
        channeladapter=ArrayAdapter.createFromResource(this,R.array.channels,android.R.layout.simple_spinner_item);
        channeladapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(channeladapter);
        //duration = (EditText)findViewById(R.id.duration);
        routine = new ArrayList<String>();
        scrollwheel= (NumberPicker) findViewById(R.id.numberPicker);
        scrollwheel.setMinValue(1);
        scrollwheel.setMaxValue(60);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                channel= (String) parent.getItemAtPosition(position);
                Toast.makeText(getBaseContext(), (CharSequence) channel,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
            public String toString() {
                return "$classname{}";
            }

            @Override
            public void onClick(View view) {
                int myChannel =0;
//                if(channel== "Bright") {
//                    Toast.makeText(getBaseContext(), channel,
//                            Toast.LENGTH_SHORT).show();
//                    myChannel = 1;
//                }
//                else if(channel=="Dark")
//                    myChannel=2;
//                else if (channel=="Food")
//                    myChannel=3;
//                else if(channel=="Water")
//                    myChannel=4;
                        try{

                            myChannel = Integer.parseInt(channel);
                        }
                        catch (Exception my){
                            AlertDialog myDialog = new AlertDialog.Builder(Schedule.this).setTitle("Alert").setMessage("Invalid Input").setNeutralButton("Close",null).show();

                        }


                HashMap<Integer,Integer> myMap = new HashMap<Integer, Integer>();
                //myMap.put(2,myChannel);
                myMap.put(scrollwheel.getValue(),myChannel);

                TimerDetails.add(myMap);

               // routine.add(order.getText().toString()+" channel ="+channel.getText().toString()+" time="+duration.getText().toString());

                for(Integer i = TimerDetails.size()-1; i< TimerDetails.size();i++){
                    Set mySet = TimerDetails.get(i).entrySet();
                    Iterator myIterator = mySet.iterator();
                    while (myIterator.hasNext()){
                        Map.Entry me = (Map.Entry) myIterator.next();
                        routine.add("Channel = "+me.getValue().toString()+"Duration = "+me.getKey().toString());
                    }
                }
//                order.setText("");
               //channel.setText("");
              //  duration.setText("");
                scrollwheel.setValue(1);
//                order.setHint("order");
              //  channel.setHint("channel");
         //       duration.setHint("time");
             Log.d("sd","addedd");
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
