import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;


 
public class SerialTest {


    	public static void main(String[] args) throws Exception{
            SerialPort port = (SerialPort)CommPortIdentifier
                        .getPortIdentifier("COM4").open("serial madness", 4000);
            InputStream input = port.getInputStream();
            OutputStream output = port.getOutputStream();
            port.setSerialPortParams(
                        9600, // 9600bps
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
            for(;;) {
                  while (input.available()>0) {
                        System.out.print((char)(input.read()));
                  }
            }
      }
}