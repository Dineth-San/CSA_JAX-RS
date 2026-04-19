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
public class AllExceptionMapper implements ExceptionMapper<Throwable>{

    @Override
    public Response toResponse(Throwable throwable) {
        
        Map<String, String> errMap = new HashMap<>();
        errMap.put("error", "Internal Server Error");
        errMap.put("message", "An unexpected system error occurred.");
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errMap)
                .build();
    }
}
