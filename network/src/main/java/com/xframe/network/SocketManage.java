package com.xframe.network;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SocketManage extends Thread {
    private static final String TAG = "SocketManage";
    private final StringBuilder sb = new StringBuilder();
    private OnData data;
    private SocketChannel socketChannel;
    private final int ByteMax = 65535;
    private Selector selector;
    private boolean isRun = true;

    public static void init(OnData onData) {
        SocketManage socketManage = new SocketManage();
        socketManage.setData(onData);
        socketManage.start();
    }

    @Override
    public void run() {
        initSocketChannel();
    }

    public void initSocketChannel() {
        //开始建立连接
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.socket().setSoTimeout(500);
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
            // socketChannel.connect(new InetSocketAddress("f36i940486.wicp.vip", 17468));
            socketChannel.connect(new InetSocketAddress("192.168.1.19", 6333));
            long timeout = System.currentTimeMillis() + 5000; // 5 seconds
            while (isRun) {
                int readyChannels = selector.select(timeout);
                if (readyChannels == 0) {
                    // No channels are ready, so check if the timeout has elapsed
                    if (System.currentTimeMillis() >= timeout) {
                        if (data != null) {
                            data.error("timeout error");
                        }
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
        handleData(body);
        close();
    }

    /**
     * @param toString 处理数据
     */
    private void handleData(String toString) {
        if (data != null) {
            data.handle(toString);
        }
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
            if (data != null) {
                data.error(e.getMessage());
            }
        }
        return false;
    }

    public void close() {
        socketChannel.socket();
        isRun = false;
    }
}