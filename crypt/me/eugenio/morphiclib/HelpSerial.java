package me.eugenio.morphiclib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
/**
 * 
 * @author Eugenio
 *
 * Utilities for serializable objects from/to base64 Strings
 */
public class HelpSerial {
	
    /** Read the object from Base64 string. */
   public static Object fromString( String s ) {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(  data ) );
	        Object o  = ois.readObject();
	        ois.close();
	        return o;
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

   }

    /** Write the object to a Base64 string. */
    public static String toString( Serializable o ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream( baos );
	        oos.writeObject( o );
	        oos.close();
	        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

    }
}
