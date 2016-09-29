package me.eugenio.homospace.utils;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import me.eugenio.morphiclib.HelpSerial;
import me.eugenio.morphiclib.HomoDet;
import me.eugenio.morphiclib.HomoMult;
import me.eugenio.morphiclib.HomoOpeInt;
import me.eugenio.morphiclib.HomoRand;
import me.eugenio.morphiclib.HomoSearch;
import me.eugenio.morphiclib.HomoAdd;
import depspace.general.DepTuple;
import me.eugenio.homospace.coders.SuperTuple;

public class Utilitarios {
	
	public static boolean compatible(String operation, String property){
		if((operation.equals("<") || operation.equals("<=") || operation.equals(">")  || operation.equals(">=")) &&
				!(property.equals("ordenavel") || property.equals("claro")))return false;
		if((operation.equals("+")) &&
				!property.equals("somavel")) return false;	
		if((operation.equals("&")) &&
				!property.equals("multiplicavel")) return false;		
		if((operation.equals("%")) &&
				!property.equals("pesquisavel")) return false;			
		return true;
	}

	public static String arrayToSuper(SuperTuple tuple, Object[] array) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(tuple.getId());
		list.add(tuple.getCommand());
		boolean first = true;
		for(Object obj: array){
			if(first) first = false; //ignore first element that is the id
			else list.add(obj.toString());
		}
		String[] aux = list.toArray(new String[list.size()]);
		String entrada = fieldsToXML(aux);
		SuperTuple[] tupleArray = new SuperTuple[1];
		tupleArray[0] = new SuperTuple(entrada, false);
		return HelpSerial.toString(tupleArray);
	}
	
	public static String arraysToSuper(SuperTuple superTuple, Collection<DepTuple> list) {
		SuperTuple[] tupleArray = new SuperTuple[list.size()];
		int i = 0;
		for(DepTuple tup: list){
			ArrayList<String> stringList = new ArrayList<String>();
			stringList.add(superTuple.getId());
			stringList.add(superTuple.getCommand());
			Object[] array = tup.getFields();
			//System.out.println("Arrays To Super - tamanho: "+array.length);
			boolean first = true;
			for(Object obj: array){
				if(first) first = false; //ignore id
				else stringList.add(obj.toString());
			}
			String[] aux = stringList.toArray(new String[stringList.size()]);
			String entrada = fieldsToXML(aux);
			tupleArray[i] = new SuperTuple(entrada, false);
			i++;

		}
		return HelpSerial.toString(tupleArray);
	}
	
	
	public static String[] descodificaXML(String xML, String tagName) {
		//System.out.println("Descodifica ("+xML+")");
		ArrayList<String> lista = new ArrayList<String>();
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	    InputSource is = new InputSource(new StringReader(xML));
	    Document doc;
		try {
			doc = builder.parse(is);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			return null;
		}
	    NodeList valores = doc.getElementsByTagName(tagName);
	    for( int i = 0; i < valores.getLength(); i++) {
	    	Node no = valores.item(i);
	    	if (no == null) System.out.println("O elemento � nulo");
	    	//System.out.println(no.getTextContent());
	    	lista.add((String) no.getTextContent());
	    }
		String[] retorno = new String[lista.size()];
		retorno = lista.toArray(retorno);
		return retorno;
	}
	
	public static String element( String nome, String entrada) {
		String saida = "<"+nome+">"+entrada+"</"+nome+">";	
		return saida;
	}
	
	public static String baseElement( String nome, String entrada) {		
		return element(nome, Base64.getEncoder().encodeToString(entrada.getBytes()));
	}
	
	public static String decode(String valor, String complexa){
		String retorno = "";
		if(valor.length() == 0) return valor;
		if(valor.equals("*")) return(valor);
		//System.out.println(valor+" "+propriedade+" "+complexa);
		String[] aux = complexa.split(":");
		String propriedade = aux[0];
		String chave = aux[1];
		//System.out.println("Codifica :"+propriedade+":");
		if(propriedade.equals("cifrado")){
			retorno = HomoRand.decrypt(chave, valor);
		} else if(propriedade.equals("claro")){
			retorno =  valor;
		} else if(propriedade.equals("comparavel")){
			retorno =  HomoDet.decrypt(HomoDet.keyFromString(chave), valor);
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
	
	public static String code(String valor, String complexa){
		String retorno = "";
		if(valor.length() == 0) return valor;
		if(valor.equals("*")) return(valor);
		//System.out.println(valor+" "+propriedade+" "+complexa);
		String[] aux = complexa.split(":");
		String propriedade = aux[0];
		String chave = aux[1];
		//System.out.println("Codifica :"+propriedade+":");
		if(propriedade.equals("cifrado")){
			retorno = HomoRand.encrypt(chave, valor);
		} else if(propriedade.equals("claro")){
			retorno =  valor;
		} else if(propriedade.equals("comparavel")){
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
		return retorno;
	}
	
	public static String inputToXML(String line){
		String[] array = line.split(":");

		return fieldsToXML(array);
	}
	
	public static String fieldsToXML(String[] array){
		String retorno = "";
		if(array.length >= 3){
			retorno = Utilitarios.element("command", array[1]);
			retorno += Utilitarios.element("id", array[0]);
			for(int i = 2; i < array.length; i++) {
				//System.out.println(i+" ("+array[i]+")");
				if(array[i] == null) array[i] = new String("*"); else
				if(array[i].length() == 0) array[i] = "*";
				//System.out.println(array[i]);
				retorno += Utilitarios.baseElement("field", array[i]);
			}
			retorno = Utilitarios.element("tuple", retorno);
			//System.out.println(retorno);
		}
		return retorno;
	}
	
	
	public static String criaMensagem(SuperTuple inTuple, String entrada){
		
		String aux = inputToXML(inTuple.getId()+":"+inTuple.getCommand()+":"+entrada);
		SuperTuple tuple = new SuperTuple(aux);
		tuple.setMsg();
		SuperTuple[] tupleArray = new SuperTuple[1];
		tupleArray[0] = tuple;
		return HelpSerial.toString(tupleArray);
	
		
	}
	
	

}

