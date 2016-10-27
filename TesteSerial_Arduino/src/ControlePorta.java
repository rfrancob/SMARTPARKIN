
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

	/**
	 * Construtor da classe ControlePorta
	 * @param portaCOM - Porta COM que ser� utilizada para enviar os dados para o arduino
	 * @param taxa - Taxa de transfer�ncia da porta serial geralmente � 9600
	 */
	public ControlePorta(String portaCOM, int taxa) {
		this.portaCOM = portaCOM;
		this.taxa = taxa;
		this.con = new ConnectionFactory();
		this.initialize();
	}     

	/**
	 * M�doto que verifica se a comunica��o com a porta serial est� ok
	 */
	private void initialize() {
		try {
			//Define uma vari�vel portId do tipo CommPortIdentifier para realizar a comunica��o serial
			CommPortIdentifier portId = null;
			try {
				//Tenta verificar se a porta COM informada existe
				portId = CommPortIdentifier.getPortIdentifier(this.portaCOM);
			}catch (NoSuchPortException npe) {
				//Caso a porta COM n�o exista ser� exibido um erro 
				System.out.println("Porta COM n�o encontrada.");
			}
			//Abre a porta COM 
			SerialPort port = (SerialPort) portId.open("Comunica��o serial", this.taxa);
			serialOut = port.getOutputStream();
			serialInput = port.getInputStream();
			port.setSerialPortParams(this.taxa, //taxa de transfer�ncia da porta serial 
					SerialPort.DATABITS_8, //taxa de 10 bits 8 (envio)
					SerialPort.STOPBITS_1, //taxa de 10 bits 1 (recebimento)
					SerialPort.PARITY_NONE); //receber e enviar dados
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�todo que fecha a comunica��o com a porta serial
	 */
	public void close() {
		try {
			serialOut.close();
		}catch (IOException e) {
			System.out.println("N�o foi poss�vel fechar porta COM. \n Fechar porta COM");
		}
	}

	/**
	 * @param opcao - Valor a ser enviado pela porta serial
	 */
	public void enviaDados(int opcao){
		try {
			serialOut.write(opcao);//escreve o valor na porta serial para ser enviado
		} catch (IOException ex) {
			System.out.println("N�o foi poss�vel enviar o dado. \nEnviar dados");
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
			stringToParse = "{\"KpVagaTeste\":" + stringToParse + "}"; 
			System.out.print(stringToParse);
			con.getSsapResourceMedida().setData(stringToParse);
			con.getApi().insert(con.getSsapResourceMedida());

			Response responseInsert=con.getApi().insert(con.getSsapResourceMedida());
			if(responseInsert.getStatus()!=200){
				System.out.println("Error Insertando");
			}
			}else{
				System.out.println("Erro de Leitura");
			}

			//FactorySofiaJSON.SalvaJSON(stringToParse);

			//	    	JsonParser jsonParser = new JsonParser();
			//	    	JsonElement element = jsonParser.parse(stringToParse);
			//	    	System.out.println(element);
		} catch (IOException ex) {
			System.out.println("N�o foi poss�vel enviar o dado. \nEnviar dados");
		}
	} 
}