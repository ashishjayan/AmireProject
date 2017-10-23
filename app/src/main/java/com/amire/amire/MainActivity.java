package com.amire.amire;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;

import android.support.v7.app.AppCompatActivity;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Timer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnServiceStart;
    private Button btnServiceStop;
    private TextView tvServiceTime;

    private long timer_unit = 1000;
    private long service_distination_total = timer_unit*200;





    private CountDownTimerService countDownTimerService;


    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case 2:
                    tvServiceTime.setText(formateTimer(countDownTimerService.getCountingTime()));
                    if(countDownTimerService.getTimerStatus()==CountDownTimerUtil.PREPARE){
                        btnServiceStart.setText("START");
                    }
                    break;
            }
        }
    };

    private class MyCountDownLisener implements CountDownTimerListeners {

        @Override
        public void onChange() {
            mHandler.sendEmptyMessage(2);
        }
    }



    // private final String DEVICE_ADDRESS="00:14:03:06:2D:7C";

    String DEVICE_ADDRESS;
    public String recieveText;

    public long grandTimerexecutor;
    public int routinOrder;

    final String[] answer = new String[1];

    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    Button btnNorthDoor, btnSouthDoor, btnWestDoor, btnEastDoor, routineButton;
    TextView textView, hiddentexts;

    boolean deviceConnected = false;


    byte buffer[];

    boolean stopThread;

    private ToggleButton togglebutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent newint = getIntent();
        DEVICE_ADDRESS = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);
        setContentView(R.layout.activity_my);


        btnServiceStart = (Button) findViewById(R.id.btn_start2);
        btnServiceStop = (Button) findViewById(R.id.btn_stop2);
        tvServiceTime = (TextView) findViewById(R.id.tv_time2);

        btnServiceStart.setOnClickListener(this);
        btnServiceStop.setOnClickListener(this);

//Mdestinationtotal = 200000 3 minutes 20 seconds.
        countDownTimerService = CountDownTimerService.getInstance(new MyCountDownLisener()
                ,service_distination_total);
        initServiceCountDownTimerStatus();



        answer[0] = "50";
        textView = (TextView) findViewById(R.id.textView);
        togglebutton = (ToggleButton) findViewById(R.id.toggleButton);
        routinOrder = 1;
        btnNorthDoor = (Button) findViewById(com.amire.amire.R.id.North);
        btnSouthDoor = (Button) findViewById(com.amire.amire.R.id.South);
        btnWestDoor = (Button) findViewById(com.amire.amire.R.id.West);
        btnEastDoor = (Button) findViewById(com.amire.amire.R.id.east);
        hiddentexts = (TextView) findViewById(R.id.hiddentext);

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
//        routineButton.setEnabled(bool);
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

public void runSimulation()
{

    Set mySet = Schedule.TimerDetails.get(routinOrder).entrySet();
    Iterator myIterator = mySet.iterator();
    Log.d("simbeforeroutine", Integer.toString(routinOrder));
    while (myIterator.hasNext()) {
        Map.Entry me = (Map.Entry) myIterator.next();
       final Map.Entry ender=me;
        try {
            grandTimerexecutor = Long.parseLong(me.getKey().toString());


            //OPENS DOOR
            executeCommand(me.getValue().toString());
            executeCommand(ender.getValue().toString());




        } catch (Exception ex) {
            Log.d("hadf", "asdf");
        }

    }


}




    public void beginListenForData() {
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread = new Thread(new Runnable() {
            int bytes;

            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopThread) {
                    try {
                        byte[] buffer = new byte[128];
                        Arrays.fill(buffer, (byte) 0x00);
                        bytes = inputStream.read(buffer);
                        if (bytes > 0) {
                            final String string = new String(buffer, "US-ASCII");
                            handler.post(new Runnable() {
                                public void run() {

                                    String sender;
                                    sender = string.trim();
                                    sender = sender.replaceAll("^\"|\"$", "");

                                    recieveText = sender;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_start2:
                switch (countDownTimerService.getTimerStatus()){
                    case CountDownTimerUtil.PREPARE:
                        countDownTimerService.startCountDown();
                        btnServiceStart.setText("PAUSE");
                        break;
                    case CountDownTimerUtil.START:
                        countDownTimerService.pauseCountDown();
                        btnServiceStart.setText("RESUME");
                        break;
                    case CountDownTimerUtil.PASUSE:
                        countDownTimerService.startCountDown();
                        btnServiceStart.setText("PAUSE");
                        break;
                }
                break;
            case R.id.btn_stop2:
                btnServiceStart.setText("START");
                countDownTimerService.stopCountDown();
                break;
        }
    }



    /**
     * formate timer shown in textview
     * @param time
     * @return
     */
    private String formateTimer(long time){
        String str = "00:00:00";
        int hour = 0;
        if(time>=1000*3600){
            hour = (int)(time/(1000*3600));
            time -= hour*1000*3600;
        }
        int minute = 0;
        if(time>=1000*60){
            minute = (int)(time/(1000*60));
            time -= minute*1000*60;
        }
        int sec = (int)(time/1000);
        str = formateNumber(hour)+":"+formateNumber(minute)+":"+formateNumber(sec);
        return str;
    }

    /**
     * formate time number with two numbers auto add 0
     * @param time
     * @return
     */
    private String formateNumber(int time){
        return String.format("%02d", time);
    }




    /**
     * init countdowntimer buttons status for servce
     */
    private void initServiceCountDownTimerStatus(){
        switch (countDownTimerService.getTimerStatus()) {
            case CountDownTimerUtil.PREPARE:
                btnServiceStart.setText("START");
                break;
            case CountDownTimerUtil.START:
                btnServiceStart.setText("PAUSE");
                break;
            case CountDownTimerUtil.PASUSE:
                btnServiceStart.setText("RESUME");
                break;
        }
        tvServiceTime.setText(formateTimer(countDownTimerService.getCountingTime()));

    }
}
