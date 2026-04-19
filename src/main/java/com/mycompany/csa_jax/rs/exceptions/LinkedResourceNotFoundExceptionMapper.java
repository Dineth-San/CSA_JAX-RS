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
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException>{
    
    @Override
    public Response toResponse(LinkedResourceNotFoundException exception){
        
        Map<String, String> errMsg = new HashMap<>();
        errMsg.put("error", "Unprocessable Entity");
        errMsg.put("message", exception.getMessage());
        
        // the integer 422 is passed instead of the Enum of type Status
        return Response.status(422)
                .entity(errMsg)
                .build();
    }
}
