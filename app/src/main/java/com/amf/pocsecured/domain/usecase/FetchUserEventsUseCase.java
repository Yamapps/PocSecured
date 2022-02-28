package com.amf.pocsecured.domain.usecase;

import com.amf.pocsecured.data.IUserRepository;
import com.amf.pocsecured.model.Event;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Fetches user events from MSAL distant servers
 *
 * @author youssefamrani
 */

public class FetchUserEventsUseCase extends UseCase<List<Event>, Void>
{
	private final IUserRepository mUserRepository;

	@Inject
	public FetchUserEventsUseCase(IUserRepository userRepository)
	{
		super();
		mUserRepository = userRepository;
	}

	@Override
	Observable<List<Event>> buildUseCaseObservable(Void unused)
	{

		return mUserRepository.fetchUserEvents();
	}

	@Override
	public void onDestroy()
	{
		dispose();
	}
}
