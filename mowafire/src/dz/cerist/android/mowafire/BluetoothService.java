package dz.cerist.android.mowafire;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;


public class BluetoothService  implements SmartSocket{

    private BluetoothAdapter mAdapter = null;

    private BluetoothSocket mSocket = null;

    private Handler handler = null;

    private ConnectedThread mConnectedThread = null;

    private final static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private String address = "20:13:05:02:00:13";

    private Boolean status = Boolean.FALSE;

    public BluetoothService() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();		// get Bluetooth adapter
        try {
            Connect_Bluetooth();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(BluetoothAdapter mbtAdapter) {
        this.mAdapter = mbtAdapter;
    }

    public BluetoothSocket getSocket() {
        return mSocket;
    }

    public void setSocket(BluetoothSocket mSocket) {
        this.mSocket = mSocket;
    }


    public void Write(String message){
        mConnectedThread.write(message);
    }

    public void Close_Bluetooth() throws IOException{
        getSocket().close();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {

            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    public void Connect_Bluetooth() throws  IOException{
        BluetoothDevice device = mAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        mSocket = createBluetoothSocket(device);

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        mAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        mSocket.connect();

        mConnectedThread = new ConnectedThread(mSocket);
        mConnectedThread.start();
    }

    @Override
    public Boolean getStatus() {
        return status;
    }

    @Override
    public void setStatus(Boolean status) {
        String msg = status ? "1" : "0";
        Write(msg);
        this.status = status;
    }

    @Override
    public void On() {
        setStatus(Boolean.TRUE);
    }

    @Override
    public void Off() {
        setStatus(Boolean.FALSE);
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void Connect(){
        try {
            Connect_Bluetooth();
        }catch (IOException e){

        }
    }

    @Override
    public void Close() {
        try {
            Close_Bluetooth();
        }catch (IOException e){

        }
    }

    /*TODO
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
    */
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        StringBuilder sb = new StringBuilder();

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);		// Get number of bytes and message in "buffer"

                    byte[] readBuf = buffer;

                    String strIncom = new String(readBuf, 0, bytes);					// create string from bytes array

                    sb.append(strIncom);												// append string
                    int endOfLineIndex = sb.indexOf("\r\n");							// determine the end-of-line
                    if (endOfLineIndex > 0) { 											// if end-of-line,
                        String sbprint = sb.substring(0, endOfLineIndex);				// extract string
                        sb.delete(0, sb.length());										// and clear
                        handler.obtainMessage(SmartSocket.RECIEVE_MESSAGE, sbprint).sendToTarget();
                        handler.obtainMessage(SmartSocket.BT_RECIEVE_MESSAGE, sbprint).sendToTarget();
                    }

                    //handler.obtainMessage(SmartSocket.RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();		// Send to message queue Handler

                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
            }
        }
    }

    }
