package me.eugenio.homospace.demos;

import java.util.Base64;
import me.eugenio.homospace.coders.SuperTuple;
import me.eugenio.homospace.utils.Utilitarios;

public class OpeEvaluation {


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
		if(!superTuple.encodeFields()) System.out.println("Não foi possível escrever o ficheiro de chaves");
		long startTime = System.currentTimeMillis();
		String resposta = superTuple.service(serverHost);
		long endTime = System.currentTimeMillis();
		System.out.println(recupera(resposta));
		return (int) (endTime - startTime);

	}
	
	public static void main(String[] args) {

		String baseLine = "10:out:";
		String line = "";
		System.out.println(System.getProperty("user.dir"));
		int tuplesSent = 100;
		int tentativas = 31;
		int[] tempo = new int[tentativas];
		int[] queryTempo = new int[tentativas];
		int[] inAllTempo = new int[tentativas];
		for(int j = 0; j < tentativas; j++) {
			tempo[j] = 0;
			queryTempo[j] = 0;
			inAllTempo[j] = 0;
			//int cryptTempo = sendOneCommand("10:crypt:>");
			for (int i = 0; i < tuplesSent; i++){
				line =baseLine+i;
				tempo[j] += sendOneCommand(line);
				//System.out.println("Sent: "+line);
			}
			queryTempo[j] = sendOneCommand("10:rdp:>50");
			inAllTempo[j] = sendOneCommand("10:inAll:*");	
			//System.out.println("Crypt time "+cryptTempo+" miliseconds");
			System.out.println("Insert time "+tempo[j]+" miliseconds");
			System.out.println("Query time "+queryTempo[j]+" miliseconds");
			System.out.println("inAll time "+inAllTempo[j]+" miliseconds");		
		}
		
		for(int j = 0; j < tentativas; j++){
			System.out.println(tempo[j]+" ; "+queryTempo[j]+" ; "+inAllTempo[j]);
		}
		
	}

}
