package dz.cerist.android.mowafire;

import android.os.Handler;
import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketService implements SmartSocket {
    private WebSocketClient mWebSocketClient;
    private String ws_server_address = "ws://10.0.1.32:8888";
    //private String ws_server_address = "ws://10.0.2.2:8888";
    private Boolean status = Boolean.FALSE;
    private Handler handler = null;


    public WebSocketService() {
        connectWebSocket();
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(ws_server_address);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                //mWebSocketClient.send("open");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                handler.obtainMessage(SmartSocket.RECIEVE_MESSAGE, message).sendToTarget();
                handler.obtainMessage(SmartSocket.WS_RECIEVE_MESSAGE, message).sendToTarget();
                status = Boolean.TRUE;
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    private void sendMessage(String msg) {
        mWebSocketClient.send(msg);
    }

    @Override
    public Boolean getStatus() {
        return null;
    }

    @Override
    public void setStatus(Boolean status) {
        String msg = status ? "1" : "0";
        sendMessage(msg);
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
    public void Connect() {

    }

    @Override
    public void Close() {

    }

}
