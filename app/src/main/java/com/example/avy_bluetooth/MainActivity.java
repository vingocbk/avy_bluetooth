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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static android.R.layout.simple_list_item_1;
import static android.R.layout.simple_spinner_dropdown_item;

public class MainActivity extends AppCompatActivity {
    ImageView imgSetup, imgMenuListDevice, imgBluetoothConnection;
    View layoutSetup, layoutListDevice;
    TextView txtBackSetting, txtBackListDevice, txtNameBluetoothConnection;

    ProgressBar pgbRefreshListDevice;
    ListView lvListDevice;
    //Main Control
    Button btnMainOpenCloseCabinet;
    RelativeLayout layoutColorRgb, layoutOnOffLed;


    ArrayAdapter<String> arrayAdapterListDevice;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); // hide the title bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_main);

        anhXa();

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


        //-------------------------------------------------------------------
        imgSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutSetup.setVisibility(View.VISIBLE);
                layoutColorRgb.setVisibility(View.GONE);
                layoutOnOffLed.setVisibility(View.GONE);
                btnMainOpenCloseCabinet.setVisibility(View.GONE);
                if (mmDevice !=null && isConnected(mmDevice)) {
                    String data = "{\"type\":\"get_status\",\"name\":\"\"}";
                    byte[] bytes = data.getBytes(Charset.defaultCharset());
                    mConnectedThread.write(bytes);
                    layoutSetup.setVisibility(View.VISIBLE);
                }
            }
        });

        txtBackSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btnControl.setVisibility(View.VISIBLE);
                layoutSetup.setVisibility(View.GONE);
                btnMainOpenCloseCabinet.setVisibility(View.VISIBLE);
                layoutColorRgb.setVisibility(View.VISIBLE);
                layoutOnOffLed.setVisibility(View.VISIBLE);
            }
        });
        //------------------------------------------------------------------

        imgMenuListDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutListDevice.setVisibility(View.VISIBLE);
//                imgRefreshListDevice.setVisibility(View.VISIBLE);
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                List<String> s = new ArrayList<String>();
//                s.add("---Thiết bị đã ghép đôi---");
                for(BluetoothDevice bt : pairedDevices){
                    s.add(bt.getName() + "\n" + bt.getAddress());
                }
                ObjectBluetooth = pairedDevices.toArray();
//                s.add("---Thiết bị hiện có---");
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
    }

    public void anhXa(){

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
            String data[] = new String[24];
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
                        data[0] = reader.getString("1-1");
                        incomingMessage = "";
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //-------
//                            txtViewDistantMotor1.setText(data[0]);
                        }
                    });


                } catch (IOException | JSONException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
//                    Toast.makeText(MainActivity.this, "Kết nối thất bại", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
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