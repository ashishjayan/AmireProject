package com.amire.amire;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    // private final String DEVICE_ADDRESS="00:14:03:06:2D:7C";
    String DEVICE_ADDRESS;

    Timer timer;
    public long grandTimerexecutor;
    public int routinOrder;
    final String [] answer= new String[1];
    public int testArray[];
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    Button btnNorthDoor, btnSouthDoor, btnWestDoor, btnEastDoor, routineButton;
    TextView textView, hiddentexts;
    EditText editText;
    boolean deviceConnected = false;
    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;
    //

    private ToggleButton togglebutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newint = getIntent();
        DEVICE_ADDRESS = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);
        setContentView(R.layout.activity_my);
        answer[0]="50";
        textView = (TextView) findViewById(R.id.textView);
        togglebutton = (ToggleButton) findViewById(R.id.toggleButton);
        routinOrder = 1;
        btnNorthDoor = (Button) findViewById(com.amire.amire.R.id.North);
        btnSouthDoor = (Button) findViewById(com.amire.amire.R.id.South);
        btnWestDoor = (Button) findViewById(com.amire.amire.R.id.West);
        btnEastDoor = (Button) findViewById(com.amire.amire.R.id.east);
        hiddentexts =(TextView) findViewById(R.id.hiddentext);
        routineButton = (Button) findViewById(com.amire.amire.R.id.elight);
        setUiEnabled(false);


        btnNorthDoor.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    executeCommand("1");
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

        );
        btnSouthDoor.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    executeCommand("2");
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

        );
        btnWestDoor.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               try {
                                                   executeCommand("3");
                                               } catch (IOException e) {
                                                   e.printStackTrace();
                                               }
                                           }
                                       }

        );
        btnEastDoor.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               try {
                                                   executeCommand("4");
                                               } catch (IOException e) {
                                                   e.printStackTrace();
                                               }
                                           }
                                       }

        );

    }

    public void toggleclick(View v) throws IOException {
        if (togglebutton.isChecked()) {
            if (BTinit()) {
                if (BTconnect()) {
                    Toast.makeText(getApplicationContext(), "Connection Working", Toast.LENGTH_LONG).show();
                    setUiEnabled(true);
                    deviceConnected = true;

                    beginListenForData();
                    textView.append("\nConnection Opened!\n");
                    Log.d("Sent value", "HELLO STARTED");
                }

            }
            Toast.makeText(MainActivity.this, "ON", Toast.LENGTH_SHORT).show();
        } else {
            stopThread = true;
            outputStream.close();
            inputStream.close();
            socket.close();
            setUiEnabled(false);
            deviceConnected = false;
            textView.append("\nConnection Closed!\n");
            Toast.makeText(MainActivity.this, "OFF", Toast.LENGTH_SHORT).show();
        }

    }


    public void setUiEnabled(boolean bool) {
        // startButton.setEnabled(!bool);
//        sendButton.setEnabled(bool);
        btnEastDoor.setEnabled(bool);
        btnNorthDoor.setEnabled(bool);
        btnSouthDoor.setEnabled(bool);
        btnWestDoor.setEnabled(bool);
        routineButton.setEnabled(bool);
        //  stopButton.setEnabled(bool);
        textView.setEnabled(bool);

    }

    public boolean BTinit() {
        boolean found = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesnt Support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    public boolean BTconnect() {
        boolean connected = true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }
        if (connected) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return connected;
    }






    public void startRoutine(View view) {


            while (routinOrder < Schedule.TimerDetails.size()) {

                    Set mySet = Schedule.TimerDetails.get(routinOrder).entrySet();
                    Iterator myIterator = mySet.iterator();
                    while (myIterator.hasNext()) {
                        Map.Entry me = (Map.Entry) myIterator.next();
                        try {
                            grandTimerexecutor = Long.parseLong(me.getKey().toString());


                            //OPENS DOOR
                            executeCommand(me.getValue().toString());
                            Thread.sleep(grandTimerexecutor * 1000);
                            textView.setText(inputStream.toString());
                            // (new Handler()).postDelayed(this::yourMethod, grandTimerexecutor);

                           // if(inputStream.read()==20)



                        } catch (Exception ex) {
                            Log.d("hadf", "asdf");
                        }
                    }
                    routinOrder++;
                }
            }










    public void beginListenForData() {
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopThread) {
                    try {
                        int byteCount = inputStream.available();
                        if (byteCount > 0) {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);


                            final String string = new String(rawBytes, "UTF-8");
                            if(string=="") {
                                hiddentexts.setText(string);
                            }

                            handler.post(new Runnable() {
                                public void run() {



                                    textView.append(string);
                                }
                            });

                        }

                    } catch (IOException ex) {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }

    private void executeCommand(String command) throws IOException {
        command.concat("\n");
        try {
            outputStream.write(command.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView.append("\nCommand:" + command + "\n");
    }



    public void onClickClear(View view) {
        textView.setText("");
    }
}
