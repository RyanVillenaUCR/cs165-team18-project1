import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.util.Arrays;

import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;


public class Cracker {

	final static String SALT  = "hfT7jp2q";
	final static String HASH  = "JU0X9xRQyTWTWY59e3Iqj1";
	final static String MAGIC = "$1$";
	
	final static String MD5   = "MD5";	//for instantiating MessageDigests
	
	final static String CRYPT_BASE64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	public static String crack() {
		
		for (char c1 = 'a'; c1 <= 'z'; c1++) {
			
			String s1 = new String();
			s1 += c1;
			
			if (hash(s1).equals(HASH))
				return s1;
			
			for (char c2 = 'a'; c2 <= 'z'; c2++) {
				
				String s2 = new String(s1);
				s2 += c2;
				
				if (hash(s2).equals(HASH))
					return s2;
				
				for (char c3 = 'a'; c3 <= 'z'; c3++) {
					
					String s3 = new String(s2);
					s3 += c3;
					
					if (hash(s3).equals(HASH))
						return s3;
					
					for (char c4 = 'a'; c4 <= 'z'; c4++) {
						
						String s4 = new String(s3);
						s4 += c4;
						
						if (hash(s4).equals(HASH))
							return s4;
						
						for (char c5 = 'a'; c5 <= 'z'; c5++) {
							
							String s5 = new String(s4);
							s5 += c5;
							
							if (hash(s5).equals(HASH))
								return s5;
							
							for (char c6 = 'a'; c6 <= 'z'; c6++) {
								
								String s6 = new String(s5);
								s6 += c6;
								
								if (hash(s6).equals(HASH))
									return s6;
							}
						}
					}
				}
			}
		}
		
		return "Crack failed";
	}
	
	public static byte[] getIntermediate_0(String password, byte[] alternateSum)
			throws NoSuchAlgorithmException {
			
		MessageDigest md = MessageDigest.getInstance("MD5");
		
		md.update(password.getBytes());
		md.update(MAGIC.getBytes());
		md.update(SALT.substring(0, 8).getBytes());
		md.update(alternateSum, 0, password.length());	//only works because pw.len() is always <= 6
		
		String pwLengthBitString = Integer.toBinaryString(password.length());
		for (int i = pwLengthBitString.length() - 1; i >= 0; i--) {
			
			if (pwLengthBitString.charAt(i) == '1')
				md.update( (byte) '\0');
			else
				md.update( (byte) password.charAt(0));
		}
		
		return md.digest();
	}
	
	public static byte[] getAlternateSum(String password, String salt)
			throws NoSuchAlgorithmException {
		
		MessageDigest md = MessageDigest.getInstance(MD5);
		
		md.update(password.getBytes());
		md.update(salt.getBytes());
		md.update(password.getBytes());
		
		return md.digest();
	}
	
	public static byte[] getIntermediate_1000(String password, byte[] alternateSum, byte[] intermediate_0) throws NoSuchAlgorithmException {
		
		MessageDigest md = MessageDigest.getInstance(MD5);
		byte[] intermediate = Arrays.copyOf(intermediate_0, intermediate_0.length);
		
		for (int i = 0; i < 1000; i++) {
			
			md.reset();
			
			if (i % 2 == 0)
				md.update(intermediate);
			if (i % 2 == 1)
				md.update(password.getBytes());
			if (i % 3 != 0)
				md.update(SALT.getBytes());
			if (i % 7 != 0)
				md.update(password.getBytes());
			if (i % 2 == 0)
				md.update(password.getBytes());
			if (i % 2 == 1)
				md.update(intermediate);
			
			intermediate = md.digest();
		}
		
		return intermediate;
	}
	
	public static void shuffleBytes(byte[] shuffleMe) {
		
		assert shuffleMe.length == 16;
		
		int[] key = { 11, 4, 10, 5, 3, 9, 15, 2, 8, 14, 1, 7, 13, 0, 6, 12 };
		byte[] temp = Arrays.copyOf(shuffleMe, shuffleMe.length);
		
		for (int i = 0; i < shuffleMe.length; i++) {
			
			shuffleMe[i] = temp[key[i]];
		}
	}
	
	public static String cryptBase64(byte[] encryptMe) {
		
		assert encryptMe.length == 16;
		
		//Convert encryptMe to one huge bitstring
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < encryptMe.length; i++) {
			
			String binary_byte = Integer.toBinaryString(encryptMe[i]);
			sb.append(binary_byte);
		}
		String bitstring = sb.toString();
		
