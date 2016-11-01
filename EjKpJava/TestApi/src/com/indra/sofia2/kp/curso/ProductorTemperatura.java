package com.indra.sofia2.kp.curso;

import javax.ws.rs.core.Response;

import com.indra.sofia2.ssap.kp.implementations.rest.SSAPResourceAPI;
import com.indra.sofia2.ssap.kp.implementations.rest.exception.ResponseMapperException;
import com.indra.sofia2.ssap.kp.implementations.rest.resource.SSAPResource;

public class ProductorTemperatura {
	
	public static void main(String args[]) throws ResponseMapperException{
		
		SSAPResourceAPI api=new SSAPResourceAPI("http://sofia2.com/sib/services/api_ssap/");
		
		SSAPResource ssapResource=new SSAPResource();
		ssapResource.setJoin(true);
		ssapResource.setToken("");
		ssapResource.setInstanceKP("");
		
		Response responseJoin=api.insert(ssapResource);
		
		if(responseJoin.getStatus() == 200){
			String sessionKey=api.responseAsSsap(responseJoin).getSessionKey();
			
			
			System.out.println("Sessionkey recibida "+sessionKey);
			
			SSAPResource ssapResourceMedida=new SSAPResource();
			ssapResourceMedida.setSessionKey(sessionKey);
			ssapResourceMedida.setOntology("SensorTemperaturaEjBienvenida");
			
			for(int i=0;i<10;i++){
				String temperatura = Double.toString(Math.random()*100);
				String instanciaOntologia="\"Sensor\": { \"geometry\": {\"coordinates\": [40.512967, -3.67495 ],\"type\": \"Point\" },\"assetId\": \"S_Temperatura_00066\", \"measure\": "+temperatura+", \"timestamp\": {\"$date\": \"2014-04-29T08:24:54.005Z\" }}";
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

}
