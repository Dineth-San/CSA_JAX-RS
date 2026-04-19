/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_jax.rs.exceptions;

/**
 *
 * @author ASUS
 */
public class LinkedResourceNotFoundException extends RuntimeException{
    
    public LinkedResourceNotFoundException(String message){
        super(message);
    }
}
