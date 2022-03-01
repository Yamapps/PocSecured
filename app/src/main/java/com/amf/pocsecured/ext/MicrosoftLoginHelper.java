package com.amf.pocsecured.ext;

import android.app.Activity;
import android.content.Context;

import com.amf.pocsecured.BuildConfig;
import com.amf.pocsecured.R;
import com.amf.pocsecured.model.Role;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalException;
import com.nimbusds.jose.shaded.json.JSONArray;

import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import timber.log.Timber;

/**
 * Helper class to interact with MSAL library
 *
 * @author youssefamrani
 */
@Singleton public class MicrosoftLoginHelper
{

	public final static String[] USER_INFOS_SCOPES = {"User.Read", "Calendars.Read"};
	public final static String[] DTD_PLANNER_SCOPES = {"api://9498ad5c-5a2e-43af-8eca-29d8c52aaf4d/api-access"};

	private final static String CLAIM_ROLES_KEY = "roles";

	/* Azure AD v2 Configs */
	private ISingleAccountPublicClientApplication mSingleAccountApp;
	private IAccount mAccount;
	private IAuthenticationResult mAuthResult;

	@Inject
	public MicrosoftLoginHelper()
	{
	}

	/**
	 * Initializes MSAL library and loads the currently signed-in account, if there's any.
	 *
	 * @param context current {@link Context}
	 * @return {@link Observable}<{@link Boolean}> true is init is successful
	 */
	public Observable<Boolean> init(Context context)
	{
		return Observable.create(emitter->PublicClientApplication.createSingleAccountPublicClientApplication(context, R.raw.auth_config_single_account, new IPublicClientApplication.ISingleAccountApplicationCreatedListener()
		{
			@Override
			public void onCreated(ISingleAccountPublicClientApplication application)
			{
				mSingleAccountApp = application;
				if (mSingleAccountApp == null)
				{
					emitter.onNext(false);
					emitter.onComplete();
				}

				mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback()
				{
					@Override
					public void onAccountLoaded(@Nullable IAccount activeAccount)
					{
						if (activeAccount != null)
						{
							/*
							 * A user was previsouly signed in
							 */
							mAccount = activeAccount;
							printCurrentAccount();
							emitter.onNext(true);
						}
						else
						{
							/*
							 * No user signed in
							 */
							emitter.onNext(false);
						}
						emitter.onComplete();
					}

					@Override
					public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount)
					{
					}

					@Override
					public void onError(@NonNull MsalException exception)
					{
						emitter.onError(exception);
					}
				});
			}

			@Override
			public void onError(MsalException exception)
			{
				if(BuildConfig.DEBUG){
					exception.printStackTrace();
				}
				emitter.onError(exception);
			}
		}));
	}

	/**
	 * Acquires access token for specified scopes.
	 *
	 * @return {@link Observable}<{@link String}> access token
	 */
	public Observable<String> acquireTokenSilentAsync(String[] scopes)
	{
		if (mSingleAccountApp == null)
		{
			return Observable.error(new IllegalStateException("Missing MSAL public account application"));
		}
		if (mAccount == null)
		{
			return Observable.error(new IllegalStateException("Missing MSAL user account"));
		}
		return Observable.create(emitter->mSingleAccountApp.acquireTokenSilentAsync(scopes, mAccount.getAuthority(), new SilentAuthenticationCallback()
		{

			@Override
			public void onSuccess(IAuthenticationResult authenticationResult)
			{
				/* Successfully got a token */
				mAuthResult = authenticationResult;
				mAccount = authenticationResult.getAccount();
				String accessToken = mAuthResult.getAccessToken();
				emitter.onNext(accessToken);
				emitter.onComplete();
			}

			@Override
			public void onError(MsalException exception)
			{
				/* Failed to acquireToken */
				Timber.e("Authentication failed: %s", exception.toString());
				emitter.onError(exception);
			}
		}));
	}

	/**
	 * @return true if a user is currently signed in
	 */
	public boolean isUserSignedIn()
	{
		return mAccount != null && mAuthResult != null;
	}

	/**
	 * @return current user {@link Role} if a user is currently signed in
	 */
	public Role getCurrentUserRole()
	{
		Role role = Role.DEFAULT_ACCESS;

		if (mAccount == null)
		{
			Timber.e("No user account available. Please sign in.");
			return role;
		}

		/*
		 * We get the claims from the previously retrieved 'mAccount' instance
		 */
		Map<String, ?> claims = mAccount.getClaims();
		if (claims == null || claims.isEmpty())
		{
			return role;
		}

		if (claims.get(CLAIM_ROLES_KEY) != null)
		{
			JSONArray roles = (JSONArray) claims.get(CLAIM_ROLES_KEY);
			if (roles != null)
			{
				role = Role.findByValue((String) roles.get(0));
			}
		}

		return role;
	}

	/**
	 * @param activity {@link Activity} used for signing in
	 * @return {@link Observable}<{@link String}> access token if sign in is successful
	 */
	public Observable<String> signIn(Activity activity, String[] scopes)
	{
		if (mSingleAccountApp == null)
		{
			return Observable.error(new IllegalStateException("Library not connected"));
		}

		return Observable.create((ObservableOnSubscribe<String>) emitter->mSingleAccountApp.signIn(activity, null, scopes, new AuthenticationCallback()
		{
			@Override
			public void onCancel()
			{
				/* Operation cancelled by user */
				mAuthResult = null;
				mAccount = null;
				emitter.onError(new Exception("Operation cancelled"));
			}

			@Override
			public void onSuccess(IAuthenticationResult authenticationResult)
			{
				/* Successfully got a token */
				Timber.d("Successfully authenticated");
				mAuthResult = authenticationResult;
				mAccount = authenticationResult.getAccount();
				emitter.onNext(mAuthResult.getAccessToken());
				emitter.onComplete();
			}

			@Override
			public void onError(MsalException exception)
			{
				/* Failed to acquireToken */
				Timber.e("Authentication failed: %s", exception.toString());
				emitter.onError(exception);
			}
		}));
	}

	/**
	 * Removes the signed-in account and cached tokens from this app (or device, if the device is in
	 * shared mode).
	 *
	 * @return {@link Completable} completed if sign out is successful
	 */
	public Completable signOut()
	{
		return Completable.create(emitter->mSingleAccountApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback()
		{
			@Override
			public void onSignOut()
			{
				/*
				 * User is signed out
				 * clean signed out user infos
				 */
				mAccount = null;
				mAuthResult = null;

				emitter.onComplete();
			}

			@Override
			public void onError(@NonNull MsalException exception)
			{
				emitter.onError(exception);
			}
		}));
	}

	/**
	 * Logger method that prints on Logcat the MSAL account informations
	 */
	private void printCurrentAccount()
	{
		Timber.d(">>> ");
		Timber.d(">>> ");
		Timber.d(">>> PRINT ACCOUNT ------");
		if (mAccount == null)
		{
			Timber.d(">>> No account available");
			Timber.d(">>> ");
			Timber.d(">>> ");
			return;
		}
		String userName = Objects.requireNonNull(mAccount).getUsername();
		String authority = Objects.requireNonNull(mAccount).getAuthority();
		String id = Objects.requireNonNull(mAccount).getId();
		Map<String, ?> claims = Objects.requireNonNull(mAccount).getClaims();
		Timber.d(">>> userName : %s", userName);
		Timber.d(">>> authority : %s", authority);
		Timber.d(">>> id : %s", id);
		assert claims != null;
		for (Map.Entry<String, ?> entry : claims.entrySet())
		{
			String key = entry.getKey();
			Object value = entry.getValue();
			Timber.d(">>> HomeFragment.onAccountLoaded - CLAIM - key : " + key + " - value : " + value);
		}
		if (claims.get("roles") != null)
		{
			com.nimbusds.jose.shaded.json.JSONArray roles = (com.nimbusds.jose.shaded.json.JSONArray) claims.get("roles");
			assert roles != null;
			String role = (String) roles.get(0);
			Timber.d(">>> role : %s", role);
		}
		Timber.d(">>> userName : %s", userName);
		Timber.d(">>> ");
		Timber.d(">>> ");
	}

	/**
	 * Cleans used resources
	 */
	public void onDestroy()
	{
		mAuthResult = null;
		mAccount = null;
	}
}
