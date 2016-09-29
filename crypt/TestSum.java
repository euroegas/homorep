import java.math.BigInteger;

import me.eugenio.morphiclib.HelpSerial;
import me.eugenio.morphiclib.HomoAdd;
import me.eugenio.morphiclib.PaillierKey;

public class TestSum {

	public static void main(String[] args) {

		try {
			PaillierKey pk = HomoAdd.generateKey();
			pk.printValues();
			BigInteger big1 = new BigInteger("22");
			BigInteger big2 = new BigInteger("33");		
			BigInteger big1Code = HomoAdd.encrypt(big1, pk);
			BigInteger big2Code = HomoAdd.encrypt(big2, pk);
			System.out.println("big1:     "+big1);
			System.out.println("big2:     "+big2);
			System.out.println("big1Code: "+big1Code);
			System.out.println("big2Code: "+big2Code);			
			BigInteger big3Code = HomoAdd.sum(big1Code, big2Code, pk.getNsquare());
			System.out.println("big3Code: "+big3Code);			
			BigInteger big3 = HomoAdd.decrypt(big3Code, pk);
			System.out.println("Resultado = "+big3.intValue());
			System.out.println("Teste de subtrac��o");
			BigInteger op1 = new BigInteger("5");
			System.out.println("Op1 ="+op1);
			BigInteger op2 = new BigInteger("3");
			System.out.println("Op2 ="+op2);
			BigInteger op1Code = HomoAdd.encrypt(op1, pk);
			BigInteger op2Code = HomoAdd.encrypt(op2, pk);	
			BigInteger op3Code = HomoAdd.dif(op1Code, op2Code, pk.getNsquare());	
			// Test key serialization
			String chaveGuardada ="";

			chaveGuardada = HelpSerial.toString(pk);

			System.out.println("Chave guardada: "+chaveGuardada);
			// Test with saved key
			PaillierKey pk2 = null;
			BigInteger op3 = null;
			pk2 = (PaillierKey) HelpSerial.fromString(chaveGuardada);
			op3 = HomoAdd.decrypt(op3Code, pk2);
			System.out.println("Subtracao: "+op3);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}
