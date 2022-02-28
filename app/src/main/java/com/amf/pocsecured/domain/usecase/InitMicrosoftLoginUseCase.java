package com.amf.pocsecured.domain.usecase;

import android.content.Context;

import com.amf.pocsecured.data.IUserRepository;
import com.amf.pocsecured.model.User;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Implements MSAL library initialization use case.
 *
 * @author youssefamrani
 */
public class InitMicrosoftLoginUseCase extends UseCase<User, InitMicrosoftLoginUseCase.Params>
{
	private final IUserRepository mUserRepository;

	@Inject
	public InitMicrosoftLoginUseCase(IUserRepository userRepository)
	{
		super();
		mUserRepository = userRepository;
	}

	@Override
	Observable<User> buildUseCaseObservable(InitMicrosoftLoginUseCase.Params params)
	{
		return mUserRepository.init(params.context) //
					   .observeOn(Schedulers.io()) //
					   .flatMap((Function<Boolean, ObservableSource<User>>) initDone->{
						   Observable<User> result;
						   if (initDone)
						   {
							   result = mUserRepository.fetchUserInfos();
						   }
						   else
						   {
							   result = Observable.empty();
						   }
						   return result;
					   });
	}

	@Override
	public void onDestroy()
	{
		dispose();
	}

	public static final class Params
	{
		private final Context context;

		public Params(Context context)
		{
			this.context = context;
		}
	}
}
