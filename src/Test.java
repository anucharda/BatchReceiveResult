import th.co.ais.cpac.cl.batch.Constants;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName="DMSRequest_20150817_232541.ok";
		
		System.out.println(fileName.indexOf(".ok"));
		System.out.println(fileName.indexOf(".err"));
		
		String text="01|DMSRequest_20150817_232541.dat ";
		String [] arrStr=text.split(Constants.PIPE);
		System.out.println(arrStr[1]);
	}

}
