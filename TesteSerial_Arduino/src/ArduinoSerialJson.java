
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;





public class ArduinoSerialJson {



	public static void main(String[] args) {
		ControlePorta arduino = new ControlePorta("COM4",9600);
		Scanner scanner = new Scanner(System.in);
		boolean quit = false;
		while(!quit){
			
			System.out.print("Digite 1 para Ligar e 2 para Desligar :\n3 - Para sair.!\n");
			String sentence = scanner.nextLine();
			
			switch(sentence){
			
			case "1":  arduino.enviaDados(1);
			arduino.recebeDados();
			break;
			
			case "2":  arduino.enviaDados(2);
			arduino.recebeDados();
			break;
			
			case "3": arduino.close();
			arduino.enviaDados(2);
			quit = true;
			break;

			default: System.out.print("Comando invalido\n");

			}
		}
		System.exit(0);
	}

}
