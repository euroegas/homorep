package me.eugenio.homospace.Dispatcher;

import java.util.Collection;

import depspace.client.DepSpaceAccessor;
import depspace.extension.EDSExtensionRegistration;
import depspace.general.DepSpaceException;
import depspace.general.DepTuple;
import me.eugenio.homospace.extension.EDSHomoExtension;


public class EDSHomo extends DepSpaceHomo {
	

	

	public EDSHomo(int id, String tsName, boolean createSpace, String basePath) throws DepSpaceException {
		super(id, tsName, createSpace);
		EDSExtensionRegistration.registerExtension(admin, EDSHomoExtension.class, basePath);	
	}

	public DepSpaceAccessor getAccessor() {
		return depSpace;
	}
	
	@Override
	public String enviaPesquisa(String[] linha) throws Exception {
		DepTuple template = DepTuple.createTuple((Object[]) linha);
		DepTuple result = null;
		Collection<DepTuple> list = depSpace.rdAll(template, 100);
		for(DepTuple tup : list)
			result = tup;
		String resultado = result.toStringTuple();
		return resultado;
	}
	

	
}
