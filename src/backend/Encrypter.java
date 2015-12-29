package backend;


import gui.SettingsGUI;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter {
	private SettingsGUI GUI;
	private SecretKeySpec secretKeySpec;
	private Cipher cipher;
	private String key;

	public Encrypter(String key) {
		this.key = key;
	}
	
	public void SetupEncrypter() throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
		//errors handled in GUI try catch
		System.out.println("Key for encrypter: "+key);
		byte[] bytekey = null;

		bytekey = key.getBytes("UTF-8");

		MessageDigest sha = null;
		sha = MessageDigest.getInstance("SHA-1");

		bytekey = sha.digest(bytekey);
		bytekey = Arrays.copyOf(bytekey, 16); // use only first 128 bit

		secretKeySpec = new SecretKeySpec(bytekey, "AES");

		cipher = Cipher.getInstance("AES");
	}
	
	public void setGUI(SettingsGUI GUI) {
		this.GUI = GUI;
	}

	public String decrypt(String content) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		//errors handled by try catch in loadorsaveGUI processLoad method
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			return new String(cipher.doFinal(hexToByte(content)));
	}
	
	private String asHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X ", b));
		}
		return sb.toString();
	}

	private byte[] hexToByte(String s) {
		s = s.replaceAll("\\s+","");
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
		
		return data;
	}

	public String encrypt(String content) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			byte[] encrypted = cipher.doFinal((content).getBytes("UTF-8"));
			return asHex(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	public SettingsGUI getGUI() {
		return GUI;
	}

}
