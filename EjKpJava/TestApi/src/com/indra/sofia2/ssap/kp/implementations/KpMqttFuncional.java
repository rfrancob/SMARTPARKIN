/*******************************************************************************
 * © Indra Sistemas, S.A.
 *  2013 - 2014  SPAIN
 *  
 *  All rights reserved
 ******************************************************************************/
package com.indra.sofia2.ssap.kp.implementations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fusesource.mqtt.client.QoS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.indra.sofia2.ssap.kp.Kp;
import com.indra.sofia2.ssap.kp.Listener4SIBIndicationNotifications;
import com.indra.sofia2.ssap.kp.SSAPMessageGenerator;
import com.indra.sofia2.ssap.kp.config.MQTTConnectionConfig;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.indra.sofia2.ssap.ssap.SSAPQueryType;
import com.indra.sofia2.ssap.ssap.body.SSAPBodyReturnMessage;

public class KpMqttFuncional {
	
	private static Log log = LogFactory.getLog(KpGatewayRestFuncional.class);
	
	private final static String HOST="sofia2.com";
	private final static int PORT=1880;
	
	private final static String TOKEN = "e5e8a005d0a248f1ad2cd60a821e6838";
	private final static String KP_INSTANCE = "KPTestTemperatura:KPTestTemperatura01";
	
	private final static String ONTOLOGY_NAME = "TestSensorTemperatura";
	private final static String ONTOLOGY_INSTANCE = "{ \"Sensor\": { \"geometry\": { \"coordinates\": [ 40.512967, -3.67495 ], \"type\": \"Point\" }, \"assetId\": \"S_Temperatura_00066\", \"measure\": 25, \"timestamp\": { \"$date\": \"2014-04-29T08:24:54.005Z\"}}}";
	
	private final static String ONTOLOGY_UPDATE = "{\"Sensor\": { \"geometry\": { \"coordinates\": [ 40.512967, -3.67495 ], \"type\": \"Point\" }, \"assetId\": \"S_Temperatura_00066\", \"measure\": 20, \"timestamp\": { \"$date\": \"2014-04-29T08:24:54.005Z\"}}}";
	private final static String ONTOLOGY_UPDATE_WHERE = "{Sensor.assetId:\"S_Temperatura_00066\"}";
	
	private final static String ONTOLOGY_QUERY_NATIVE = "{Sensor.measure:{$gt:10}}";
	
	private final static String ONTOLOGY_INSERT_SQLLIKE = "insert into TestSensorTemperatura(geometry, assetId, measure, timestamp) values (\"{ 'coordinates': [ 40.512967, -3.67495 ], 'type': 'Point' }\", \"S_Temperatura_00066\", 15, \"{ '$date': '2014-04-29T08:24:54.005Z'}\")";
	private final static String ONTOLOGY_UPDATE_SQLLIKE = "update TestSensorTemperatura set measure = 20 where Sensor.assetId = \"S_Temperatura_00066\"";
	
	private final static String ONTOLOGY_QUERY_SQLLIKE = "select * from TestSensorTemperatura where Sensor.assetId = \"S_Temperatura_00066\"";
		

	private Kp kp;
	
	@Before
	public void setUpBeforeClass() throws Exception {
		
		MQTTConnectionConfig config=new MQTTConnectionConfig();
		config.setHostSIB(HOST);
		config.setPortSIB(PORT);
		config.setKeepAliveInSeconds(5);
		config.setQualityOfService(QoS.AT_LEAST_ONCE);
		config.setTimeOutConnectionSIB(5000);
		
		
		this.kp=new KpMQTTClient(config);
		
		this.kp.connect();
	}
	
	@After
	public void disconnectAfterClass() throws Exception {
		
		this.kp.disconnect();
	}
	
