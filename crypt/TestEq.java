
import java.io.UnsupportedEncodingException;

import javax.crypto.SecretKey;

import me.eugenio.morphiclib.HomoDet;


public class TestEq {

	public static void main(String[] args) {
		//String key = "Bar12345Bar12345Bar12345"; // 128 bit key
		SecretKey key = HomoDet.generateKey();
		System.out.println(HomoDet.decrypt(key, 
				HomoDet.encrypt(key,  "Isto � uma String, testada na vers�o String da API")));
		try {
			System.out.println(new String(
					HomoDet.decrypt(key, HomoDet.encrypt(key,  "Isto � outra String, testada na vers�o array de bytes".getBytes("UTF-8"))),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}        

		String str1 = new String("String 1");
		String str2 = new String("String 2");
		String str3 = new String("String 1");
		String sentenca;
		System.out.println("Teste de igualdade de Cifras na vers�o String");
		if(HomoDet.compare(HomoDet.encrypt(key, str1), HomoDet.encrypt(key, str2)))	
			sentenca = new String("Igual");
		else 	sentenca = new String("Diferente");
		System.out.println(str1+" - "+str2+" - "+sentenca);
		if(HomoDet.compare(HomoDet.encrypt(key, str1), HomoDet.encrypt(key, str3)))	
			sentenca = new String("Igual");
		else 	sentenca = new String("Diferente");
		System.out.println(str1+" - "+str3+" - "+sentenca);
		
		System.out.println("Teste de igualdade de Cifras na vers�o Array de Bytes");
		
		try {
			if(HomoDet.compare(HomoDet.encrypt(key, str1.getBytes("UTF-8")), HomoDet.encrypt(key, str2.getBytes("UTF-8"))))	
				sentenca = new String("Igual");
			else 	sentenca = new String("Diferente");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(str1+" - "+str2+" - "+sentenca);
		
		
		try {
			if(HomoDet.compare(HomoDet.encrypt(key, str1.getBytes("UTF-8")), HomoDet.encrypt(key, str3.getBytes("UTF-8"))))	
				sentenca = new String("Igual");
			else 	sentenca = new String("Diferente");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		System.out.println(str1+" - "+str3+" - "+sentenca);     
	 


	}

}
