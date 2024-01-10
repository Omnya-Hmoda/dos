package com.mycompany.mavenproject2;

import static com.mycompany.mavenproject2.order.callpurchase;
import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.List;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


public class cataloge {
    
    public static List<JSONObject> search(String top) {
          String sql = "SELECT * FROM book WHERE topic='"+top+"'";
          List<JSONObject> userDetailsList = new ArrayList<>();
            Connection conn = null;  
       try {
            // db parameters  
            String url = "jdbc:sqlite:C:/sqlite/data.db";  
            // create a connection to the database  
            conn = DriverManager.getConnection(url);  
              
            System.out.println("Connection to SQLite has been established.");  
            Statement stmt= conn.createStatement();   
                      ResultSet rs  = stmt.executeQuery(sql);  
                        // loop through the result set  
              
             while (rs.next()) {
                
                 JSONObject user = new JSONObject();
                 user.put("id", rs.getInt("id"));
                 user.put("title",rs.getString("title"));
                     userDetailsList.add(user);}
               if (userDetailsList.isEmpty()) {
                System.out.println("There is no topic like this.");
                JSONObject user = new JSONObject();
                user.put("message", "There is no topic like this.");
                userDetailsList.add(user);
            }
                   conn.close();
                 
        } 
        catch (SQLException e) {  
            System.out.println(e.getMessage());  
              
        }
       replicateCatalogChanges(new JSONObject().put("action", "search").put("topic", top));

        return userDetailsList;
    }
    
    public static JSONObject info(int id){
      String sql = "SELECT * FROM book WHERE id='"+id+"'";
      JSONObject userDetails = new JSONObject();
            Connection conn = null;  
       try {
            // db parameters  
            String url = "jdbc:sqlite:C:/sqlite/data.db";  
            // create a connection to the database  
            conn = DriverManager.getConnection(url);  
              
            System.out.println("Connection to SQLite has been established.");  
            Statement stmt= conn.createStatement();  
            var rs  = stmt.executeQuery(sql);  
          
              if (rs.next()){
            // loop through the result set  
            
                 userDetails.put("title", rs.getString("title"));
                 userDetails.put("quantity",rs.getInt("quantity"));
                 userDetails.put("price", rs.getInt("price"));
                     }
              else   userDetails.put("massege", "not find the id of the Book ");
                  conn.close();
                 }
        catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
         replicateCatalogChanges(new JSONObject().put("action", "info").put("bookId", id));

      return userDetails;
    }  
//------------------------------------------------------------------------------------------------------------------------
     public static String decrease(int id){
        String sql = "SELECT * FROM book WHERE id='"+id+"'";
                String res="";
               
                Connection conn = null;  
       try {
             int q;
            // db parameters  
            String url = "jdbc:sqlite:C:/sqlite/data.db";  
            // create a connection to the database  
            conn = DriverManager.getConnection(url);  
            System.out.println("Connection to SQLite has been established.");  
            Statement stmt= conn.createStatement(); 
             var rs  = stmt.executeQuery(sql);  
              if (rs.next()){
                  q=rs.getInt("quantity");
                  conn.close();
                   if (q==0){
                       return "There is no book to purchase";}
                   else {
                       String sql2 = "UPDATE book SET quantity = quantity - 1 WHERE id = '" + id + "'";
                        conn = DriverManager.getConnection(url);
                        conn = DriverManager.getConnection(url); 
                        Statement stmt2 = conn.createStatement();
                        int rowsAffected = stmt2.executeUpdate(sql2);
                          if (rowsAffected > 0) {
                             conn.close();
                                return "Update successful, and you purchased a book.";
                                 } else {
                             conn.close();
                                     return "Book not found or quantity is already zero.";
    }}
               
            } else {
                return "the  book id is not found"; // Assuming 0 quantity if the book is not found
                   
            }
      
            
           
               // String sql2 = "UPDATE book SET quantity = quantity - 1 WHERE id = '"+id+"'";
                //Statement stmt2= conn.createStatement();  
                  //   stmt2.executeQuery(sql2);
               
       }
        catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
         //return "";
     replicateCatalogChanges(new JSONObject().put("action", "decrease").put("bookId", id));

        return res;
     }
     
     //*--------------------------------
   private static void replicateCatalogChanges(JSONObject changes) {
    for (String catalogReplica : Mavenproject2.getCatalogReplicas()) {
        replicateChanges(catalogReplica, "/replicateCatalog", changes);
    }
}
       
       //---------------------
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
     //------------------------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        // Handle requests to get user details by ID
        port(4567);

            get("CATALOG_WEBSERVICE_IP/info/:id", (req, res) -> {
            int userId = Integer.parseInt(req.params(":id"));
            JSONObject userDetails = info(userId);
            res.type("application/json");
            return userDetails.toString();
        });

            get("CATALOG_WEBSERVICE_IP/search/:top", (req, res) -> {
            String top = req.params(":top");
            String decodedTopic = URLDecoder.decode(top, StandardCharsets.UTF_8.toString());
            List<JSONObject> userDetailsList = search(decodedTopic);
            res.type("application/json");
            return new JSONArray(userDetailsList).toString();
        });

            put("order_webservice_ip/purchase/:id", (req, res) -> {
            int userId = Integer.parseInt(req.params(":id"));
            String userDetails = decrease(userId);
            res.type("application/json");
            return userDetails;
        });

            put("/replicateCatalog", (req, res) -> {
            JSONObject replicatedChanges = new JSONObject(req.body());
            // Apply changes to the local database or handle the replication as needed
            // ...
            return "Catalog Replication successful";
        });
    }

}
 