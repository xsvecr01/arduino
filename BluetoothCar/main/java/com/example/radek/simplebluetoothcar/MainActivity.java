package com.example.radek.simplebluetoothcar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Button OffBtn;
    private Button OnBtn;
    private SeekBar SteerSeekBar;
    private SeekBar SpeedSeekBar;
    private Button ConnectBtn;
    private EditText inputMin;
    private EditText inputMax;

    private BluetoothAdapter mBluetoothAdapter;

    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names

    private final String DEVICE_ADDRESS = "98:D3:33:80:A6:CB"; //MAC Address of Bluetooth Module
    static final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    int Sp;
    int St;
    String command;
    boolean connected = false;

    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OffBtn = findViewById(R.id.OffBtn);
        OnBtn = findViewById(R.id.OnBtn);
        SteerSeekBar = findViewById(R.id.steerSeekBar);
        SpeedSeekBar = findViewById(R.id.speedSeekBar);
        ConnectBtn = findViewById(R.id.ConnectBtn);



        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getApplicationContext(), "Bluetooth is not supported", Toast.LENGTH_SHORT).show();
        }

        OnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothOn();
            }
        });

        OffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothOff();
            }
        });

        ConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(BTinit())
                {
                    BTconnect();
                }
            }
        });

        SteerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int Steer, boolean fromUser) {


                //Steer = SteerSeekBar.getProgress();
                //St = Steer;
                St = SteerSeekBar.getProgress() + 50;

                //sendValues();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                SteerSeekBar.setProgress(40);

            }
        });

        SpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int Speed, boolean fromUser) {

                Speed = SpeedSeekBar.getProgress();
                if (Speed <= 255){
                    Sp = Speed - 255;
                }
                else if (Speed >= 305){
                    Sp = Speed - 305;
                }
                else Sp = 0;

                //sendValues();


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                SpeedSeekBar.setProgress(280);

            }
        });

        final long period = 50;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // do your task here
                try {
                    sendValues();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, 0, period);

    }

    //**********************************************************************************************

    public void sendValues(){
        command = St+"X"+Sp+"\n";
        if (connected){
            try
            {
                outputStream.write(command.getBytes("UTF-8"));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public void bluetoothOn(){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(), "Bluetooth turned on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    public void bluetoothOff() {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(), "Bluetooth turned off", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already off", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean BTinit()
    {
        boolean found = false;

        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();

        if(bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean BTconnect()
    {

        try
        {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();
            connected = true;

            Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            connected = false;

            Toast.makeText(getApplicationContext(),
                    "Failed connecting to device", Toast.LENGTH_LONG).show();
        }

        if(connected)
        {
            try
            {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        return connected;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }
}
