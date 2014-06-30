package dz.cerist.android.mowafire;


import android.os.Handler;

public interface SmartSocket {
    public final static int RECIEVE_MESSAGE = 1;
    public final static int WS_RECIEVE_MESSAGE = 2;
    public final static int BT_RECIEVE_MESSAGE = 3;

    public Boolean getStatus();
    public void setStatus(Boolean status);
    public void On();
    public void Off();
    public void setHandler(Handler handler);
    public void Connect();
    public void Close();
}
