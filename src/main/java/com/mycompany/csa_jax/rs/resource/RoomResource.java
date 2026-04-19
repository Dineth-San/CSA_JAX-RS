/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_jax.rs.resource;

import com.mycompany.csa_jax.rs.data.DataStore;
import com.mycompany.csa_jax.rs.exceptions.LinkedResourceNotFoundException;
import com.mycompany.csa_jax.rs.exceptions.RoomNotEmptyException;
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
public class RoomResource{
    
    private final DataStore DATA = DataStore.getInstance();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms(){
        List<Room> roomList = new ArrayList<>(DATA.getRooms().values());
        return Response.ok(roomList).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newRoom(Room room, @Context UriInfo uriInfo){
        
        String roomId = room.getId();
        // check for duplicated rooms or empty ids
        if(roomId == null || roomId.trim().isEmpty() || DATA.getRoom(roomId) != null){
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
            throw new LinkedResourceNotFoundException("Room " + id + " does not exist");
        }
        
        return Response.ok(room).build();
    }
    
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoomById(@PathParam("roomId") String id){
        
        Room room = DATA.getRoom(id);
        
        // return a 404 not found if the given id does not return a valid Froom
        if (room == null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Not Found");
            errorMap.put("message", "A room with the given ID does not exist");
            
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMap)
                    .build();
        }
        
        if(!DATA.removeRoom(id)){
            throw new RoomNotEmptyException("Cannot delete room " + id + ". It is occupied by sensors");
        }
        
        return Response.noContent()
                .build();
    }
}
