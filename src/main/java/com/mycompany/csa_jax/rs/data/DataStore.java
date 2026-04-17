/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_jax.rs.data;

import com.mycompany.csa_jax.rs.models.Room;
import com.mycompany.csa_jax.rs.models.Sensor;
import com.mycompany.csa_jax.rs.models.SensorReading;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();
    
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
    
    public boolean removeRoom(String id){
        Room room = rooms.get(id);
        if(room == null || !room.getSensorIds().isEmpty()){
            return false;
        }
        for(String sensorId : room.getSensorIds()){
            if(sensors.get(sensorId).getType().equalsIgnoreCase("active")){
                return false;
            }
        }
        return true;
    }

    
    public Map<String, Sensor> getSensors() {
        return sensors;
    }
    
    public Sensor getSensor(String id){
        return sensors.get(id);
    }
    
    public void addSensor(Sensor sensor){
        sensors.put(sensor.getId(), sensor);
    }
    
    public Collection<Sensor> getAllSensors(){
        return sensors.values();
    }
    
    
    public Map<String, List<SensorReading>> getReadings() {
        return readings;
    }
    
    public List<SensorReading> getReadingsById(String id){
        // this ensures that the function returns an empty list if no readings exist
        return readings.getOrDefault(id, new ArrayList<>());
    }
    
    public void addReading(String sensorId, SensorReading reading){
        List<SensorReading> readingValues = readings.getOrDefault(sensorId, new ArrayList<>());
        readingValues.add(reading);
        readings.put(sensorId, readingValues);
    }
}
