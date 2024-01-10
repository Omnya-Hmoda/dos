/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject2;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import static spark.Spark.*;
import org.json.JSONObject;

public class Mavenproject2 {
    private static String[] catalogRep = {"3000", "3005"};
    private static String[] orderRep = {"3003", "3004"};
    private static int catalogIndex = 0;
    private static int orderIndex = 0;

    private static Map<String, JSONObject> cache = new HashMap<>();

    public static void main(String[] args) {
            port(8082);

            get("CATALOG_WEBSERVICE_IP/info/:id", (req, res) -> {
            String userId = req.params(":id");
               if (cache.containsKey("id_" + userId)) {
                System.out.println("found in cache");
                return cache.get("id_" + userId);
            }
                 System.out.println("not in cache");
            catalogIndex = (catalogIndex >= catalogRep.length - 1) ? 0 : catalogIndex + 1;


            JSONObject userDetails = callinfo(userId);
            res.type("application/json");

            // Notify replicas about the changes in the Catalog
            replicateCatalogChanges(userDetails);

            return userDetails.toString();
        });

            get("CATALOG_WEBSERVICE_IP/search/:topic", (req, res) -> {
            String topic = req.params(":topic");
              if (cache.containsKey("topic_" + topic)) {
                System.out.println("found in cache");
                return cache.get("topic_" + topic);}
            System.out.println("not found in cache");
            catalogIndex = (catalogIndex >= catalogRep.length - 1) ? 0 : catalogIndex + 1;


            JSONArray searchResult = callSearch(topic);
            res.type("application/json");

            // Notify replicas about the changes in the Catalog
            replicateCatalogChanges(new JSONObject().put("action", "search").put("topic", topic));

            return searchResult.toString();
        });

             put("order_webservice_ip/purchase/:id", (req, res) -> {
             String userId = req.params(":id");
             int id = Integer.parseInt(userId); 
            String userDetails = callpurchase(userId);
            res.type("application/json");
               if (cache.containsKey("id_" + id)) {
                cache.remove("id_" + id); 
                userDetails = callpurchase(userId);   
                catalogIndex = (catalogIndex >= catalogRep.length - 1) ? 0 : catalogIndex + 1;
                JSONObject searchResult = callinfo2(userId);
                cache.put("id_" + userId,searchResult);
                System.out.println(cache.size());
              }
              else{
            System.out.println("current cache size:" + cache.size());
            userDetails = callpurchase(userId);}
            replicateOrderChanges(new JSONObject().put("action", "purchase").put("userId", userId));

            return userDetails;
       

            // Notify replicas about the purchase in the Order service

        });
    }

    private static void replicateCatalogChanges(JSONObject changes) {
        for (String catalogReplica : catalogRep) {
            replicateChanges(catalogReplica, "/replicateCatalog", changes);
        }
    }

    private static void replicateOrderChanges(JSONObject changes) {
        for (String orderReplica : orderRep) {
            replicateChanges(orderReplica, "/replicateOrder", changes);
        }
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
      public static String[] getCatalogReplicas() {
        return catalogRep;
    }
       public static String[] getOrderReplicas() {
        return orderRep;
    }
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
        JSONObject  responseBody;
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
             responseBody = new JSONObject(responseStringBuilder.toString());
            }
        } else {
            // Handle error cases
            System.out.println("Error response code from Microservice info : " + responseCode);
           responseBody = new JSONObject(); // or throw an exception, depending on your error handling strategy
        }
    } catch (Exception e) {
        e.printStackTrace();
       responseBody = new JSONObject(); // or throw an exception
    }
     System.out.println("retrieved from db");
    cache.put("id_" + userId,responseBody);
    System.out.println(cache.size());
    return   responseBody; 
    }   
  private static JSONArray callSearch(String top) {
       JSONArray  responseBody;
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
             responseBody = new JSONArray(responseStringBuilder.toString());
}
        } else {
            // Handle error cases
            System.out.println("Error response code from Microservice search : " + responseCode);
         responseBody = new JSONArray(); // or throw an exception, depending on your error handling strategy
        }
    } catch (Exception e) {
        e.printStackTrace();
     responseBody = new JSONArray(); // or throw an exception
    }
    System.out.println("retrieved from db");
    cache.put("topic_" + top, new JSONObject().put("data", responseBody));
    System.out.println(cache.size());
    return responseBody;
}

private static JSONObject callinfo2(String userId) {
        JSONObject  responseBody;
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
             responseBody = new JSONObject(responseStringBuilder.toString());
            }
        } else {
            // Handle error cases
            System.out.println("Error response code from Microservice info : " + responseCode);
           responseBody = new JSONObject(); // or throw an exception, depending on your error handling strategy
        }
    } catch (Exception e) {
        e.printStackTrace();
       responseBody = new JSONObject(); // or throw an exception
    }

    cache.put("id_" + userId,responseBody);
    System.out.println(cache.size());
    return   responseBody; 
    }   

   
}
