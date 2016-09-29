package tests;


import me.filesystem.Coder;
import me.filesystem.SendToURL;



public class TestSend {
	
	static String word = "teste";

	public static void main(String[] args) {
		String[] chaves = Coder.readKeys();	
		System.out.println(Coder.encode(word, "pesquisavel", chaves));
		String encWord = ":"+Coder.searchableWord(word, chaves);
		SendToURL sendToURL = new SendToURL("http://localhost/executa.php", "word", encWord);
		String[] resposta = sendToURL.comunicate();
		for (String linha : resposta) System.out.println("Recebi: "+linha);
	}

}
