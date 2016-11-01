Este ejemplo está listo para ser usado contra sofia2.com con los siguientes datos:

Ontología
SensorTemperaturaEjBienvenida

Kp:
Prod_TemperaturaEjBienvenida
Instancia de kp
Prod_TemperaturaEjBienvenida:Prod_TemperaturaEjBienvenida01


Token de autenticación
3b2f5d745c4f45ecb293d55f72d06fe4


Para poder adaptarlo a tus datos necesitarás seguir los pasos de la página de bienvenida para crear ontología, kp y token propios
Una vez los tengas, para modificar el ejemplo tendrás que introducir los datos en las clases ProductorTemperatura.java y ConsumidorTemperatura.java:
Productor:
		ssapResource.setToken("Tu token");
		ssapResource.setInstanceKP("Tu instancia de token");
		ssapResourceMedida.setOntology("Tu ontología");
Consumidor:
//TODO Configurar Token e Instancia de KP
	private final static String TOKEN = "Tu token";
	private final static String KP_INSTANCE = "Tu instancia de token";
	private final static String ONTOLOGY_NAME = "Tu ontología";		
		