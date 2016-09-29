package me.eugenio.homospace.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import me.eugenio.morphiclib.HomoDet;
import me.eugenio.morphiclib.HomoMult;
import me.eugenio.morphiclib.HomoOpeInt;
import me.eugenio.morphiclib.HomoRand;
import me.eugenio.morphiclib.HomoSearch;
import me.eugenio.morphiclib.HomoAdd;
import me.eugenio.homospace.coders.SuperTuple;

public class KeyKeeper {
	//private static final String HOMEDIR = "C:/workspace/HomoDepSpace/KeyKeeper/";
	
private static String getDir(){
	String homeDir = System.getProperty("user.dir")+"/KeyKeeper/";
	Path path = Paths.get(homeDir);
	if(Files.exists(path)) return homeDir;
	else{
		boolean success = (new File(homeDir)).mkdirs();
		if(!success) System.out.println("Error - could not create folder for key keeping");
		return homeDir;
	}
	
}

private static String getKey(String propriedade){
		//System.out.println("Propriedade: "+propriedade);
		String chave = "";
		if(propriedade.equals("cifrado")){
			chave = propriedade+":"+HomoRand.stringFromKeyIv(HomoRand.generateKeyIv());
		} else if(propriedade.equals("claro")){
			chave +=  propriedade+":"+"nada";
		} else if(propriedade.equals("comparavel")){
			chave +=  propriedade+":"+HomoDet.stringFromKey(HomoDet.generateKey());
		} else if(propriedade.equals("multiplicavel")){
			chave +=  propriedade+":"+HomoMult.stringFromKey(HomoMult.generateKey());
		} else if(propriedade.equals("somavel")){
			chave +=  propriedade+":"+HomoAdd.stringFromKey(HomoAdd.generateKey());
		} else if(propriedade.equals("ordenavel")){
			chave +=  propriedade+":"+String.valueOf(HomoOpeInt.generateKey());
		} else if(propriedade.equals("pesquisavel")){
			chave +=  propriedade+":"+HomoSearch.stringFromKey(HomoSearch.generateKey());
		}  else {
			chave = propriedade+":propriedade desconhecida";
		}
		//System.out.println(chave);
		return chave;
	}

	public static boolean writeFile(SuperTuple tuple){
		final String HOMEDIR = getDir();
		String pathStr = HOMEDIR+tuple.getId()+".ky";
		Path path = Paths.get(pathStr);
		if(Files.exists(path) && !tuple.getCommand().equals("crypt")) {
			//System.out.println("Ficheiro "+pathStr+" existe");
			return true;
		}
		if(!tuple.getCommand().equals("crypt")) {
			System.out.println("Deve haver primeiro um comando de crypt para o id: "+tuple.getId());
			return false;
		}
		else System.out.println("Ficheiro "+pathStr+" n�o existe, vou criar");	
		BufferedWriter writer = null;
		//System.out.println("1");
		String conteudo = "";
		//System.out.println("2");
		for (ParsedField parsedField: tuple.getFields()){
			String propriedade = "claro"; // Default, random encryption
			if(parsedField.operation().equals("=")) propriedade = "comparavel";
			else if(parsedField.operation().equals("<>")) propriedade = "comparavel";
			else if(parsedField.operation().equals("<")) propriedade = "ordenavel";
			else if(parsedField.operation().equals("<=")) propriedade = "ordenavel";
			else if(parsedField.operation().equals(">")) propriedade = "ordenavel";
			else if(parsedField.operation().equals(">=")) propriedade = "ordenavel";
			else if(parsedField.operation().equals("%")) propriedade = "pesquisavel";
			else if(parsedField.operation().equals("+")) propriedade = "somavel";
			else if(parsedField.operation().equals("&")) propriedade = "multiplicavel";
			else if(parsedField.operation().equals(".")) propriedade = "cifrado";
			conteudo += getKey(propriedade)+"\n";
		}
		//System.out.println("Vou come�ar a escrever");
		try
		{
			writer = new BufferedWriter( new FileWriter(pathStr));
			writer.write( conteudo);
			System.out.println("Vou escrever: "+conteudo);

		}
		catch ( IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( writer != null)
					writer.close( );
			}
			catch ( IOException e)
			{
				e.printStackTrace();
			}
		}
		return true;

	}

	
	public static String[] readKeys(String nome){
		final String HOMEDIR = getDir();
		String pathStr = HOMEDIR+nome+".ky";
		//System.out.println("Vou ler "+pathStr);
        FileReader fileReader;
		try {
			fileReader = new FileReader(pathStr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        try {
			while ((line = bufferedReader.readLine()) != null) {
			    lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
        try {
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return lines.toArray(new String[lines.size()]);
	}
	
	

}
