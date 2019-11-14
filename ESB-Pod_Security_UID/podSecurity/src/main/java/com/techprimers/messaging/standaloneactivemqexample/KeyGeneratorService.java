package com.techprimers.messaging.standaloneactivemqexample;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.sun.glass.ui.Pixels.Format;

import sun.text.resources.cldr.kea.FormatData_kea;

public class KeyGeneratorService {

	public Map<String,String> keyGeneratorCustom(int size)
	{
		Map<String,String> keyContainer = new HashMap<String,String>();
		/*Key key;
		KeyGenerator keyGen = null;
		try {
			keyGen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		keyGen.init(256); // for example
		key = keyGen.generateKey();
		System.out.println(Format( key.getEncoded()));
		*/
		RandomString nw = new RandomString(size);
		keyContainer.put("K1", nw.nextString());
		keyContainer.put("K2", nw.nextString());
		return keyContainer;
		
	}
	
	public static void main(String args[])
	{
		KeyGeneratorService kc = new KeyGeneratorService();
		
		System.out.println(kc.keyGeneratorCustom(26));
			
	}
	
}
