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
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException>{
    
    @Override
    public Response toResponse(RoomNotEmptyException exception){
        Map<String, String> errMsg = new HashMap<>(); 
        errMsg.put("error", "Conflict");
        errMsg.put("message", exception.getMessage());
        
        // Status.CONFLICT returns a 409 error
        return Response.status(Response.Status.CONFLICT)
                .entity(errMsg)
                .build();
    }
}
