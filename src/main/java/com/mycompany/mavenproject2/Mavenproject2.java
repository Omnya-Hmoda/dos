/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject2;
import com.google.gson.Gson;
import static spark.Spark.*;


public class Mavenproject2 {

    public static void main(String[] args) {
 {
      port(5678);
       Gson gson = new Gson();
       /* get("CATALOG_WEBSERVICE_IP/search/:type", (req, res)->{
            res.type("application/json");
            return new Gson().toJson();
        });*/
        
        get("CATALOG_WEBSERVICE_IP/info/:id", (req,res)->{
            
            
            
            
            
            return "Hello, "+ req.params(":id");
        });
        
         get("ORDER_WEBSERVICE_IP/purchase/:id", (req,res)->{
             
             
             
             
            return "Hello, "+ req.params(":id");
        });
        ////ORDER_WEBSERVICE_IP/purchase/2
    }    }
}
