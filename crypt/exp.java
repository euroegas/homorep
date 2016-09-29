import java.security.SecureRandom;

public class exp {

	public static void main(String[] args) {
		System.out.println("Máximo inteiro: "+Integer.MAX_VALUE);
		System.out.println("Mínimo inteiro: "+Integer.MIN_VALUE);
		System.out.println("Máximo long: "+Long.MAX_VALUE);
		byte[] array  = SecureRandom.getSeed(16);
		System.out.println(array[0]+" "+array[1]+" "+array[2]);
		array  = SecureRandom.getSeed(16);
		System.out.println(array[0]+" "+array[1]+" "+array[2]);		
		

	}

}
