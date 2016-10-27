
public class ArduinoSerialAutoSend {

	public static void main(String[] args) {
		ControlePorta arduino = new ControlePorta("COM4",9600);
		while(1==1){
 		    System.out.println();
			arduino.enviaDados(1);
 		    arduino.recebeDados();
			try {
			    Thread.sleep(1000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			System.out.println();
			arduino.enviaDados(2);
 		    arduino.recebeDados();

			try {
			    Thread.sleep(1000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
		}
	}

}
