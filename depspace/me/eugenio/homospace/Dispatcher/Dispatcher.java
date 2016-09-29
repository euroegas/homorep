package me.eugenio.homospace.Dispatcher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import depspace.client.DepSpaceAccessor;
import depspace.general.DepSpaceConfiguration;
import depspace.general.DepSpaceException;
import me.eugenio.homospace.extension.EDSHomoExtension;

public class Dispatcher {
	    static final int PORT = 9999;

	    public static void main(String args[]) {
	        ServerSocket serverSocket = null;
	        Socket socket = null;

	        try {
	            serverSocket = new ServerSocket(PORT);
	        } catch (IOException e) {
	            e.printStackTrace();

	        }
	        
	        
			int clientID = 9;
			String configDir = "config";
			String extensionCodeDir = "src";
			String name = EDSHomoExtension.TS_NAME;
			// Preparations
			DepSpaceConfiguration.init(configDir);
			EDSHomo espaco;
			try {
				espaco = new EDSHomo(clientID, name, true, extensionCodeDir);
			} catch (DepSpaceException e1) {
				e1.printStackTrace();
				return;
			}
			try {
				espaco.start();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	        DepSpaceAccessor accessor = espaco.getAccessor();
			
			//int contador = 0;
	        while (true) {
	        	//System.out.println("Aguarda: "+contador);
	            try {
	                socket = serverSocket.accept();
	            } catch (IOException e) {
	                System.out.println("I/O error: " + e);
	            }
	            // new thread for a client
	        	//System.out.println("Trata: "+contador++);
	            new MyThread(socket, accessor).start();
	        }
	    }
	
}
