
import javax.crypto.SecretKey;

import me.eugenio.morphiclib.HelpSerial;
import me.eugenio.morphiclib.HomoSearch;

public class TestSearch {

	public static void main(String[] args) {
		String palavra = "palavra1";
		SecretKey key = HomoSearch.generateKey();
		String texto = "palavra8 palavra1 palavra2 palavra3 palavra4 palavra1";
		System.out.println(texto);
		String resultado = HomoSearch.encrypt(key , texto);
		System.out.println(resultado);
		String palavraEnc = HomoSearch.wordDigest64(key, palavra);
		if(HomoSearch.pesquisa(palavraEnc, resultado)) System.out.println("A palavra "+palavra+" existe");
		else System.out.println("A palavra "+palavra+" n�o existe");
		String subTexto = "palavra2 palavra4";
		if(HomoSearch.searchAll(HomoSearch.encrypt(key, subTexto), resultado)) System.out.println("O subtexto "+subTexto+" existe");
		else System.out.println("O subtexto "+subTexto+" n�o existe");		
		
		String chaveGuardada = HelpSerial.toString(key);
		System.out.println("Chave guardada: "+chaveGuardada);
		// Test with saved key
		SecretKey key2 = (SecretKey) HelpSerial.fromString(chaveGuardada);
		String retorno = HomoSearch.decrypt(key2, resultado);
		System.out.println(retorno);

	}

}
