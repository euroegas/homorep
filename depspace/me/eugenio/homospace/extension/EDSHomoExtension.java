package me.eugenio.homospace.extension;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import depspace.extension.EDSBaseExtension;
import depspace.general.Context;
import depspace.general.DepSpaceException;
import depspace.general.DepSpaceOperation;
import depspace.general.DepTuple;
import me.eugenio.homospace.utils.ParsedField;
import me.eugenio.morphiclib.HelpSerial;
import me.eugenio.morphiclib.HomoAdd;
import me.eugenio.morphiclib.HomoMult;
import me.eugenio.morphiclib.HomoSearch;


public class EDSHomoExtension extends EDSBaseExtension {

	public static final String TS_NAME = "Homomorphic Space";
	
	
	// ################
	// # SUBSCRIPTION #
	// ################

	@Override
	public boolean matchesOperation(String tsName, DepSpaceOperation operation, Object arg) {
		if((operation != DepSpaceOperation.RDALL) &&
			(operation != DepSpaceOperation.RDP) &&
			(operation != DepSpaceOperation.INP) &&
			(operation != DepSpaceOperation.INALL)
				) return false;
		return TS_NAME.equals(tsName);
	}
	
	@Override
	public boolean matchesEvent(String tsName, DepSpaceOperation operation, DepTuple tuple) {
		return false;
	}


	// #############
	// # EXECUTION #
	// #############
	
	@Override
	protected Collection<DepTuple> rdAll(DepTuple template, Context ctx) throws DepSpaceException{
		return doCollection(template, ctx);
	}
	
	@Override
	protected Collection<DepTuple> inAll(DepTuple template, Context ctx) throws DepSpaceException{
		Collection<DepTuple> coleccao;
		coleccao = doCollection(template, ctx);
		// Delete Tuples
		Iterator<DepTuple> iter = coleccao.iterator();
		DepTuple lixo;
		while(iter.hasNext()){
			lixo = extensionGate.inp(iter.next());
			if(lixo == null) System.out.println("Attempt to delete inexistent Tuple");
		}
		return coleccao;
	}
	
	@Override
	protected DepTuple rdp(DepTuple template, Context ctx) throws DepSpaceException{
		return doOne(template, ctx);
	}
	
	@Override
	protected DepTuple inp(DepTuple template, Context ctx) throws DepSpaceException{
		DepTuple one = doOne(template, ctx);
		DepTuple lixo = extensionGate.inp(one);
		if(lixo == null) System.out.println("Attempt to delete inexistent Tuple");
		return one ;
	}
	
	private DepTuple doOne(DepTuple template, Context ctx) throws DepSpaceException{
		Collection<DepTuple> coleccao;
		coleccao = doCollection(template, ctx);
		//System.out.println("EDSHOMOEXTENSION - selected "+coleccao.size()+" elements");
		if(coleccao.isEmpty()) return null;
		
		return coleccao.iterator().next();
	}
	

