import java.util.Random;

import me.eugenio.morphiclib.HomoOpeInt;

public class Txt {

	public static void main(String[] args) {
		HomoOpeInt ope = new HomoOpeInt("Ola");
		/*
		for(int i = 1; i < 2; i++){
			System.out.println("Linha "+i+" - "+ope.encrypt((long) i*10));
		}
		*/
		Random r = new Random(1234567890);
		long numRange = Long.MAX_VALUE/2;
		long numDomain = Integer.MAX_VALUE;
		long k = numRange/2;
		double resposta = ope.hgd(k , numDomain, numRange-numDomain, r);
		System.out.println("Resposta: "+resposta);
	}

}
