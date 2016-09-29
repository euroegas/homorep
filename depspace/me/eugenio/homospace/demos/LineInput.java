package me.eugenio.homospace.demos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import me.eugenio.homospace.coders.SuperTuple;
import me.eugenio.homospace.utils.Utilitarios;

public class LineInput {


	
	private static String recupera(String line){
		String retorno ="";
		String[] array = Utilitarios.descodificaXML(line, "error");
		if(array.length > 0) return array[0];
		array = Utilitarios.descodificaXML(line, "id");
		if(array.length < 1) return ("Erro na sintaxe do input comprimento: "+array.length);
		String id  = array[0];
		array = Utilitarios.descodificaXML(line, "command");
		if(array.length < 1) return ("Erro na sintaxe do input (command)");
		int numTuplas = array.length;
		String command = array[0];
		array = Utilitarios.descodificaXML(line, "field");
		if(array.length < 1) return ("Erro na sintaxe do input (fields)");
		int numFields = array.length;
		retorno = "";
		for(int i = 0; i < numFields; i++){
			if (i % (numFields / numTuplas) == 0) {
				if(i>0) retorno +="\n";
				retorno += id+":"+command;
			}
			retorno += ":"+ new String(Base64.getDecoder().decode(array[i].getBytes()));
		}
		return retorno;
	}

	public static void main(String[] args) {
		String serverHost = "host";
		if(args.length > 0) serverHost = args[0];
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		System.out.println(System.getProperty("user.dir"));
		while (true){
			System.out.print("$ ");
			try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Lido ("+line+")");
			line = Utilitarios.inputToXML(line);
			if(line.equals("")){
				System.out.println("Syntax Error - not enough fields\n$ ");
				continue;
			}
			System.out.println("Line Input vou enviar:"+line);
			SuperTuple superTuple = new SuperTuple(line);
			line = superTuple.revertToXML();
			System.out.println("O XML reconstru�do �: "+line); 
			String deVolta = recupera(line);
			System.out.println(deVolta);
			if(!superTuple.encodeFields()) System.out.println("N�o foi poss�vel escrever o ficheiro de chaves");
			line = superTuple.revertToXML();
			System.out.println("O XML reconstru�do �: "+line); 
			deVolta = recupera(line);
			System.out.println(deVolta);
			String resposta = superTuple.service(serverHost);
			System.out.println("Line Input: Recebi:"+resposta);
			System.out.println(recupera(resposta));
		}



	}

}
