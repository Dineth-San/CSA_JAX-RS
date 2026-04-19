/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_jax.rs.resource;

import com.mycompany.csa_jax.rs.data.DataStore;
import com.mycompany.csa_jax.rs.exceptions.LinkedResourceNotFoundException;
import com.mycompany.csa_jax.rs.exceptions.SensorUnavailableException;
import com.mycompany.csa_jax.rs.models.Sensor;
import com.mycompany.csa_jax.rs.models.SensorReading;
import java.net.URI;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author ASUS
 */
public class SensorReadingResource {
    
    private final String sensorId;
    private final DataStore DATA = DataStore.getInstance();
    
    public SensorReadingResource(String sensorId){
        this.sensorId = sensorId;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistory(){
        
        Sensor sensor = DATA.getSensor(sensorId);
        if(sensor == null){
            throw new LinkedResourceNotFoundException("A sensor with the given ID " + sensorId + " does not exist");
        }
        
        List<SensorReading> sensorReadings = DATA.getReadingsById(sensorId);
        
        return Response.ok(sensorReadings)
                .build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response appendNewReading(SensorReading newReading, @Context UriInfo uriInfo){
        
        Sensor sensor = DATA.getSensor(sensorId);
        if(sensor == null){
            throw new LinkedResourceNotFoundException("A sensor with the given ID " + sensorId + " does not exist");
        }
        
        if(sensor.getStatus().equalsIgnoreCase("maintenance")){
            throw new SensorUnavailableException("The selected sensor is under maintenance");
        }
        
        DATA.addReading(this.sensorId, newReading);
        DATA.getSensor(this.sensorId).setCurrentValue(newReading.getValue());
        
        URI locationUri = uriInfo.getAbsolutePathBuilder()
                .path(newReading.getId())
                .build();
        
        return Response.created(locationUri)
                .entity(newReading)
                .build();
    }
}
