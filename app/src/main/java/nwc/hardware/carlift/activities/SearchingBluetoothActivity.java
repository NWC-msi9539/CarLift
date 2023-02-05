package nwc.hardware.carlift.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nwc.hardware.Adapters.SearchingDeviceAdapter;
import nwc.hardware.Interfaces.OnGattListener;
import nwc.hardware.bletool.BluetoothGeneralTool;
import nwc.hardware.bletool.BluetoothPermissionTool;
import nwc.hardware.bletool.BluetoothSearchingTool;
import nwc.hardware.carlift.R;

public class SearchingBluetoothActivity extends AppCompatActivity {

    private BluetoothSearchingTool bluetoothSearchingTool;
    private BluetoothPermissionTool bluetoothPermissionTool;
    private BluetoothGeneralTool bluetoothGeneralTool;

    private RecyclerView RCC;
    private TextView statusText;
    private TextView connectedDeviceNameText;
    private TextView connectedDeviceAddressText;
    private CardView connectedDeviceInfo;

    private SearchingDeviceAdapter adapter;
    private Timer timer = new Timer();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    private boolean isPermission = false;

    private TimerTask timerTask = new TimerTask() {
        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(!bluetoothSearchingTool.isScanning()){
                        connectedDeviceInfo.setVisibility(View.GONE);
                        if (progressBar.getVisibility() == View.GONE) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        statusText.setText("Discovering devices.");
                        bluetoothSearchingTool.startScan(SearchingBluetoothActivity.this);
                    }
                }
            });
        }

        @Override
        public boolean cancel() {
            return super.cancel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_bluetooth);

        bluetoothSetting();
    }

    private void bluetoothSetting(){
        progressBar = findViewById(R.id.progressBar);
        statusText = findViewById(R.id.statusTXT);
        connectedDeviceNameText = findViewById(R.id.connectedDevice_name);
        connectedDeviceAddressText = findViewById(R.id.connectedDevice_address);
        connectedDeviceInfo = findViewById(R.id.connectedDevice);
        connectedDeviceInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                bluetoothGeneralTool.close();
                timer = new Timer();
                timerTask = new TimerTask() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void run() {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!bluetoothSearchingTool.isScanning()){
                                    connectedDeviceInfo.setVisibility(View.GONE);
                                    if (progressBar.getVisibility() == View.GONE) {
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                    statusText.setText("Discovering devices.");
                                    bluetoothSearchingTool.startScan(SearchingBluetoothActivity.this);
                                }
                            }
                        });
                    }

                    @Override
                    public boolean cancel() {
                        return super.cancel();
                    }
                };
                timer.schedule(timerTask,0,100);
                connectedDeviceInfo.setVisibility(View.GONE);
                return false;
            }
        });
        swipeRefreshLayout = findViewById(R.id.SEARCHING_deviceLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.resetItem();
                timer.cancel();
                timer = new Timer();
                timerTask = new TimerTask() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void run() {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!bluetoothSearchingTool.isScanning()){
                                    if (progressBar.getVisibility() == View.GONE) {
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                    statusText.setText("Discovering devices.");
                                    bluetoothSearchingTool.startScan(SearchingBluetoothActivity.this);
                                }
                            }
                        });
                    }

                    @Override
                    public boolean cancel() {
                        return super.cancel();
                    }
                };
                timer.schedule(timerTask,0,1000);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        List<BluetoothDevice> devices = new ArrayList<>();
        adapter = new SearchingDeviceAdapter(devices, this) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.device_item, viewGroup, false);
                MyViewHolder myViewHolder = new MyViewHolder(linearLayout) {
                    @Override
                    public void setContents() {
                        textViews.put("name", itemView.findViewById(R.id.devicename_txt));
                        textViews.put("address",itemView.findViewById(R.id.devicename_address));

                        itemView.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onClick(View view) {
                                BluetoothDevice device = getItem(getAdapterPosition());
                                AlertDialog.Builder dlg = new AlertDialog.Builder(SearchingBluetoothActivity.this);
                                dlg.setTitle("Connect"); //제목
                                dlg.setMessage(device.getName() + ", Do you want to connect?"); // 메시지
                                //버튼 클릭시 동작
                                dlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Handler handler = new Handler(Looper.getMainLooper());
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        statusText.setText("Connecting.");
                                                        progressBar.setVisibility(View.GONE);
                                                        timer.cancel();
                                                        if(bluetoothSearchingTool.isScanning()) {
                                                            bluetoothSearchingTool.stopScan(SearchingBluetoothActivity.this);
                                                        }
                                                    }
                                                });

                                                bluetoothGeneralTool.connect(device, SearchingBluetoothActivity.this);
                                            }

                                        }
                                );
                                dlg.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        //토스트 메시지
                                        Toast.makeText(getApplicationContext(),"Connection cancelled successfully.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dlg.setCancelable(false);
                                dlg.show();
                            }
                        });
                    }
                };
                return myViewHolder;
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                BluetoothDevice device = getItem(position);
                holder.textViews.get("name").setText("" + device.getName());
                holder.textViews.get("address").setText("" + device.getAddress());
            }
        };

        RCC = findViewById(R.id.deviceRCC);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(RCC.getContext());
        RCC.setLayoutManager(layoutManager1);
        RCC.setAdapter(adapter);

        bluetoothPermissionTool = new BluetoothPermissionTool(this){
            @Override
            public boolean onRequestPermissionScanResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                return super.onRequestPermissionScanResult(requestCode, permissions, grantResults);

            }

            @Override
            public boolean onRequestPermissionLocationResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                boolean result = super.onRequestPermissionLocationResult(requestCode, permissions, grantResults);
                if(result) {
                    isPermission = true;
                    Log.d("TESTING", "Permission ON");
                    connectedDeviceInfo.setVisibility(View.GONE);
                    bluetoothSearchingTool.startScan(SearchingBluetoothActivity.this);
                    timer.schedule(timerTask, 0, 100);
                }
                return result;
            }
        };
        bluetoothSearchingTool = new BluetoothSearchingTool() {
            @SuppressLint("MissingPermission")
            @Override
            public void onScanResult(BluetoothDevice bluetoothDevice) {
                Log.d("TESTING", "FOUND DEVICE ----> " + bluetoothDevice.getName());
                adapter.addDevice(bluetoothDevice);
            }
        };

        bluetoothGeneralTool = BluetoothGeneralTool.getInstance(new OnGattListener() {
            @Override
            public void connectionStateConnecting(BluetoothGatt bluetoothGatt) {
                statusText.setText("Connecting.");
                timer.cancel();
                if(bluetoothSearchingTool.isScanning()) {
                    bluetoothSearchingTool.stopScan(SearchingBluetoothActivity.this);
                }
            }

            @Override
            public void connectionStateConnected(BluetoothGatt bluetoothGatt) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        statusText.setText("Connected.");

                    }
                });
                Handler handler1 = new Handler(Looper.getMainLooper());
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1500);
            }

            @Override
            public void connectionStateDisconnected(BluetoothGatt bluetoothGatt) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        statusText.setText("DisConnected.");
                        timer = new Timer();
                        timerTask = new TimerTask() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void run() {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!bluetoothSearchingTool.isScanning()){
                                            connectedDeviceInfo.setVisibility(View.GONE);
                                            if (progressBar.getVisibility() == View.GONE) {
                                                progressBar.setVisibility(View.VISIBLE);
                                            }
                                            statusText.setText("Discovering devices.");
                                            bluetoothSearchingTool.startScan(SearchingBluetoothActivity.this);
                                        }
                                    }
                                });
                            }

                            @Override
                            public boolean cancel() {
                                return super.cancel();
                            }
                        };
                        timer.schedule(timerTask,0,100);
                        connectedDeviceInfo.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void discoveredServices() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void run() {
                        bluetoothGeneralTool.setMAIN(1, -1);
                        adapter.resetItem();
                        BluetoothDevice device = bluetoothGeneralTool.getGatt().getDevice();
                        connectedDeviceInfo.setVisibility(View.VISIBLE);
                        connectedDeviceNameText.setText(device.getName());
                        connectedDeviceAddressText.setText("(" + device.getAddress() + ")");
                    }
                });
                Log.d("TESTING","연결 됌");
            }

            @Override
            public void characteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
                Log.d("TESTING","(MAINACTIVITY)Write Success !! --> [" + bluetoothGattCharacteristic.getUuid() + "] " + new String(bluetoothGattCharacteristic.getValue()));
            }

            @Override
            public void characteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {

            }

            @Override
            public void characteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {

            }

            @Override
            public void readRssi(BluetoothGatt bluetoothGatt, int rssi, int status) {

            }
        });
            if (bluetoothGeneralTool.getGatt() == null) {
                bluetoothPermissionTool.checkPermission();
            } else {
                statusText.setText("Connected");
                progressBar.setVisibility(View.GONE);
                BluetoothDevice device = bluetoothGeneralTool.getGatt().getDevice();
                connectedDeviceInfo.setVisibility(View.VISIBLE);
                connectedDeviceNameText.setText(device.getName());
                connectedDeviceAddressText.setText("(" + device.getAddress() + ")");
            }
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressBar.setVisibility(View.GONE);
        timer.cancel();
        if(bluetoothSearchingTool.isScanning()) {
            bluetoothSearchingTool.stopScan(SearchingBluetoothActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == BluetoothPermissionTool.SCAN_PERMISSION_CODE){
            if (!((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED))) {
                Toast.makeText(this, "Peripheral navigation permission are required to use Bluetooth.", Toast.LENGTH_SHORT).show();
            } else {
                if (this.checkSelfPermission(BluetoothPermissionTool.LOCATION_permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                        this.checkSelfPermission(BluetoothPermissionTool.LOCATION_permissions[1]) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(BluetoothPermissionTool.LOCATION_permissions, BluetoothPermissionTool.LOCATION_PERMISSION_CODE);
                }
            }
        }else{
            if (!((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED))) {
                Toast.makeText(this, "Location permission are required to use Bluetooth.", Toast.LENGTH_SHORT).show();
            }else{
                isPermission = true;
                Log.d("TESTING", "Permission ON");
                connectedDeviceInfo.setVisibility(View.GONE);
                bluetoothSearchingTool.startScan(SearchingBluetoothActivity.this);
                timer.schedule(timerTask, 0, 100);
            }
        }

    }
}