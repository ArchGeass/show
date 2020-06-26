package org.org.geass.dubbo;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @Author: ArchGeass
 * @Date: 2020/6/26,下午1:11
 */
public class MyDubboClient<T> {

    private Class<T> interfaceClass;

    private Socket socket;

    public MyDubboClient(Class<T> interfaceClass) throws IOException {
        this.interfaceClass = interfaceClass;
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress("127.0.0.1", 8080));
    }

    public T getRef() {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{interfaceClass},
                (proxy, method, args) -> {
                    MethodInfo methodInfo = new MethodInfo(method.getName(),
                            Stream.of(args).collect(Collectors.toList()));
                    OutputStream outputStream = MyDubboClient.this.socket.getOutputStream();
                    //写数据
                    outputStream.write((JSON.toJSON(methodInfo) + "\n").getBytes());
                    //数据发送
                    outputStream.flush();
                    String returnValueJson = new BufferedReader(
                            new InputStreamReader(MyDubboClient.this.socket.getInputStream())).readLine();
                    return JSON.parse(returnValueJson);
                }
        );
    }
}