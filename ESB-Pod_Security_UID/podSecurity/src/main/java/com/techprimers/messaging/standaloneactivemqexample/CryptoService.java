package com.techprimers.messaging.standaloneactivemqexample;

import java.util.Base64;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class CryptoService {

	
		
	public static void main(String args[])
	{
		BasicDBObject j = new BasicDBObject();
		j= (BasicDBObject) JSON.parse("{\r\n" + 
				"  \"funcionarioDTO\": {\r\n" + 
				"    \"mail\": \"vivianam.prada@davivienda.com\"\r\n" + 
				"  }\r\n" + 
				"}");
		
		BasicDBObject j1=(BasicDBObject) j.get("funcionarioDTO");
		System.out.println(j1.get("mail"));
		/*KeyGeneratorService kc = new KeyGeneratorService();
		CryptoService cs = new CryptoService();
		Map<String, String> k = kc.keyGeneratorCustom(32);
		String K1=k.get("K1");
		String K2=k.get("K2");
		String encryptedString = AES.encrypt("<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:typ='http://localhost:8088/WsFXF.wsdl/types/'>\r\n" + 
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
				"</soapenv:Envelope>", K1 + K2) ;
		System.out.println(encryptedString);
		
		/*String keyString ="4d89g13j4j91j27c582ji69373y788r1"; // I made this key up by the way!


		byte[] keyB = new byte[24]; // a Triple DES key is a byte[24] array


		for (int i = 0; i < keyString.length() && i < keyB.length; i++) {
		keyB[i] = (byte) keyString.charAt(i);
		}

		// Make the Key
		SecretKey key = new SecretKeySpec(keyB, "DESede");
		String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
		System.out.println("key "+encodedKey);*/
			
	}
}
