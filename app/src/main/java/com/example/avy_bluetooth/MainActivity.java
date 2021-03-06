package com.example.avy_bluetooth;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static android.R.layout.simple_list_item_1;
import static android.R.layout.simple_spinner_dropdown_item;

public class MainActivity extends AppCompatActivity {
    ImageView imgSetup, imgMenuListDevice, imgBluetoothConnection;
    View layoutSetup, layoutListDevice;
    TextView txtBackListDevice, txtNameBluetoothConnection;

    ProgressBar pgbRefreshListDevice;
    ListView lvListDevice;

    Bitmap bitmap;
    //Main Control
    Button btnMainOpenCloseCabinet;
    RelativeLayout layoutColorRgb, layoutOnOffLed;
    ImageView imgRefreshNameDevice, imgRgbColor;
    Spinner spNameCabinet;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch swOnOffLed;
    SeekBar sbAlphaRgb;

    //Setup layout
    TextView txtBackSetting;
    Spinner spSettingNameCabinet;
    EditText edtSettingNewNameCabinet;
    Button btnSettingMappingNameCabinet;
    EditText edtSetDeviceID;
    Button btnSettingSetIdDevice;
    EditText edtSetDeviceIDStart, edtSetDeviceIDEnd;
    Button btnSettingSetAllDevice;
    EditText edtSettingTimeReturn, edtSettingModeRun, edtSettingDelayPush, edtSettingMaxValuePush,
            edtSettingTimeAutoClose, edtSettingPercentLowIn, edtSettingPercentLowOut, edtSettingMinStopSpeed;
    CheckBox cbSettingResetDistant;
    Button btnSettingSendDataSetup;


    ArrayAdapter<String> arrayAdapterListDevice;
    int mRed = 255, mGreen = 255, mBlue = 255;
    public static int REQUEST_BLUETOOTH = 1;
    public static int REQUEST_DISCOVERABLE_BT = 1;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    String TAG = "MainActivity";
    String deviceName;
    private BluetoothDevice mmDevice;
    //private UUID deviceUUID;

    ParcelUuid[] mDeviceUUIDs;
    ConnectedThread mConnectedThread;
    //    private Handler handler;
    Object[] ObjectBluetooth;

    HashMap<String, Integer> hMapNameDevice = new HashMap<String, Integer>();
    List<String> list = new ArrayList<>();
    int idStart, idEnd;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); // hide the title bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_main);

        initLayout();
        checkTurnOnBluetooth();

        //-------------------------------------------------------------------
        imgSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutSetup.setVisibility(View.VISIBLE);
                layoutColorRgb.setVisibility(View.GONE);
                layoutOnOffLed.setVisibility(View.GONE);
//                btnMainOpenCloseCabinet.setVisibility(View.GONE);
            }
        });

        txtBackSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btnControl.setVisibility(View.VISIBLE);
                layoutSetup.setVisibility(View.GONE);
//                btnMainOpenCloseCabinet.setVisibility(View.VISIBLE);
                layoutColorRgb.setVisibility(View.VISIBLE);
                layoutOnOffLed.setVisibility(View.VISIBLE);
            }
        });
        //------------------------------------------------------------------

        imgMenuListDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutListDevice.setVisibility(View.VISIBLE);
//                btnMainOpenCloseCabinet.setVisibility(View.INVISIBLE);
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                List<String> s = new ArrayList<String>();
//                s.add("---Thi???t b??? ???? gh??p ????i---");
                for(BluetoothDevice bt : pairedDevices){
                    s.add(bt.getName() + "\n" + bt.getAddress());
                }
                ObjectBluetooth = pairedDevices.toArray();
