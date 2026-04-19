/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_jax.rs.exceptions;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author ASUS
 */
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException>{
    
    @Override
    public Response toResponse(SensorUnavailableException exception){
        
        Map<String, String> errMap = new HashMap<>();
        errMap.put("error", "Forbidden");
        errMap.put("message", exception.getMessage());
        
        return Response.status(Response.Status.FORBIDDEN)
                .entity(errMap)
                .build();
    }
}