	@Test
	public void testJoinByTokenLeave(){
		//Genera mensaje de JOIN
		SSAPMessage msgJoin=SSAPMessageGenerator.getInstance().generateJoinByTokenMessage(TOKEN, KP_INSTANCE);
		log.info("Envia mensaje JOIN al SIB: "+msgJoin.toJson());
		
		//Envia el mensaje
		SSAPMessage response=kp.send(msgJoin);
		
		log.info("Recibe respuesta desde el SIB: "+response.toJson());
		
		//Comprueba que el mensaje trae session key
		assertNotSame(response.getSessionKey(), null);
		log.info("Sessionkey recibida: "+ response.getSessionKey());
		
		String sessionKey=response.getSessionKey();
		
		
		//Comprueba el BodyResponse
		SSAPBodyReturnMessage bodyReturn=SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(response.getBody());
		assertEquals(bodyReturn.getData(), sessionKey);
		assertTrue(bodyReturn.isOk());
		assertSame(bodyReturn.getError(), null);
		
		
		//Genera un mensaje de LEAVE
		SSAPMessage msgLeave=SSAPMessageGenerator.getInstance().generateLeaveMessage(sessionKey);
		log.info("Envia mensaje LEAVE al SIB: "+msgLeave.toJson());
		
		//Envia el mensaje
		SSAPMessage responseLeave=kp.send(msgLeave);
		
		log.info("Recive respuesta desde el SIB: "+responseLeave.toJson());
		
		//Comprueba el BodyResponse
		SSAPBodyReturnMessage bodyReturnLeave=SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(responseLeave.getBody());
		assertEquals(bodyReturnLeave.getData(), sessionKey);
		assertTrue(bodyReturnLeave.isOk());
		assertSame(bodyReturnLeave.getError(), null);
	}
	
	@Test
	public void testInsertNative(){
		//Genera mensaje de JOIN
		SSAPMessage msgJoin=SSAPMessageGenerator.getInstance().generateJoinByTokenMessage(TOKEN, KP_INSTANCE);
		
		//Envia el mensaje
		SSAPMessage response=kp.send(msgJoin);
		
		//Comprueba que el mensaje trae session key
		assertNotSame(response.getSessionKey(), null);
		String sessionKey=response.getSessionKey();
		
		
		//Genera un mensaje de INSERT
		SSAPMessage msgInsert=SSAPMessageGenerator.getInstance().generateInsertMessage(sessionKey, ONTOLOGY_NAME, ONTOLOGY_INSTANCE);
		log.info("Envia mensaje INSERT al SIB: "+msgInsert.toJson());
		SSAPMessage responseInsert=kp.send(msgInsert);
		
		
		
		//Checks if insert message was OK in SIB
		SSAPBodyReturnMessage returned = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(responseInsert.getBody());
		assertTrue(returned.isOk());
		log.info("Intancia de ontologia insertada con objectId: "+returned.getData());
		
		
		
		//Genera un mensaje de LEAVE
		SSAPMessage msgLeave=SSAPMessageGenerator.getInstance().generateLeaveMessage(sessionKey);
		
		//Envia el mensaje
		SSAPMessage responseLeave=kp.send(msgLeave);
		
	}
	
	@Test
	public void testUpdateNative(){
		//Genera mensaje de JOIN
		SSAPMessage msgJoin=SSAPMessageGenerator.getInstance().generateJoinByTokenMessage(TOKEN, KP_INSTANCE);
		
		//Envia el mensaje
		SSAPMessage response=kp.send(msgJoin);
		
		//Comprueba que el mensaje trae session key
		assertNotSame(response.getSessionKey(), null);
		String sessionKey=response.getSessionKey();
		
		
		//Genera un mensaje de UPDATE
		SSAPMessage msgUpate=SSAPMessageGenerator.getInstance().generateUpdateMessage(sessionKey, ONTOLOGY_NAME, ONTOLOGY_UPDATE, ONTOLOGY_UPDATE_WHERE);
		log.info("Envia mensaje UPDATE al SIB: "+msgUpate.toJson());
		SSAPMessage responseUpdate=kp.send(msgUpate);
		
		
		
		//Checks if update message was OK in SIB
		SSAPBodyReturnMessage returned = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(responseUpdate.getBody());
		assertTrue(returned.isOk());
		log.info("Intancias de ontologia actualizadas: "+returned.getData());
		
		
		//Genera un mensaje de LEAVE
		SSAPMessage msgLeave=SSAPMessageGenerator.getInstance().generateLeaveMessage(sessionKey);
		
		//Envia el mensaje
		SSAPMessage responseLeave=kp.send(msgLeave);
	}
	
