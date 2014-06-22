package dz.cerist.android.mowafir;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class Mowafir extends Activity {
    private static final String TAG = "mowafir";

    StringBuilder sb = new StringBuilder();

    Button btnOn, btnOff;
    TextView txtArduino;

    Handler handler;

    private BluetoothService mbluetooth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        btnOn = (Button) findViewById(R.id.btnOn);					// button LED ON
        btnOff = (Button) findViewById(R.id.btnOff);				// button LED OFF
        txtArduino = (TextView) findViewById(R.id.txtArduino);		// for display the received data from the Arduino

        mbluetooth = new BluetoothService();
        checkBluetoothState();

        initHandler();
        mbluetooth.setHandler(handler);;

        btnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //btnOn.setEnabled(false);
                mbluetooth.Write("1");	// Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //btnOff.setEnabled(false);
                mbluetooth.Write("0");	// Send "0" via Bluetooth
                //Toast.makeText(getBaseContext(), "Turn off LED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initHandler(){
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case BluetoothService.RECIEVE_MESSAGE:													// if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);					// create string from bytes array

                        sb.append(strIncom);												// append string
                        int endOfLineIndex = sb.indexOf("\r\n");							// determine the end-of-line
                        if (endOfLineIndex > 0) { 											// if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);				// extract string
                            sb.delete(0, sb.length());										// and clear
                            txtArduino.setText("Data from Arduino: " + sbprint); 	        // update TextView
                            //btnOff.setEnabled(true);
                            //btnOn.setEnabled(true);
                        }
                        break;
                }
            }
        };
    }

    private void checkBluetoothState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(mbluetooth.getAdapter() ==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (mbluetooth.getAdapter().isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            mbluetooth.Resume();
        } catch (IOException e) {
            try {
                mbluetooth.CloseSocket();
            } catch (IOException e2) {
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try     {
            mbluetooth.CloseSocket();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }
    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }
}