package me.eugenio.homospace.coders;

import java.util.ArrayList;
import java.util.Base64;

import me.eugenio.morphiclib.HelpSerial;
import me.eugenio.homospace.coders.Service;
import me.eugenio.homospace.utils.KeyKeeper;
import me.eugenio.homospace.utils.ParsedField;
import me.eugenio.homospace.utils.Utilitarios;

public class SuperTuple implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<ParsedField> lista = new ArrayList<ParsedField>();
	private boolean error = true;
	private boolean msg = false;
	private String command = "NONE";
	private String id = "NONE";
	
	private String errorXML(String msg){
		return Utilitarios.element("error", msg);
	}
	
	public String[] getStringFields() {
		ArrayList<String> strings = new ArrayList<String>();
		strings.add(id);
		for(ParsedField parsedField: lista){
			strings.add(parsedField.operation()+parsedField.field());		
		}
		
		return strings.toArray(new String[strings.size()]);
		
	}
	
	public String[] getOnlyFields() {
		ArrayList<String> strings = new ArrayList<String>();
		strings.add(id);
		for(ParsedField parsedField: lista){
			strings.add(parsedField.field());		
		}
		
		return strings.toArray(new String[strings.size()]);
		
	}
	
	public void setMsg(){
		this.msg = true;
	}
	
	public String service(){
		return service("localhost");
	}
	
	public String service(String hostname){
		//boolean extended = true;
		String original = this.command;;
		if(error){
			return errorXML("Error in the Tuple, service not accomplished");
		} else if(this.command.equals("crypt")) {
			return errorXML("Command Crypt not sent to server");
		} else if(this.command.equals("rdSum")){
			//extended = true;
			this.command = "rdAll";
			String[] keys = KeyKeeper.readKeys(this.id);
			for(int i = 0; i < lista.size();i++){
				if(lista.get(i).field().equals("*")){
					if(keys[i].split(":")[0].equals("somavel")) lista.get(i).setOperation("+");
				}
			}
		} else if(this.command.equals("rdProd")){
			//extended = true;
			this.command = "rdAll";
			String[] keys = KeyKeeper.readKeys(this.id);
			for(int i = 0; i < lista.size();i++){
				if(lista.get(i).field().equals("*")){
					if(keys[i].split(":")[0].equals("multiplicavel")) lista.get(i).setOperation("&");
				}
			}
		} 
		
		Service service = new Service(hostname);
		//System.out.println("Supertuple: Vou enviar");
		String tupleArrayString = service.servicoOut(HelpSerial.toString(this));
		//System.out.println("Supertuple: Enviei");
		SuperTuple[] tupleArray = (SuperTuple[]) HelpSerial.fromString(tupleArrayString);
		String retorno = "";
		for(SuperTuple tuple: tupleArray){
			tuple.command = original;
			retorno += revertToXML(decodeFields(tuple));
		}
		retorno = Utilitarios.element("list", retorno);
		//System.out.println("Supertuple: Vou retornar resultado");
		
		return retorno;
	}
	
	private SuperTuple decodeFields(SuperTuple tuple){
		if (tuple.msg) return tuple;
		String[] keys = KeyKeeper.readKeys(tuple.id);
		if(keys.length != tuple.lista.size()) {
			System.out.println("O id: "+tuple.id+" não é compatível com o ficheiro "+tuple.id+".ky"+
		 " tamanho chaves: "+keys.length+" tamanho tupla: "+tuple.lista.size());
			for(ParsedField field: tuple.lista){
				System.out.println(field.operation()+" - "+field.field());
				
			}
			error = true;
		}else {
		int i = 0;
		for(ParsedField field: tuple.lista){
			field.setField(Utilitarios.decode(field.field(), keys[i]));
			i++;			
		}
		}
		return tuple;
	}
	
	public void printFields(){
		for(ParsedField parsedField: lista){
			System.out.println("Value: "+parsedField.field()+" operatios: "+parsedField.operation());
		}
	}
	
	public boolean encodeFields() {
		if(!KeyKeeper.writeFile(this)) {
			//System.out.println("Deve haver primeiro um comando de crypt para o id: "+this.getId());
			error = true;
			return false;
		}
		String[] keys = KeyKeeper.readKeys(this.id);
		if(keys.length != lista.size()) {
			System.out.println("O id: "+this.id+" não é compatível com o ficheiro "+this.id+"ky");
			error = true;
			return false;
		}
		int i = 0;
		for(ParsedField field: lista){
			String property = keys[i].split(":")[0];
			if(!Utilitarios.compatible(field.operation(), property)){
				System.out.println("A operação "+field.operation()+" não é compatível com a propriedade "+property);
				error = true;
				return false;
			}
			field.setField(Utilitarios.code(field.field(), keys[i]));
			i++;			
		}
		return true;	
	}
	
	public String revertToXML(){
		return revertToXML(this);
	}
	
	private String revertToXML(SuperTuple tuple) {
		String retorno = Utilitarios.element("error", "Sintax Error");

		if(!error) {
			retorno = Utilitarios.element("command", tuple.command);
			retorno += Utilitarios.element("id", tuple.id);
			String aux = "";
			for(ParsedField field: tuple.lista){
				if(!field.hasError()){
					aux = field.operation()+field.field();
				}
				aux = Utilitarios.baseElement("field", aux);
				retorno += aux;
			}
			retorno = Utilitarios.element("tuple", retorno);
		}
		
		return retorno;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ArrayList<ParsedField> getFields() {
		return lista;
	}

	public boolean isError() {
		return error;
	}

	public String getCommand() {
		return command;
	}

	public String getId() {
		return id;
	}

	public String[] getValues(){
		String[] fields = new String[lista.size()];
		int i = 0;
		for(ParsedField parsedField: lista){
			fields[i++] = parsedField.field();
		}
		return fields;
	}

	public String[] getOperations(){
		String[] operations = new String[lista.size()];
		int i = 0;
		for(ParsedField parsedField: lista){
			operations[i++] = parsedField.operation();
		}
		return operations;
	}
	
	private void init (String commandString, boolean parse) {
		this.command = Utilitarios.descodificaXML(commandString, "command")[0];
		if(this.command == null) return;
		this.id      = Utilitarios.descodificaXML(commandString, "id")[0];
		if(this.id == null) return; 
		error = false;
		String[] readFields = Utilitarios.descodificaXML(commandString, "field");
		for(String field: readFields){
			field = new String(Base64.getDecoder().decode(field.getBytes()));
			ParsedField parsedField = new ParsedField(field, parse);
			//System.out.println("Supertuple: "+parsedField.field()+" - "+parsedField.operation());
			if(!parsedField.hasError()){
				lista.add(parsedField);
			} else {
				error = true;
			}
		}
	}

	public SuperTuple(String commandString, boolean parse) {		
		init(commandString, parse);
	}
	
	public SuperTuple(String commandString) {
		init(commandString, true);
	}
	
}
