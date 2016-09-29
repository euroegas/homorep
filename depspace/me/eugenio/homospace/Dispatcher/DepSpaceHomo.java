package me.eugenio.homospace.Dispatcher;

import depspace.client.DepSpaceAccessor;
import depspace.client.DepSpaceAdmin;
import depspace.general.DepSpaceException;
import depspace.general.DepSpaceProperties;


public class DepSpaceHomo implements HomoService {

	protected final DepSpaceAdmin admin;
	protected final DepSpaceAccessor depSpace;
	
	
	public DepSpaceHomo(int id, String tsName, boolean createSpace) throws DepSpaceException {
		this.admin = new DepSpaceAdmin(id);
		this.depSpace = admin.createAccessor(DepSpaceProperties.createDefaultProperties(tsName), createSpace);
	}
	
	
	@Override
    public void start() throws Exception {
		System.out.println("Arranque do serviço DepSpaceHomo");
    }

	@Override
    public void close() throws Exception {
		admin.finalizeAccessor(depSpace);
    }


    
    // #############################
    // # HOMO OPERATIONS #
    // #############################
    
    @Override
    public String enviaPesquisa(String[] linha) throws Exception {
    	return "Fim";
    }
    
    
}
