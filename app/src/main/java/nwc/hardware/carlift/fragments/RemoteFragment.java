package nwc.hardware.carlift.fragments;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

import nwc.hardware.Interfaces.OnGattListener;
import nwc.hardware.bletool.BluetoothGeneralTool;
import nwc.hardware.carlift.R;
import nwc.hardware.carlift.activities.HomeActivity;

public class RemoteFragment extends Fragment {
    private final String TAG = "RemoteFragment";

    private final int release = -1;

    private final int lock = 0;
    private final int up = 1;
    private final int down = 2;
    private final int stop = 3;

    private final byte[] READ_SENSOR1 = { 'R' , 'S' , 'P' , '1', 'E' , 0x0d, 0x0a};
    private final byte[] READ_SENSOR2 = { 'R' , 'S' , 'P' , '2', 'E' , 0x0d, 0x0a};
    private final byte[] READ_SENSOR3 = { 'R' , 'S' , 'P' , '3', 'E' , 0x0d, 0x0a};
    private final byte[] READ_SENSOR4 = { 'R' , 'S' , 'P' , '4', 'E' , 0x0d, 0x0a};

    private final byte[] LIFT_UP = { 'L' , 'U' , 'E' , 0x0d, 0x0a};
    private final byte[] LIFT_LOCK = { 'L' , 'L' , 'E' , 0x0d, 0x0a};
    private final byte[] LIFT_DOWN = { 'L' , 'D' , 'E' , 0x0d, 0x0a};
    private final byte[] LIFT_STOP = { 'L' , 'T' , 'E' , 0x0d, 0x0a};

    private TextView fl_sensor;
    private TextView fl_lockTXT;
    private TextView fr_sensor;
    private TextView fr_lockTXT;
    private TextView rl_sensor;
    private TextView rl_lockTXT;
    private TextView rr_sensor;
    private TextView rr_lockTXT;

    private Button LIFT_upBTN;
    private Button LIFT_lockBTN;
    private Button LIFT_downBTN;

    private BluetoothGeneralTool bluetoothGeneralTool;

    private int status = release;

