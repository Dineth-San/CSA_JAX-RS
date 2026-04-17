/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_jax.rs.resource;

import com.mycompany.csa_jax.rs.data.DataStore;
import com.mycompany.csa_jax.rs.models.Room;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author ASUS
 */
@Path("/rooms")
public class SensorRoom{
    
    private final DataStore DATA = DataStore.getInstance();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms(){
        List<Room> roomList = new ArrayList<>(DATA.getRooms().values());
        
        if (roomList.isEmpty()){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "No rooms found");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMap)
                    .build();
        }
        
        return Response.ok(roomList).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newRoom(Room room, @Context UriInfo uriInfo){
        
        // check for duplicated rooms or empty ids
        if(room.getId() == null || room.getId().trim().isEmpty() || DATA.getRoom(room.getId()) != null){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Bad Request");
            errorMap.put("message", "Room ID is mandatory, and cannot be duplicated");
            
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMap)
                    .build();
        }
        
        DATA.addRoom(room);
        URI locationUri = uriInfo.getAbsolutePathBuilder()
                .path(room.getId())
                .build();
        
        return Response.created(locationUri)
                .entity(room)
                .build();
    }
    
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String id){
        Room room = DATA.getRoom(id);
          
        if (room == null){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "A room with the given ID does not exist");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMap)
                    .build();
        }
        
        return Response.ok(room).build();
    }
    
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoomById(@PathParam("roomId") String id){
        if(DATA.removeRoom(id)){
            return Response.noContent()
                    .build();
        }
        
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Bad Request");
        errorMap.put("message", "A room with the given ID does not exist or the room has active sensors");

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMap)
                .build();
    }
}
