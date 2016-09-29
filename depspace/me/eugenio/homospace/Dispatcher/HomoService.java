package me.eugenio.homospace.Dispatcher;


public interface HomoService {

	public void start() throws Exception;
	public void close() throws Exception;
	String enviaPesquisa(String[] linha) throws Exception;
	
}
