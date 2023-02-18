package nwc.hardware.carlift.fragments;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageButton;

import java.util.Timer;
import java.util.TimerTask;

import nwc.hardware.Interfaces.OnGattListener;
import nwc.hardware.bletool.BluetoothGeneralTool;
import nwc.hardware.carlift.R;
import nwc.hardware.carlift.activities.HomeActivity;

public class AutoFragment extends Fragment {

    private ImageButton service_BTN;
    private ImageButton wheel_BTN;
    private ImageButton bottom_BTN;
    private ImageButton spoiler_BTN;

    private BluetoothGeneralTool bluetoothGeneralTool;

    private final byte[] AUTO_SERVICE_POSITION = { 'S' , 'A' , '1', 'E' , 0x0d, 0x0a};
    private final byte[] AUTO_WHEEL_POSITION = { 'S' , 'A' , '2', 'E' , 0x0d, 0x0a};
    private final byte[] AUTO_BOTTOM_POSITION = { 'S' , 'A' , '3', 'E' , 0x0d, 0x0a};
    private final byte[] AUTO_SPOILER = { 'S' , 'A' , '4', 'E' , 0x0d, 0x0a};

    private final byte[] AUTO_STOP = { 'L', 'T', 'E', 0x0d, 0x0a };

    private final int RELEASE = -1;
    private final int SERVICE_POSITION = 0;
    private final int WHEEL_POSITION = 1;
    private final int BOTTOM_POSITION = 2;
    private final int SPOILER = 3;
    private final int STOP = 4;

    private int type = -1;

    private Timer timer;
    private TimerTask timerTask;

    private Drawable service_position_Enable;
    private Drawable service_position_Disable;
    private Drawable wheel_postion_Enable;
    private Drawable wheel_postion_Disable;
    private Drawable bottom_position_Enable;
    private Drawable bottom_position_Disable;
    private Drawable spoiler_Enable;
    private Drawable spoiler_Disable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_auto, container, false);

        service_position_Enable = getContext().getDrawable(R.drawable.auto_2);
        service_position_Disable = getContext().getDrawable(R.drawable.auto_1);
        wheel_postion_Enable = getContext().getDrawable(R.drawable.auto_wheel_2);
        wheel_postion_Disable = getContext().getDrawable(R.drawable.auto_wheel_1);
        bottom_position_Enable = getContext().getDrawable(R.drawable.auto_bottom_2);
        bottom_position_Disable = getContext().getDrawable(R.drawable.auto_bottom_1);
        spoiler_Enable = getContext().getDrawable(R.drawable.auto_spoiler_2);
        spoiler_Disable = getContext().getDrawable(R.drawable.auto_spoiler_1);

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
                if(HomeActivity.terminalLayout.getVisibility() == View.VISIBLE) {
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
            }

            @Override
            public void characteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {

            }

            @Override
            public void characteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {

            }

            @Override
            public void readRssi(BluetoothGatt bluetoothGatt, int i, int i1) {

            }
        });

        service_BTN = v.findViewById(R.id.Auto_SP_BTN);
        service_BTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(type == RELEASE){
                            service_BTN.setImageDrawable(service_position_Enable);
                            type = SERVICE_POSITION;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        service_BTN.setImageDrawable(service_position_Disable);
                        type = STOP;
                        break;
                }

                return false;
            }
        });

        wheel_BTN = v.findViewById(R.id.Auto_WP_BTN);
        wheel_BTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(type == RELEASE){
                            wheel_BTN.setImageDrawable(wheel_postion_Enable);
                            type = WHEEL_POSITION;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        wheel_BTN.setImageDrawable(wheel_postion_Disable);
                        type = STOP;
                        break;
                }

                return false;
            }
        });

        bottom_BTN = v.findViewById(R.id.Auto_BP_BTN);
        bottom_BTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(type == RELEASE){
                            bottom_BTN.setImageDrawable(bottom_position_Enable);
                            type = BOTTOM_POSITION;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        bottom_BTN.setImageDrawable(bottom_position_Disable);
                        type = STOP;
                        break;
                }

                return false;
            }
        });

        spoiler_BTN = v.findViewById(R.id.Auto_spoiler_BTN);
        spoiler_BTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(type == RELEASE) {
                            spoiler_BTN.setImageDrawable(spoiler_Enable);
                            type = SPOILER;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        spoiler_BTN.setImageDrawable(spoiler_Disable);
                        type = STOP;
                        break;
                }

                return false;
            }
        });

        SetTimer();

        return v;
    }

    private void SetTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(type == STOP){
                    bluetoothGeneralTool.Write(AUTO_STOP);
                    type = RELEASE;
                }
                if(type == SERVICE_POSITION){
                    bluetoothGeneralTool.Write(AUTO_SERVICE_POSITION);
                }
                if(type == WHEEL_POSITION){
                    bluetoothGeneralTool.Write(AUTO_WHEEL_POSITION);
                }
                if(type == BOTTOM_POSITION){
                    bluetoothGeneralTool.Write(AUTO_BOTTOM_POSITION);
                }
                if(type == SPOILER){
                    bluetoothGeneralTool.Write(AUTO_SPOILER);
                }
            }
        };

        timer.schedule(timerTask, 0, 100);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}