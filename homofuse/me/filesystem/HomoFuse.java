package me.filesystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import jnr.ffi.Pointer;
import jnr.ffi.types.mode_t;
import jnr.ffi.types.off_t;
import jnr.ffi.types.size_t;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.FuseStubFS;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.FuseFileInfo;

public class HomoFuse extends FuseStubFS {
	

	private String mountPoint;
	private long uID;
	private long gID;
	private String[] keys;
	private String[] dateList;
	private String[] wordList;
	
	public HomoFuse(String mountPoint){
		this.mountPoint = mountPoint;
		this.uID = getId("u");
		this.gID = getId("g");
		//System.out.println("UID: "+uID+" GID: "+gID);
		Coder.writeFile();
		keys = Coder.readKeys();	
		PathDecoded root = new PathDecoded("/", keys);
		if(!root.exists)	createDir("/");
		else System.out.println("/ already exists");
	}
	

	
    protected long getId(String option) {
    	try {
    	    String userName = System.getProperty("user.name");
    	    String command = "id -"+option+" "+userName;
    	    Process child = Runtime.getRuntime().exec(command);

    	    // Get the input stream and read from it
    	    InputStreamReader in = new InputStreamReader(child.getInputStream());
    	    
    	    BufferedReader br = new BufferedReader((InputStreamReader) in);
    	    String linha = br.readLine();
    	    in.close();
    	    return Long.parseLong(linha);
    	    

    	} catch (IOException e) {
    		return 0;
    	}
    }
    
    /*
    private boolean isDir(String dir){
    	return true;
    }
*/
    
    

    private int addToParent(String path){
    	String parentPath = MyTools.getParentPath(path);
    	if(parentPath.length() == 0) parentPath = "/";
    	System.out.println("Vou criar debaixo de: "+parentPath);
    	PathDecoded p = new PathDecoded(parentPath, keys);
    	if(p.isDir){
    		p.addChild(path);
    		
    		return 0;
    	} else return -ErrorCodes.ENOTDIR();
    }
    
    
    
    private void createPath(String path, boolean isDir, String content, String mode){
    	System.out.println("Create path: "+path);
    	String filename = MyTools.BASEDIR+MyTools.getFileName(path, isDir, keys);
    	System.out.println(filename);
    	String totalContent = MyTools.getMeta(path, mode, uID, gID, keys)+content;
    	System.out.println("Total content: "+totalContent);
    	MyTools.execute ("rm "+filename+" ;echo \""+totalContent+"\" > "+filename);
    	if(!path.equals("/"))
    			addToParent(path);	
    }
    
    private void createPath(String path, boolean isDir, String content){
    	createPath(path, isDir, content, "0777");
    }
    
    private void createDir(String path){
    	createPath(path, true, "");
    }
    
    private void createFile(String path, String content){
    	createPath(path, false , content);
    }
    
    
    
