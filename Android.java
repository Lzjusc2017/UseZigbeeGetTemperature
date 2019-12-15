package com.example.zigbeeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {

    private Button GetNetwork_topology,GetTemperature,On,Off,Toggle,Connect;
    private TextView ShowNetwork_topology,ShowTemperature;
    private EditText IpAddress,IpPort;
    private Socket mysocket;
    private String controlName = "";

    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(Readable).start();


            }
        });
        GetNetwork_topology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlName = "network_topology";
                new Thread(Sendable).start();
            }
        });
        GetTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlName = "get_temperature";
                new Thread(Sendable).start();
            }
        });
        On.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlName = "on";
                new Thread(Sendable).start();
            }
        });
        Off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlName = "off";
                new Thread(Sendable).start();
            }
        });
        Toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlName = "toggle";
                new Thread(Sendable).start();
            }
        });
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x00){

                Toast.makeText(MainActivity.this,"已连接到服务端",Toast.LENGTH_LONG).show();
            }
            else if (msg.what == 0x01){

                //将发送过来的数据进行解析.
                Bundle data = msg.getData();
                //发送过来很多数据.
                String result = data.getString("msg");
                if (result.indexOf("Coordinator")!=-1){
                    //说明是网络的拓扑结构
                    ShowNetwork_topology.append(result);
                }else if (result.indexOf("Temperature")!=-1){
                    ShowTemperature.append(result);
                }

            }
            else if (msg.what == 0x02){
                Toast.makeText(MainActivity.this,"消息已经发送完毕",Toast.LENGTH_LONG).show();
            }else if (msg.what == 0x03){

            }
        }
    };

    private void init(){
        Connect = (Button)findViewById(R.id.connect);
        GetNetwork_topology = (Button)findViewById(R.id.getNetwork_topology);
        GetTemperature = (Button)findViewById(R.id.getTemperature);
        On = (Button)findViewById(R.id.on);
        Off = (Button)findViewById(R.id.off);
        Toggle = (Button)findViewById(R.id.toggle);
        ShowNetwork_topology = (TextView)findViewById(R.id.showNetwork_topology);
        ShowTemperature = (TextView)findViewById(R.id.showTemperature);

        IpAddress = (EditText)findViewById(R.id.ipAddress);
        IpPort = (EditText)findViewById(R.id.ipPort);
    }
    Runnable Sendable = new Runnable() {
        @Override
        public void run() {
            try{
                /*
                 * DataOutputStream out = new DataOutputStream(OutputStream  out),数据输出流
                 * */

                DataOutputStream writer = new DataOutputStream(mysocket.getOutputStream());
                writer.write(controlName.getBytes());
                //发送，我也要写入handler中.
                Message message = new Message();
                message.what = 0x02;   //标识符为1
                handler.sendMessage(message);
                //writer.writeUTF(str);   //写一个utf-8的信息
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    Runnable Readable = new Runnable() {
        @Override
        public void run() {
            try{
                int port = Integer.parseInt(IpPort.getText().toString());
                mysocket = new Socket(IpAddress.getText().toString(),port); //连成功了，服务器一直显示数据.
            }catch (UnknownHostException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try{
                while(true){
                    InputStream is = mysocket.getInputStream(); //192.168.43.224,手机的wifi ip 地址.
                    byte[] buffer = new byte[1024*1024];
                    int len = is.read(buffer);
                    String result = new String(buffer, 0, len);
                    /*  第一次读到的是Ip地址，必须保存.*/
                    Message message = new Message();
                    message.what = 1;   //标识符为1
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", result);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