    private Timer timer;
    private TimerTask timerTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_remote, container, false);

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    if(status == stop){
                        status = release;
                        bluetoothGeneralTool.Write(LIFT_STOP);
                        Thread.sleep(200);
                    }
                    if(status == up){
                        bluetoothGeneralTool.Write(LIFT_UP);
                        Thread.sleep(200);
                    }
                    if(status == down){
                        bluetoothGeneralTool.Write(LIFT_DOWN);
                        Thread.sleep(200);
                    }
                    if(status == lock){
                        bluetoothGeneralTool.Write(LIFT_LOCK);
                        Thread.sleep(200);
                    }
                    bluetoothGeneralTool.Write(READ_SENSOR1);
                    Thread.sleep(200);
                    bluetoothGeneralTool.Write(READ_SENSOR2);
                    Thread.sleep(200);
                    bluetoothGeneralTool.Write(READ_SENSOR3);
                    Thread.sleep(200);
                    bluetoothGeneralTool.Write(READ_SENSOR4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        fl_sensor = v.findViewById(R.id.FL_sensor);
        fl_lockTXT = v.findViewById(R.id.FL_lock);

        fr_sensor = v.findViewById(R.id.FR_sensor);
        fr_lockTXT = v.findViewById(R.id.FR_lock);

        rl_sensor = v.findViewById(R.id.RL_sensor);
        rl_lockTXT = v.findViewById(R.id.RL_lock);

        rr_sensor = v.findViewById(R.id.RR_sensor);
        rr_lockTXT = v.findViewById(R.id.RR_lock);

        bluetoothGeneralTool = BluetoothGeneralTool.getInstance(new OnGattListener() {
            @Override
            public void connectionStateConnecting(BluetoothGatt bluetoothGatt) {

            }

            @Override
            public void connectionStateConnected(BluetoothGatt bluetoothGatt) {

            }

            @Override
            public void connectionStateDisconnected(BluetoothGatt bluetoothGatt) {

            }

            @Override
            public void discoveredServices() {

            }

            @Override
            public void characteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        byte[] datas = bluetoothGattCharacteristic.getValue();
                        String s = "";
                        for(byte b : datas){
                            s += (char)b;
                        }
                        if(HomeActivity.terminalLayout.getVisibility() == View.VISIBLE) {
                            HomeActivity.terminal_logField.setText(HomeActivity.terminal_logField.getText() +
                                    "\bReceive Data\b --------------------------\n" +
                                    s +
                                    "\n");
                        }
                    }
                });
            }

            @Override
            public void characteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {

            }

            @Override
            public void characteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                byte[] datas = bluetoothGattCharacteristic.getValue();
                String s = "";
                for (byte b : datas) {
                    s += (char) b;
                }
                if(datas[0] == 'R'){
                    if (datas[1] == 'S') {
                        switch (datas[2]) {
                            case '1':
                                float fl_value = 0;
                                float fl_lock = 0;

                                for (int i = 3; i < 6; i++) {
                                    float disc = 1;
                                    for (int j = 0; j < (i - 3); j++) {
                                        disc *= 10;
                                    }
                                    disc = 10 / disc;
                                    fl_value += Float.parseFloat(String.valueOf((char) datas[i])) * disc;
                                }

                                fl_lock += Float.parseFloat(String.valueOf((char) datas[6]));

                                fl_sensor.setText(String.format("%.1f",fl_value));
                                fl_lockTXT.setText("" + (int)fl_lock);
                                break;
                            case '2':
                                float fr_value = 0;
                                float fr_lock = 0;

                                for (int i = 3; i < 6; i++) {
                                    float disc = 1;
                                    for (int j = 0; j < (i - 3); j++) {
                                        disc *= 10;
                                    }
                                    disc = 10 / disc;
                                    fr_value += Float.parseFloat(String.valueOf((char) datas[i])) * disc;
                                }

                                fr_lock += Float.parseFloat(String.valueOf((char) datas[6]));

                                fr_sensor.setText(String.format("%.1f",fr_value));
                                fr_lockTXT.setText("" + (int)fr_lock);
                                break;
                            case '3':
                                float rl_value = 0;
                                float rl_lock = 0;

                                for (int i = 3; i < 6; i++) {
                                    float disc = 1;
                                    for (int j = 0; j < (i - 3); j++) {
                                        disc *= 10;
                                    }
                                    disc = 10 / disc;
                                    rl_value += Float.parseFloat(String.valueOf((char) datas[i])) * disc;
                                }

                                rl_lock += Float.parseFloat(String.valueOf((char) datas[6]));

                                rl_sensor.setText(String.format("%.1f",rl_value));
                                rl_lockTXT.setText("" + (int)rl_lock);
                                break;
                            case '4':
                                float rr_value = 0;
                                float rr_lock = 0;

                                for (int i = 3; i < 6; i++) {
                                    float disc = 1;
                                    for (int j = 0; j < (i - 3); j++) {
                                        disc *= 10;
                                    }
                                    disc = 10 / disc;
                                    rr_value += Float.parseFloat(String.valueOf((char) datas[i])) * disc;
                                }

                                rr_lock += Float.parseFloat(String.valueOf((char) datas[6]));

                                rr_sensor.setText(String.format("%.1f",rr_value));
                                rr_lockTXT.setText("" + (int)rr_lock);
                                break;
                            default:
                                break;
                        }
                    }
                }
                Log.d(TAG,"Read Data -> " + s);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        byte[] datas = bluetoothGattCharacteristic.getValue();
                        String s = "";
                        for(byte b : datas){
                            s += (char)b;
                        }

                        HomeActivity.terminal_logField.setText(HomeActivity.terminal_logField.getText() +
                                "\bReceive Data\b --------------------------\n" +
                                s +
                                "\n");
                    }
                });
            }

            @Override
            public void readRssi(BluetoothGatt bluetoothGatt, int i, int i1) {

            }
        });

        LIFT_upBTN = v.findViewById(R.id.Remote_upBTN);
        LIFT_upBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "EVENT --> DOWN");
                        status = up;
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "EVENT --> UP");
                        status = stop;
                        break;
                }

                return false;
            }
        });

        LIFT_lockBTN = v.findViewById(R.id.Remote_lockBTN);
        LIFT_lockBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "EVENT --> DOWN");
                        status = lock;
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "EVENT --> UP");
                        status = stop;
                        break;
                }
                return false;
            }
        });


        LIFT_downBTN = v.findViewById(R.id.Remote_downBTN);
        LIFT_downBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "EVENT --> DOWN");
                        status = down;
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "EVENT --> UP");
                        status = stop;
                        break;
                }
                return false;
            }
        });

        timer.schedule(timerTask, 0, 1000);

        return v;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}