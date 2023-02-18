package nwc.hardware.carlift.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import nwc.hardware.Interfaces.OnGattListener;
import nwc.hardware.bletool.BluetoothGeneralTool;
import nwc.hardware.bletool.BluetoothSearchingTool;
import nwc.hardware.carlift.R;
import nwc.hardware.carlift.fragments.AutoFragment;
import nwc.hardware.carlift.fragments.RemoteFragment;
import nwc.hardware.carlift.fragments.SettingFragment;
import nwc.hardware.carlift.fragments.SingleFragment;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = "HomeActivity";

    private Drawable settingIMG_Enable;
    private Drawable settingIMG_disable;
    private Drawable remoteIMG_Enable;
    private Drawable remoteIMG_disable;
    private Drawable singleIMG_Enable;
    private Drawable singleIMG_disable;
    private Drawable autoIMG_Enable;
    private Drawable autoIMG_disable;

    private SettingFragment settingFragment = new SettingFragment();
    private RemoteFragment remoteFragment = new RemoteFragment();
    private SingleFragment singleFragment = new SingleFragment();
    private AutoFragment autoFragment = new AutoFragment();

    private ImageButton settingBTN;
    private ImageButton remoteBTN;
    private ImageButton independent_BTN;
    private ImageButton auto_BTN;
    private BluetoothGeneralTool bluetoothGeneralTool;
    private TextView titleTXT;

    private static ConstraintLayout progressLayout;
    private static TextView progressMsg;

    public static boolean isConnect = true;

    /**
     * 터미널 데이터
    **/
    public static ConstraintLayout terminalLayout;

    private EditText terminal_dataField;
    private Button terminal_sendBTN;
    private ImageButton terminal_deleteBTN;
    public static TextView terminal_logField;
    /**
     * 터미널 데이터
     **/

    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(!isConnect){
                isConnect = true;
                timer.cancel();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothGeneralTool.getInstance().close();

                        AlertDialog.Builder dlg = new AlertDialog.Builder(HomeActivity.this);
                        dlg.setTitle("Disconnected");
                        dlg.setMessage("Do you want to rediscover device?");

                        dlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(getApplicationContext(), SearchingBluetoothActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }

                                }
                        );
                        dlg.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                //토스트 메시지
                                finish();
                            }
                        });
                        dlg.setCancelable(false);
                        dlg.show();
                    }
                });

            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bluetoothGeneralTool = BluetoothGeneralTool.getInstance();

        terminalSet();

        titleTXT = findViewById(R.id.titleTXT);

        progressLayout = findViewById(R.id.progressLayout);
        progressMsg = findViewById(R.id.progressMsg);
        progressLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        settingBTN = findViewById(R.id.setting_BTN);
        settingBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleTXT.setText("VALUES SETTING");
                settingBTN.setImageDrawable(settingIMG_Enable);
                remoteBTN.setImageDrawable(remoteIMG_disable);
                independent_BTN.setImageDrawable(singleIMG_disable);
                auto_BTN.setImageDrawable(autoIMG_disable);
                changeFragment(0);
            }
        });
        settingBTN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                terminalLayout.setVisibility(View.VISIBLE);
                return true;
            }
        });

        remoteBTN = findViewById(R.id.remote_BTN);
        remoteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleTXT.setText("REMOTE CONTROL");
                settingBTN.setImageDrawable(settingIMG_disable);
                remoteBTN.setImageDrawable(remoteIMG_Enable);
                independent_BTN.setImageDrawable(singleIMG_disable);
                auto_BTN.setImageDrawable(autoIMG_disable);
                changeFragment(1);
            }
        });

        independent_BTN = findViewById(R.id.single_BTN);
        independent_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleTXT.setText("INDIVIDUAL CONTROL");
                settingBTN.setImageDrawable(settingIMG_disable);
                remoteBTN.setImageDrawable(remoteIMG_disable);
                independent_BTN.setImageDrawable(singleIMG_Enable);
                auto_BTN.setImageDrawable(autoIMG_disable);
                changeFragment(2);
            }
        });

        auto_BTN = findViewById(R.id.auto_BTN);
        auto_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleTXT.setText("AUTO POSITION");
                settingBTN.setImageDrawable(settingIMG_disable);
                remoteBTN.setImageDrawable(remoteIMG_disable);
                independent_BTN.setImageDrawable(singleIMG_disable);
                auto_BTN.setImageDrawable(autoIMG_Enable);
                changeFragment(3);
            }
        });

        settingIMG_Enable = getDrawable(R.drawable.b_value_button_2);
        settingIMG_disable = getDrawable(R.drawable.b_value_button_1);
        remoteIMG_Enable = getDrawable(R.drawable.b_remote_button_2);
        remoteIMG_disable = getDrawable(R.drawable.b_remote_button_1);
        singleIMG_Enable = getDrawable(R.drawable.b_indivisual_button_2);
        singleIMG_disable = getDrawable(R.drawable.b_indivisual_button_1);
        autoIMG_Enable = getDrawable(R.drawable.b_auto_button_2);
        autoIMG_disable = getDrawable(R.drawable.b_auto_button_1);

        settingBTN.callOnClick();

        timer.schedule(timerTask, 0, 250);
    }

    private void changeFragment(int selectIndex){
        switch (selectIndex){
            case 0:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.homeFrame, settingFragment).commitAllowingStateLoss();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.homeFrame, remoteFragment).commitAllowingStateLoss();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.homeFrame, singleFragment).commitAllowingStateLoss();
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.homeFrame, autoFragment).commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    public static void progressOn(String message){
        progressLayout.setVisibility(View.VISIBLE);
        progressMsg.setText(message);
    }

    public static void progressOff(){
        progressLayout.setVisibility(View.GONE);
    }

    public static void setIsConnect(boolean value){
        isConnect = value;
    }

    @Override
    public void onBackPressed() {
        if(terminalLayout == null){
            finish();
        }else{
            if(terminalLayout.getVisibility() == View.VISIBLE){
                terminalLayout.setVisibility(View.GONE);
            }else{
                finish();
            }
        }
    }

    public void terminalSet(){
        terminalLayout = findViewById(R.id.terminalLayout);
        terminalLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        terminal_sendBTN = findViewById(R.id.TERMINAL_sendBTN);
        terminal_sendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String packet = terminal_dataField.getText().toString();
                if(packet.isEmpty()){
                    Toast.makeText(getApplicationContext(), "보낼 데이터를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    byte[] datas = new byte[packet.length() + 2];
                    for(int i=0; i<datas.length - 2; i++){
                        datas[i] = (byte)packet.charAt(i);
                    }

                    datas[datas.length - 2] = 0x0d;
                    datas[datas.length - 1] = 0x0a;

                    bluetoothGeneralTool.Write(datas);
                    terminal_dataField.setText("");
                    terminal_logField.setText(terminal_logField.getText() + "\bSend Data\b --> " + packet + "[0x0d0x0a]" + "\n");
                }
            }
        });

        terminal_deleteBTN = findViewById(R.id.TERMINAL_logDeleteBTN);
        terminal_deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminal_logField.setText("");
            }
        });

        terminal_dataField = findViewById(R.id.TERMINAL_dataField);
        terminal_logField = findViewById(R.id.TERMINAL_LogField);
        terminal_logField.setMaxLines(1000);
    }
}