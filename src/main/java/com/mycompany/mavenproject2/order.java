package com.mycompany.mavenproject2;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
      
      //----------------------------
     private static void replicateOrderChanges(JSONObject changes) {
        String[] orderReplicas = Mavenproject2.getOrderReplicas();
        for (String orderReplica : orderReplicas) {
            replicateChanges(orderReplica, "/replicateOrder", changes);
        }
    }
       //--------------------------------------------
    public static void main(String[] args) {
         port(4566);

            put("order_webservice_ip/purchase/:id", (req, res) -> {
            int userId = Integer.parseInt(req.params(":id"));
            String userDetails = callpurchase(userId);
            res.type("application/json");

            // Notify replicas about the purchase in the Order service
            replicateOrderChanges(new JSONObject().put("action", "purchase").put("userId", userId));

            return userDetails;
        });

            put("/replicateOrder", (req, res) -> {
            JSONObject replicatedChanges = new JSONObject(req.body());
           
            // Acknowledge the successful replication
            return "Order Replication successful";
        });

        // Additional endpoint for handling specific replication actions, if needed
            post("/replicateOrderAction", (req, res) -> {
            JSONObject replicatedChanges = new JSONObject(req.body());
            String action = replicatedChanges.getString("action");

            switch (action) {
                case "purchase":
                    int userId = replicatedChanges.getInt("userId");
                     put("order_webservice_ip/purchase/:id", (purchaseReq, purchaseRes) -> {
                     //  int userId = Integer.parseInt(purchaseReq.params(":id"));
                     String userDetails = callpurchase(userId);
                     purchaseRes.type("application/json");

                     // Notify replicas about the purchase in the Order service
                     replicateOrderChanges(new JSONObject().put("action", "purchase").put("userId", userId));

                     return userDetails;
    });
                   
                    break;

                default:
                    // Handle unknown action
                    res.status(400);
                    return "Unknown replication action";
            }

            // Acknowledge the successful replication
            return "Order Replication Action successful";
        });
      /*   */
    }

     private static void replicateChanges(String replicaPort, String endpoint, JSONObject changes) {
        try {
            URL url = new URL("http://localhost:" + replicaPort + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = changes.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Error response code from replica: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

