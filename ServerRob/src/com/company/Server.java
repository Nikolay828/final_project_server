package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {
    String url="jdbc:postgresql://localhost/final-project-db?user=postgres&password=1111"; // сюда ссылку на pgsql
    public void start(){
        System.out.println("Server start work");
        try {
            ServerSocket serverSocket = new ServerSocket(7999);
            ConnectionPool connectionPool = new ConnectionPool(url);
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("connected");
                new Thread(new ClientListener(socket,connectionPool)).start();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
