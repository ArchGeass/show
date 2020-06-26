package org.org.geass.dubbo;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description: 服务端NIO实现
 * @Author: ArchGeass
 * @Date: 2020/6/26,下午1:20
 */
public class NioProvider<T> {
    private T service;
    private ServerSocketChannel serverSocketChannel;

    public NioProvider(T service) throws IOException {
        this.service = service;
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.socket().bind(new InetSocketAddress(8080));
    }

    public void start() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Selector selector = Selector.open();
        //将selector注册给一个ACCEPT事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            //阻塞在这里,直到有IO事件发生
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                //拿到当前key后从iterator中删除
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    //拿到当前连接的client
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel client = channel.accept();
                    //设置client不阻塞
                    client.configureBlocking(false);
                    //注册读写事件,并注明附件使得可以维护一个状态,实现重复调用
                    client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, new SocketData());
                }
                //进行读写事件处理
                if (key.isReadable()) {
                    //拿到当前连接的client
                    SocketChannel client = (SocketChannel) key.channel();
                    //拿到上次的socketData
                    SocketData socketData = (SocketData) key.attachment();
                    int bytesRead = client.read(socketData.buffer);
                    if (bytesRead > 0) {
                        //读取到东西
                        //转变模式
                        socketData.buffer.flip();
                        socketData.append(bytesRead);
                        if (socketData.sb.toString().contains("\n")) {
                            //数据读取完毕,开始写入
                            MethodInfo methodInfo = JSON.parseObject(socketData.sb.toString(), MethodInfo.class);
                            Method method = service.getClass().getMethod(methodInfo.getMethodName(),
                                    methodInfo.getParams().stream().map(Object::getClass).toArray(Class[]::new));
                            Object returnValue = method.invoke(service, methodInfo.getParams().toArray());
                            byte[] returnValueBytes = (JSON.toJSONString(returnValue) + "\n").getBytes();
                            socketData.buffer.flip();
                            //改为可写模式
                            socketData.isReading = false;
                            socketData.buffer.put(returnValueBytes);
                        }
                    } else if (bytesRead < 0) {
                        //读取完毕后关闭连接
                        key.channel().close();
                    }
                }
                //可写的时候执行写操作
                if (key.isWritable()) {
                    //拿到当前连接的client
                    SocketChannel client = (SocketChannel) key.channel();
                    //拿到上次的socketData
                    SocketData socketData = (SocketData) key.attachment();
                    //只有可写的时候才去写(可能立刻可写但可能没数据)
                    if (!socketData.isReading) {
                        //改为可读模式
                        socketData.isReading = true;
                        socketData.buffer.flip();
                        while (socketData.buffer.hasRemaining()) {
                            //执行具体写操作
                            client.write(socketData.buffer);
                        }
                        client.close();
                    }
                }
            }
        }
    }

    //用来读写数据的class
    private static class SocketData {
        StringBuilder sb = new StringBuilder();
        //基于ByteBuffer的读写,每次申请1024的空间
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //标记读写状态
        boolean isReading = true;

        public void append(int bytesRead) {
            byte[] tmp = new byte[bytesRead];
            buffer.get(tmp);
            sb.append(new String(tmp));
        }
    }

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        new NioProvider<>(new GreetingsServiceImpl()).start();
        System.in.read();
    }
}