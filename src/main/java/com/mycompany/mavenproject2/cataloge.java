package com.mycompany.mavenproject2;

import java.sql.*;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Msys
 */
public class cataloge {
    
  
      
      
      
    public static void searcht(String top) {
     String sql = "SELECT * FROM book WHERE topic='"+top+"'";
 
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
                System.out.println(rs.getInt("id") +  "\t" +   
                                   rs.getString("title") + "\t" );  
        } }
        catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
       } 
       
    public static void infot(int id){
      String sql = "SELECT * FROM book WHERE id='"+id+"'";
 
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
                System.out.println(rs.getInt("id") +  "\t" +   
                                   rs.getString("title") + "\t" +   
                                   rs.getInt("quantity") + "\t"+   
                                   rs.getInt("price") + "\t"+
                                   rs.getString("topic") + "\t");
                        
                       
        } }
        catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
    }  
    
    
    public static void search(String top) {
   String sql = "SELECT * FROM book WHERE topic='"+top+"'";
 
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
                System.out.println(rs.getInt("id") +  "\t" +   
                                   rs.getString("title") + "\t" );  
        } }
        catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
       } 
        

}
 