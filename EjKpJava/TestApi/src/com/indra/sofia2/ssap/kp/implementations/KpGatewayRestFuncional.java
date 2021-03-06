/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.ssap.kp.implementations;


import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import com.indra.sofia2.ssap.kp.implementations.rest.SSAPResourceAPI;
import com.indra.sofia2.ssap.kp.implementations.rest.exception.ResponseMapperException;
import com.indra.sofia2.ssap.kp.implementations.rest.resource.SSAPResource;



public class KpGatewayRestFuncional {
	
	private static Log log = LogFactory.getLog(KpGatewayRestFuncional.class);
	
	private final static String SERVICE_URL="http://sofia2.com/sib/services/api_ssap/";
	
	private final static String TOKEN = "e5e8a005d0a248f1ad2cd60a821e6838";
	private final static String KP_INSTANCE = "KPTestTemperatura:KPTestTemperatura01";
	
	private final static String ONTOLOGY_NAME = "TestSensorTemperatura";
	private final static String ONTOLOGY_INSTANCE = "{ \"Sensor\": { \"geometry\": { \"coordinates\": [ 40.512967, -3.67495 ], \"type\": \"Point\" }, \"assetId\": \"S_Temperatura_00066\", \"measure\": 25, \"timestamp\": { \"$date\": \"2014-04-29T08:24:54.005Z\"}}}";
	private final static String ONTOLOGY_UPDATE = "{ \"_id\":{\"$oid\": \"<ObjId>\"}, \"Sensor\": { \"geometry\": { \"coordinates\": [ 40.512967, -3.67495 ], \"type\": \"Point\" }, \"assetId\": \"S_Temperatura_00066\", \"measure\": 20, \"timestamp\": { \"$date\": \"2014-04-29T08:24:54.005Z\"}}}";
	private final static String ONTOLOGY_QUERY_NATIVE_CRITERIA = "{\"Sensor.assetId\": \"S_Temperatura_00066\"}";
	private final static String ONTOLOGY_QUERY_NATIVE_STATEMENT = "db.TestSensorTemperatura.find({\"Sensor.assetId\": \"S_Temperatura_00066\"})";
	private final static String ONTOLOGY_QUERY_SQLLIKE = "select * from TestSensorTemperatura where Sensor.assetId = \"S_Temperatura_00066\"";
	private final static String ONTOLOGY_INSERT_SQLLIKE = "insert into TestSensorTemperatura(geometry, assetId, measure, timestamp) values (\"{ 'coordinates': [ 40.512967, -3.67495 ], 'type': 'Point' }\", \"S_Temperatura_00066\", 15, \"{ '$date': '2014-04-29T08:24:54.005Z'}\")";
	private final static String ONTOLOGY_DELETE = "{\"_id\":{\"$oid\":\"<ObjId>\"}}";
												  
	
	private SSAPResourceAPI api;
	
	
	@Before
	public void setUpBeforeClass() throws Exception {
		this.api=new SSAPResourceAPI(SERVICE_URL);
	}
	
