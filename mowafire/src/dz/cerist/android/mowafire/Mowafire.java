package dz.cerist.android.mowafire;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Mowafire extends Activity {
    private static final String TAG = "mowafire";


    Button btnOn, btnOff;
    TextView txtArduino;

    Handler handler;

    private SmartSocket mSmartSocket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        btnOn = (Button) findViewById(R.id.btnOn);					// button LED ON
        btnOff = (Button) findViewById(R.id.btnOff);				// button LED OFF
        txtArduino = (TextView) findViewById(R.id.txtArduino);		// for display the received data from the Arduino

        mSmartSocket = new WebSocketService();

        initHandler();
        mSmartSocket.setHandler(handler);;

        btnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //btnOn.setEnabled(false);
                mSmartSocket.On();	// Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //btnOff.setEnabled(false);
                mSmartSocket.Off();	// Send "0" via Bluetooth
                //Toast.makeText(getBaseContext(), "Turn off LED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initHandler(){
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case SmartSocket.RECIEVE_MESSAGE:													// if receive massage
                        String message = (String)msg.obj;
                        txtArduino.setText("Energy Consumption from smart socket: " + message);
                        /*
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
                        */

                        break;

                }
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
            mSmartSocket.Connect();
            //TODO Handle the exceptions

            //mSmartSocket.Close();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        //TODO Handle the exceptions
        mSmartSocket.Close();
    }
    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }
}