	@Test
	public void testQueryNative(){
		//Genera mensaje de JOIN
		SSAPMessage msgJoin=SSAPMessageGenerator.getInstance().generateJoinByTokenMessage(TOKEN, KP_INSTANCE);
		
		//Envia el mensaje
		SSAPMessage response=kp.send(msgJoin);
		
		//Comprueba que el mensaje trae session key
		assertNotSame(response.getSessionKey(), null);
		String sessionKey=response.getSessionKey();
		
		
		//Genera un mensaje de QUERY
		SSAPMessage msgQuery=SSAPMessageGenerator.getInstance().generateQueryMessage(sessionKey, ONTOLOGY_NAME, ONTOLOGY_QUERY_NATIVE);
		log.info("Envia mensaje QUERY al SIB: "+msgQuery.toJson());
		SSAPMessage responseQuery=kp.send(msgQuery);
		
		
		
		//Checks if update message was OK in SIB
		SSAPBodyReturnMessage returned = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(responseQuery.getBody());
		assertTrue(returned.isOk());
		log.info("Resutado de la query: "+returned.getData());
		
		
		//Genera un mensaje de LEAVE
		SSAPMessage msgLeave=SSAPMessageGenerator.getInstance().generateLeaveMessage(sessionKey);
		
		//Envia el mensaje
		SSAPMessage responseLeave=kp.send(msgLeave);
	}
	
	@Test
	public void testInsertSqlLike(){
		//Genera mensaje de JOIN
		SSAPMessage msgJoin=SSAPMessageGenerator.getInstance().generateJoinByTokenMessage(TOKEN, KP_INSTANCE);
		
		//Envia el mensaje
		SSAPMessage response=kp.send(msgJoin);
		
		//Comprueba que el mensaje trae session key
		assertNotSame(response.getSessionKey(), null);
		String sessionKey=response.getSessionKey();
		
		
		//Genera un mensaje de INSERT
		SSAPMessage msgInsert=SSAPMessageGenerator.getInstance().generateInsertMessage(sessionKey, ONTOLOGY_NAME, ONTOLOGY_INSERT_SQLLIKE, SSAPQueryType.SQLLIKE);
		log.info("Envia mensaje INSERT al SIB: "+msgInsert.toJson());
		SSAPMessage responseInsert=kp.send(msgInsert);
		
		
		
		//Checks if insert message was OK in SIB
		SSAPBodyReturnMessage returned = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(responseInsert.getBody());
		assertTrue(returned.isOk());
		log.info("Intancia de ontologia insertada con objectId: "+returned.getData());
		
		
		
		//Genera un mensaje de LEAVE
		SSAPMessage msgLeave=SSAPMessageGenerator.getInstance().generateLeaveMessage(sessionKey);
		
		//Envia el mensaje
		SSAPMessage responseLeave=kp.send(msgLeave);
		
	}
	
	
	@Test
	public void testUpdateSqlLike(){
		//Genera mensaje de JOIN
		SSAPMessage msgJoin=SSAPMessageGenerator.getInstance().generateJoinByTokenMessage(TOKEN, KP_INSTANCE);
		
		//Envia el mensaje
		SSAPMessage response=kp.send(msgJoin);
		
		//Comprueba que el mensaje trae session key
		assertNotSame(response.getSessionKey(), null);
		String sessionKey=response.getSessionKey();
		
		
		//Genera un mensaje de UPDATE
		SSAPMessage msgUpate=SSAPMessageGenerator.getInstance().generateUpdateMessage(sessionKey, ONTOLOGY_NAME, null, ONTOLOGY_UPDATE_SQLLIKE, SSAPQueryType.SQLLIKE);
		log.info("Envia mensaje UPDATE al SIB: "+msgUpate.toJson());
		SSAPMessage responseUpdate=kp.send(msgUpate);
		
		
		
		//Checks if update message was OK in SIB
		SSAPBodyReturnMessage returned = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(responseUpdate.getBody());
		assertTrue(returned.isOk());
		log.info("Intancias de ontologia actualizadas: "+returned.getData());
		
		
		//Genera un mensaje de LEAVE
		SSAPMessage msgLeave=SSAPMessageGenerator.getInstance().generateLeaveMessage(sessionKey);
		
		//Envia el mensaje
		SSAPMessage responseLeave=kp.send(msgLeave);
	}
	
	
	@Test
	public void testQuerySql(){
		//Genera mensaje de JOIN
		SSAPMessage msgJoin=SSAPMessageGenerator.getInstance().generateJoinByTokenMessage(TOKEN, KP_INSTANCE);
		
		//Envia el mensaje
		SSAPMessage response=kp.send(msgJoin);
		
		//Comprueba que el mensaje trae session key
		assertNotSame(response.getSessionKey(), null);
		String sessionKey=response.getSessionKey();
		
		
		//Genera un mensaje de QUERY
		SSAPMessage msgQuery=SSAPMessageGenerator.getInstance().generateQueryMessage(sessionKey, ONTOLOGY_NAME, ONTOLOGY_QUERY_SQLLIKE, SSAPQueryType.SQLLIKE);
		log.info("Envia mensaje QUERY al SIB: "+msgQuery.toJson());
		SSAPMessage responseQuery=kp.send(msgQuery);
		
		
		
		//Checks if update message was OK in SIB
		SSAPBodyReturnMessage returned = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(responseQuery.getBody());
		assertTrue(returned.isOk());
		log.info("Resutado de la query: "+returned.getData());
		
		
		//Genera un mensaje de LEAVE
		SSAPMessage msgLeave=SSAPMessageGenerator.getInstance().generateLeaveMessage(sessionKey);
		
		//Envia el mensaje
		SSAPMessage responseLeave=kp.send(msgLeave);
	}
	
	
	public static boolean indicationReceived=false;
	
