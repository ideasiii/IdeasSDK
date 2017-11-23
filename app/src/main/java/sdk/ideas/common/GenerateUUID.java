package sdk.ideas.common;

import java.nio.ByteBuffer;

public class GenerateUUID
{
	public static String uuIDRandom()
	{
		return java.util.UUID.randomUUID().toString();
	}

	public static String uuIDRandomShort()
	{
		int i = ByteBuffer.wrap(uuIDRandom().getBytes()).getInt();
		return Integer.toString(i,Character.MAX_RADIX );
	}
	// 7e9e64ed-2005-4dd8-b790-5236a5aa5e3c
	public static String uuIDRandomShort(String uuID)
	{
		int i = ByteBuffer.wrap(uuID.getBytes()).getInt();
		return Integer.toString(i,Character.MAX_RADIX );
	}

}
