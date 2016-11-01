package com.indra.sofia2.kp.curso;

import org.fusesource.mqtt.client.QoS;

import com.indra.sofia2.ssap.kp.Kp;
import com.indra.sofia2.ssap.kp.Listener4SIBIndicationNotifications;
import com.indra.sofia2.ssap.kp.SSAPMessageGenerator;
import com.indra.sofia2.ssap.kp.config.MQTTConnectionConfig;
import com.indra.sofia2.ssap.kp.implementations.KpMQTTClient;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.indra.sofia2.ssap.ssap.SSAPQueryType;
import com.indra.sofia2.ssap.ssap.body.SSAPBodyReturnMessage;

public class ConsumidorTemperatura {
	

	//TODO Configurar IP y puerto del SIB
	private final static String HOST="sofia2.com";
	private final static int PORT=1883;
	
	
	//TODO Configurar Token e Instancia de KP
	private final static String TOKEN = "3b2f5d745c4f45ecb293d55f72d06fe4";
	private final static String KP_INSTANCE = "Prod_TemperaturaEjBienvenida:Prod_TemperaturaEjBienvenida 01";
	
	
	private final static String ONTOLOGY_NAME = "SensorTemperaturaEjBienvenida";
	
	
	public static void main(String args[]){
		//Configura los par�metros de la conexi�n MQTT
		MQTTConnectionConfig config=new MQTTConnectionConfig();
		config.setHostSIB(HOST);
		config.setPortSIB(PORT);
		config.setKeepAliveInSeconds(5);
		config.setQualityOfService(QoS.AT_LEAST_ONCE);
		config.setTimeOutConnectionSIB(5000);
		
		//Intancia la interfaz del KP con la implementaci�n de MQTT para la configuraci�n indicada
		Kp kp=new KpMQTTClient(config);		
		
		//Crea una conexi�n MQTT f�sica con el SIB
		kp.connect();
		
		
		//Construye un mensaje SSAP JOIN
		SSAPMessage msgJoin=SSAPMessageGenerator.getInstance().generateJoinByTokenMessage(TOKEN, KP_INSTANCE);
		
		//Envia el mensaje JOIN al SIB
		SSAPMessage responseJoin=kp.send(msgJoin);
		
		//Recupera la sessionKey
		String sessionKey=responseJoin.getSessionKey();
		
		
		//Registra un listener para recibir notificaciones para las suscripciones
		kp.addListener4SIBNotifications(new Listener4SIBIndicationNotifications() {
			
			/**
			 * Metodo Invocado por el API cuando se recibe una notificacion de suscripci�n desde el SIB
			 */
			@Override
			public void onIndication(String messageId, SSAPMessage ssapMessage) {
				SSAPBodyReturnMessage indicationMessage=SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(ssapMessage.getBody());
				
				if(indicationMessage.isOk()){
					System.out.println(indicationMessage.getData());
				}
				
			}
		});
		
		
		//Construye un mensaje SSAP SUBSCRIBE
		SSAPMessage msg=SSAPMessageGenerator.getInstance().generateSubscribeMessage(sessionKey, ONTOLOGY_NAME, 100,
				"select * from "+ONTOLOGY_NAME, SSAPQueryType.SQLLIKE);
		
		//Envia el mensaje SUBSCRIBE al SIB
		SSAPMessage msgSubscribe = kp.send(msg);
		
		SSAPBodyReturnMessage responseSubscribeBody = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(msgSubscribe.getBody());
		
		//Queda a la espera de recibir notificaciones
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
