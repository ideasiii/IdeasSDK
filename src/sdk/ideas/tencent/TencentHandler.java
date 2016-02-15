package sdk.ideas.tencent;

import org.json.JSONObject;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.util.SparseArray;
import sdk.ideas.common.Logs;

public class TencentHandler
{
	private QQAuth									mQQAuth;
	private UserInfo								mInfo;
	private Tencent									mTencent;
	private Activity								theActivity				= null;
	private SparseArray<OnTencentLoginResult>		listLoginListener		= null;
	private SparseArray<OnTencentUserInfoListener>	listUserInfoListener	= null;

	public TencentHandler(Activity activity)
	{
		theActivity = activity;
		listLoginListener = new SparseArray<OnTencentLoginResult>();
		listUserInfoListener = new SparseArray<OnTencentUserInfoListener>();
	}

	@Override
	protected void finalize() throws Throwable
	{
		listLoginListener.clear();
		listLoginListener = null;
		listUserInfoListener.clear();
		listUserInfoListener = null;
		super.finalize();
	}

	public void init(final String strAppId)
	{
		mQQAuth = QQAuth.createInstance(strAppId, theActivity.getApplicationContext());
		mTencent = Tencent.createInstance(strAppId, theActivity);
	}

	/**
	 * Callback Tencent login complete
	 */
	public static interface OnTencentLoginResult
	{
		void onLoginResult(final String strOpenID, final String strToken, final String strError);
	}

	public void setOnTencentLoginResultListener(TencentHandler.OnTencentLoginResult listener)
	{
		if (null != listener)
		{
			listLoginListener.put(listLoginListener.size(), listener);
		}
	}

	private void callbackTencentLoginResult(final String strOpenID, final String strToken, final String strError)
	{
		for (int i = 0; i < listLoginListener.size(); ++i)
		{
			listLoginListener.get(i).onLoginResult(strOpenID, strToken, strError);
		}
	}

	/**
	 * Callback Tencent User Info
	 */
	public static interface OnTencentUserInfoListener
	{
		void onUserInfo(final String strNickname, final String strPhotoURL, final String strError);
	}

	public void setOnTencentUserInfoListener(TencentHandler.OnTencentUserInfoListener listener)
	{
		if (null != listener)
		{
			listUserInfoListener.put(listUserInfoListener.size(), listener);
		}
	}

	private void callbackTencentUserInfo(final String strNickname, final String strPhotoURL, final String strError)
	{
		for (int i = 0; i < listUserInfoListener.size(); ++i)
		{
			listUserInfoListener.get(i).onUserInfo(strNickname, strPhotoURL, strError);
		}
	}

	/**
	 * Show tencent qq login activity
	 */
	public void login()
	{
		if (!mQQAuth.isSessionValid())
		{
			IUiListener listener = new BaseUiListener()
			{
				@Override
				protected void doComplete(JSONObject values)
				{
					Logs.showTrace("QQ Login Complete: " + values.toString());

					if (null != values)
					{
						Logs.showTrace("QQ Open Id: " + mQQAuth.getQQToken().getOpenId());
						Logs.showTrace("QQ Access Token:" + mQQAuth.getQQToken().getAccessToken());

						if (mQQAuth.isSessionValid())
						{
							updateUserInfo(mQQAuth.getQQToken());
						}
						callbackTencentLoginResult(mQQAuth.getQQToken().getOpenId(),
								mQQAuth.getQQToken().getAccessToken(), null);
					}
				}
			};
			mQQAuth.login(theActivity, "all", listener);
			mTencent.login(theActivity, "all", listener);
			Logs.showTrace("QQ Login");
		}
		else
		{
			logout();
		}

	}

	public void logout()
	{
		mQQAuth.logout(theActivity);
		if (null != mQQAuth && mQQAuth.isSessionValid())
		{
			updateUserInfo(mQQAuth.getQQToken());
		}
		Logs.showTrace("QQ Logout");
	}

	private class BaseUiListener implements IUiListener
	{

		@Override
		public void onComplete(Object response)
		{
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values)
		{

		}

		@Override
		public void onError(UiError e)
		{
			Logs.showError("QQ Error: " + e.errorDetail);
			callbackTencentLoginResult(null, null, e.errorDetail);
		}

		@Override
		public void onCancel()
		{
			Logs.showTrace("QQ Cancel");
			callbackTencentLoginResult(null, null, "QQ Cancel");
		}
	}

	private void updateUserInfo(QQToken strAccessToken)
	{

		if (null != strAccessToken)
		{
			IUiListener listener = new IUiListener()
			{
				@Override
				public void onComplete(final Object response)
				{
					Logs.showTrace("updateUserInfo QQ Response: " + response.toString());
					try
					{
						JSONObject json = (JSONObject) response;
						if (json.has("nickname"))
						{
							Logs.showTrace("QQ Nickname:" + json.getString("nickname"));
						}
						if (json.has("figureurl"))
						{
							Logs.showTrace("QQ Photo:" + json.getString("figureurl_qq_2"));
						}
						callbackTencentUserInfo(json.getString("nickname"), json.getString("figureurl_qq_2"), null);
					}
					catch (Exception e)
					{
						Logs.showError("Tencent Exception:" + e.toString());
						callbackTencentUserInfo(null, null, e.toString());
					}
				}

				@Override
				public void onCancel()
				{
					Logs.showError("Tencent Get User Info Cancel");
					callbackTencentUserInfo(null, null, "Tencent Get User Info Cancel");
				}

				@Override
				public void onError(UiError e)
				{
					Logs.showError("Tencent Exception:" + e.errorDetail);
					callbackTencentUserInfo(null, null, e.errorDetail);
				}
			};
			mInfo = new UserInfo(theActivity, strAccessToken);
			mInfo.getUserInfo(listener);
		}
	}
}
