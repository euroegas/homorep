
import me.eugenio.morphiclib.HomoOpeInt;

public class TestOpeInt {

	public static void main(String[] args) {
		HomoOpeInt ope = new HomoOpeInt("Ola Palerma");
		long resultado;
		int contrario;
		for(int i = -10; i < 10; i++){
			resultado =ope.encrypt(i);
			contrario = ope.decrypt(resultado);
			System.out.println("Linha "+i+" - plano "+i+" cifra = "+resultado+" decifra = "+contrario);
		}

	}

}
