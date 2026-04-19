/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_jax.rs.resource;

import com.mycompany.csa_jax.rs.data.DataStore;
import com.mycompany.csa_jax.rs.exceptions.LinkedResourceNotFoundException;
import com.mycompany.csa_jax.rs.models.Room;
import com.mycompany.csa_jax.rs.models.Sensor;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author ASUS
 */

@Path("/sensors")
public class SensorResource {
    
    private final DataStore DATA = DataStore.getInstance();
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newSensor(Sensor sensor, @Context UriInfo uriInfo){
        
        Room room = DATA.getRoom(sensor.getRoomId());
        
        if(room == null){
            throw new LinkedResourceNotFoundException("Room " + sensor.getRoomId() + " does not exist");
        }
        
        // check for null/empty id values and do not allow overriding sensor data
        String sensorId = sensor.getId();
        if(sensorId == null || sensorId.trim().isEmpty() || DATA.getSensor(sensorId) != null){
            Map<String, String> errMap = new HashMap<>();
            errMap.put("error", "Bad Request");
            errMap.put("message", "Sensor ID cannot be null and Sensor data cannot be overriden");
            
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errMap)
                    .build();
        }
        
        DATA.addSensor(sensor);
        List<String> newSensorList = new ArrayList<>(room.getSensorIds());
        newSensorList.add(sensorId);
        room.setSensorIds(newSensorList);
                
        URI locationUri = uriInfo.getAbsolutePathBuilder()
                .path(sensorId)
                .build();
        
        return Response.created(locationUri)
                .entity(sensor)
                .build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type){
        
        Collection<Sensor> allSensors = DATA.getAllSensors();
        
        if(type == null || type.trim().isEmpty()){
            return Response.ok(allSensors)
                    .build();
        }
        
        List<Sensor> filteredSensors = new ArrayList<>();
        
        for(Sensor sensor : allSensors){
            if(type.equalsIgnoreCase(sensor.getType())){
                filteredSensors.add(sensor);
            }
        }
        
        return Response.ok(filteredSensors)
                .build();
    }
    
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId){
        
        if(DATA.getSensor(sensorId) == null){
            throw new LinkedResourceNotFoundException("A Sensor with ID " + sensorId + " does not exist");
        }
        
        return new SensorReadingResource(sensorId);
    }

}
