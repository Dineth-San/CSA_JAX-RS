/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_jax.rs.resource;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author ASUS
 */
@Path("/")
public class DiscoveryResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryInfo(){
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("version", "v1.0");
        metadata.put("contact", "dineth.s.kaluarachchi@gmail.com");
        metadata.put("description", "Smart Campus Sensor API");
        
        Map<String, String> resources = new HashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        resources.put("room_methods", "GET, POST, DELETE"); 
        
        metadata.put("resources", resources);
        
        return Response.ok(metadata).build();
    }
}