	@Test
	public void testJoinByTokenLeave(){
		
		//Genera un recurso SSAP con un JOIN
		SSAPResource ssapJoin=new SSAPResource();
		ssapJoin.setJoin(true);
		ssapJoin.setInstanceKP(KP_INSTANCE);
		ssapJoin.setToken(TOKEN);
		
		
		//Hace un POST del recurso, equivalente a una petici�n SSAP JOIN
		Response respJoin=this.api.insert(ssapJoin);
		
		String sessionkey=null;
		log.info("Codigo http retorno JOIN: "+respJoin.getStatus());
		try{
			sessionkey=this.api.responseAsSsap(respJoin).getSessionKey();
			log.info("Sessionkey:"+sessionkey);
			assertEquals(respJoin.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.info(e.getMessage());
			assertEquals(false, true);
		}
		
		if(sessionkey!=null){
			//Genera un recurso SSAP con un LEAVE
			SSAPResource ssapLeave=new SSAPResource();
			ssapLeave.setLeave(true);
			ssapLeave.setSessionKey(sessionkey);
			Response respLeave=this.api.insert(ssapLeave);
			
			log.info("Codigo http retorno LEAVE: "+respLeave.getStatus());
			assertEquals(respLeave.getStatus(), 200);
			
		}
	}
	
	
	@Test
	public void testInsert(){
		
		//Genera un recurso SSAP con un JOIN
		SSAPResource ssapJoin=new SSAPResource();
		ssapJoin.setJoin(true);
		ssapJoin.setInstanceKP(KP_INSTANCE);
		ssapJoin.setToken(TOKEN);
		
		
		//Hace un POST del recurso, equivalente a una petici�n SSAP JOIN
		Response respJoin=this.api.insert(ssapJoin);
		
		String sessionkey=null;
		log.info("Codigo http retorno JOIN: "+respJoin.getStatus());
		try{
			sessionkey=this.api.responseAsSsap(respJoin).getSessionKey();
			log.info("Sessionkey:"+sessionkey);
			assertEquals(respJoin.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.info(e.getMessage());
			assertEquals(false, true);
		}
		
		
		
		
		//Genera un recurso SSAP con un INSERT
		SSAPResource ssapInsert=new SSAPResource();
		ssapInsert.setData(ONTOLOGY_INSTANCE);
		ssapInsert.setSessionKey(sessionkey);
		ssapInsert.setOntology(ONTOLOGY_NAME);
				
		//Hace un POST del recurso, equivalente a una petici�n SSAP INSERT
		Response respInsert=this.api.insert(ssapInsert);
		
		log.info("Codigo http retorno INSERT: "+respInsert.getStatus());
		try{
			String data=this.api.responseAsSsap(respInsert).getData();
			log.info("Instancia de la ontologia insertada en BDTR:"+data);
			assertEquals(respInsert.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.info(e.getMessage());
			assertEquals(false, true);
		}
				
		if(sessionkey!=null){
			//Genera un recurso SSAP con un LEAVE
			SSAPResource ssapLeave=new SSAPResource();
			ssapLeave.setLeave(true);
			ssapLeave.setSessionKey(sessionkey);
			Response respLeave=this.api.insert(ssapLeave);
			
			log.info("Codigo http retorno LEAVE: "+respLeave.getStatus());
			assertEquals(respLeave.getStatus(), 200);
			
		}
	}
	
	@Test
	public void testUpdate(){
		
		//Genera un recurso SSAP con un JOIN
		SSAPResource ssapJoin=new SSAPResource();
		ssapJoin.setJoin(true);
		ssapJoin.setInstanceKP(KP_INSTANCE);
		ssapJoin.setToken(TOKEN);
		
		
		//Hace un POST del recurso, equivalente a una petici�n SSAP JOIN
		Response respJoin=this.api.insert(ssapJoin);
		
		String sessionkey=null;
		log.info("Codigo http retorno JOIN: "+respJoin.getStatus());
		try{
			sessionkey=this.api.responseAsSsap(respJoin).getSessionKey();
			log.info("Sessionkey:"+sessionkey);
			assertEquals(respJoin.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.info(e.getMessage());
			assertEquals(false, true);
		}
		
			
		
		//Genera un recurso SSAP con un INSERT
		SSAPResource ssapInsert=new SSAPResource();
		ssapInsert.setData(ONTOLOGY_INSTANCE);
		ssapInsert.setSessionKey(sessionkey);
		ssapInsert.setOntology(ONTOLOGY_NAME);
				
		//Hace un POST del recurso, equivalente a una petici�n SSAP INSERT
		Response respInsert=this.api.insert(ssapInsert);
		
		log.info("Codigo http retorno INSERT: "+respInsert.getStatus());
		String objectId=null;
		try{
			objectId=this.api.responseAsSsap(respInsert).getData();
			log.info("Instancia de la ontologia insertada en BDTR:"+objectId);
			assertEquals(respInsert.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		//Hace un PUT del recurso recien insertado para actualizarlo
		if(objectId!=null){
			String objId=objectId.replace("{\"_id\":ObjectId(\"", "");
			objId=objId.replace("\")}", "");
			String updateData=ONTOLOGY_UPDATE.replace("<ObjId>", objId);
			
			//Genera un recurso SSAP con un UPDATE
			SSAPResource ssapUpdate=new SSAPResource();
			ssapUpdate.setSessionKey(sessionkey);
			ssapUpdate.setOntology(ONTOLOGY_NAME);
			ssapUpdate.setData(updateData);
			
			Response respUpdate=this.api.update(ssapUpdate);
			log.info("Codigo http retorno UPDATE: "+respUpdate.getStatus());
			assertEquals(respUpdate.getStatus(), 200);
			
		}
				
		if(sessionkey!=null){
			//Genera un recurso SSAP con un LEAVE
			SSAPResource ssapLeave=new SSAPResource();
			ssapLeave.setLeave(true);
			ssapLeave.setSessionKey(sessionkey);
			Response respLeave=this.api.insert(ssapLeave);
			
			log.info("Codigo http retorno LEAVE: "+respLeave.getStatus());
			assertEquals(respLeave.getStatus(), 200);
			
		}
	}
	
	@Test
	public void testQueryByObjectId(){
		
		//Genera un recurso SSAP con un JOIN
		SSAPResource ssapJoin=new SSAPResource();
		ssapJoin.setJoin(true);
		ssapJoin.setInstanceKP(KP_INSTANCE);
		ssapJoin.setToken(TOKEN);
		
		
		//Hace un POST del recurso, equivalente a una petici�n SSAP JOIN
		Response respJoin=this.api.insert(ssapJoin);
		
		String sessionkey=null;
		log.info("Codigo http retorno JOIN: "+respJoin.getStatus());
		try{
			sessionkey=this.api.responseAsSsap(respJoin).getSessionKey();
			log.info("Sessionkey:"+sessionkey);
			assertEquals(respJoin.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		
		
		
		//Genera un recurso SSAP con un INSERT
		SSAPResource ssapInsert=new SSAPResource();
		ssapInsert.setData(ONTOLOGY_INSTANCE);
		ssapInsert.setSessionKey(sessionkey);
		ssapInsert.setOntology(ONTOLOGY_NAME);
				
		//Hace un POST del recurso, equivalente a una petici�n SSAP INSERT
		Response respInsert=this.api.insert(ssapInsert);
		
		log.info("Codigo http retorno INSERT: "+respInsert.getStatus());
		String objectId=null;
		try{
			objectId=this.api.responseAsSsap(respInsert).getData();
			log.info("Instancia de la ontologia insertada en BDTR:"+objectId);
			assertEquals(respInsert.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		//Hace un GET del recurso recien insertado a partir de su objectId
		if(objectId!=null){
			String objId=objectId.replace("{\"_id\":ObjectId(\"", "");
			objId=objId.replace("\")}", "");
			
			Response respQuery=this.api.query(objId, sessionkey, ONTOLOGY_NAME);
			
			assertEquals(respQuery.getStatus(), 200);
			
			log.info("Codigo http retorno QUERY: "+respQuery.getStatus());
			try{
				log.info("Respuesta de la Query: "+this.api.responseAsSsap(respQuery).getData());
			}catch(ResponseMapperException e){
				log.error(e.getMessage());
				assertEquals(false, true);
			}
			
		}
				
		if(sessionkey!=null){
			//Genera un recurso SSAP con un LEAVE
			SSAPResource ssapLeave=new SSAPResource();
			ssapLeave.setLeave(true);
			ssapLeave.setSessionKey(sessionkey);
			Response respLeave=this.api.insert(ssapLeave);
			
			log.info("Codigo http retorno LEAVE: "+respLeave.getStatus());
			assertEquals(respLeave.getStatus(), 200);
			
		}
	}
	
	
	@Test
	public void testQueryNativeCriteria(){
		
		//Genera un recurso SSAP con un JOIN
		SSAPResource ssapJoin=new SSAPResource();
		ssapJoin.setJoin(true);
		ssapJoin.setInstanceKP(KP_INSTANCE);
		ssapJoin.setToken(TOKEN);
		
		
		//Hace un POST del recurso, equivalente a una petici�n SSAP JOIN
		Response respJoin=this.api.insert(ssapJoin);
		
		String sessionkey=null;
		log.info("Codigo http retorno JOIN: "+respJoin.getStatus());
		try{
			sessionkey=this.api.responseAsSsap(respJoin).getSessionKey();
			log.info("Sessionkey:"+sessionkey);
			assertEquals(respJoin.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		
		
		
		//Genera un recurso SSAP con un INSERT
		SSAPResource ssapInsert=new SSAPResource();
		ssapInsert.setData(ONTOLOGY_INSTANCE);
		ssapInsert.setSessionKey(sessionkey);
		ssapInsert.setOntology(ONTOLOGY_NAME);
				
		//Hace un POST del recurso, equivalente a una petici�n SSAP INSERT
		Response respInsert=this.api.insert(ssapInsert);
		
		log.info("Codigo http retorno INSERT: "+respInsert.getStatus());
		String objectId=null;
		try{
			objectId=this.api.responseAsSsap(respInsert).getData();
			log.info("Instancia de la ontologia insertada en BDTR:"+objectId);
			assertEquals(respInsert.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		//Hace un GET del recurso recien insertado 
		if(objectId!=null){
			Response respQuery=this.api.query(sessionkey, ONTOLOGY_NAME, ONTOLOGY_QUERY_NATIVE_CRITERIA, null, "NATIVE");
			
			assertEquals(respQuery.getStatus(), 200);
			
			log.info("Codigo http retorno QUERY: "+respQuery.getStatus());
			try{
				log.info("Respuesta de la Query: "+this.api.responseAsSsap(respQuery).getData());
			}catch(ResponseMapperException e){
				log.error(e.getMessage());
				assertEquals(false, true);
			}
			
		}
				
		if(sessionkey!=null){
			//Genera un recurso SSAP con un LEAVE
			SSAPResource ssapLeave=new SSAPResource();
			ssapLeave.setLeave(true);
			ssapLeave.setSessionKey(sessionkey);
			Response respLeave=this.api.insert(ssapLeave);
			
			log.info("Codigo http retorno LEAVE: "+respLeave.getStatus());
			assertEquals(respLeave.getStatus(), 200);
			
		}
	}
	
	
	
	@Test
	public void testQueryNativeStatement(){
		
		//Genera un recurso SSAP con un JOIN
		SSAPResource ssapJoin=new SSAPResource();
		ssapJoin.setJoin(true);
		ssapJoin.setInstanceKP(KP_INSTANCE);
		ssapJoin.setToken(TOKEN);
		
		
		//Hace un POST del recurso, equivalente a una petici�n SSAP JOIN
		Response respJoin=this.api.insert(ssapJoin);
		
		String sessionkey=null;
		log.info("Codigo http retorno JOIN: "+respJoin.getStatus());
		try{
			sessionkey=this.api.responseAsSsap(respJoin).getSessionKey();
			log.info("Sessionkey:"+sessionkey);
			assertEquals(respJoin.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		
		
		
		//Genera un recurso SSAP con un INSERT
		SSAPResource ssapInsert=new SSAPResource();
		ssapInsert.setData(ONTOLOGY_INSTANCE);
		ssapInsert.setSessionKey(sessionkey);
		ssapInsert.setOntology(ONTOLOGY_NAME);
				
		//Hace un POST del recurso, equivalente a una petici�n SSAP INSERT
		Response respInsert=this.api.insert(ssapInsert);
		
		log.info("Codigo http retorno INSERT: "+respInsert.getStatus());
		String objectId=null;
		try{
			objectId=this.api.responseAsSsap(respInsert).getData();
			log.info("Instancia de la ontologia insertada en BDTR:"+objectId);
			assertEquals(respInsert.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		//Hace un GET del recurso recien insertado 
		if(objectId!=null){
			Response respQuery=this.api.query(sessionkey, ONTOLOGY_NAME, ONTOLOGY_QUERY_NATIVE_STATEMENT, null, "NATIVE");
			
			assertEquals(respQuery.getStatus(), 200);
			
			log.info("Codigo http retorno QUERY: "+respQuery.getStatus());
			try{
				log.info("Respuesta de la Query: "+this.api.responseAsSsap(respQuery).getData());
			}catch(ResponseMapperException e){
				log.error(e.getMessage());
				assertEquals(false, true);
			}
			
		}
				
		if(sessionkey!=null){
			//Genera un recurso SSAP con un LEAVE
			SSAPResource ssapLeave=new SSAPResource();
			ssapLeave.setLeave(true);
			ssapLeave.setSessionKey(sessionkey);
			Response respLeave=this.api.insert(ssapLeave);
			
			log.info("Codigo http retorno LEAVE: "+respLeave.getStatus());
			assertEquals(respLeave.getStatus(), 200);
			
		}
	}
	
	
	@Test
	public void testQuerySQLLIKEStatement(){
		
		//Genera un recurso SSAP con un JOIN
		SSAPResource ssapJoin=new SSAPResource();
		ssapJoin.setJoin(true);
		ssapJoin.setInstanceKP(KP_INSTANCE);
		ssapJoin.setToken(TOKEN);
		
		
		//Hace un POST del recurso, equivalente a una petici�n SSAP JOIN
		Response respJoin=this.api.insert(ssapJoin);
		
		String sessionkey=null;
		log.info("Codigo http retorno JOIN: "+respJoin.getStatus());
		try{
			sessionkey=this.api.responseAsSsap(respJoin).getSessionKey();
			log.info("Sessionkey:"+sessionkey);
			assertEquals(respJoin.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		
		
		
		//Genera un recurso SSAP con un INSERT
		SSAPResource ssapInsert=new SSAPResource();
		ssapInsert.setData(ONTOLOGY_INSTANCE);
		ssapInsert.setSessionKey(sessionkey);
		ssapInsert.setOntology(ONTOLOGY_NAME);
				
		//Hace un POST del recurso, equivalente a una petici�n SSAP INSERT
		Response respInsert=this.api.insert(ssapInsert);
		
		log.info("Codigo http retorno INSERT: "+respInsert.getStatus());
		String objectId=null;
		try{
			objectId=this.api.responseAsSsap(respInsert).getData();
			log.info("Instancia de la ontologia insertada en BDTR:"+objectId);
			assertEquals(respInsert.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		//Hace un GET del recurso recien insertado 
		if(objectId!=null){
			Response respQuery=this.api.query(sessionkey, ONTOLOGY_NAME, ONTOLOGY_QUERY_SQLLIKE, null, "SQLLIKE");
			
			assertEquals(respQuery.getStatus(), 200);
			
			log.info("Codigo http retorno QUERY: "+respQuery.getStatus());
			try{
				log.info("Respuesta de la Query: "+this.api.responseAsSsap(respQuery).getData());
			}catch(ResponseMapperException e){
				log.error(e.getMessage());
				assertEquals(false, true);
			}
			
		}
				
		if(sessionkey!=null){
			//Genera un recurso SSAP con un LEAVE
			SSAPResource ssapLeave=new SSAPResource();
			ssapLeave.setLeave(true);
			ssapLeave.setSessionKey(sessionkey);
			Response respLeave=this.api.insert(ssapLeave);
			
			log.info("Codigo http retorno LEAVE: "+respLeave.getStatus());
			assertEquals(respLeave.getStatus(), 200);
			
		}
	}
	
	@Test
	public void testInsertSQLLIKEStatement(){
		
		//Genera un recurso SSAP con un JOIN
		SSAPResource ssapJoin=new SSAPResource();
		ssapJoin.setJoin(true);
		ssapJoin.setInstanceKP(KP_INSTANCE);
		ssapJoin.setToken(TOKEN);
		
		
		//Hace un POST del recurso, equivalente a una petici�n SSAP JOIN
		Response respJoin=this.api.insert(ssapJoin);
		
		String sessionkey=null;
		log.info("Codigo http retorno JOIN: "+respJoin.getStatus());
		try{
			sessionkey=this.api.responseAsSsap(respJoin).getSessionKey();
			log.info("Sessionkey:"+sessionkey);
			assertEquals(respJoin.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		
		Response respQuery=this.api.query(sessionkey, ONTOLOGY_NAME, ONTOLOGY_INSERT_SQLLIKE, null, "SQLLIKE");
		
		assertEquals(respQuery.getStatus(), 200);
		
		log.info("Codigo http retorno INSERT SQL: "+respQuery.getStatus());
		try{
			log.info("Respuesta del Insert: "+this.api.responseAsSsap(respQuery).getData());
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
			
		
				
		if(sessionkey!=null){
			//Genera un recurso SSAP con un LEAVE
			SSAPResource ssapLeave=new SSAPResource();
			ssapLeave.setLeave(true);
			ssapLeave.setSessionKey(sessionkey);
			Response respLeave=this.api.insert(ssapLeave);
			
			log.info("Codigo http retorno LEAVE: "+respLeave.getStatus());
			assertEquals(respLeave.getStatus(), 200);
			
		}
	}
	
	
	@Test
	public void testDeleteByObjectId(){
		//Genera un recurso SSAP con un JOIN
		SSAPResource ssapJoin=new SSAPResource();
		ssapJoin.setJoin(true);
		ssapJoin.setInstanceKP(KP_INSTANCE);
		ssapJoin.setToken(TOKEN);
		
		
		//Hace un POST del recurso, equivalente a una petici�n SSAP JOIN
		Response respJoin=this.api.insert(ssapJoin);
		
		String sessionkey=null;
		log.info("Codigo http retorno JOIN: "+respJoin.getStatus());
		try{
			sessionkey=this.api.responseAsSsap(respJoin).getSessionKey();
			log.info("Sessionkey:"+sessionkey);
			assertEquals(respJoin.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		
		
		
		//Genera un recurso SSAP con un INSERT
		SSAPResource ssapInsert=new SSAPResource();
		ssapInsert.setData(ONTOLOGY_INSTANCE);
		ssapInsert.setSessionKey(sessionkey);
		ssapInsert.setOntology(ONTOLOGY_NAME);
				
		//Hace un POST del recurso, equivalente a una petici�n SSAP INSERT
		Response respInsert=this.api.insert(ssapInsert);
		
		log.info("Codigo http retorno INSERT: "+respInsert.getStatus());
		String objectId=null;
		try{
			objectId=this.api.responseAsSsap(respInsert).getData();
			log.info("Instancia de la ontologia insertada en BDTR:"+objectId);
			assertEquals(respInsert.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		//Hace un DELETE del recurso recien insertado a partir de su objectId
		if(objectId!=null){
			String objId=objectId.replace("{\"_id\":ObjectId(\"", "");
			objId=objId.replace("\")}", "");
			
			Response respQuery=this.api.deleteOid(objId, sessionkey, ONTOLOGY_NAME);
			
			assertEquals(respQuery.getStatus(), 200);
			
			log.info("Codigo http retorno DELETE: "+respQuery.getStatus());
			
		}
				
		if(sessionkey!=null){
			//Genera un recurso SSAP con un LEAVE
			SSAPResource ssapLeave=new SSAPResource();
			ssapLeave.setLeave(true);
			ssapLeave.setSessionKey(sessionkey);
			Response respLeave=this.api.insert(ssapLeave);
			
			log.info("Codigo http retorno LEAVE: "+respLeave.getStatus());
			assertEquals(respLeave.getStatus(), 200);
			
		}
		
	}
	
	
	@Test
	public void testDelete(){
		//Genera un recurso SSAP con un JOIN
		SSAPResource ssapJoin=new SSAPResource();
		ssapJoin.setJoin(true);
		ssapJoin.setInstanceKP(KP_INSTANCE);
		ssapJoin.setToken(TOKEN);
		
		
		//Hace un POST del recurso, equivalente a una petici�n SSAP JOIN
		Response respJoin=this.api.insert(ssapJoin);
		
		String sessionkey=null;
		log.info("Codigo http retorno JOIN: "+respJoin.getStatus());
		try{
			sessionkey=this.api.responseAsSsap(respJoin).getSessionKey();
			log.info("Sessionkey:"+sessionkey);
			assertEquals(respJoin.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		
		
		
		//Genera un recurso SSAP con un INSERT
		SSAPResource ssapInsert=new SSAPResource();
		ssapInsert.setData(ONTOLOGY_INSTANCE);
		ssapInsert.setSessionKey(sessionkey);
		ssapInsert.setOntology(ONTOLOGY_NAME);
				
		//Hace un POST del recurso, equivalente a una petici�n SSAP INSERT
		Response respInsert=this.api.insert(ssapInsert);
		
		log.info("Codigo http retorno INSERT: "+respInsert.getStatus());
		String objectId=null;
		try{
			objectId=this.api.responseAsSsap(respInsert).getData();
			log.info("Instancia de la ontologia insertada en BDTR:"+objectId);
			assertEquals(respInsert.getStatus(), 200);
			
		}catch(ResponseMapperException e){
			log.error(e.getMessage());
			assertEquals(false, true);
		}
		
		//Hace un DELETE del recurso recien insertado a partir de su objectId
		if(objectId!=null){
			String objId=objectId.replace("{\"_id\":ObjectId(\"", "");
			objId=objId.replace("\")}", "");
			String deleteData=ONTOLOGY_DELETE.replace("<ObjId>", objId);
			
			//Genera un recurso SSAP con un INSERT
			SSAPResource ssapDelete=new SSAPResource();
			ssapDelete.setData(deleteData);
			ssapDelete.setSessionKey(sessionkey);
			ssapDelete.setOntology(ONTOLOGY_NAME);
			
			Response respQuery=this.api.delete(ssapDelete);
			
			assertEquals(respQuery.getStatus(), 200);
			
			log.info("Codigo http retorno DELETE: "+respQuery.getStatus());
			
		}
				
		if(sessionkey!=null){
			//Genera un recurso SSAP con un LEAVE
			SSAPResource ssapLeave=new SSAPResource();
			ssapLeave.setLeave(true);
			ssapLeave.setSessionKey(sessionkey);
			Response respLeave=this.api.insert(ssapLeave);
			
			log.info("Codigo http retorno LEAVE: "+respLeave.getStatus());
			assertEquals(respLeave.getStatus(), 200);
			
		}
		
	}
	
	
}
