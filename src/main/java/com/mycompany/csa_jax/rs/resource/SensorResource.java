/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_jax.rs.resource;

import com.mycompany.csa_jax.rs.data.DataStore;
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
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Bad Request");
            errorMap.put("message", "The given room does not exist");
            
            Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMap)
                    .build();
        }
        
        DATA.addSensor(sensor);
        List<String> newSensorList = new ArrayList<>(room.getSensorIds());
        newSensorList.add(sensor.getId());
        room.setSensorIds(newSensorList);
                
        URI locationUri = uriInfo.getAbsolutePathBuilder()
                .path(sensor.getId())
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
            return new SensorReadingResource(sensorId);
        }

}