//                s.add("---Thi???t b??? hi???n c??---");
                arrayAdapterListDevice = new ArrayAdapter<String>(
                        MainActivity.this,
                        simple_list_item_1,
                        s );
                lvListDevice.setAdapter(arrayAdapterListDevice);
            }
        });
        txtBackListDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                btnMainOpenCloseCabinet.setVisibility(View.VISIBLE);
                layoutListDevice.setVisibility(View.INVISIBLE);
                pgbRefreshListDevice.setVisibility(View.INVISIBLE);
            }
        });
        lvListDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.i(TAG, arrayAdapterListDevice.getItem(i));

                pgbRefreshListDevice.setVisibility(View.VISIBLE);
                BluetoothDevice bluetoothDeviceConnect = (BluetoothDevice)ObjectBluetooth[i];

                deviceName = bluetoothDeviceConnect.getName();
                String deviceAddress = bluetoothDeviceConnect.getAddress();

                Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);
                Log.d(TAG, "Trying to Pair with " + deviceName);
                bluetoothDeviceConnect.createBond();

                mDeviceUUIDs = bluetoothDeviceConnect.getUuids();

                Log.d(TAG, "Trying to create UUID: " + deviceName);

                for (ParcelUuid uuid: mDeviceUUIDs) {
                    Log.d(TAG, "UUID: " + uuid.getUuid().toString());
                }

