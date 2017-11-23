package sdk.ideas.tool.fingerprint;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;

@TargetApi(23)
public class FingerPrintHandler extends BaseHandler implements ListenReceiverAction
{
	private KeyguardManager mKeyguardManager = null;
	private FingerprintManager mFingerprintManager = null;
	private CancellationSignal cancellationSignal = null;
	private boolean isStartListen = false;
	private boolean lockUse = false;

	public FingerPrintHandler(Context mContext)
	{
		super(mContext);
		init();
	}

	private void init()
	{
		mKeyguardManager = (KeyguardManager) (((Activity) mContext).getSystemService(Activity.KEYGUARD_SERVICE));
		mFingerprintManager = (FingerprintManager) (((Activity) mContext)
				.getSystemService(Activity.FINGERPRINT_SERVICE));// FingerprintManager.class

		if (check() == false)
		{
			lockUse = true;
		}
	}

	private boolean check()
	{
		boolean checkGreen = true;
		if (!mKeyguardManager.isKeyguardSecure())
		{
			// 是否有設定 fingerprint screen lock
			Logs.showTrace("fingerprint screen lock = false");
			checkGreen = false;
		}
		if (!mFingerprintManager.isHardwareDetected())
		{
			// 硬體裝置是否有 fingerprint reader
			Logs.showTrace("fingerprint reader = false");
			checkGreen = false;
		}

		if (!mFingerprintManager.hasEnrolledFingerprints())
		{
			// 是否有設定至少一枚指紋
			Logs.showTrace("fingerprint has Fingerprints = false");
			checkGreen = false;
		}
		return checkGreen;
	}

	@Override
	public void startListenAction()
	{
		if (lockUse == true)
		{

			return;
		}
		if (isStartListen == false)
		{
			isStartListen = true;
			Logs.showTrace("isStartListen = true");

			cancellationSignal = new CancellationSignal();

			if (null != mFingerprintManager)
			{
				try
				{
					mFingerprintManager.authenticate(null, // crypto objects 的
															// wrapper
															// class，可以透過它讓
															// authenticate
															// 過程更為安全，但也可以不使用。
							cancellationSignal, // 用來取消 authenticate 的 object
							0, // optional flags; should be 0
							mAuthenticationCallback, // callback 用來接收
														// authenticate
														// 成功與否，有三個 callback
														// method
							null); // optional 的參數，如果有使用，FingerprintManager
									// 會透過它來傳遞訊息
				}
				catch (Exception e)
				{
					Logs.showTrace(e.toString());
				}
			}
		}
	}

	@Override
	public void stopListenAction()
	{
		if (lockUse == true)
		{

			return;
		}

		if (isStartListen == true)
		{
			Logs.showTrace("isStartListen = false");
			if (null != mFingerprintManager && null != cancellationSignal)
			{
				cancellationSignal.cancel();
				cancellationSignal = null;
			}
			isStartListen = false;
		}
	}

	FingerprintManager.AuthenticationCallback mAuthenticationCallback = new FingerprintManager.AuthenticationCallback()
	{
		@Override
		public void onAuthenticationError(int errorCode, CharSequence errString)
		{
			Logs.showError("[FingerPrintHandler]Error Code:" + errorCode + " Error Message:" + errString);
		}

		@Override
		public void onAuthenticationFailed()
		{
			Logs.showTrace("[FingerPrintHandler] onAuthenticationFailed");
		}

		@Override
		public void onAuthenticationHelp(int helpCode, CharSequence helpString)
		{
			// 指纹验证失败，可再验，可能手指过脏，或者移动过快等原因。
			Logs.showTrace("[FingerPrintHandler] onAuthenticationHelp");
		}

		@Override
		public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result)
		{
			Logs.showTrace("[FingerPrintHandler] onAuthenticationSucceeded");
		}
	};

}
