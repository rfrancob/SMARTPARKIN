import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class TimeTeste {

	public static long getTimeDiference(JsonElement element, JsonElement element2) throws ParseException  {
//		String stringToParse = "{\"vaga\":1,\"ocupada\":1,\"latitude\":1.52e7,\"longitude\":5.46e7,\"key\":[\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"],\"time\":\"2016-11-01 09:07:37\"}";
//		String stringToParse2 = "{\"vaga\":1,\"ocupada\":1,\"latitude\":1.52e7,\"longitude\":5.46e7,\"key\":[\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"],\"time\":\"2016-11-01 09:07:39\"}";
//		JsonParser jsonParser = new JsonParser();
//		JsonElement element = jsonParser.parse(stringToParse);
//		JsonElement element2 = jsonParser.parse(stringToParse2);
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date now = (Date)sdfDate.parse(element.getAsJsonObject().get("time").getAsString());
	    Date now2 = (Date)sdfDate.parse(element2.getAsJsonObject().get("time").getAsString());
		long diff = now2.getTime() - now.getTime();
		//return TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
		return TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS);

	}
	

}
