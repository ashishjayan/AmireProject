package com.amire.amire;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import  java.util.Set;

public class AmireControl extends ActionBarActivity {

    Button btnDis,btnNorthDoor, btnSouthDoor, btnWestDoor, btnEastDoor,btnSchedule, btnNlight,btnSlight,btnWlight,btnElight;
    public static ArrayList<HashMap<Integer,Integer>> TimerDetails;

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the AmireControl
        setContentView(com.amire.amire.R.layout.activity_my);

        //call the widgtes
        btnSchedule =(Button)findViewById(com.amire.amire.R.id.buttonschedule);
        btnNorthDoor =(Button)findViewById(com.amire.amire.R.id.North);
        btnSouthDoor =(Button)findViewById(com.amire.amire.R.id.South);
        btnWestDoor =(Button)findViewById(com.amire.amire.R.id.West);
        btnEastDoor =(Button)findViewById(com.amire.amire.R.id.east);
        btnNlight =(Button)findViewById(com.amire.amire.R.id.nlight);
        btnSlight =(Button)findViewById(com.amire.amire.R.id.slight);
        btnWlight =(Button)findViewById(com.amire.amire.R.id.wlight);
        btnElight  =(Button)findViewById(com.amire.amire.R.id.elight);
        btnDis = (Button)findViewById(com.amire.amire.R.id.buttondisconnect);


        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
        btnNorthDoor.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                executeCommand("Nd");
                                            }
                                        }

        );
        btnSouthDoor.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                executeCommand("Sd");
                                            }
                                        }

        );
        btnWestDoor.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                executeCommand("Wd");
                                            }
                                        }

        );
        btnEastDoor.setOnClickListener(new View.OnClickListener()
                                       {
                                           @Override
                                           public void onClick(View v)
                                           {
                                               executeCommand("Ed");
                                           }
                                       }

        );
//

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scheduleIntent = new Intent(view.getContext(),Schedule.class);
                startActivityForResult(scheduleIntent,0);
            }
        });

        btnElight.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(Integer i = 0; i< Schedule.TimerDetails.size();i++){

                    Set mySet = Schedule.TimerDetails.get(i).entrySet();
                    Iterator myIterator = mySet.iterator();
                    while (myIterator.hasNext()){
                        Map.Entry me = (Map.Entry) myIterator.next();
                        try{
                            executeCommand(me.getValue().toString());
                            Long value = Long.parseLong(me.getKey().toString());
                            TimeUnit.SECONDS.sleep(value);
                        }
                        catch (Exception ex){
                            Log.d("hadf","asdf");
                        }
                    }
                }

            }
        }));

        btnDis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });


    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }


    private void executeCommand(String command)
    {
        if(btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(command.toString().getBytes());
            }
            catch(IOException e)
            {
                msg("Error");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.amire.amire.R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.amire.amire.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(AmireControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                 myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                 BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                 btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                 BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                 btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
