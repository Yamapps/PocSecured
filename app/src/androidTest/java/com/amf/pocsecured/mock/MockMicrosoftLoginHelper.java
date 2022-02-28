package com.amf.pocsecured.mock;

import android.content.Context;

import com.amf.pocsecured.ext.MicrosoftLoginHelper;
import com.amf.pocsecured.model.Role;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * @author youssefamrani
 */

public class MockMicrosoftLoginHelper extends MicrosoftLoginHelper
{

	private final Boolean mInitResponse;
	private final Boolean mUserSignedIn;

	private Exception mInitException;
	private Boolean mSignOutResponse;
	private Role mRole = Role.DEFAULT_ACCESS;

	public MockMicrosoftLoginHelper(Boolean initResponse, Boolean userSignedIn)
	{
		super();
		mInitResponse = initResponse;
		mUserSignedIn = userSignedIn;
	}

	public MockMicrosoftLoginHelper(Boolean initResponse, Boolean userSignedIn, Exception initException)
	{
		super();
		mInitResponse = initResponse;
		mUserSignedIn = userSignedIn;
		mInitException = initException;
	}

	public MockMicrosoftLoginHelper(Boolean initResponse, Boolean userSignedIn, Boolean signOutResponse)
	{
		super();
		mInitResponse = initResponse;
		mUserSignedIn = userSignedIn;
		mSignOutResponse = signOutResponse;
	}

	public Role getRole()
	{
		return mRole;
	}

	public void setRole(Role role)
	{
		mRole = role;
	}

	@Override
	public boolean isUserSignedIn()
	{
		return mUserSignedIn;
	}

	@Override
	public Observable<Boolean> init(Context context)
	{
		if (!mInitResponse && mInitException != null)
		{
			return Observable.error(mInitException);
		}
		else
		{
			return Observable.just(mInitResponse);
		}
	}

	@Override
	public Completable signOut()
	{
		return mSignOutResponse
			   ? Completable.complete()
			   : Completable.error(new Exception(SIGN_OUT_ERROR_MESSAGE));
	}

	@Override
	public Observable<String> acquireTokenSilentAsync(String[] scopes)
	{
		return Observable.just("access_token");
	}

	@Override
	public Role getCurrentUserRole()
	{
		return mRole;
	}

	public static String SIGN_OUT_ERROR_MESSAGE = "Sign out error";
}
