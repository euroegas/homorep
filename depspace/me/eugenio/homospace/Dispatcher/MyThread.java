package me.eugenio.homospace.Dispatcher;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collection;

import depspace.client.DepSpaceAccessor;
import depspace.general.DepSpaceException;
import depspace.general.DepTuple;
import me.eugenio.homospace.coders.SuperTuple;
import me.eugenio.homospace.utils.Utilitarios;
import me.eugenio.morphiclib.HelpSerial;

public class MyThread extends Thread {
    protected Socket socket;
    protected DepSpaceAccessor accessor;
    public MyThread(Socket clientSocket, DepSpaceAccessor accessor) {
        this.socket = clientSocket;
        this.accessor = accessor;
    }

    public void run() {
        InputStream inp = null;
        BufferedReader brinp = null;
        DataOutputStream out = null;
		DepTuple template, tuple;
		String retorno = "Operacao desconhecida";
		//System.out.println("My Thread: vou ler o socket" );
        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
			e.printStackTrace();
            return;
        }
        String line ="";
		try {
			line = brinp.readLine();        
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//System.out.println("MyThread: li o socket: "+line );
		SuperTuple superTuple = (SuperTuple) HelpSerial.fromString(line);
		String[] fieldStrings = superTuple.getStringFields();
		String comando = superTuple.getCommand();
		//System.out.println("My Thread: Vou executar comando ("+comando+")");
		if(comando.equals("out")) {
			tuple = DepTuple.createTuple((Object[])superTuple.getOnlyFields());
			try {
				this.accessor.out(tuple);
				retorno = Utilitarios.criaMensagem(superTuple, "Envio concluido com sucesso");
			} catch (DepSpaceException e) {
				retorno = Utilitarios.criaMensagem(superTuple, "Erro na opera��o out");							
				e.printStackTrace();
			}
			
		} else if(comando.equals("rdp")) {

			template = DepTuple.createTuple((Object[])fieldStrings);
			DepTuple dt;
			try {
				dt = accessor.rdp(template);
				if(dt==null)
					retorno = Utilitarios.criaMensagem(superTuple, "N�o h� match");
				else {	
					Object[] auxiliar = dt.getFields();
					retorno = Utilitarios.arrayToSuper(superTuple, auxiliar);
					//System.out.println(retorno);
				}
			} catch (DepSpaceException e) {
				retorno = Utilitarios.criaMensagem(superTuple, "Erro na opera��o rdp");
				e.printStackTrace();
			}
			
		} else if(comando.equals("inp")) {
			//System.out.println("Opera��o inp");
			template = DepTuple.createTuple((Object[])fieldStrings);
			DepTuple dt;
			try {
				dt = accessor.inp(template);
				if(dt==null)
					retorno = Utilitarios.criaMensagem(superTuple,"N�o h� match");
				else {
					Object[] auxiliar = dt.getFields();
					retorno = Utilitarios.arrayToSuper(superTuple, auxiliar);;
				}
			} catch (DepSpaceException e) {
				retorno = Utilitarios.criaMensagem(superTuple,"Erro na opera��o inp");
				e.printStackTrace();
			}
			
		}  else if(comando.equals("rdAll")) {
			
			//System.out.println("Opera��o rdAll");
			template = DepTuple.createTuple((Object[])fieldStrings);
			Collection<DepTuple> list=null;	
			try {
				list = accessor.rdAll(template, 0);
				if(list.isEmpty())
					retorno = Utilitarios.criaMensagem(superTuple, "N�o h� match");
				else {
					retorno = Utilitarios.arraysToSuper(superTuple, list);;
					
					
				}
			} catch (DepSpaceException e) {
				retorno = Utilitarios.criaMensagem(superTuple, "Erro na opera��o rdAll");
				e.printStackTrace();
			}
		}else if(comando.equals("inAll")) {
			
			//System.out.println("Opera��o inAll");
			template = DepTuple.createTuple((Object[])fieldStrings);
			Collection<DepTuple> list=null;	
			try {
				list = accessor.inAll(template);
				if(list.isEmpty())
					retorno = Utilitarios.criaMensagem(superTuple, "N�o h� match");
				else {
					retorno = Utilitarios.arraysToSuper(superTuple, list);;
					
					
				}
			} catch (DepSpaceException e) {
				retorno = Utilitarios.criaMensagem(superTuple, "Erro na opera��o rdAll");
				e.printStackTrace();
			}
		
		} else retorno = Utilitarios.criaMensagem(superTuple, retorno);
		
        try {
        	//System.out.println("My Thread: vou retornar resultado: "+retorno);
			out.writeBytes(retorno + "\n\r");
	        out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        //System.out.println("My Thread: terminei" );
		
    }
    
    
}