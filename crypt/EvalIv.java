

import javax.crypto.SecretKey;

import me.eugenio.morphiclib.HomoRand;


public class EvalIv {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		long endTime = 0;
		for(int i = 0; i < 1000; i++){
			System.out.println("Round: "+i);
			SecretKey key = HomoRand.generateKey();
			endTime = System.currentTimeMillis();
			System.out.println("Generate Key in: "+(endTime-startTime));
			startTime = endTime;
			byte[] iV = HomoRand.generateIV();
			endTime = System.currentTimeMillis();
			System.out.println("Generate Iv in: "+(endTime-startTime));
			startTime = endTime;
	
		}


	}

}
