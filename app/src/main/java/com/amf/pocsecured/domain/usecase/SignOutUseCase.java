package com.amf.pocsecured.domain.usecase;

import com.amf.pocsecured.data.IUserRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 *
 * Sign out user using MSAL library
 *
 * @author youssefamrani
 */

public class SignOutUseCase extends UseCase<Void, Void>
{
	private final IUserRepository mUserRepository;

	@Inject
	public SignOutUseCase(IUserRepository userRepository)
	{
		super();
		mUserRepository = userRepository;
	}

	@Override
	Observable<Void> buildUseCaseObservable(Void unused)
	{
		return mUserRepository.signOut().toObservable();
	}

	@Override
	public void onDestroy()
	{
		dispose();
	}
}
