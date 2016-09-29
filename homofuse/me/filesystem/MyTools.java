package me.filesystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;


public class MyTools {
	
	public static final String BASEDIR = "folder/";
	//public static final String BASEDIR = "/home/euroegas/mnt/remote/";
    public static String getLastComponent(String path) {
    	//System.out.println("MyTools getLastComponent of: ("+path+")");
    	while (path.substring(path.length() - 1).equals("/")) {
    		path = path.substring(0, path.length() - 1);

    		if (path.isEmpty()) {
    			return "";
    		}
    	}
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static String getParentPath(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }
	
    public static String getFileName(String path, boolean isDir, String[] keys){
    	String filename;
    	if(isDir)filename = "d"+rfc3548(Coder.encode(path, "hashed", keys));
    	else filename = "f"+rfc3548(Coder.encode(path, "hashed", keys));
    	return filename;
    }
    
    
    public static String getMeta(String path, String mode, long uID, long gID, String[] keys){
    	System.out.println("Get Meta of: "+path);
    	String subStrPath = Coder.encode(path, "cifrado", keys);
    	String subStrID = Coder.encode(Long.toString(uID)+" "+Long.toString(gID), "comparavel", keys);
    	String subStrMode = Coder.encode(mode, "cifrado", keys);	
    	Calendar rightNow = Calendar.getInstance();
    	//System.out.println("Miliseconds: "+rightNow.getTimeInMillis());
    	long seconds1970 = rightNow.getTimeInMillis()/1000;
    	//System.out.println("Seconds1970: "+seconds1970);
    	int days1970 = (int) seconds1970/3600/24;
    	int secondsOfDay = (int) seconds1970%(3600*24);
    	//System.out.println("Days1970: "+days1970+" seconds of the day: "+secondsOfDay);
    	String subStrDay = Coder.encode(Integer.toString(days1970), "ordenavel", keys);
    	String subStrSeconds = Coder.encode(Integer.toString(secondsOfDay), "ordenavel", keys);
    	return subStrPath+" "+subStrID+" "+subStrMode+" "+subStrDay+" "+subStrSeconds;
    }
    
    public static String rfc3548(String entrada){ //adaptf Base64
    	String saida = entrada.replace("/", "_");
    	saida = saida.replace("+","-");
    	return saida;
    }
    
    public static String reverseRfc3548(String entrada){ //adaptf Base64
    	String saida = entrada.replace("_", "/");
    	saida = saida.replace("-","+");
    	return saida;
    }
    
    /*
    public static String[] executeShell(String command){
    	String linha;
    	String[] retorno = null;
	    ArrayList <String> lista = new ArrayList<String>();
	    //System.out.println("execute, comando:"+command);
    	try {
    		//System.out.println("exec vou correr: "+command);
			Process child = new ProcessBuilder("/bin/bash", "-c", command).start();
			//Process child = new ProcessBuilder("ssh", "remote@localhost", command).start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
	        while ((linha = stdInput.readLine()) != null) {
	        		//System.out.println("execute:"+linha);
	        		if(linha.length() > 0)
	        			lista.add(linha);
	            }

    	    retorno =  lista.toArray(new String[lista.size()]);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    	//System.out.println("Execute, lines read: "+retorno.length);
    	return retorno;
    }
    */
    
	public static String[] execute(String command) {
		return executeAction("exec", command);

	}
    
	public static String[] executeAction(String action, String command) {
		System.out.println("Command: "+command);
		SendToURL sendToURL = new SendToURL("http://localhost/executa.php", action, command);
		return sendToURL.comunicate();

	}
	
}
