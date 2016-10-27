
import javax.ws.rs.core.Response;


import com.indra.sofia2.ssap.kp.implementations.rest.SSAPResourceAPI;
import com.indra.sofia2.ssap.kp.implementations.rest.exception.ResponseMapperException;
import com.indra.sofia2.ssap.kp.implementations.rest.resource.SSAPResource;

public class FactorySofiaJSON {
	public static void setProxy(){
		System.setProperty("java.net.useSystemProxies", "true");
		System.setProperty("http.proxyHost", "localhost");
		System.setProperty("http.proxyPort", "8123");
	}
	
	
	
	public static void SalvaJSON(String instanciaOntologia) throws ResponseMapperException{
		setProxy();
		
		SSAPResourceAPI api=new SSAPResourceAPI("http://sofia2.com/sib/services/api_ssap/");
		
		SSAPResource ssapResource=new SSAPResource();
		ssapResource.setJoin(true);
		ssapResource.setToken("6640dea04a3d4fe99b1d962ea11d77b0");
		ssapResource.setInstanceKP("KpVagaTesteKP:VagasDesc");
		
		Response responseJoin=api.insert(ssapResource);
		
		if(responseJoin.getStatus() == 200){
			String sessionKey=api.responseAsSsap(responseJoin).getSessionKey();
			
			
			System.out.println("Sessionkey recibida "+sessionKey);
			
			SSAPResource ssapResourceMedida=new SSAPResource();
			ssapResourceMedida.setSessionKey(sessionKey);
			ssapResourceMedida.setOntology("KpVagaTeste");

			ssapResourceMedida.setData(instanciaOntologia);
			
			Response responseInsert=api.insert(ssapResourceMedida);
			
			if(responseInsert.getStatus()!=200){
				System.out.println("Error Insertando");
			}
			
			try {
			   Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
