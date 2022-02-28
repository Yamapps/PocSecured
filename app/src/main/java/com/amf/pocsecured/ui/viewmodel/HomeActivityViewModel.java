package com.amf.pocsecured.ui.viewmodel;

import android.content.Context;

import com.amf.pocsecured.BuildConfig;
import com.amf.pocsecured.domain.usecase.FetchPublicHolidaysUseCase;
import com.amf.pocsecured.domain.usecase.InitMicrosoftLoginUseCase;
import com.amf.pocsecured.domain.usecase.SignInUseCase;
import com.amf.pocsecured.model.User;
import com.amf.pocsecured.ui.home.HomeActivity;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

/**
 * @author youssefamrani
 */

public class HomeActivityViewModel extends ViewModel
{

	protected MutableLiveData<User> mUser;
	protected MutableLiveData<Boolean> mLoading;
	protected MutableLiveData<String> mError;

	protected SignInUseCase mSignInUseCase;
	protected InitMicrosoftLoginUseCase mInitMicrosoftLoginUseCase;
	protected FetchPublicHolidaysUseCase mFetchPublicHolidaysUseCase;

	@Inject
	public HomeActivityViewModel(SignInUseCase signInUseCase, //
								 InitMicrosoftLoginUseCase initMicrosoftLoginUseCase, //
								 FetchPublicHolidaysUseCase fetchPublicHolidaysUseCase) //
	{
		mUser = new MutableLiveData<>();
		mLoading = new MutableLiveData<>();
		mError = new MutableLiveData<>();

		mSignInUseCase = signInUseCase;
		mInitMicrosoftLoginUseCase = initMicrosoftLoginUseCase;
		mFetchPublicHolidaysUseCase = fetchPublicHolidaysUseCase;
	}

	public LiveData<User> getUser()
	{
		return mUser;
	}

	public void setUser(User user)
	{
		mUser.postValue(user);
	}

	public LiveData<Boolean> getLoading()
	{
		return mLoading;
	}

	public LiveData<String> getError()
	{
		return mError;
	}

	/**
	 * Initialize MSAL library.
	 * Post results using {@link LiveData}
	 *
	 * @param context current {@link Context}
	 */
	public void initLogin(Context context)
	{
		mLoading.postValue(true);
		mInitMicrosoftLoginUseCase.execute(new DisposableObserver<User>()
		{
			@Override
			public void onNext(@NotNull User user)
			{
				mLoading.postValue(false);
				mUser.postValue(user);
			}

			@Override
			public void onError(@NotNull Throwable e)
			{
				mLoading.postValue(false);
				mUser.postValue(null);
				mError.postValue(e.getMessage());
			}

			@Override
			public void onComplete()
			{
				mLoading.postValue(false);
				dispose();
			}
		}, new InitMicrosoftLoginUseCase.Params(context));
	}

	/**
	 * SignIn user using MSAL library.
	 * Post results using {@link LiveData}
	 *
	 * @param activity current {@link HomeActivity}
	 */
	public void signIn(HomeActivity activity)
	{
		mLoading.postValue(true);
		mSignInUseCase.execute(new DisposableObserver<User>()
		{
			@Override
			public void onNext(@NotNull User user)
			{
				mUser.postValue(user);
				mLoading.postValue(false);
			}

			@Override
			public void onError(@NotNull Throwable e)
			{
				mError.postValue(e.getMessage());
				mUser.postValue(mUser.getValue());
				mLoading.postValue(false);
				Timber.e(e);
				if (BuildConfig.DEBUG)
				{
					e.printStackTrace();
				}
			}

			@Override
			public void onComplete()
			{
				dispose();
				mLoading.postValue(false);
			}
		}, new SignInUseCase.Params(activity));
	}

	/**
	 * Clean current {@link HomeActivityViewModel}
	 */
	public void onDestroy()
	{
		mInitMicrosoftLoginUseCase.onDestroy();
	}
}
