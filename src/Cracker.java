import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Cracker {

	final static String SALT  = "hfT7jp2q";
	final static String HASH  = "JU0X9xRQyTWTWY59e3Iqj1";
	final static String MAGIC = "$1$";
	
	final static String MD5   = "MD5";	//for instantiating MessageDigests
	
	public static void crack() {
		
		//TODO
		
	}
	
	public static byte[] getIntermediate0(String password, byte[] alternateSum) {
		
		try {
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			md.update(password.getBytes());
			md.update(MAGIC.getBytes());
			md.update(SALT.substring(0, 9).getBytes());
			md.update(alternateSum, 0, password.length());
			
			String pwLengthBitString = Integer.toBinaryString(password.length());
			for (int i = password.length() - 1; i >= 0; i--) {
				
				if (pwLengthBitString.charAt(i) == '1')
					md.update( (byte) '\0');
				else
					md.update( (byte) password.charAt(0));
			}
			
			
			
			return md.digest();
			
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage().getBytes();
		}
		
		 
	}
	
	public static byte[] getAlternateSum(String password) {
		
		MessageDigest md;
		
		try {
			
			md = MessageDigest.getInstance("MD5");
			
			md.update(password.getBytes());
			md.update(SALT.getBytes());
			md.update(password.getBytes());
			
			return md.digest();
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "\0".getBytes();
		}
	}
	
	public static boolean testPassword(String password) {
		
		//@TODO
		return false;
	}
	
	public static void randomTests() {
		
//		MessageDigest md;
//		
//		try {
//			
//			md = MessageDigest.getInstance("MD5");
//			System.out.println(md.toString());
//			
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
		
//		char a = 'a';
//		String test_str = Integer.toBinaryString((int) a);
//		System.out.println("Binary representation of 'a' is " + test_str);
//		System.out.println("ASCII representation of " + test_str + " is " + (char) 97);
		
//		System.out.println("Binary representation of 6 is " + Integer.toBinaryString(6));

//		int test = 0;
//		test = test - 1;
//		System.out.println(test);
		
	}
	
	public static void main(String[] args) {

		System.out.println("Hello world!");
		
		randomTests();

	}

}
