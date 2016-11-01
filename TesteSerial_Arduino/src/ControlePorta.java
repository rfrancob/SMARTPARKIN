
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.indra.sofia2.ssap.kp.implementations.rest.exception.ResponseMapperException;


public class ControlePorta {
	private OutputStream serialOut;
	private InputStream serialInput;
	private int taxa;
	private String portaCOM;
	private ConnectionFactory con;
	private JsonElement oldElement;
	private int first = 0;
	/**
	 * Construtor da classe ControlePorta
	 * @param portaCOM - Porta COM que será utilizada para enviar os dados para o arduino
	 * @param taxa - Taxa de transferência da porta serial geralmente é 9600
	 */
	public ControlePorta(String portaCOM, int taxa) {
		this.portaCOM = portaCOM;
		this.taxa = taxa;
		this.con = new ConnectionFactory();
		this.initialize();
	}     

	/**
	 * Médoto que verifica se a comunicação com a porta serial está ok
	 */
	private void initialize() {
		try {
			//Define uma variável portId do tipo CommPortIdentifier para realizar a comunicação serial
			CommPortIdentifier portId = null;
			try {
				//Tenta verificar se a porta COM informada existe
				portId = CommPortIdentifier.getPortIdentifier(this.portaCOM);
			}catch (NoSuchPortException npe) {
				//Caso a porta COM não exista será exibido um erro 
				System.out.println("Porta COM não encontrada.");
			}
			//Abre a porta COM 
			SerialPort port = (SerialPort) portId.open("Comunicação serial", this.taxa);
			serialOut = port.getOutputStream();
			serialInput = port.getInputStream();
			port.setSerialPortParams(this.taxa, //taxa de transferência da porta serial 
					SerialPort.DATABITS_8, //taxa de 10 bits 8 (envio)
					SerialPort.STOPBITS_1, //taxa de 10 bits 1 (recebimento)
					SerialPort.PARITY_NONE); //receber e enviar dados
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método que fecha a comunicação com a porta serial
	 */
	public void close() {
		try {
			serialOut.close();
		}catch (IOException e) {
			System.out.println("Não foi possível fechar porta COM. \n Fechar porta COM");
		}
	}

	/**
	 * @param opcao - Valor a ser enviado pela porta serial
	 */
	public void enviaDados(int opcao){
		try {
			serialOut.write(opcao);//escreve o valor na porta serial para ser enviado
		} catch (IOException ex) {
			System.out.println("Não foi possível enviar o dado. \nEnviar dados");
		}
	} 

	public void recebeDados() {
		try {
			//	    	while (serialInput.available()>0) {
			//                System.out.print((char)(serialInput.read()));
			//          }

			int available = serialInput.available();
			byte chunk[] = new byte[available];
			String stringToParse;
			serialInput.read(chunk, 0, available);  
			stringToParse = new String(chunk);


			if(!"".equals(stringToParse)){
				java.util.Date date= new java.util.Date();
				JsonParser jsonParser = new JsonParser();
				JsonElement element = jsonParser.parse(stringToParse);
				SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
				Date now = new Date();
				String strDate = sdfDate.format(now);
				element.getAsJsonObject().addProperty("time", strDate);
				element.getAsJsonObject().addProperty("estacionado", 0);


				if(element.getAsJsonObject().get("key").getAsJsonArray().get(1).getAsInt() != 0 && element.getAsJsonObject().get("ocupada").getAsInt() == 1 ){
					if(first == 0){
						oldElement = element;
						first = 1;
					}
					if(oldElement != null){
						long time = TimeTeste.getTimeDiference(oldElement, element);
						element.getAsJsonObject().addProperty("estacionado", time);
					}
				}

				if(element.getAsJsonObject().get("ocupada").getAsInt() == 0){
					first = 0;
					oldElement = null;

				}

				con.getSsapResourceMedida().setData("{\"Vagas\":" + element.toString()+"}");
				con.getApi().insert(con.getSsapResourceMedida());

				Response responseInsert=con.getApi().insert(con.getSsapResourceMedida());
				if(responseInsert.getStatus()!=200){
					System.out.println("Error Insertando");
				}
				System.out.println(element.toString());
			}else{
				System.out.println("Erro de Leitura");
			}

			//FactorySofiaJSON.SalvaJSON(stringToParse);


		} catch (IOException ex) {
			System.out.println("Não foi possível enviar o dado. \nEnviar dados");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
}