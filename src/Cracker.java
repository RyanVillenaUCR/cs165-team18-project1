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
			
			if (hash(s1, SALT).equals(HASH))
				return s1;
			
			for (char c2 = 'a'; c2 <= 'z'; c2++) {
				
				String s2 = new String(s1);
				s2 += c2;
				
				if (hash(s2, SALT).equals(HASH))
					return s2;
				
				for (char c3 = 'a'; c3 <= 'z'; c3++) {
					
					String s3 = new String(s2);
					s3 += c3;
					
					if (hash(s3, SALT).equals(HASH))
						return s3;
					
					for (char c4 = 'a'; c4 <= 'z'; c4++) {
						
						String s4 = new String(s3);
						s4 += c4;
						
						if (hash(s4, SALT).equals(HASH))
							return s4;
						
						for (char c5 = 'a'; c5 <= 'z'; c5++) {
							
							String s5 = new String(s4);
							s5 += c5;
							
							if (hash(s5, SALT).equals(HASH))
								return s5;
							
							for (char c6 = 'a'; c6 <= 'z'; c6++) {
								
								String s6 = new String(s5);
								s6 += c6;
								
								if (hash(s6, SALT).equals(HASH))
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
		
	public static String toBitstring(byte b) {

		int integer = (b & 0x00FF);	//mask off any sign bits from cast
		
		String bin_str = Integer.toBinaryString(integer);
		assert bin_str.length() <= 8;
		
		int extraZeroes = (-1 * bin_str.length()) + 8;	//needed to make bin_str exactly 8 elements big
		for (int i = 0; i < extraZeroes; i++) {
			bin_str = "0" + bin_str;
		}
		assert bin_str.length() == 8;		
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			
			sb.append(bin_str.charAt(i));
		}
			
		return sb.toString();
	}
	
	public static String cryptBase64(byte[] encryptMe) {
		
		//Convert encryptMe to one huge bitstring
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < encryptMe.length; i++) {

			String binary_byte = toBitstring(encryptMe[i]);
			sb.append(binary_byte);
		}
		String bitstring = sb.toString();
		
		
		//Then, store 22 groups of 6-bit bitstrings into an array
		String[] miniBitstrings = new String[22];
		
		miniBitstrings[0] = bitstring.substring(0, 2);	//Handle the first string separately
		for (int i = 0; i < miniBitstrings.length - 1; i++) {
			
			int firstIndex = i * 6 + 2;
			int lastIndex = firstIndex + 6;
			String substr = bitstring.substring(firstIndex, lastIndex);
			miniBitstrings[i + 1] = substr;
		}
		
		
		//Finally, output the corresponding CryptBase64 character to each miniBitstring's decimal conversion
		StringBuilder finalResult = new StringBuilder();
		for (int i = miniBitstrings.length - 1; i >= 0; i--) {
			
			int index = Integer.parseInt(miniBitstrings[i], 2);
			char c = CRYPT_BASE64.charAt(index);
			finalResult.append(c);
		}
		
		return finalResult.toString();
	}
	
	public static String hash(String password, String salt) {
		
		try {
			
			byte[] alternateSum = getAlternateSum(password, salt);
			byte[] intermediate_0 = getIntermediate_0(password, alternateSum);
			byte[] intermediate_1000 = getIntermediate_1000(password, alternateSum, intermediate_0);
		
			//now, intermediate is intermediate_1000
			assert intermediate_1000.length == 16;
			shuffleBytes(intermediate_1000);
			assert intermediate_1000.length == 16;
			
			String finalHash = cryptBase64(intermediate_1000);
			
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
		
//		byte b = -61;
//		System.out.println("Testing byte with value " + b);
//		System.out.println("Bitstring: " + toBitstring(b));
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
			byte[] expectedAlternateSum = {
					(byte) 0x8c, (byte) 0x84, (byte) 0xbb, 0x09,
					0x04, (byte) 0x89, (byte) 0xd3, 0x04,
					0x49, 0x26, 0x09, 0x3f,
					0x48, 0x1f, 0x7f, 0x46
			};
			byte[] actualAlternateSum = getAlternateSum(password, salt);
			System.out.println("Expected AlternateSum: " + Arrays.toString(expectedAlternateSum) + "");
			System.out.println("Actual AlternateSum:   " + Arrays.toString(actualAlternateSum) + "");
			System.out.println("Works? " + Arrays.equals(expectedAlternateSum, actualAlternateSum));
			System.out.println("");
			
			
			
			//Test Intermediate0
			byte[] expectedIntermediate0 = {
					       0x72,        0x24,        0x56,        0x0b,
					       0x3e,        0x7a, (byte) 0xd1, (byte) 0xfa,
					(byte) 0xb8,        0x7f,        0x05,        0x6b,
					(byte) 0x94, (byte) 0xbd, (byte) 0x9b,        0x06
			};
			byte[] actualIntermediate0 = getIntermediate_0(password, actualAlternateSum);
			System.out.println("Expected Intermediate_0: " + Arrays.toString(expectedIntermediate0));
			System.out.println("Actual Intermediate_0:   " + Arrays.toString(actualIntermediate0));
			System.out.println("Works? " + Arrays.equals(expectedIntermediate0, actualIntermediate0));
			System.out.println("");
			
			
			//Test Intermediate1000
			byte[] expectedIntermediate1000 = {
					       0x3f, (byte) 0xb1, (byte) 0xf8,        0x62,
					       0x3a,        0x46, (byte) 0xb7, (byte) 0xbf,
					(byte) 0xb8,        0x17,        0x3c,        0x38,
					(byte) 0xcb, (byte) 0x9e, (byte) 0xb7, (byte) 0xb5
			};
			byte[] actualIntermediate1000 = getIntermediate_1000(password, actualAlternateSum, actualIntermediate0);
			System.out.println("Expected Intermediate_1000: " + Arrays.toString(expectedIntermediate1000));
			System.out.println("Actual Intermediate_1000:   " + Arrays.toString(actualIntermediate1000));
			System.out.println("Works? " + Arrays.equals(expectedIntermediate1000, actualIntermediate1000));
			System.out.println("");
			
			
			
			//Test shuffleBytes
			byte[] expectedShuffleBytes = {
					0x0B, 0x04, 0x0A, 0x05,
					0x03, 0x09, 0x0F, 0x02,
					0x08, 0x0e, 0x01, 0x07,
					0x0d, 0x00, 0x06, 0x0c
			};
			byte[] actualShuffleBytes = {
					0x00, 0x01, 0x02, 0x03,
					0x04, 0x05, 0x06, 0x07,
					0x08, 0x09, 0x0A, 0x0B,
					0x0C, 0x0D, 0x0E, 0x0F
			};
			shuffleBytes(actualShuffleBytes);
			System.out.println("Expected shuffle bytes: " + Arrays.toString(expectedShuffleBytes));
			System.out.println("Actual shuffle bytes:   " + Arrays.toString(expectedShuffleBytes));
			System.out.println("Works? " + Arrays.equals(expectedShuffleBytes, actualShuffleBytes));
			System.out.println("");
			
			
			
			//Test cryptBase64
			String expectedCryptBase64 = "9TvDSyPgrW9ypSVM4lXCs.";
			shuffleBytes(actualIntermediate1000);
			String actualCryptBase64 = cryptBase64(actualIntermediate1000);
			System.out.println("Expected CryptBase64: " + expectedCryptBase64);
			System.out.println("Actual CryptBase 64:  " + actualCryptBase64);
			System.out.println("Works? " + expectedCryptBase64.equals(actualCryptBase64));
			System.out.println("");
			
			
			
			//Test hash
			String expectedHash = "9TvDSyPgrW9ypSVM4lXCs.";
			String actualHash = hash(password, salt);
			System.out.println("Expected hash: " + expectedHash);
			System.out.println("Actual hash:   " + actualHash);
			System.out.println("Works? " + expectedHash.equals(actualHash));
			System.out.println("");
			
			
			
			//Test hash 2
			String expectedHash2 = "g.L45izUKySxx0yWx8.xn1";
			String actualHash2 = hash("aecujj", "hfT7jp2q");
			System.out.println("Expected hash: " + expectedHash2);
			System.out.println("Actual hash:   " + actualHash2);
			System.out.println("Works? " + expectedHash2.equals(actualHash2));
			System.out.println("");
			
			
			
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
