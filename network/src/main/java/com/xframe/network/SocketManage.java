package com.xframe.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SocketManage implements Handler.Callback, Runnable {
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private static final String TAG = "SocketManage";
    private final StringBuilder sb = new StringBuilder();
    private OnData data;
    private SocketChannel socketChannel;
    private final int ByteMax = 65535;
    private boolean isRun = true;
    private int position;
    private static final int corePoolSize = 5; // 核心线程数
    private static final int maxPoolSize = 10; // 最大线程数
    private static final long keepAliveTime = 60L; // 线程空闲时间
    private static final TimeUnit unit = TimeUnit.SECONDS; // 时间单位
    private static final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(); // 任务队列
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue);

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static void init(OnData onData) {
        SocketManage socketManage = new SocketManage();
        socketManage.setData(onData);
        executor.execute(socketManage);
    }

    public static void init(OnData onData, int position) {
        SocketManage socketManage = new SocketManage();
        socketManage.setData(onData);
        socketManage.setPosition(position);
        executor.execute(socketManage);
    }

    @Override
    public void run() {
        initSocketChannel();
    }


    public boolean isConnected() {
        return socketChannel.isConnected();
    }

    public void initSocketChannel() {
        try {
            Selector selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
            socketChannel.connect(new InetSocketAddress("f36i940486.wicp.vip", 17468));
            long timeout = System.currentTimeMillis() + 5_000; // 5 seconds
            while (isRun) {
                int readyChannels = selector.select(timeout);
                if (readyChannels == 0) {
                    //会在这里等待服务端反馈
                    // No channels are ready, so check if the timeout has elapsed
                    if (System.currentTimeMillis() >= timeout) {
                        sendMessage(2, "timeout error");
                        System.out.println("timeout error");
                        // The timeout has elapsed, so cancel the connection attempt
                        socketChannel.close();
                        break;
                    }
                    // No channels are ready, but the timeout hasn't elapsed, so continue waiting
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                for (SelectionKey key : selectedKeys) {
                    if (key.isConnectable()) {
                        // The socket channel is connected
                        SocketChannel connectedChannel = (SocketChannel) key.channel();
                        if (connectedChannel.isConnectionPending()) {
                            boolean is = connectedChannel.finishConnect();
                            if (is) {
                                if (data != null) {
                                    data.connect(this);
                                }
                                //连接成功
                                Log.d(TAG, "Connection true");
                            } else {
                                Log.d(TAG, "Connection false");
                            }
                        }
                        // Do something with the connected socket channel
                    } else if (key.isReadable()) {
                        handleReadable(key);
                    }
                }
            }
        } catch (Exception e) {
            if (data != null) {
                data.error(e.getMessage());
            }
            return;
        }
        Log.d(TAG, "Connection close");
    }

    public void setData(OnData data) {
        this.data = data;
    }

    /**
     * @param key 读取数据
     */
    private void handleReadable(SelectionKey key) throws Exception { //读取数据
        SocketChannel socketChannel = (SocketChannel) key.channel(); //缓存大小
        ByteBuffer readBuffer = ByteBuffer.allocate(ByteMax);
        int read;
        while ((read = socketChannel.read(readBuffer)) > 0) {
            readBuffer.flip();
            byte[] readByte = new byte[read];
            readBuffer.get(readByte);
            sb.append(new String(readByte));
            readBuffer.clear();
        }
        String body = sb.toString();
        //防止未发送完毕,结束发送字符串
        if (body.endsWith("\n")) {
            String[] strings = body.split("\n");
            for (String value : strings) {
                System.out.println(value);
                handleData(value);
            }
            close();
        }
    }

    /**
     * @param toString 处理数据
     */
    private void handleData(String toString) {
        sendMessage(1, toString);
    }

    /**
     * @param text 发送短信 * @return
     */
    public boolean print(String text) {
        text = text + "\n";
        try {
            if (socketChannel != null && socketChannel.isConnected()) { //缓存大小
                ByteBuffer writeBuffer = ByteBuffer.allocate(ByteMax);
                writeBuffer.put(text.getBytes());
                writeBuffer.flip(); // 写入数据
                while (writeBuffer.hasRemaining()) {
                    socketChannel.write(writeBuffer);
                }
                writeBuffer.compact();
                return true;
            }
        } catch (Exception e) {
            sendMessage(2, e.getMessage());
        }
        return false;
    }

    public void close() {
        socketChannel.socket();
        isRun = false;
    }

    private void sendMessage(int w, String str) {
        Message message = new Message();
        message.what = w;
        message.obj = str;
        handler.sendMessage(message);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            String data = (String) msg.obj;
            if (this.data != null) {
                this.data.handle(data);
            }
        } else if (msg.what == 2) {
            String data = (String) msg.obj;
            if (this.data != null) {
                this.data.error(data);
            }
        }
        return true;
    }
}