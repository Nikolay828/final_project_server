package com.company;

import com.company.dentalclinic.utils.Doctor;
import com.company.dentalclinic.utils.RequestCode;
import com.company.dentalclinic.utils.Service;
import com.company.dentalclinic.utils.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientListener implements Runnable{
    private Socket socket;
    private ConnectionPool connectionPool;
    private Connection connection;
    private Database database;

    public ClientListener(Socket socket, ConnectionPool connectionPool) {
        this.socket = socket;
        this.connectionPool = connectionPool;
        try {
            this.connection = connectionPool.getConnection();
             database = new Database(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                Scanner scanner = new Scanner(socket.getInputStream())
        ) {
            while (scanner.hasNextLine()) {
                String key = scanner.nextLine();
                System.out.println(key);
                Gson gson = new Gson();
                String jsonUser;
                String jsonService;
                String jsonDoctor;
                String jsonDoctors;
                String date;
                User user;
                Doctor doctor;
                Service service;

                boolean result;
                switch (key) {
                    case RequestCode.KEY_SIGN_IN:
                        System.out.println("works");
                        jsonUser = scanner.nextLine();
                        user = gson.fromJson(jsonUser,User.class);
                        result = database.signIn(user);
                        printWriter.println(result);
                        printWriter.flush();
                        break;
                    case RequestCode.KEY_SIGN_ON:
                        System.out.println("works two");
                        jsonUser = scanner.nextLine();
                        user = gson.fromJson(jsonUser,User.class);
                        result = database.signOn(user);
                        printWriter.println(result);
                        printWriter.flush();
                        break;
                    case RequestCode.KEY_SAVE_ORDER:
                        System.out.println("works three");
                        jsonUser = scanner.nextLine();
                        user = gson.fromJson(jsonUser,User.class);
                        System.out.println(user);
                        jsonDoctor = scanner.nextLine();
                        doctor = gson.fromJson(jsonDoctor,Doctor.class);
                        System.out.println(doctor);
                        jsonService = scanner.nextLine();
                        service = gson.fromJson(jsonService,Service.class);
                        System.out.println(service);
                        date = scanner.nextLine();
                        database.saveOrder(user,doctor,service,date);
                        break;
                    case RequestCode.KEY_GET_DOCTOR:
                        System.out.println("works four");
                        ArrayList<Doctor> doctors = database.getDoctors();
                        for (Doctor doctor234: doctors) {
                            System.out.println(doctor234.getFullNаme());
                        }
                        jsonDoctors = gson.toJson(doctors);
                        printWriter.println(jsonDoctors);
                        printWriter.flush();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectionPool.returnConnection(connection);
    }
}