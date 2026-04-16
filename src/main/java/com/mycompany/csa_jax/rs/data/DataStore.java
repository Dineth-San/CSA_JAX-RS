/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_jax.rs.data;

import com.mycompany.csa_jax.rs.models.Room;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author ASUS
 */
// this class follows the singleton pattern to model a database
public class DataStore {
    
    // this is the only instance of this class
    private static final DataStore DATA = new DataStore();
    
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
//    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
//    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();
    
    private DataStore(){}
    
    public static DataStore getInstance(){
        return DATA;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }
    
    public void addRoom(Room room){
        // add a new room to the hashmap
        rooms.put(room.getId(), room);
    }
    
    public Room getRoom(String id){
        return rooms.get(id);
    }

//    public Map<String, Sensor> getSensors() {
//        return sensors;
//    }
//
//    public Map<String, List<SensorReading>> getReadings() {
//        return readings;
//    }
    
    
}
