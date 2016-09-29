package me.eugenio.homospace.coders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Service{
	
	private int portNumber;
	private String hostName;
	private Socket servidor;
	private PrintWriter out;
	private BufferedReader in;
	private boolean error = false;
	
	public Service(String hostname) {
		super();
		//System.out.println("Inicialização");
		this.hostName = hostname;
		init();

	}
	
	public Service() {
		super();
		//System.out.println("Inicializaçao");
		this.hostName = "localhost";
		init();
	}

	private void init(){
		portNumber = 9999;
		//System.out.println("Ligação ao host:"+hostName+" e porto:"+portNumber);
		try {
		    servidor = new Socket(hostName, portNumber);
		    out =     new PrintWriter(servidor.getOutputStream(), true);
		    in =	new BufferedReader( new InputStreamReader(servidor.getInputStream()));
		}
		catch (Exception e) { 
			e.printStackTrace();
			error = true;
			
		}
	}

	
	public String servicoOut(String tupleString) { 
		if(error) return "Erro na liga��o ao Dispatcher - verifique se o processo foi lan�ado";
		this.out.println(tupleString);
		String resposta = "Não houve resposta";
		try {
			//System.out.println("Service: Vou enviar");
			resposta = this.in.readLine();
			//System.out.println("Service: recebi "+resposta);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resposta;
		}

}
