package sdk.ideas.tool.cipher.aes;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;



/**
 * sample 來源: http://dean-android.blogspot.tw/2013/07/androidaes.html
 * //16位的英數組合位元，可自行填寫 (下為小黑人暫訂) //32位的英數組合Key欄位，可自行填寫 (下為小黑人暫訂) //欲進行加密的文字字串
 * private final static String IvAES = "1234567890abcdef" ; private final static
 * String KeyAES = "12345678901234567890123456789012"; private final static
 * String TextAES = "小黑人的Android教室 !"; --->此為欲加密文字
 */


public class AES
{
	public static String EncryptAES(String stringIV, String stringKey, String stringText)
	{
		try
		{
			byte[] iv = stringIV.getBytes("UTF-8");
			byte[] key = stringKey.getBytes("UTF-8");
			byte[] text = stringText.getBytes("UTF-8");

			AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
			SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
			Cipher mCipher = null;
			mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec);
			
			Base64.Encoder encoder= Base64.getEncoder();
			return encoder.encodeToString(mCipher.doFinal(text));
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	// AES解密，帶入byte[]型態的16位英數組合文字、32位英數組合Key、需解密文字
	public static String DecryptAES(String stringIV, String stringKey, String stringText)
	{
		try
		{
			byte[] iv = stringIV.getBytes("UTF-8");
			byte[] key = stringKey.getBytes("UTF-8");
			
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] text = decoder.decode((stringText));
			
			AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
			SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
			Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec);

			return new String(mCipher.doFinal(text),"UTF-8");
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	public static byte[] EncryptAES(byte[] iv, byte[] key, byte[] text)
	{
		try
		{
			AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
			SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
			Cipher mCipher = null;
			mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec);

			return mCipher.doFinal(text);
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	// AES解密，帶入byte[]型態的16位英數組合文字、32位英數組合Key、需解密文字
	public static byte[] DecryptAES(byte[] iv, byte[] key, byte[] text)
	{
		try
		{
			AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
			SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
			Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec);

			return mCipher.doFinal(text);
		}
		catch (Exception ex)
		{
			return null;
		}
	}
}
