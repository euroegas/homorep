package me.filesystem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import jnr.ffi.Pointer;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.struct.FileStat;

public class PathDecoded {
	public String path = null;
	public String randomPath = null;
	public String encoded = null;
	public boolean isDir = false;
	public boolean exists = false;
	public long uID;
	public long gID;
	public int mode;
	public int days;
	public int seconds;
	private String[] keys;
	private String metaLine = null;
	
	private void decodePath(){
		String aux = MyTools.rfc3548(Coder.encode(path, "hashed", keys));
		//String command = "ls "+MyTools.BASEDIR;
		String command = "ls "+MyTools.BASEDIR+"*"+aux;
		//System.out.println("Comando: "+command);
		String[] lines = MyTools.execute(command);
		System.out.println("Linhas lidas: "+lines.length);
		if(lines.length > 0) {
			encoded = lines[0];
			isDir = true;
			exists = true;
			if(MyTools.getLastComponent(encoded).startsWith("d")){
				isDir = true;
				readMeta("d"+aux);
			}
			else {
				
				isDir = false;
				readMeta("f"+aux);
			}
			
		} 

	}
	
	private void readMeta(String filename){
		String command = "cat "+MyTools.BASEDIR+filename;
		//System.out.println("Comando: "+command);
		String[] lines = MyTools.execute(command);
		//System.out.println("Linhas lidas: "+lines.length);
		if(lines.length > 0) {
			metaLine = lines[0];
			String[] splitted = metaLine.split(" ");
	    	randomPath = splitted[0];
	    	String subStrID = Coder.decode(splitted[1], "comparavel", keys);
	    	uID = Long.parseLong(subStrID.split(" ")[0]);
	    	gID = Long.parseLong(subStrID.split(" ")[1]);
	    	String subStrMode = Coder.decode(splitted[2], "cifrado", keys);	
	    	this.mode = Integer.parseInt(subStrMode, 8);
	    	String subStrDay = Coder.decode(splitted[3], "ordenavel", keys);
	    	days = Integer.parseInt(subStrDay);
	    	String subStrSeconds = Coder.decode(splitted[4], "ordenavel", keys);
	    	seconds = Integer.parseInt(subStrSeconds);
		}
		
		
	}
	
	public void getattr(FileStat stat){
		if(isDir)stat.st_mode.set(FileStat.S_IFDIR | mode);
		else {
			stat.st_mode.set(FileStat.S_IFREG | mode);
			stat.st_size.set(1000); //rever como faço isto
		}
        stat.st_uid.set(this.uID);
        stat.st_gid.set(this.gID);
        stat.st_mtim.tv_sec.set( this.seconds + (this.days*24*3600));
        System.out.println("getattr de "+path+" mode "+mode+" uID "+uID+" gID "+gID+" seconds "+( this.seconds + (this.days*24*3600)));		
	}
	
    public synchronized void readDir(Pointer buf, FuseFillDir filler) {
		String command = "cat "+encoded;
		System.out.println("PathDecoded readDir - executa: "+command);
		String[] lines = MyTools.execute(command);
		System.out.println("PathDecoded readDir - Linhas lidas: "+lines.length+" "+lines[0]);
		for(int i = 1; i < lines.length; i++){
			String elemento = MyTools.getLastComponent(Coder.decode(lines[i], "cifradir", keys));
			System.out.println("O elemento é: "+elemento);
			filler.apply(buf, elemento, null, 0);
		}

    }
    
    public void addChild(String path){
    	String encPath = Coder.encode(path, "cifradir", keys);
    	String command = "echo \""+encPath+"\" >> "+encoded;
    	MyTools.execute (command);
    }
	
	public PathDecoded(String path, String[]  keys){
		this.keys = keys;
		this.path = path;
		decodePath();
		
	}
	
	public int write(Pointer buffer, long bufSize, long writeOffset) {
		byte[] bytesToWrite = new byte[(int) bufSize];
		buffer.get(0, bytesToWrite, 0, (int) bufSize);
		String conteudo = "";
		try {
			conteudo = new String(bytesToWrite, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Vou escrever em: "+encoded);
		System.out.println(conteudo);
		System.out.println(Coder.encode(conteudo, "pesquisavel", keys));
		String command = "echo \""+metaLine+"\n"+Coder.encode(conteudo, "pesquisavel", keys)+"\" > "+encoded;
		MyTools.execute(command);
        return (int) bufSize;
    }

    public int read(Pointer buffer, long size, long offset) {
    	String command = "cat "+encoded;
    	String[] lines = MyTools.execute(command);
    	String content = lines[1];
    	byte[] contentBytes = null;
		try {
			contentBytes = Coder.decode(content, "pesquisavel", keys).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ByteBuffer contentBuffer = ByteBuffer.wrap(contentBytes);
        int bytesToRead = (int) Math.min(contentBuffer.capacity() - offset, size);
        byte[] bytesRead = new byte[bytesToRead];
        synchronized (this) {
            contentBuffer.position((int) offset);
            contentBuffer.get(bytesRead, 0, bytesToRead);
            buffer.put(0, bytesRead, 0, bytesToRead);
            contentBuffer.position(0); // Rewind
        }
        return bytesToRead;
    }
	
    private void deleteChilds(){
    	String command = "cat "+encoded;
    	String[] lines = MyTools.execute(command);
    	for(int i = 1; i < lines.length; i++){
    		PathDecoded c = new PathDecoded(Coder.decode(lines[i], "cifradir", keys), keys);
    		c.delete();
    		
    	}
    }
    
    public void delete() {
    	if(isDir) deleteChilds();
    	String command = "rm "+encoded;
    	MyTools.execute(command);
    	// remove from parent
    	PathDecoded parent = new PathDecoded(MyTools.getParentPath(path), keys);
		command = "cat "+parent.encoded;
		String[] lines = MyTools.execute(command);
		String allText = lines[0]; // meta
		for(int i = 1; i < lines.length; i++){
			String aux = Coder.encode(Coder.decode(lines[i], "cifradir", keys), "cifrado", keys);
			System.out.println("delete, no dir está ("+aux+") aqui está ("+randomPath+")");
			if(!aux.equals(randomPath)) allText += "\n"+lines[i];
		}
		// write to new file
		command = "echo \""+allText+"\" > "+parent.encoded;
		MyTools.execute(command);
    }
    
}

