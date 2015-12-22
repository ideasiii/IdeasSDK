package sdk.ideas.common;

public abstract class ResponseCode
{
	public static final int	ERR_SUCCESS							= 1;
	static public final int	ERR_UNKNOW							= 0;
	public static final int	ERR_IO_EXCEPTION					= -1;
	public static final int	ERR_ILLEGAL_ARGUMENT_EXCEPTION		= -2;
	public static final int	ERR_CLIENT_PROTOCOL_EXCEPTION		= -3;
	public static final int	ERR_UNSUPPORTED_ENCODING_EXCEPTION	= -4;
	public static final int	ERR_ILLEGAL_STRING_LENGTH_OR_NULL	= -5;
	public static final int	ERR_NOT_INIT						= -6;
	public static final int	ERR_MAX								= -7;

	public static final int	METHOLD_START_TRACKER	= 0;
	public static final int	METHOLD_TRACKER			= 1;
	public static final int	METHOLD_STOP_TRACKER	= 2;

}
