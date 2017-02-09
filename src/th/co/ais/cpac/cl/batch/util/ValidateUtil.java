package th.co.ais.cpac.cl.batch.util;

public class ValidateUtil {
	public static boolean isNull(String value){
		if(value==null||"".equals(value)){
			return true;
		}else{
			return false;
		}
	}
	public static boolean isEqual(String a,String b){
		if(a.equals(b)){
			return true;
		}else{
			return false;
		}
	}
}
