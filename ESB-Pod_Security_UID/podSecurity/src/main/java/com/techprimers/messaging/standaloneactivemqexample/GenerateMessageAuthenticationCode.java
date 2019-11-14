package com.techprimers.messaging.standaloneactivemqexample;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
 
public class GenerateMessageAuthenticationCode {
	private static byte[] keyb;
	
	public String getMac(String message,String keyToEncript)
	{
		try {
			Key key = new SecretKeySpec(keyToEncript.getBytes("UTF-8"), "HmacMD5");
            
            // get a key generator for the HMAC-MD5 keyed-hashing algorithm
            //KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
             
            // generate a key from the generator
          //  SecretKey key = SecretKey();
            
   
            // create a MAC and initialize with the above key
            Mac mac = Mac.getInstance(key.getAlgorithm());
            mac.init(key);
 

             
            // get the string as UTF-8 bytes
            byte[] b = message.getBytes("UTF-8");
             
            // create a digest from the byte array
            byte[] digest = mac.doFinal(b);
     //System.out.println(digest);
     String encodedKey = Base64.getEncoder().encodeToString(digest);
     //System.out.println(encodedKey);
     return encodedKey;
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("No Such Algorithm:" + e.getMessage());
            return ParamDefinitions._NO_MAC;
        }
        catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported Encoding:" + e.getMessage());
            return ParamDefinitions._NO_MAC;
        }
        catch (InvalidKeyException e) {
            System.out.println("Invalid Key:" + e.getMessage());
            return ParamDefinitions._NO_MAC;
        }
	}
	
    public static void main(String[] args) {
         
 
            String message = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:typ='http://localhost:8088/WsFXF.wsdl/types/'>\r\n" + 
    				"   <soapenv:Header/>\r\n" + 
    				"   <soapenv:Body>\r\n" + 
    				"      <typ:prcFdValidarSaldoResponseElement>\r\n" + 
    				"         <typ:result>\r\n" + 
    				"            <typ:errnumOut>1334442</typ:errnumOut>\r\n" + 
    				"            <typ:saldoOut>1000 0</typ:saldoOut>\r\n" + 
    				"            <typ:descrOut>OK</typ:descrOut>\r\n" + 
    				"         </typ:result>\r\n" + 
    				"      </typ:prcFdValidarSaldoResponseElement>\r\n" + 
    				"   </soapenv:Body>\r\n" + 
    				"</soapenv:Envelope>";
            
    		KeyGeneratorService kc = new KeyGeneratorService();
    		Map<String, String> k = kc.keyGeneratorCustom(32);
    		String K1=k.get("K1");
    		String K2=k.get("K2");
    		System.out.println("K1="+K1);
    		System.out.println("K2="+K2);
             GenerateMessageAuthenticationCode g = new GenerateMessageAuthenticationCode();
             System.out.println("MAC = "+ g.getMac(message, K1+K2));
   }
 
}