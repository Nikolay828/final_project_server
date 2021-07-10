package com.company;

import com.company.dentalclinic.utils.Doctor;
import com.company.dentalclinic.utils.Service;
import com.company.dentalclinic.utils.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {
    private Connection connection;
    private MessageDigest md5;
    public Database(Connection connection) {
        this.connection = connection;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public boolean signOn(User user) {
        boolean result = false;
        String name = user.getName();
        String surname = user.getSurname();
        String email = user.getEmail();
        String password = user.getPassword();
        byte[] passwordBytes = md5.digest(password.getBytes());
        password = bytesToHex(passwordBytes);
        System.out.println(password);

        String checkUserForUniqueness = "SELECT * FROM users WHERE EMAIL = '" + email + "'";
        String saveUser = "insert into users(first_name,last_name,email,password) values('"+ name +"','"+surname +"','"+email+"','"+password+"')";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(checkUserForUniqueness);
            if (!resultSet.next()) {
                statement.execute(saveUser);

                System.out.println(saveUser);
                System.out.println("user added!");
                result = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }


    public boolean signIn(User user) {
        String password = user.getPassword();
        String email = user.getEmail();
        byte[] passwordBytes = md5.digest(password.getBytes());
        String hexPassword = bytesToHex(passwordBytes);
        System.out.println(password);
        String query = "select * from users where email = '"+email +"' and password = '"+hexPassword+"'";
        boolean flag = false;
        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)
        ) {
            if (resultSet.next()) {
                flag = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return flag;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public void saveOrder(User user, Doctor doctor, Service service, String date) {
        String queryGetUserId ="SELECT * FROM users WHERE EMAIL = '" + user.getEmail() + "'";
        String queryGetDoctorId = "SELECT * FROM doctors WHERE fullName = '" + doctor.getFullNаme() + "'";
        String queryGetServiceId = "SELECT * FROM services WHERE name = '" + service.getName() + "'";


        try {
            int idUser = 0;
            int idDoctor = 0;
            int idService = 0;
            Statement statement = connection.createStatement();

            ResultSet resultQueryGetUserId = statement.executeQuery(queryGetUserId);
            while (resultQueryGetUserId.next()){
                idUser = resultQueryGetUserId.getInt("id");
            }
            ResultSet  resultQueryGetDoctorId = statement.executeQuery(queryGetDoctorId);
            while (resultQueryGetDoctorId.next()){

                idDoctor = resultQueryGetDoctorId.getInt("id");
            }
            ResultSet  resultQueryGetServiceId = statement.executeQuery(queryGetServiceId);
            while (resultQueryGetServiceId.next()){
               idService = resultQueryGetServiceId.getInt("id");
            }
            if(idUser != 0 || idDoctor != 0 || idService != 0){
                String queryInsertOrderIntoDb = "INSERT INTO ORDERS(idUsers,idDoctors,idServices,date) VALUES('"+idUser+"','"+idDoctor+"','"+idService+"','"+date+"')";
                System.out.println(queryInsertOrderIntoDb);
                statement.execute(queryInsertOrderIntoDb);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<Doctor> getDoctors() {
        String queryGetDoctors = "SELECT * FROM doctors";
        ArrayList<Doctor> doctors = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryGetDoctors);
            while (resultSet.next()){
                Doctor doctor = new Doctor();
                doctor.setFullNаme(resultSet.getString("fullName"));
                doctor.setExperience(resultSet.getString("experience"));
                doctor.setDescription(resultSet.getString("description"));
                doctors.add(doctor);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return doctors;
    }
}