    private void readDirDate(String path, Pointer buf, FuseFillDir filler) {
    	String date1Str = "";
    	String date2Str = "";
    	System.out.println("ReadDirDate para: "+path);
    	if(path.matches("^/date_[2]\\d{3}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])$")){ //mudar no próximo milénio
    		System.out.println("Primeiro caso");
    		date1Str = path.substring(6, 14);
    	} else if(path.matches("^/date_[2]\\d{3}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])_[2]\\d{3}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])$")) {
    		System.out.println("Segundo caso");
    		date1Str = path.substring(6, 14);
    		date2Str = path.substring(15);
    	} else {
    		System.out.println("readDirDate: Invalid date");
    		return;
    	}
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	Date date1Date = null;
		try {
			System.out.println("Parse para: "+date1Str);
			date1Date = sdf.parse(date1Str);
			System.out.println("Date: "+date1Date.toString());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		long days1 =  (date1Date.getTime()/1000/3600+ TimeZone.LONG)/24;
    	Date date2Date = new Date();	
    	long days2 = date2Date.getTime()/1000/3600/24;
    	if(date2Str.length() > 0){
    		try {
				date2Date = sdf.parse(date2Str);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		days2 = (date2Date.getTime()/1000/3600+ TimeZone.LONG)/24;
        } 
    	//System.out.println("Date: "+date2Date.toString());
    	
    	
    	System.out.println("Desde "+days1+" até "+days2);
    	//System.out.println("Aqui 2");
    	String dates = Coder.encode(Long.toString(days1), "ordenavel", keys)+" "+
    			Coder.encode(Long.toString(days2), "ordenavel", keys);
    	//System.out.println("Dates: "+dates);
    	String[] lines = MyTools.executeAction("date", dates);
    	System.out.println("Readir date lines read: "+lines.length);
    	dateList = lines;
		for(int i = 0; i < lines.length; i++){
			String elemento = MyTools.getLastComponent(Coder.decode(lines[i].split(" ")[0], "cifrado", keys));
			System.out.println("readdirDate, elemento é: "+elemento);
			filler.apply(buf, elemento, null, 0);
		}
    }
    
    private void readDirWord(String path, Pointer buf, FuseFillDir filler) {

    	String word = path.substring(1+path.indexOf('_'));
    	System.out.println("ReadDirWord para a palavra: "+word);
    	String encWord = ":"+Coder.searchableWord(word, keys);
    	String[] lines = MyTools.executeAction("word", encWord);
    	System.out.println("Readir word lines read: "+lines.length);
    	wordList = lines;
		for(int i = 0; i < lines.length; i++){
			String elemento = MyTools.getLastComponent(Coder.decode(lines[i].split(" ")[0], "cifrado", keys));
			System.out.println("readdirWord, elemento é: "+elemento);
			filler.apply(buf, elemento, null, 0);
		}
    	return;
    }
    
	public void getattrVar(String path, FileStat stat, String[] list){
		String thisLast = MyTools.getLastComponent(path);
		String metaLine = null;
		for(int i = 0; i < list.length; i++){
			String listLast = MyTools.getLastComponent(Coder.decode(list[i].split(" ")[0], "cifrado",keys));
			//System.out.println("getattrDate: "+path+" "+Coder.decode(dateList[i].split(" ")[0], "cifrado",keys));
			if(thisLast.equals(listLast)){
				metaLine = list[i];
				break;
			} 
		} 
		if(metaLine == null){
			System.out.println("getattrVar: Error, path does not exists: "+path);
			return;
		}
		
		
		String[] splitted = metaLine.split(" ");
	    String subStrID = Coder.decode(splitted[1], "comparavel", keys);
	    uID = Long.parseLong(subStrID.split(" ")[0]);
	    gID = Long.parseLong(subStrID.split(" ")[1]);
	    String subStrMode = Coder.decode(splitted[2], "cifrado", keys);	
	    int mode = Integer.parseInt(subStrMode, 8);
	    String subStrDay = Coder.decode(splitted[3], "ordenavel", keys);
	    int days = Integer.parseInt(subStrDay);
	    String subStrSeconds = Coder.decode(splitted[4], "ordenavel", keys);
	    int seconds = Integer.parseInt(subStrSeconds);
		stat.st_mode.set(FileStat.S_IFREG | mode);
		stat.st_size.set(1000); //rever como faço isto
        stat.st_uid.set(uID);
        stat.st_gid.set(gID);
        stat.st_mtim.tv_sec.set( seconds + (days*24*3600));
	}
	
	
    @Override
    public int getattr(String path, FileStat stat) {
    	System.out.println("getattr: "+path);
    	
    	//if(path.matches("^/date_[2]\\d{3}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])")){
        //if(path.matches("^/date_[2]\\d{3}*")){
    	if(path.startsWith("/date_2")) {
    		if(path.lastIndexOf('/')> 10) {
        		getattrVar(path, stat, dateList);
        		return 0;
    		} else path = "/";

    	}
    	if(path.startsWith("/word_")) {
    		if(path.lastIndexOf('/')> 5) {
        		getattrVar(path, stat, wordList);
        		return 0;
    		} else path = "/";

    	}
    	
    	//if(path.startsWith("/date_") || path.startsWith("/word_")) path = "/"; // special directories treated as root
        PathDecoded p = new PathDecoded(path, keys);
       
        if (p.exists) {
        	System.out.println("Criar parametros de "+path);
        	p.getattr(stat); 
            return 0;
        }
        return -ErrorCodes.ENOENT();
    }
    
    @Override
    public int mkdir(String path, @mode_t long mode) {
    	System.out.println("mkdir: "+path);
        if (path.equals("/")) {
            return -ErrorCodes.EEXIST();
        }
        String parentPath = MyTools.getParentPath(path);
        if(parentPath.startsWith("/date_") || path.startsWith("/word_")) return -ErrorCodes.ENOENT(); 
        PathDecoded parent = new PathDecoded(parentPath, keys);
        if (parent.isDir) {
            createDir(path);
            return 0;
        }
        return -ErrorCodes.ENOENT();
    }
    
    @Override
    public int readdir(String path, Pointer buf, FuseFillDir filter, @off_t long offset, FuseFileInfo fi) {
    	System.out.println("readir: "+path+" offset "+offset);
        filter.apply(buf, ".", null, 0);
        filter.apply(buf, "..", null, 0);
    	if(path.startsWith("/date_")) {
    		readDirDate(path, buf, filter);
    		return 0;
    	}
    	if(path.startsWith("/word_")) {
    		readDirWord(path, buf, filter);
    		return 0;
    	}
        PathDecoded p = new PathDecoded(path, keys);
        if (!p.exists) {
            return -ErrorCodes.ENOENT();
        }
        if (!p.isDir) {
            return -ErrorCodes.ENOTDIR();
        }

        p.readDir(buf, filter);
        return 0;
    }
    
    @Override
    public int create(String path, @mode_t long mode, FuseFileInfo fi) {
        if(path.startsWith("/date_") || path.startsWith("/word_")) return -ErrorCodes.ENOENT(); 
    	PathDecoded f = new PathDecoded(path, keys);
        if (f.exists) {
            return -ErrorCodes.EEXIST();
        }
        String parentPath = MyTools.getParentPath(path);
        PathDecoded parent = new PathDecoded(parentPath, keys);
        if (parent.isDir) {
            createFile(path, "");
            return 0;
        }
        return -ErrorCodes.ENOENT();
    }

    @Override
    public int write(String path, Pointer buf, @size_t long size, @off_t long offset, FuseFileInfo fi) {
        if(path.startsWith("/date_") || path.startsWith("/word_")) return -ErrorCodes.ENOENT(); 
    	PathDecoded p = new PathDecoded(path, keys);
        if (!p.exists) {
            return -ErrorCodes.ENOENT();
        }
        if (p.isDir) {
            return -ErrorCodes.EISDIR();
        }
        return p.write(buf, size, offset);
    }
    
    
    @Override
    public int read(String path, Pointer buf, @size_t long size, @off_t long offset, FuseFileInfo fi) {
        if(path.startsWith("/date_") || path.startsWith("/word_")) return -ErrorCodes.ENOENT(); 
        PathDecoded p = new PathDecoded(path, keys);
        if (!p.exists) {
            return -ErrorCodes.ENOENT();
        }
        if (p.isDir) {
            return -ErrorCodes.EISDIR();
        }
        return p.read(buf, size, offset);
    }
    
    @Override
    public int rename(String path, String newName) {
    	System.out.println("Rename do path: "+path);
    	/*
        MemoryPath p = getPath(path);
        if (p == null) {
            return -ErrorCodes.ENOENT();
        }
        MemoryPath newParent = getParentPath(newName);
        if (newParent == null) {
            return -ErrorCodes.ENOENT();
        }
        if (!(newParent instanceof MemoryDirectory)) {
            return -ErrorCodes.ENOTDIR();
        }
        p.delete();
        p.rename(newName.substring(newName.lastIndexOf("/")));
        ((MemoryDirectory) newParent).add(p);
        */
        return 0;
    }

    @Override
    public int rmdir(String path) {
    	System.out.println("rmdir do path: "+path);
    	unlink(path);
        return 0;
    }

    @Override
    public int truncate(String path, long offset) {
    	System.out.println("Truncate  do path: "+path);
    	/*
        MemoryPath p = getPath(path);
        if (p == null) {
            return -ErrorCodes.ENOENT();
        }
        if (!(p instanceof MemoryFile)) {
            return -ErrorCodes.EISDIR();
        }
        ((MemoryFile) p).truncate(offset);
        */
        return 0;
    }

    @Override
    public int unlink(String path) {
    	System.out.println("Ulink do path: "+path);
        PathDecoded p = new PathDecoded(path, keys);
        if (!p.exists) {
            return -ErrorCodes.ENOENT();
        }
        p.delete();
    	/*
        MemoryPath p = getPath(path);
        if (p == null) {
            return -ErrorCodes.ENOENT();
        }
        p.delete();
        */
        return 0;
    }
    
	public static void main(String[] args) {

	    	if(args.length != 1){
	    		System.out.println("Error - usage: cyptFS mountPoint unsecureRepository");
	    		return;
	    	}
	        HomoFuse cryptFS = new HomoFuse(args[0]);	    		        
	        try {
	            cryptFS.mount(Paths.get(cryptFS.mountPoint), true);
	        } finally {
	            cryptFS.umount();
	        }
	    
	}
	

}
