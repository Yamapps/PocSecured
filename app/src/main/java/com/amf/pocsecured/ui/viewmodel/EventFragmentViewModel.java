package com.amf.pocsecured.ui.viewmodel;

import com.amf.pocsecured.BuildConfig;
import com.amf.pocsecured.domain.usecase.FetchUserEventsUseCase;
import com.amf.pocsecured.model.Event;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

public class EventFragmentViewModel extends ViewModel
{

	protected MutableLiveData<List<Event>> mEvents;
	protected MutableLiveData<Boolean> mLoading;
	protected MutableLiveData<String> mError;

	protected FetchUserEventsUseCase mFetchUserEventsUseCase;

	@Inject
	public EventFragmentViewModel(FetchUserEventsUseCase fetchUserEventsUseCase)
	{
		mEvents = new MutableLiveData<>();
		mLoading = new MutableLiveData<>();
		mError = new MutableLiveData<>();

		mFetchUserEventsUseCase = fetchUserEventsUseCase;
	}

	public LiveData<List<Event>> getEvents()
	{
		return mEvents;
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
	 * Fetch list of {@link Event} required by events screen
	 */
	public void fetchEvents(){

		mLoading.postValue(true);
		mFetchUserEventsUseCase.execute(new DisposableObserver<List<Event>>()
		{
			@Override
			public void onNext(@NotNull List<Event> events)
			{
				mEvents.postValue(events);
				mLoading.postValue(false);
			}

			@Override
			public void onError(@NotNull Throwable e)
			{
				mError.postValue(e.getMessage());
				mEvents.postValue(mEvents.getValue());
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
		}, null);
	}
}