/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

/**
 *
 * @author duemchen
 */
public class TestSinus {

    public static void main(String[] args) throws Exception {
        double c = Math.asin(Math.sin(Math.toRadians(90)) * Math.cos(Math.toRadians(10)));
        //System.out.println(c);
        c = Math.toDegrees(c);
        System.out.println(c);
    }
}
