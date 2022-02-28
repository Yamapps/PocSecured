package com.amf.pocsecured.domain.usecase;

import android.text.TextUtils;

import com.amf.pocsecured.data.IUserRepository;
import com.amf.pocsecured.ext.MicrosoftLoginHelper;
import com.amf.pocsecured.model.User;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Fetches user informations from MSAL distant servers
 *
 * @author youssefamrani
 */

public class FetchUserInfosUseCase extends UseCase<User, Void>
{
	private final IUserRepository mUserRepository;

	@Inject
	public FetchUserInfosUseCase(IUserRepository userRepository)
	{
		super();
		mUserRepository = userRepository;
	}

	@Override
	Observable<User> buildUseCaseObservable(Void unused)
	{
		return mUserRepository.fetchUserInfos();
	}

	@Override
	public void onDestroy()
	{
		dispose();
	}
}
