package me.filesystem;

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

import me.eugenio.morphiclib.HomoAdd;
import me.eugenio.morphiclib.HomoDet;
import me.eugenio.morphiclib.HomoMult;
import me.eugenio.morphiclib.HomoOpeInt;
import me.eugenio.morphiclib.HomoRand;
import me.eugenio.morphiclib.HomoSearch;


public class Coder {

	
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
		if(propriedade.equals("hashed")){
			chave = propriedade+":"+HomoDet.stringFromKey(HomoDet.generateKey());
		} else if(propriedade.equals("cifrado")){
			chave = propriedade+":"+HomoRand.stringFromKeyIv(HomoRand.generateKeyIv());
		} else if(propriedade.equals("cifradir")){
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

	public static void writeFile(){
		final String HOMEDIR = getDir();
		String pathStr = HOMEDIR+"cryptfs"+".ky";
		Path path = Paths.get(pathStr);
		if(Files.exists(path)){
			return;
		}
		BufferedWriter writer = null;

		String conteudo = "";
		conteudo += getKey("hashed")+"\n";
		conteudo += getKey("comparavel")+"\n";
		conteudo += getKey("ordenavel")+"\n";
		conteudo += getKey("cifrado")+"\n";
		conteudo += getKey("cifradir")+"\n";
		conteudo += getKey("pesquisavel")+"\n";
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
		return;

	}

	
	public static String[] readKeys(){
		final String HOMEDIR = getDir();
		String pathStr = HOMEDIR+"cryptfs"+".ky";
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
	
	private static String buscaChave(String propriedade, String[] chaves){
		for(String complexo : chaves){
			String[] split = complexo.split(":");
			if(split[0].equals(propriedade)) return split[1];
		}
		return "";
		
	}
	
	public static String decode(String valor, String propriedade, String[] chaves){
		String retorno = "";
		if(valor.length() == 0) return valor;
		String chave = buscaChave(propriedade, chaves);
		//System.out.println("Codifica :"+propriedade+":");
		
		if(propriedade.equals("cifrado")){
			retorno = HomoRand.decrypt(chave, valor);
		} else 	if(propriedade.equals("cifradir")){
			retorno = HomoRand.decrypt(chave, valor);
		} else if(propriedade.equals("claro")){
			retorno =  valor;
		} else if(propriedade.equals("comparavel")){
			retorno =  HomoDet.decrypt(HomoDet.keyFromString(chave), valor);
			//System.out.println("Decode comparavel: "+retorno);
		} else if(propriedade.equals("multiplicavel")){
			retorno =  HomoMult.decrypt(chave, valor);
		} else if(propriedade.equals("somavel")){
			retorno =  HomoAdd.decrypt(chave, valor);
		} else if(propriedade.equals("ordenavel")){
			long chaveL = Long.parseLong(chave);
			HomoOpeInt ope = new HomoOpeInt(chaveL);
			retorno = String.valueOf(ope.decrypt(Long.parseLong(valor)));
		} else if(propriedade.equals("pesquisavel")){
			retorno =  HomoSearch.decrypt(chave, valor);
		}  else {
			retorno = propriedade+":propriedade desconhecida";
		}
		return retorno;
	}
	
	
	public static String encode(String valor, String propriedade, String[] chaves){
		//System.out.println("Encode com propriedade: "+propriedade+" valor: "+valor);
		String retorno = "";
		if(valor.length() == 0) return valor;
		String chave = buscaChave(propriedade, chaves);

		if(propriedade.equals("hashed")){
			retorno = HomoSearch.wordDigest64(HomoSearch.keyFromString(chave), valor);
		} else if(propriedade.equals("cifrado")){
			retorno = HomoRand.encrypt(chave, valor);
		} else if(propriedade.equals("cifradir")){
			retorno = HomoRand.encrypt(chave, valor);
		}  else if(propriedade.equals("claro")){
			retorno =  valor;
		} else if(propriedade.equals("comparavel")){
			//System.out.println("Comparavel: "+valor);
			retorno =  HomoDet.encrypt(HomoDet.keyFromString(chave), valor);
		} else if(propriedade.equals("multiplicavel")){
			retorno =  HomoMult.encrypt(chave, valor);
		} else if(propriedade.equals("somavel")){
			retorno =  HomoAdd.encrypt(chave, valor);
		} else if(propriedade.equals("ordenavel")){
			long chaveL = Long.parseLong(chave);
			HomoOpeInt ope = new HomoOpeInt(chaveL);
			retorno = String.valueOf(ope.encrypt(Integer.parseInt(valor)));
		} else if(propriedade.equals("pesquisavel")){
			retorno =  HomoSearch.encrypt(chave, valor);
		}  else {
			retorno = propriedade+":propriedade desconhecida";
		}
		//System.out.println("Encode, calculei: "+retorno);
		return retorno;
	}
	
	public static String searchableWord(String word, String[] chaves){
		String retorno;
		String chave = buscaChave("pesquisavel", chaves);
		retorno = HomoSearch.wordDigest64(HomoSearch.keyFromString(chave), word);
		return retorno;
	}

}