	private Collection<DepTuple> doCollection(DepTuple template, Context ctx) throws DepSpaceException {
		Object[] array = (Object[]) template.getFields();
		//System.out.println("Operacao para um template de tamanho "+array.length);
		String[] valores = new String[array.length];
		String[] comparacoes = new String[array.length];
		String[] auxiliar = new String[array.length];
		for(int i = 0; i < array.length; i++){
			ParsedField parsedField = new ParsedField(array[i].toString());
			valores[i] = parsedField.field();
			comparacoes[i] = parsedField.operation();
			auxiliar[i] = valores[i];
		}
		for(int i = 0; i < array.length; i++) {
			if(!(comparacoes[i].equals("=") || comparacoes[i].length() == 0)) auxiliar[i] = "*";
		}
 		Collection<DepTuple>  mTuple = extensionGate.rdAll(DepTuple.createTuple((Object[]) auxiliar));	
 		if(mTuple.isEmpty())return mTuple;
 		
 		boolean consolida = false;
 		// Trata as comparacoes apagando os n�o match para al�m das EQ
 		for(int i= 0; i < array.length; i++){
 			//System.out.println("("+comparacoes[i]+")");
			Iterator<DepTuple> iter = mTuple.iterator();			
 			if(comparacoes[i].equals(">") || comparacoes[i].equals("<") ||
 			   comparacoes[i].equals(">=") || comparacoes[i].equals("<=") ||
 			  comparacoes[i].equals("<>")){
 				long limiar = Long.parseLong(valores[i].toString()); 
 				while(iter.hasNext()){
 					DepTuple tup = iter.next();
 					Object[] objArray = tup.getFields();
					long naTupla = Long.parseLong(objArray[i].toString());
 					if(comparacoes[i].equals(">") && naTupla <= limiar) iter.remove();
 					else if(comparacoes[i].equals("<") && naTupla >= limiar) iter.remove();
 					else if(comparacoes[i].equals(">=") && naTupla < limiar) iter.remove();
 					else if(comparacoes[i].equals("<=") && naTupla > limiar) iter.remove();
 					else if(comparacoes[i].equals("<>") && naTupla == limiar) iter.remove();
 				}
 			}	
 			if(comparacoes[i].equals("%")){
 				while(iter.hasNext()){
 					DepTuple tup = iter.next();
 					Object[] objArray = tup.getFields();
					String naTupla = objArray[i].toString();
					//System.out.println("Comparacao de: "+valores[i].toString()+" ---- "+naTupla);
 					if(!HomoSearch.searchAll(valores[i].toString(), naTupla)) iter.remove();
 				}
 			}
 			if(comparacoes[i].equals("+") || comparacoes[i].equals("&")){
 				consolida = true;
 			}
 		}
		//System.out.println("No reposit�rio as tuplas eram de tamanho "+ mTuple.iterator().next().getFields().length );				
		if (!consolida) return mTuple;
		String[] arrConsolidado = new String[array.length];
		arrConsolidado[0] = valores[0].toString();
 		for(int i= 0; i < array.length; i++){
 			if(comparacoes[i].equals("=")) arrConsolidado[i] = valores[i].toString();
 			else arrConsolidado[i] = "*";
 			if(comparacoes[i].equals("+")){
 				BigInteger bigConsolidado = new BigInteger("1");
 				boolean primeiro = true;
				for(DepTuple tup : mTuple){
					Object[] objArray = tup.getFields();
					String[] campos = objArray[i].toString().split(":");
					BigInteger naTupla = new BigInteger(campos[0]);
					BigInteger nsquare = new BigInteger(campos[1]);
					if(primeiro) {
						primeiro = false;
						bigConsolidado = naTupla;
					}
					else bigConsolidado = HomoAdd.sum(bigConsolidado, naTupla, nsquare);
					//System.out.println("EDSHomoExtensiom, bigConsolidado: "+bigConsolidado);
				}
				arrConsolidado[i] = bigConsolidado.toString();
 			}
 			if(comparacoes[i].equals("&")){
 				BigInteger bigConsolidado = new BigInteger("1");
 				boolean primeiro = true;
				for(DepTuple tup : mTuple){
					Object[] objArray = tup.getFields();
					String[] campos = objArray[i].toString().split(":");
					BigInteger naTupla = new BigInteger(campos[0]);
					RSAPublicKey publicKey = (RSAPublicKey) HelpSerial.fromString(campos[1]);
					if(primeiro) {
						primeiro = false;
						bigConsolidado = naTupla;
					}
					else bigConsolidado = HomoMult.multiply(bigConsolidado, naTupla, publicKey);
					//System.out.println("EDSHomoExtensiom, bigConsolidado: "+bigConsolidado);
				}
				arrConsolidado[i] = bigConsolidado.toString();
 			}		
 			
 		}
 		DepTuple novaTupla = DepTuple.createTuple((Object[]) arrConsolidado);
 		Collection<DepTuple> mTupleAux = new ArrayList<DepTuple>();
 		mTupleAux.add(novaTupla);
 		return mTupleAux;
	}
	
}
