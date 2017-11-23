package sdk.ideas.tool.cipher.blowfish;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
public class Blowfish
{


	public static String encrypt(String strText, String strKey)
	{
		try
		{
			SecretKeySpec key = new SecretKeySpec(strKey.getBytes("UTF8"), "Blowfish");
			byte[] text = strText.getBytes("UTF8");

			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encrypted = cipher.doFinal(text);
			
			Base64.Encoder encoder= Base64.getEncoder();
			
			return encoder.encodeToString(encrypted);

		}
		catch (Exception e)
		{
			
			return null;
		}
	}

	public static String decrypt(String strEncrypted, String strKey)
	{
		try
		{
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] text = decoder.decode((strEncrypted));
			
			SecretKeySpec key = new SecretKeySpec(strKey.getBytes("UTF8"), "Blowfish");
			
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, key);
			
			byte[] decrypted = cipher.doFinal(text);
			return new String(decrypted,"UTF-8");
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			return null;
		}
	}
}
