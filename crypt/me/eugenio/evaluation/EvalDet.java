package me.eugenio.evaluation;

import java.io.UnsupportedEncodingException;

import javax.crypto.SecretKey;

import me.eugenio.morphiclib.HomoDet;


public class EvalDet {

	public static void main(String[] args) {
		//String key = "Bar12345Bar12345Bar12345"; // 128 bit key
		SecretKey key = HomoDet.generateKey();
		String auxMessage = "0123456789012345678901234567890123456789012345678901234567890123";
		byte[] message = null;
		try {
			message = auxMessage.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("Starting Deterministic Encryption with message: \n"+auxMessage);
		int voltas = 100000;
		byte[] lixo = null;
		int amostras = 31;
		
		for(int j = 0; j < amostras; j++){

			long startTime = System.currentTimeMillis();
			for (int i = 0; i < voltas; i++){
				lixo = HomoDet.encrypt(key,  message);
			}
			long endTime = System.currentTimeMillis(); 
			System.out.print(voltas+" ; "+(endTime-startTime)+" ; ");
			startTime = System.currentTimeMillis();
			for (int i = 0; i < voltas; i++){
				message = HomoDet.decrypt(key,  lixo);
			}
			endTime = System.currentTimeMillis();
			System.out.print((endTime-startTime)+" ; ");
			byte[] lixo2 = HomoDet.encrypt(key,  message);
			startTime = System.currentTimeMillis();
			boolean lixoBool;
			for (int i = 0; i < voltas; i++){
				try {
					lixoBool = HomoDet.compare(lixo, lixo2);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			endTime = System.currentTimeMillis();
			System.out.println(endTime-startTime);			
			
			
		}

	}

}
