package nwc.hardware.carlift.fragments;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nwc.hardware.Interfaces.OnGattListener;
import nwc.hardware.bletool.BluetoothGeneralTool;
import nwc.hardware.carlift.R;
import nwc.hardware.carlift.activities.HomeActivity;
import nwc.hardware.carlift.activities.SearchingBluetoothActivity;
import nwc.hardware.carlift.datas.Data;
import nwc.hardware.carlift.datas.DataItemAdapter;

public class SettingFragment extends Fragment {
    private final byte[] Read_Packet1 = {'R','D','P','1','E',0x0d, 0x0a};
    private final byte[] Read_Packet2 = {'R','D','P','2','E',0x0d, 0x0a};
    private final byte[] Read_Packet3 = {'R','D','P','3','E',0x0d, 0x0a};
    private final byte[] Read_Packet4 = {'R','D','P','4','E',0x0d, 0x0a};

    private final String TAG = "SettingFragment";
    private RecyclerView dataField;
    private DataItemAdapter adapter;
    private BluetoothGeneralTool bluetoothGeneralTool;
    private List<Data> dataList;

    private Button readBTN;
    private Button writeBTN;

    private TextView notice;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setting, container, false);

        bluetoothGeneralTool = BluetoothGeneralTool.getInstance(new OnGattListener() {
            @Override
            public void connectionStateConnecting(BluetoothGatt bluetoothGatt) {

            }

            @Override
            public void connectionStateConnected(BluetoothGatt bluetoothGatt) {

            }

            @Override
            public void connectionStateDisconnected(BluetoothGatt bluetoothGatt) {
                HomeActivity.setIsConnect(false);
                Log.d(TAG,"Disconnected!!");
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

                        HomeActivity.terminal_logField.setText(HomeActivity.terminal_logField.getText() +
                                "\bReceive Data\b --------------------------\n" +
                                s +
                                "\n");
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
                for(byte b : datas){
                    s += (char)b;
                }
                Log.d(TAG, "DATA ==> " + s);
                if(datas[0] == 'P'){
                    switch (datas[1]){
                        case '1':
                            float sensor_display = 0;
                            float usage_counter = 0;
                            float usage_day = 0;
                            float service_interval = 0;
                            float sync_interval_time = 0;

                            sensor_display += Float.parseFloat(String.valueOf((char)datas[2]));

                            for(int i=3; i<8; i++){
                                int disc = 1;
                                for(int j=0; j<(i-3); j++){
                                    disc *= 10;
                                }
                                disc = 10000 / disc;
                                usage_counter +=  Float.parseFloat(String.valueOf((char)datas[i])) * disc;
                            }

                            for(int i=8; i<13; i++){
                                int disc = 1;
                                for(int j=0; j<(i-8); j++){
                                    disc *= 10;
                                }
                                disc = 10000 / disc;
                                usage_day += Float.parseFloat(String.valueOf((char)datas[i])) * disc;
                            }

                            service_interval += Float.parseFloat(String.valueOf((char)datas[13]));
                            sync_interval_time += Float.parseFloat(String.valueOf((char)datas[14]));

                            dataList.get(0).setValue(sensor_display);
                            dataList.get(1).setValue(service_interval);
                            dataList.get(2).setValue(sync_interval_time);

                            bluetoothGeneralTool.Write(Read_Packet2);
                            break;
                        case '2':
                            float fr_sync_interval = 0;
                            float second_stage = 0;
                            float lr_sync_interval = 0;
                            float angle_sensor = 0;
                            float photo_sensor = 0;

                            fr_sync_interval += Float.parseFloat(String.valueOf((char)datas[2]));
                            second_stage += Float.parseFloat(String.valueOf((char)datas[3]));
                            lr_sync_interval += Float.parseFloat(String.valueOf((char)datas[4]));
                            angle_sensor += Float.parseFloat(String.valueOf((char)datas[5]));
                            photo_sensor += Float.parseFloat(String.valueOf((char)datas[6]));

                            dataList.get(3).setValue(fr_sync_interval);
                            dataList.get(4).setValue(second_stage);
                            dataList.get(5).setValue(lr_sync_interval);
                            dataList.get(6).setValue(angle_sensor);
                            dataList.get(7).setValue(photo_sensor);

                            bluetoothGeneralTool.Write(Read_Packet3);
                            break;
                        case '3':
                            float lock_sensor = 0;
                            float sensor_limit = 0;
                            float remote_control = 0;
                            float fl_offset_value = 0;
                            float fr_offset_value = 0;

                            lock_sensor += Float.parseFloat(String.valueOf((char)datas[2]));

                            for(int i=3; i<8; i++){
                                int disc = 1;
                                for(int j=0; j<(i-3); j++){
                                    disc *= 10;
                                }
                                disc = 10 / disc;
                                sensor_limit +=  Float.parseFloat(String.valueOf((char)datas[i])) * disc;
                            }

                            remote_control += Float.parseFloat(String.valueOf((char)datas[6]));
                            fl_offset_value += Float.parseFloat(String.valueOf((char)datas[7]));
                            fr_offset_value += Float.parseFloat(String.valueOf((char)datas[8]));

                            dataList.get(8).setValue(lock_sensor);
                            dataList.get(9).setValue(sensor_limit);
                            dataList.get(10).setValue(remote_control);
                            dataList.get(11).setValue(fl_offset_value);
                            dataList.get(12).setValue(fr_offset_value);

                            bluetoothGeneralTool.Write(Read_Packet4);
                            break;
                        case '4':
                            float rl_offset_value = 0;
                            float rr_offset_value = 0;
                            float bottom_set_value = 0;
                            float spoiler_value = 0;

                            rl_offset_value += Float.parseFloat(String.valueOf((char)datas[2]));
                            rr_offset_value += Float.parseFloat(String.valueOf((char)datas[3]));
                            for(int i=4; i<6; i++){
                                int disc = 1;
                                for(int j=0; j<(i-2); j++){
                                    disc *= 10;
                                }
                                disc = 10 / disc;
                                bottom_set_value +=  Float.parseFloat(String.valueOf((char)datas[i])) * disc;
                            }
                            spoiler_value += Float.parseFloat(String.valueOf((char)datas[5]));

                            dataList.get(13).setValue(rl_offset_value);
                            dataList.get(14).setValue(rr_offset_value);
                            dataList.get(15).setValue(bottom_set_value);
                            dataList.get(16).setValue(spoiler_value);

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dataField.setVisibility(View.VISIBLE);
                                    notice.setVisibility(View.GONE);
                                    writeBTN.setEnabled(true);
                                    Toast.makeText(getContext(), "Read Complete.", Toast.LENGTH_SHORT).show();
                                    HomeActivity.progressOff();
                                }
                            });

                            break;
                        default:
                            break;
                    }
                }

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

        initailize();

        dataField = v.findViewById(R.id.dataField_RCC);
        dataField.setHasFixedSize(true);
        dataField.setLayoutManager(new LinearLayoutManager(v.getContext()));
        dataField.setAdapter(adapter);

        readBTN = v.findViewById(R.id.readBTN);
        readBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.progressOn("Loading...");
                bluetoothGeneralTool.Write(Read_Packet1);
            }
        });

        writeBTN = v.findViewById(R.id.writeBTN);
        writeBTN.setOnClickListener(new View.OnClickListener() {
            int count = 0;
            @Override
            public void onClick(View v) {
                HomeActivity.progressOn("Writing...");
                dataList = adapter.getItem();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DataWriting(1);
                            Thread.sleep(500);
                            DataWriting(2);
                            Thread.sleep(500);
                            DataWriting(3);
                            Thread.sleep(500);
                            DataWriting(4);
                            Thread.sleep(500);
                            HomeActivity.progressOff();
                            Toast.makeText(getContext(), "Write Complete.", Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            public void DataWriting(int index){
                StringBuilder builder = new StringBuilder();
                if(index == 1){
                    builder.append('W').append('P').append(index)
                            .append((int)dataList.get(count++).getValue())
                            .append('0').append('0').append('0').append('0').append('0')
                            .append('0').append('0').append('0').append('0').append('0')
                            .append((int)dataList.get(count++).getValue())
                            .append((int)dataList.get(count++).getValue())
                            .append('E').append((char)0x0d).append((char)0x0a);
                }else if(index == 2){
                    builder.append('W').append('P').append(index)
                            .append((int)dataList.get(count++).getValue())
                            .append((int)dataList.get(count++).getValue())
                            .append((int)dataList.get(count++).getValue())
                            .append((int)dataList.get(count++).getValue())
                            .append((int)dataList.get(count++).getValue())
                            .append('E').append((char)0x0d).append((char)0x0a);
                }else if(index == 3){
                    builder.append('W').append('P').append(index)
                            .append((int)dataList.get(count++).getValue());

                    String temp = String.valueOf(dataList.get(count++).getValue());
                    builder.append(temp.charAt(0)).append(temp.charAt(1)).append(temp.charAt(3))
                            .append((int)dataList.get(count++).getValue())
                            .append((int)dataList.get(count++).getValue())
                            .append((int)dataList.get(count++).getValue())
                            .append('E').append((char)0x0d).append((char)0x0a);
                }else if(index == 4){
                    builder.append('W').append('P').append(index)
                            .append((int)dataList.get(count++).getValue())
                            .append((int)dataList.get(count++).getValue());
                    String temp = String.format("%02d", (int)dataList.get(count++).getValue());
                    builder.append(temp)
                            .append((int)dataList.get(count++).getValue())
                            .append('0')
                            .append('E').append((char)0x0d).append((char)0x0a);

                    count = 0;
                }

                String temp = builder.toString();


                byte[] Write = new byte[temp.length()];
                for(int i=0; i<builder.length(); i++){
                    Write[i] = (byte)temp.charAt(i);
                }

                bluetoothGeneralTool.Write(Write);
            }
        });
        notice = v.findViewById(R.id.notice);

        return v;
    }

    public void initailize(){
        dataList = new ArrayList<>();
        dataList.add(new Data(Data.TYPE_SENSOR_DISPLAY));
        dataList.add(new Data(Data.TYPE_SERVICE_INTERVAL));
        dataList.add(new Data(Data.TYPE_SYNC_INTERVAL_TIME));
        dataList.add(new Data(Data.TYPE_FR_SYNC_INTERVAL));
        dataList.add(new Data(Data.TYPE_2ND_STAGE));
        dataList.add(new Data(Data.TYPE_LR_SYNC_INTERVAL));
        dataList.add(new Data(Data.TYPE_ANGLE_SENSOR));
        dataList.add(new Data(Data.TYPE_PHOTO_SENSOR));
        dataList.add(new Data(Data.TYPE_LOCK_SENSOR));
        dataList.add(new Data(Data.TYPE_SENSOR_LIMIT));
        dataList.add(new Data(Data.TYPE_REMOTE_CONTROL));
        dataList.add(new Data(Data.TYPE_FL_OFFSET_VALUE));
        dataList.add(new Data(Data.TYPE_FR_OFFSET_VALUE));
        dataList.add(new Data(Data.TYPE_RL_OFFSET_VALUE));
        dataList.add(new Data(Data.TYPE_RR_OFFSET_VALUE));
        dataList.add(new Data(Data.TYPE_BOTTOM_SET_VALUE));
        dataList.add(new Data(Data.TYPE_SPOILER_VALUE));

        adapter = new DataItemAdapter(dataList, getContext());
    }
}