		//DEBUG
		System.out.println("encryptMe: \"" + encryptMe.toString() + "\"");
		System.out.println("encryptMe.length: " + encryptMe.length);
		System.out.println("bitstring: " + bitstring);
		System.out.println("bitstring.length(): " + bitstring.length());
		System.out.println("");
		
		//Then, store 22 groups of 6-bit bitstrings into an array
		String[] miniBitstrings = new String[22];
		for (int i = 0; i < 22; i++) {
			
			int offset_index = 6 * i;
			String substr = bitstring.substring(offset_index, offset_index + 6);
			miniBitstrings[i] = substr;
		}
		
		
		
		//Finally, output the corresponding CryptBase64 character to each miniBitstring's decimal conversion
		StringBuilder finalResult = new StringBuilder();
		for (int i = 0; i < 22; i++) {	//TODO figure out which to use
//		for (int i = 21; i >= 0; i--) {
			
			int index = Integer.parseInt(miniBitstrings[i], 2);
			byte c = (byte) CRYPT_BASE64.charAt(index);
			finalResult.append(c);
		}
		
		return finalResult.toString();
	}
	
	public static String hash(String password) {
		
		try {
			
			byte[] alternateSum = getAlternateSum(password, SALT);
			byte[] intermediate_0 = getIntermediate_0(password, alternateSum);
			byte[] intermediate_1000 = getIntermediate_1000(password, alternateSum, intermediate_0);
		
			//now, intermediate is intermediate_1000
			assert intermediate_1000.length == 16;
			shuffleBytes(intermediate_1000);
			assert intermediate_1000.length == 16;
			
			String finalHash = cryptBase64(intermediate_1000);
			assert finalHash.length() == 22;
			
			return finalHash;
			
		} catch (NoSuchAlgorithmException e) {
			
			e.printStackTrace();
			return e.getMessage();
		}

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
		
//		writeToOutputFile("Will I append or overwrite? Hopefully overwrite!");
//		writeToOutputFile("Aaaaand, testing if I can write multiple lines!");
//		writeToOutputFile("wait am i even changing anything here?");
		
//		byte b = 0x6e;
//		System.out.println(b);
		
//		try {
//			
//			MessageDigest md = MessageDigest.getInstance(MD5);
//			String pw = "abcdef";
//			String salt = "hfT7jp2q";
//			
//			md.update(pw.getBytes());
//			md.update(salt.getBytes());
//			md.update(pw.getBytes());
//			
//			byte[] output = md.digest();
//			
//			System.out.println("Output: \"" + output + "\"");
//			System.out.println("output length: " + output.length);
//			System.out.println("Output.toString(): \"" + output.toString() + "\"");
//			System.out.println("Output toString length: " + output.toString().length());
//			
//		} catch (NoSuchAlgorithmException e) {
//			
//			e.printStackTrace();
//		}
		
		
	}
		
	public static void log(String text) {
		
		File f = new File("output.txt");
		try {
			f.createNewFile();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		
		try {
			Writer w = new FileWriter(f, true);
//			Writer w = new PrintWriter(f);
			
//			w.println(text);
//			w.append(text);
			
			System.out.println(text);
			w.write(text);
			w.write(System.lineSeparator());		
			
			w.close();
			
		} catch (IOException e) {

			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	public static void test_functions() {
		
		try {
			
			String password = "abcdef";
			String salt = "hfT7jp2q";
			
			
			
			//Test Alternate Sum
//			byte[] expectedAlternateSum = "8c:84:bb:09:04:89:d3:04:49:26:09:3f:48:1f:7f:46";
			byte[] expectedAlternateSum = {
					(byte) 0x8c, (byte) 0x84, (byte) 0xbb, 0x09,
					0x04, (byte) 0x89, (byte) 0xd3, 0x04,
					0x49, 0x26, 0x09, 0x3f,
					0x48, 0x1f, 0x7f, 0x46
			};
			byte[] actualAlternateSum = getAlternateSum(password, salt);
			System.out.println("Expected AlternateSum: \"" + Arrays.toString(expectedAlternateSum) + "\"");
			System.out.println("Actual AlternateSum:   \"" + Arrays.toString(actualAlternateSum) + "\"");
			System.out.println("Works? " + Arrays.equals(expectedAlternateSum, actualAlternateSum));
			
			
			
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public static void main(String[] args) {

		System.out.println("Hello world!");
		
//		randomTests();
		test_functions();
		
//		long initialTime = System.currentTimeMillis();
//		
//		String password = crack();
//		
//		log("Password: " + password);
//		log("Time taken: " + Long.toString(System.currentTimeMillis() - initialTime) + "ms");
		

	}

}
