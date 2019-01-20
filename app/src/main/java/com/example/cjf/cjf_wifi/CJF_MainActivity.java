package com.example.cjf.cjf_wifi;


import java.io.IOException;//java.io包含多种输入/输出功能的类
import java.io.InputStream;
import java.net.Socket;//java.net执行与网络相关操作的类
import java.io.OutputStream;
import java.net.UnknownHostException;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CJF_MainActivity extends AppCompatActivity {
    Socket socket = null;// 开辟一个socket控件
    Button enterbut = null;//
    TextView IP = null;
    TextView PORT = null;
    TextView edttemp = null,temp;// 温度
    TextView edthumi = null;// 湿度
    TextView x_num,y_num,z_num;
    InputStream in;
    boolean isConnected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cjf__main);

        findid();
        enterbut.setOnClickListener(new enterclick());

    }
    public class enterclick implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            if (!isConnected)// 没有连接上
                new ClientThread().start();// 打开连接
            else// 连接上，断开连接
            {
                if (socket != null) {
                    try {
                        socket.close();
                        socket = null;
                        isConnected = false;
                        // 得到一个消息对象，Message类是有Android操作系统提供
                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        mHandler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }
    Handler mHandler = new Handler() { // 等待socket连接成功
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String a;
            ;
            switch (msg.what) {
                case 0:// TCP断开连接
                    enterbut.setText("连接");
                    Toast.makeText(CJF_MainActivity.this, "server close", Toast.LENGTH_SHORT).show();
                    break;
                case 1:// 表明TCP连接成功，可以进行数据交互了
                    enterbut.setText("断开");
                    Toast.makeText(CJF_MainActivity.this, "server open", Toast.LENGTH_SHORT).show();
                    new InputThread().start();// 开启接收线程
                    // edttemp.setText("456");
                    break;
                case 2:// 有数据进来
                    String result = msg.getData().get("msg").toString();
                    a = result.substring(0, 1);
                    if (a.equals("T")) {
                        edttemp.setText(result.substring(1, 3) + "℃");// 获取温度
                        edthumi.setText(result.substring(3, 5) + "%");// 获取湿度
//
                    }
                    if (a.equals("M")) {
                        temp.setText(result.substring(1,3)+"."+result.substring(3,4)+"℃");

                    }
                    break;
            }
        }
    };
    // 开辟一个线程 ,线程不允许更新UI socket连接使用
    public class ClientThread extends Thread {

        public void run() {
            try {
                socket = new Socket(IP.getText().toString(), Integer.parseInt(PORT.getText().toString()));// 建立好连接之后，就可以进行数据通讯了
                isConnected = true;
                in = socket.getInputStream();
                // 得到一个消息对象，Message类是有Android操作系统提供
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                mHandler.sendMessage(msg);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }// 连接服务器
        }

    }

    public class InputThread extends Thread {

        public void run() {
            while (true) {
                if (socket != null) {

                    String result = readFromInputStream(in);
                    try {

                        if (!result.equals("")) {

                            Message msg = new Message();
                            msg.what = 2;
                            Bundle data = new Bundle();
                            data.putString("msg", result);
                            msg.setData(data);
                            mHandler.sendMessage(msg);
                        }

                    } catch (Exception e) {
                    }

                    try {
                        // 设置当前显示睡眠1秒
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String readFromInputStream(InputStream in) {
        int count = 0;
        byte[] inDatas = null;
        try {
            while (count == 0) {
                count = in.available();
            }
            inDatas = new byte[count];
            in.read(inDatas);
            return new String(inDatas, "gb2312");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private void findid(){
        enterbut = (Button) findViewById(R.id.button);// 获取ID号
        IP = (TextView) findViewById(R.id.textView3);
        IP.setText("192.168.4.1");
        PORT = (TextView) findViewById(R.id.textView4);
        PORT.setText("5000");
        edttemp= (TextView) findViewById(R.id.textView7);
        edthumi= (TextView) findViewById(R.id.textView8);
        temp   = (TextView) findViewById(R.id.textView11);
        x_num  = (TextView) findViewById(R.id.textView17);
        y_num  = (TextView) findViewById(R.id.textView18);
        z_num  = (TextView) findViewById(R.id.textView19);

    }

}
