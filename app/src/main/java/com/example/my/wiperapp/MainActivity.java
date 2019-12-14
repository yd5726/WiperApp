package com.example.my.wiperapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
/*
 * blue_btn : 블루투스 버튼
 * clear_btn : 받기 부분 지우기
 * btn1,2,3,4 : 자동 , 수동, 레인, 취소 버튼
 * text1 : 받기
 */
public class MainActivity extends AppCompatActivity {
    BluetoothSPP bt;
    /*Button blue_btn,clear_btn;*/
    ImageButton blue_btn, clear_btn;
    Button btn1,btn2,btn3,btn4;
    EditText text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 븥투루스 버튼 */
        blue_btn = findViewById(R.id.blueConn_btn);
        blue_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
        /* 블루투스 객체 생성 */
        bt = new BluetoothSPP(MainActivity.this);
        /* 블루투스 사용 가능 여부 확인 */
        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "블루투스 사용이 불가합니다."
                    , Toast.LENGTH_SHORT).show();
            finish();
        }
        /* 블루투스 연결 상태 확인 */
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        ,  name + ", 연결되었습니다."
                        , Toast.LENGTH_SHORT).show();
            }
            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "연결이 해제되었습니다.", Toast.LENGTH_SHORT).show();
            }
            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        text1 = findViewById(R.id.EditText1);
        clear_btn = findViewById(R.id.clear_btn);

        /* text1 : 받기 */
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                text1.append(message + "\n");
            }
        });
        /* clear_btn : 받기 부분 지우기 */
        clear_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                text1.setText(null);
            }
        });

        /* 와이퍼 제어
         * btn1,2,3,4 : 자동 , 수동, 레인, 취소 버튼
         * bt.send() : 버튼1 눌렀으면 "버튼1" 보냄
         */

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("AUTO", true);
                btn1.setSelected(true);
                btn2.setSelected(false);
                btn3.setSelected(false);
                btn4.setSelected(false);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("SEMI", true);
                btn1.setSelected(false);
                btn2.setSelected(true);
                btn3.setSelected(false);
                btn4.setSelected(false);
                btn2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn2.setSelected(false);
                    }
                },1000);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("RAIN", true);
                btn1.setSelected(false);
                btn2.setSelected(false);
                btn3.setSelected(true);
                btn4.setSelected(false);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("STOP", true);
                btn1.setSelected(false);
                btn2.setSelected(false);
                btn3.setSelected(false);
                btn4.setSelected(true);
                btn4.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn4.setSelected(false);
                    }
                },1000);
            }
        });
    }

    /* 앱 종료 : 뒤로가기 누르면, 종료하시겠습니까 물어보기 */
    @Override
    public void onBackPressed() {
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("종료하시겠습니까?");
        // "예" 버튼을 누르면 실행되는 리스너
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish(); // 현재 액티비티를 종료한다.
            }
        });
        // "아니오" 버튼을 누르면 실행되는 리스너
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return; // 아무런 작업도 하지 않고 돌아간다.
            }
        });
        builder.setTitle("프로그램 종료");
        builder.show(); // AlertDialog.Bulider 로 만든 AlertDialog 를 보여준다.
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }
    // Check if bluetooth is not enable when activity is onStart
    // (활동이 시작될 때 블루투스가 활성화되어 있지 않은지 확인)
    public void onStart() {
        super.onStart();
        if(!bt.isBluetoothEnabled()) {
            // Do something if bluetooth is disable(블루투스가 비활성화 된 경우)
            // (1) Intent to choose device activity(장치 선택)
            Intent blue_intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(blue_intent, BluetoothState.REQUEST_ENABLE_BT);
            // REQUEST_ENABLE_BT : 앱 상에서 블루투스 장치 켜기
        } else {
            // Do something if bluetooth is already enable(블루투스가 이미 활성화 된 경우 수행)
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }

    // (2) After intent to choose device activity and finish that activity.
    // 장치 활동을 선택하고 해당 활동을 완료하려고 합니다.
    // You need to check result data on onActivityResult
    // onActivityResult 에서 결과 데이터를 확인해야합니다
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext()
                        , "블루투스가 활성화되지 않았습니다."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}