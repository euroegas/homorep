package me.filesystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class SendToURL {
	private String urlStr;
	private String action;
	private String param;
	
	public String[] comunicate(){
		System.out.println("Communicate: "+action+" "+param);
		String[] result = new String[0];
	    URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    Map<String,Object> params = new LinkedHashMap<>();
	    params.put("action", action);
	    params.put("param", param);

	    StringBuilder postData = new StringBuilder();
	    for (Map.Entry<String,Object> param : params.entrySet()) {
	        if (postData.length() != 0) postData.append('&');
	        try {
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
		        postData.append('=');
		        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    }
	    String urlParameters = postData.toString();
	    URLConnection conn = null;
		try {
			conn = url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    conn.setDoOutput(true);

	    OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(conn.getOutputStream());
		    writer.write(urlParameters);
		    writer.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	    String line;
	    BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    try {
	    	ArrayList<String> lista = new ArrayList<String>();
	    	line = reader.readLine();
			while (line != null) {
				lista.add(line);
			    line = reader.readLine();
			}
			result = lista.toArray(new String[lista.size()]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			writer.close();
		    reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    System.out.println("Communicate result length: "+result.length);
		return result;
		
	}
	
	public SendToURL(String urlStr, String action, String param) {
		this.urlStr = urlStr;
		this.action = action;
		this.param = param;

	}
	

}
