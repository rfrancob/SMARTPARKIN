import javax.ws.rs.core.Response;

import com.indra.sofia2.ssap.kp.implementations.rest.SSAPResourceAPI;
import com.indra.sofia2.ssap.kp.implementations.rest.exception.ResponseMapperException;
import com.indra.sofia2.ssap.kp.implementations.rest.resource.SSAPResource;

public class ConnectionFactory {
	private SSAPResourceAPI api;
	private SSAPResource ssapResource;
	private Response responseJoin;
	private String sessionKey;
	private SSAPResource ssapResourceMedida;
	ConnectionFactory(){
		System.setProperty("java.net.useSystemProxies", "true");
		System.setProperty("http.proxyHost", "localhost");
		System.setProperty("http.proxyPort", "8123");
		
		this.api  = new SSAPResourceAPI("http://sofia2.com/sib/services/api_ssap/");
		this.ssapResource = new SSAPResource();
		ssapResource.setJoin(true);
		ssapResource.setToken("6640dea04a3d4fe99b1d962ea11d77b0");
		ssapResource.setInstanceKP("KpVagaTesteKP:VagasDesc");
		responseJoin=api.insert(ssapResource);
		
		if(responseJoin.getStatus() == 200){
		try {
			this.sessionKey=api.responseAsSsap(responseJoin).getSessionKey();
		} catch (ResponseMapperException e) {
			e.printStackTrace();
		}
		this.ssapResourceMedida =new SSAPResource();
		ssapResourceMedida.setSessionKey(sessionKey);
		ssapResourceMedida.setOntology("KpVagaTeste");
		}else{
			this.sessionKey = null;
		}
		
	}
	public SSAPResource getSsapResourceMedida() {
		return ssapResourceMedida;
	}
	public SSAPResourceAPI getApi() {
		return api;
	}


}
