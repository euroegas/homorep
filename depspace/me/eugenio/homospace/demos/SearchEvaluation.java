package me.eugenio.homospace.demos;

import java.util.Base64;
import me.eugenio.homospace.coders.SuperTuple;
import me.eugenio.homospace.utils.Utilitarios;

public class SearchEvaluation {


	private static String serverHost = "host";
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

	private static int sendOneCommand(String line){
		line = Utilitarios.inputToXML(line);
		if(line.equals("")){
			System.out.println("Syntax Error - not enough fields\n$ ");
			return 0;
		}
		SuperTuple superTuple = new SuperTuple(line);
		//line = superTuple.revertToXML();
		//System.out.println("O XML reconstruído é: "+line); 
		//String deVolta = recupera(line);
		//System.out.println(deVolta);
		if(!superTuple.encodeFields()) System.out.println("Não foi possível escrever o ficheiro de chaves");
		//line = superTuple.revertToXML();
		//System.out.println("O XML reconstruído é: "+line); 
		//deVolta = recupera(line);
		//System.out.println(deVolta);
		long startTime = System.currentTimeMillis();
		String resposta = superTuple.service(serverHost);
		long endTime = System.currentTimeMillis();
		//System.out.println("Evaluation: Received:"+resposta);
		System.out.println(recupera(resposta));
		return (int) (endTime - startTime);

	}
	
	public static void main(String[] args) {

		String line = "10:out:one two three four five six seven eight ten";
		System.out.println(System.getProperty("user.dir"));
		int tuplesSent = 10;
		int tempo = 0;
		int cryptTempo = sendOneCommand("10:crypt:%");
		for (int i = 0; i < tuplesSent; i++){
			tempo += sendOneCommand(line);
			//System.out.println("Sent: "+line);

		}
		tempo +=  sendOneCommand(line+" twenty");
		int attempts = 10;
		int[] queryTempo = new int[attempts];	
		for(int i = 0; i < attempts; i++) queryTempo[i] = sendOneCommand("10:rdp:%twenty");
		
		int inAllTempo = sendOneCommand("10:inAll:*");
		
		
		System.out.println("Crypt time "+cryptTempo+" miliseconds");
		System.out.println("Insert time "+tempo+" miliseconds");
		for(int i = 0; i < attempts; i++) System.out.println("Query time "+queryTempo[i]+" miliseconds");
		System.out.println("inAll time "+inAllTempo+" miliseconds");		
		
	}

}
