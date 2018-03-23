package database;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class MySession
 */
@Stateless
@LocalBean
public class MySession {

    /**
     * Default constructor. 
     */
    public MySession() {
        System.out.println("masess");
    }

}
