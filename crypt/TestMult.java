import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import me.eugenio.morphiclib.HomoMult;

public class TestMult {

	public static void main(String[] args) {
		BigInteger original = new BigInteger("10");
		System.out.println("Original: "+original);
		KeyPair keyPair = HomoMult.generateKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		BigInteger cifrado = HomoMult.encrypt(publicKey, original);
		System.out.println("Cifrado: "+cifrado);	
		BigInteger decifrado = HomoMult.decrypt(privateKey, cifrado);
		System.out.println("Decifrado: "+decifrado);
		BigInteger quadradoCifrado = HomoMult.multiply(cifrado, cifrado, publicKey);// acha o quadrado
		BigInteger quadrado = HomoMult.decrypt(privateKey, quadradoCifrado);
		System.out.println("Quadrado: "+quadrado);	
		// Test serializable
		String chaveGuardada = HomoMult.stringFromKey(keyPair);
		KeyPair keyPair2 = HomoMult.keyFromString(chaveGuardada);
		RSAPublicKey publicKey2 = (RSAPublicKey) keyPair2.getPublic();
		RSAPrivateKey privateKey2 = (RSAPrivateKey) keyPair2.getPrivate();
		quadradoCifrado = HomoMult.encrypt(publicKey2, quadrado);;// acha o quadrado
		quadrado = HomoMult.decrypt(privateKey2, quadradoCifrado);
		System.out.println("Quadrado: "+quadrado+"( depois de guardar chave)");	
	}

}
