import java.util.Scanner;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ArduinoSerial {


	public static void main(String[] args) {
//		ControlePorta arduino = new ControlePorta("COM4",9600);
//		 Scanner scanner = new Scanner(System.in);
//		 boolean quit = false;
	    	String stringToParse = "{\"a\":\"b\",\"c\":\"d\"}";
	    	System.out.print(stringToParse);
	    	JsonParser jsonParser = new JsonParser();
	    	JsonElement element = jsonParser.parse(stringToParse);
	    	System.out.print("\n"+element);
	    	System.exit(0);
//		 while(!quit){
//		        System.out.print("Digite 1 para Ligar e 2 para Desligar :\n3 - Para sair.!\n");
//		        String sentence = scanner.nextLine();
//		        switch(sentence){
//		        case "1":  arduino.enviaDados(1);
//		        break;
//		        case "2":  arduino.enviaDados(2);
//		        break;
//		        case "3": arduino.close();
//		        arduino.enviaDados(2);
//		        quit = true;
//		        break;
//		        case "4": arduino.recebeDados();
//		        break;
//		        case "5":  arduino.enviaDados(5);
//		        break;
//		        default: System.out.print("Comando invalido\n");
//		        
//		        }
//		 }
//		 System.exit(0);
}

}