	@Test
	public void testSubscribeUnsubscribe() throws Exception{
		
		//Hace JOIN al SIB
		SSAPMessage msgJoin=SSAPMessageGenerator.getInstance().generateJoinByTokenMessage(TOKEN, KP_INSTANCE);
		SSAPMessage responseJoin=kp.send(msgJoin);
		
		String sessionKey=responseJoin.getSessionKey();
		
		//Registra un listener para recibir notificaciones
		kp.addListener4SIBNotifications(new Listener4SIBIndicationNotifications() {
			
			@Override
			public void onIndication(String messageId, SSAPMessage ssapMessage) {
				
				log.info("Recibe mensaje INDICATION para la suscripción con identificador: "+messageId+" with indication message: "+ssapMessage.toJson());
				
				KpMqttFuncional.indicationReceived=true;
			
				SSAPBodyReturnMessage indicationMessage=SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(ssapMessage.getBody());
				
				assertNotSame(indicationMessage.getData(), null);
				assertTrue(indicationMessage.isOk());
				assertSame(indicationMessage.getError(), null);
				
			}
		});
		
		
		//Envia el mensaje de SUBSCRIBE
		
		SSAPMessage msg=SSAPMessageGenerator.getInstance().generateSubscribeMessage(sessionKey, ONTOLOGY_NAME, 100, ONTOLOGY_QUERY_SQLLIKE, SSAPQueryType.SQLLIKE);
		
		SSAPMessage msgSubscribe = kp.send(msg);
		
		SSAPBodyReturnMessage responseSubscribeBody = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(msgSubscribe.getBody());
		
		assertNotSame(responseSubscribeBody.getData(), null);
		assertTrue(responseSubscribeBody.isOk());
		assertSame(responseSubscribeBody.getError(), null);
		
		//Recupera el id de suscripcion
		final String subscriptionId=responseSubscribeBody.getData();
		
		
		//Envia un mensaje INSERT para recibir la notificacion de suscripcion
		SSAPMessage msgInsert=SSAPMessageGenerator.getInstance().generateInsertMessage(sessionKey, ONTOLOGY_NAME, ONTOLOGY_INSERT_SQLLIKE, SSAPQueryType.SQLLIKE);
		log.info("Envia mensaje INSERT al SIB: "+msgInsert.toJson());
		SSAPMessage responseInsert=kp.send(msgInsert);
		
		SSAPBodyReturnMessage returned = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(responseInsert.getBody());
		assertTrue(returned.isOk());
		
		
		//Comprueba si se ha recibido el mensaje de notificación
		Thread.sleep(5000);
		assertTrue(indicationReceived);
		
		
		//Envia el mensaje de UNSUBSCRIBE
		SSAPMessage msgUnsubscribe=SSAPMessageGenerator.getInstance().generateUnsubscribeMessage(sessionKey, ONTOLOGY_NAME, subscriptionId);
		
		SSAPMessage responseUnsubscribe=kp.send(msgUnsubscribe);
		SSAPBodyReturnMessage responseUnSubscribeBody = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(responseUnsubscribe.getBody());
		
		assertEquals(responseUnSubscribeBody.getData(), "");
		assertTrue(responseUnSubscribeBody.isOk());
		assertSame(responseUnSubscribeBody.getError(), null);
		
	}
	
	
	
}
