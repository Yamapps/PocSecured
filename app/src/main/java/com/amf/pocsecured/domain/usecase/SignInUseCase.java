package com.amf.pocsecured.domain.usecase;

import android.app.Activity;
import android.text.TextUtils;

import com.amf.pocsecured.data.IUserRepository;
import com.amf.pocsecured.model.User;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Sign in user using MSAL library
 *
 * @author youssefamrani
 */

public class SignInUseCase extends UseCase<User, SignInUseCase.Params>
{
	IUserRepository mUserRepository;

	@Inject
	public SignInUseCase(IUserRepository userRepository)
	{
		super();
		mUserRepository = userRepository;
	}

	@Override
	Observable<User> buildUseCaseObservable(SignInUseCase.Params params)
	{
		return mUserRepository.signIn(params.activity) //
					   .observeOn(Schedulers.io()) //
					   .flatMap((Function<String, ObservableSource<User>>) accessToken->{ //
						   if (!TextUtils.isEmpty(accessToken))
						   {
							   return mUserRepository.fetchUserInfos();
						   }
						   else
						   {
							   return Observable.empty();
						   }
					   });
	}

	public static final class Params
	{

		private final Activity activity;

		public Params(Activity activity)
		{
			this.activity = activity;
		}
	}

	@Override
	public void onDestroy()
	{
		dispose();
	}
}
