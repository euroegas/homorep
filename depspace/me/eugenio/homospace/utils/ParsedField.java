package me.eugenio.homospace.utils;


public class ParsedField implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private boolean hasErrorVar = false;
	private String fieldVar;
	private String operationVar;
	
	public void setField(String newValue){
		fieldVar = newValue;
		
	}
	
	public void setOperation(String newValue){
		operationVar = newValue;
		
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public boolean hasError() {
		return hasErrorVar;
	}


	public String field() {
		return fieldVar;
	}
	public String operation() {
		return operationVar;
	}

	private void getData(int pos, String fieldString){
		fieldVar = fieldString.substring(pos);
		return;
	}
	
	private int skipBlank(String string, int position){
		
		while(position < string.length() && string.charAt(position) == ' ') position++;
		return position;
	}
	
	private int getOperation(String fieldString){
		if(fieldString.length() < 1) return 0;
		StringBuilder sb = new StringBuilder();		
		int pos = skipBlank(fieldString, 0);		
		char first = fieldString.charAt(pos);
		char second;
		switch (first) {
		case '\\': return 1;
		case  '.':
		case  '=':
		case  '%':
		case  '+':
		case  '&':
			pos++;
			pos= skipBlank(fieldString, pos);
			sb.append(first);
			break;
		case '<':
			sb.append(first);
			pos++;
			pos= skipBlank(fieldString, pos);
			second = fieldString.charAt(pos);
			if (pos >= fieldString.length()) break;
			if(second == '>' || second == '='){
				pos++;
				pos= skipBlank(fieldString, pos);
				sb.append(second);
			}
			break;
		case '>':
			sb.append(first);
			pos++;
			pos= skipBlank(fieldString, pos);
			if (pos >= fieldString.length()) break;			
			second = fieldString.charAt(pos);
			if(second == '>' || second == '='){
				pos++;
				pos= skipBlank(fieldString, pos);
				sb.append(second);
			}
			break;	
		}
		operationVar = sb.toString();
		//System.out.println("OperationVar: "+operationVar);
		return pos;
	}
	
	private void init(String fieldString){
		//System.out.println("ParseField: "+fieldString);
		int pos = getOperation(fieldString);
		if(pos < fieldString.length()) getData(pos, fieldString);
		else {
			//hasErrorVar = true;
			fieldVar = "";
			//operationVar = "";
		}
	}
	
	public ParsedField(String fieldString) {
		init(fieldString);
	}
	
	public ParsedField(String fieldString, boolean parse){
		if(parse) init(fieldString); else{
			this.operationVar = "";
			this.fieldVar = fieldString;
		}
	}

}
