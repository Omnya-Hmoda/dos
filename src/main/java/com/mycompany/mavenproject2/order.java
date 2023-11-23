package com.mycompany.mavenproject2;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import static spark.Spark.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Msys
 */
public class order {
    
    
      public static String callpurchase(int id){
           try {
           URL url = new URL("http://localhost:4567/order_webservice_ip/purchase/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read the response as JSON
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder responseStringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseStringBuilder.append(line+"\n");
                }
               return responseStringBuilder.toString();

            }
        } else {
            // Handle error cases
            System.out.println("Error response code from Microservice urchase : " + responseCode);
            return "Error response code from Microservice"; // or throw an exception, depending on your error handling strategy
        }
    } catch (Exception e) {
        e.printStackTrace();
         return "Error response code from Microservice"; // or throw an exception
    }
      }
    public static void main(String[] args) {
         port(4566);
        put("order_webservice_ip/purchase/:id", (req, res) -> {
            int userId =Integer.parseInt(req.params(":id"));
            String userDetails = callpurchase(userId);
            return userDetails;
        });  
    }
    
}
