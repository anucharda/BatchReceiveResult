import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName="AIS_WO_20170515_235052.ack";
		int index = fileName.indexOf("WO_");
		int endIndex = fileName.indexOf(".");
		String name = fileName.substring(index+3, endIndex).replace("_", "");
		System.out.println("name="+name);

	}
}
