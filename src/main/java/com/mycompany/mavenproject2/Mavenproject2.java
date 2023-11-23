/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject2;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import static spark.Spark.*;
import org.json.JSONObject;

public class Mavenproject2 {

    public static void main(String[] args) {
 {
      port(8082);
       
        
        get("CATALOG_WEBSERVICE_IP/info/:id", (req,res)->{
           String userId = req.params(":id");
           JSONObject userDetails = callinfo(userId);
               res.type("application/json");
            return userDetails.toString();
        });
        
        get("CATALOG_WEBSERVICE_IP/search/:topic", (req,res)->{
            String topic = req.params(":topic");
            JSONArray searchResult = callSearch(topic);
            res.type("application/json");
             return new JSONArray(searchResult).toString();});
        
        
        ////ORDER_WEBSERVICE_IP/purchase/2
        put("order_webservice_ip/purchase/:id", (req,res)->{
           String userId = req.params(":id");
           String userDetails = callpurchase(userId);
            return userDetails;
        });
    }    }
 private static String callpurchase(String userId) {
         try {
           URL url = new URL("http://localhost:4566/order_webservice_ip/purchase/" + userId);
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
    private static JSONObject callinfo(String userId) {
         try {
           URL url = new URL("http://localhost:4567/CATALOG_WEBSERVICE_IP/info/" + userId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read the response as JSON
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder responseStringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseStringBuilder.append(line+"\n");
                }
                return new JSONObject(responseStringBuilder.toString());
            }
        } else {
            // Handle error cases
            System.out.println("Error response code from Microservice info : " + responseCode);
            return new JSONObject(); // or throw an exception, depending on your error handling strategy
        }
    } catch (Exception e) {
        e.printStackTrace();
        return new JSONObject(); // or throw an exception
    }
      
    }
    
  private static JSONArray callSearch(String top) {
    try {
        String encodedTopic = URLEncoder.encode(top, StandardCharsets.UTF_8.toString());
        URL url = new URL("http://localhost:4567/CATALOG_WEBSERVICE_IP/search/" + encodedTopic);
        System.out.println(encodedTopic);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder responseStringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseStringBuilder.append(line);
                }
                
                return new JSONArray(responseStringBuilder.toString());
            }
        } else {
            // Handle error cases
            System.out.println("Error response code from Microservice search : " + responseCode);
            return new JSONArray(); // or throw an exception, depending on your error handling strategy
        }
    } catch (Exception e) {
        e.printStackTrace();
        return new JSONArray(); // or throw an exception
    }
}



   
}
