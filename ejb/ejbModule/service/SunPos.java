/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import de.horatio.common.HoraTime;
import sonnenformel.AzimuthZenithAngle;
import sonnenformel.Grena3;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author duemchen
 */
public class SunPos {

    //ortsfeste Konstanten
    final static double lat = 53.106350117569;
    double lon = 12.894292481831371;
    double deltaT = 68;

    public void printSonnenstand(Date d) {
        AzimuthZenithAngle sunA = getAzZen(d);
        System.out.println(d + "  " + sunA.getAzimuth() + "   " + (90 - sunA.getZenithAngle()));
    }

    public double getZenith(Date d) {
        AzimuthZenithAngle az = getAzZen(d);
        double result = 90 - az.getZenithAngle();
        result = Math.round(100.0 * result) / 100.0;
        return result;
    }

    public double getAzimuth(Date d) {
        AzimuthZenithAngle az = getAzZen(d);
        double result = az.getAzimuth();
        result = Math.round(100.0 * result) / 100.0;
        return result;

    }

    private AzimuthZenithAngle getAzZen(Date d) {
        GregorianCalendar t = new GregorianCalendar(TimeZone.getTimeZone("CEST"));
        t.setTime(d);
        t.setTimeZone(TimeZone.getTimeZone("CEST"));
        return Grena3.calculateSolarPosition(t, lat, lon, deltaT, 1000, 20);
    }

    public static void main(String[] args) throws Exception {
        SunPos sun = new SunPos();
        sun.printSonnenstand(HoraTime.strToDateTime("24.03.2017 11:00"));
    }

}