//                ParcelUuid uuidExtra Intent intent = null;
//                intent.getParcelableExtra("android.bluetooth.device.extra.UUID");
//                UUID uuid = mDeviceUUIDs.getUuid();

                ConnectThread connect = new ConnectThread(bluetoothDeviceConnect,MY_UUID_INSECURE);
                connect.start();
            }
        });

        //------------------------------CONTROL MAIN MENU------------------------------------
        imgRefreshNameDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mmDevice !=null && isConnected(mmDevice)) {
                    String data = "{\"type\":\"get_name\"}";
                    byte[] bytes = data.getBytes(Charset.defaultCharset());
                    mConnectedThread.write(bytes);
                }
            }
        });
        imgRgbColor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                    bitmap = imgRgbColor.getDrawingCache();
                    int pixel = bitmap.getPixel((int)motionEvent.getX(), (int)motionEvent.getY());
                    mRed = Color.red(pixel);
                    mGreen = Color.green(pixel);
                    mBlue = Color.blue(pixel);
                    Log.d(TAG, String.valueOf(mRed) + " - " + String.valueOf(mGreen) + " - " + String.valueOf(mBlue));
                    if (mmDevice !=null && isConnected(mmDevice)) {
                        String data = "{\"type\":\"change_rgb\",\"id\":" +
                                String.valueOf(idStart+spNameCabinet.getSelectedItemPosition()) +
                                ",\"data\":[";
                        data += String.valueOf(mRed);
                        data += ",";
                        data += String.valueOf(mGreen);
                        data += ",";
                        data += String.valueOf(mBlue);
                        data += "]}";
                        byte[] bytes = data.getBytes(Charset.defaultCharset());
                        mConnectedThread.write(bytes);
                    }
                }
                return false;
            }
        });
        swOnOffLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mmDevice !=null && isConnected(mmDevice)) {
                    if(b){
                        String data = "{\"type\":\"change_rgb\",\"id\":" +
                                String.valueOf(idStart+spNameCabinet.getSelectedItemPosition()) +
                                ",\"data\":[";
                        data += String.valueOf(mRed);
                        data += ",";
                        data += String.valueOf(mGreen);
                        data += ",";
                        data += String.valueOf(mBlue);
                        data += "]}";
                        byte[] bytes = data.getBytes(Charset.defaultCharset());
                        mConnectedThread.write(bytes);
                    }
                    else{
                        String data = "{\"type\":\"change_rgb\",\"id\":" +
                                String.valueOf(idStart+spNameCabinet.getSelectedItemPosition()) +
                                ",\"data\":[0,0,0]}";
                        byte[] bytes = data.getBytes(Charset.defaultCharset());
                        mConnectedThread.write(bytes);
                    }
                }
            }
        });
        sbAlphaRgb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, String.valueOf(sbAlphaRgb.getProgress()));
                if (mmDevice !=null && isConnected(mmDevice)) {
                    String data = "{\"type\":\"change_rgb\",\"id\":" +
                            String.valueOf(idStart+spNameCabinet.getSelectedItemPosition()) +
                            ",\"data\":[";
                    data += String.valueOf(mRed*sbAlphaRgb.getProgress()/100);
                    data += ",";
                    data += String.valueOf(mGreen*sbAlphaRgb.getProgress()/100);
                    data += ",";
                    data += String.valueOf(mBlue*sbAlphaRgb.getProgress()/100);
                    data += "]}";
                    byte[] bytes = data.getBytes(Charset.defaultCharset());
                    mConnectedThread.write(bytes);
                }
            }
        });
        btnMainOpenCloseCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mmDevice !=null && isConnected(mmDevice)) {
                    if (btnMainOpenCloseCabinet.getText().equals("OPEN")) {
                        btnMainOpenCloseCabinet.setText("CLOSE");
                        String data = "{\"type\":\"control\",\"id\":" +
                                String.valueOf(idStart+spNameCabinet.getSelectedItemPosition()) +
                                ",\"data\":\"close\"}";
                        byte[] bytes = data.getBytes(Charset.defaultCharset());
                        mConnectedThread.write(bytes);
                    } else {
                        btnMainOpenCloseCabinet.setText("OPEN");
                        String data = "{\"type\":\"control\",\"id\":" +
                                String.valueOf(idStart+spNameCabinet.getSelectedItemPosition()) +
                                ",\"data\":\"open\"}";
                        byte[] bytes = data.getBytes(Charset.defaultCharset());
                        mConnectedThread.write(bytes);
                    }
                }
            }
        });


        //------------------------------CONTROL SETTING MENU------------------------------------
        btnSettingMappingNameCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mmDevice !=null && isConnected(mmDevice))
                {
                    try {
                        JSONObject j = new JSONObject();
                        j.put("type", "mapping_name");
                        j.put("index", String.valueOf(spSettingNameCabinet.getSelectedItemPosition()));
                        j.put("name", edtSettingNewNameCabinet.getText().toString());
                        String dataSend = j.toString();
                        byte[] bytes = dataSend.getBytes(Charset.defaultCharset());
                        mConnectedThread.write(bytes);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        btnSettingSetIdDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mmDevice !=null && isConnected(mmDevice))
                {
                    try {
                        JSONObject j = new JSONObject();
                        j.put("type", "set_id");
                        j.put("id", edtSetDeviceID.getText().toString());
                        String dataSend = j.toString();
                        byte[] bytes = dataSend.getBytes(Charset.defaultCharset());
                        mConnectedThread.write(bytes);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        btnSettingSetAllDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mmDevice !=null && isConnected(mmDevice))
                {
                    try {
                        JSONObject j = new JSONObject();
                        j.put("type", "set_all_device");
                        j.put("id_start", edtSetDeviceIDStart.getText().toString());
                        j.put("id_end", edtSetDeviceIDEnd.getText().toString());
                        String dataSend = j.toString();
                        byte[] bytes = dataSend.getBytes(Charset.defaultCharset());
                        mConnectedThread.write(bytes);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        btnSettingSendDataSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mmDevice !=null && isConnected(mmDevice))
                {
                    try {
                        JSONObject j = new JSONObject();
                        j.put("type", "setting");
                        j.put("id", idStart + spSettingNameCabinet.getSelectedItemPosition());
                        JSONArray dataArray = new JSONArray();
                        if(!edtSettingTimeReturn.getText().toString().equals("")){
                            dataArray.put(Integer.parseInt(edtSettingTimeReturn.getText().toString()));
                        }
                        else{
                            dataArray.put(0);
                        }
                        if(!edtSettingModeRun.getText().toString().equals("")){
                            dataArray.put(Integer.parseInt(edtSettingModeRun.getText().toString()));
                        }
                        else{
                            dataArray.put(0);
                        }
                        if(!edtSettingDelayPush.getText().toString().equals("")){
                            dataArray.put(Integer.parseInt(edtSettingDelayPush.getText().toString()));
                        }
                        else{
                            dataArray.put(0);
                        }
                        if(!edtSettingMaxValuePush.getText().toString().equals("")){
                            dataArray.put(Integer.parseInt(edtSettingMaxValuePush.getText().toString()));
                        }
                        else{
                            dataArray.put(0);
                        }
                        if(!edtSettingTimeAutoClose.getText().toString().equals("")){
                            dataArray.put(Integer.parseInt(edtSettingTimeAutoClose.getText().toString()));
                        }
                        else{
                            dataArray.put(0);
                        }
                        if(!edtSettingPercentLowIn.getText().toString().equals("")){
                            dataArray.put(Integer.parseInt(edtSettingPercentLowIn.getText().toString()));
                        }
                        else{
                            dataArray.put(0);
                        }
                        if(!edtSettingPercentLowOut.getText().toString().equals("")){
                            dataArray.put(Integer.parseInt(edtSettingPercentLowOut.getText().toString()));
                        }
                        else{
                            dataArray.put(0);
                        }
                        if(!edtSettingMinStopSpeed.getText().toString().equals("")){
                            dataArray.put(Integer.parseInt(edtSettingMinStopSpeed.getText().toString()));
                        }
                        else{
                            dataArray.put(0);
                        }
                        if(cbSettingResetDistant.isChecked()){
                            dataArray.put(1);
                        }
                        else{
                            dataArray.put(0);
                        }
                        j.put("data", dataArray);
                        String dataSend = j.toString();
                        byte[] bytes = dataSend.getBytes(Charset.defaultCharset());
                        mConnectedThread.write(bytes);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void initLayout(){

        imgSetup = findViewById(R.id.imgSetup);
        txtBackSetting = findViewById(R.id.txtBackSetting);

        layoutSetup = findViewById(R.id.layoutSetup);
        layoutListDevice = findViewById(R.id.layoutListDevice);
        pgbRefreshListDevice = findViewById(R.id.pgbRefreshListDevice);
        imgMenuListDevice = findViewById(R.id.imgMenuListDevice);
        txtBackListDevice = findViewById(R.id.txtBackListDevice);
        lvListDevice = findViewById(R.id.lvListDevice);
        imgBluetoothConnection = findViewById(R.id.imgBluetoothConnection);
        txtNameBluetoothConnection = findViewById(R.id.txtNameBluetoothConnection);

        //main menu
        btnMainOpenCloseCabinet = findViewById(R.id.btnMainOpenCloseCabinet);
        layoutColorRgb = findViewById(R.id.layoutColorRgb);
        layoutOnOffLed = findViewById(R.id.layoutOnOffLed);
        imgRefreshNameDevice = findViewById(R.id.imgRefreshNameDevice);
        imgRgbColor = findViewById(R.id.imgRgbColor);
        imgRgbColor.setDrawingCacheEnabled(true);
        imgRgbColor.buildDrawingCache(true);
        spNameCabinet = findViewById(R.id.spNameCabinet);
        swOnOffLed = findViewById(R.id.swOnOffLed);
        swOnOffLed.setChecked(true);
        sbAlphaRgb = findViewById(R.id.sbAlphaRgb);
        sbAlphaRgb.setProgress(100);

        //setup layout
        txtBackSetting = findViewById(R.id.txtBackSetting);
        spSettingNameCabinet = findViewById(R.id.spSettingNameCabinet);
        edtSettingNewNameCabinet = findViewById(R.id.edtSettingNewNameCabinet);
        btnSettingMappingNameCabinet = findViewById(R.id.btnSettingMappingNameCabinet);
        edtSetDeviceID = findViewById(R.id.edtSetDeviceID);
        btnSettingSetIdDevice = findViewById(R.id.btnSettingSetIdDevice);
        edtSetDeviceIDStart = findViewById(R.id.edtSetDeviceIDStart);
        edtSetDeviceIDEnd = findViewById(R.id.edtSetDeviceIDEnd);
        btnSettingSetAllDevice = findViewById(R.id.btnSettingSetAllDevice);
        edtSettingTimeReturn = findViewById(R.id.edtSettingTimeReturn);
        edtSettingModeRun = findViewById(R.id.edtSettingModeRun);
        edtSettingDelayPush = findViewById(R.id.edtSettingDelayPush);
        edtSettingMaxValuePush = findViewById(R.id.edtSettingMaxValuePush);
        edtSettingTimeAutoClose = findViewById(R.id.edtSettingTimeAutoClose);
        edtSettingPercentLowIn = findViewById(R.id.edtSettingPercentLowIn);
        edtSettingPercentLowOut = findViewById(R.id.edtSettingPercentLowOut);
        edtSettingMinStopSpeed = findViewById(R.id.edtSettingMinStopSpeed);
        cbSettingResetDistant = findViewById(R.id.cbSettingResetDistant);
        btnSettingSendDataSetup = findViewById(R.id.btnSettingSendDataSetup);


        list.add("No Device");
        //ArrayAdapter spinAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_list, list);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_list);
        spNameCabinet.setAdapter(spinnerAdapter);
        spSettingNameCabinet.setAdapter(spinnerAdapter);

    }

    public void checkTurnOnBluetooth(){
        //--------------------------------------for bluetooth--------------------------------------------------------
        BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
        // Phone does not support Bluetooth so let the user know and exit.
        if (BTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        assert BTAdapter != null;
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }
        //------------------------------------------------------------------------------------------------------
    }

    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED);
    }
    public static boolean isConnected(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            //deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.d(TAG, "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        + MY_UUID_INSECURE );
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;

            // Make a connection to the BluetoothSocket

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();

            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE );
            }

            //will talk about this in the 3rd video
            connected(mmSocket);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }
    private void connected(BluetoothSocket mmSocket) {
        Log.d(TAG, "connected: Starting.");
        pgbRefreshListDevice.setVisibility(View.INVISIBLE);

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();

//        byte[] bytes = "abcd".getBytes(Charset.defaultCharset());
//        mConnectedThread.write(bytes);
        if (mmDevice !=null && isConnected(mmDevice)) {
            String data = "{\"type\":\"get_name\"}";
            byte[] bytes = data.getBytes(Charset.defaultCharset());
            mConnectedThread.write(bytes);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // Stuff that updates the UI

                layoutListDevice.setVisibility(View.INVISIBLE);
                pgbRefreshListDevice.setVisibility(View.INVISIBLE);

                imgBluetoothConnection.setBackgroundResource(R.mipmap.ic_bluetooth_connected);
                txtNameBluetoothConnection.setText(deviceName);

            }
        });


    }
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;


            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream

            int bytes; // bytes returned from read()
            final byte delimiter = 10; //This is the ASCII code for a newline character

            byte[] readBuffer = new byte[1024];;
            int readBufferPosition = 0;
            String incomingMessage = "";
            String[] data = new String[24];
            // Keep listening to the InputStream until an exception occurs
            while (true) {

                // Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    incomingMessage += new String(buffer, 0, bytes);
                    if(incomingMessage.contains("}")){
                        Log.d(TAG, "InputStream: " + incomingMessage);
                        JSONObject reader = new JSONObject(incomingMessage);
                        //all distant
//                        data[0] = reader.getString("1-1");
                        incomingMessage = "";

                        if(reader.has("id_start")) {
                            idStart = reader.getInt("id_start");
                            idEnd = reader.getInt("id_end");
                            JSONArray arrayName= reader.getJSONArray("name");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    edtSetDeviceIDStart.setText(String.valueOf(idStart));
                                    edtSetDeviceIDEnd.setText(String.valueOf(idEnd));
                                    list.clear();
                                    for(int i = 0; i < arrayName.length(); i++){
                                        try {
                                            list.add(arrayName.getString(i));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
//                                        spNameCabinet.setSelection(1);
                                    }
                                }
                            });
                        }
                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
//                    Toast.makeText(MainActivity.this, "K???t n???i th???t b???i", Toast.LENGTH_SHORT).show();
//                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputStream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}