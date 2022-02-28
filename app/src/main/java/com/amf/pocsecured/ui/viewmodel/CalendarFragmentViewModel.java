package com.amf.pocsecured.ui.viewmodel;

import android.app.Activity;
import android.content.Context;

import com.amf.pocsecured.BuildConfig;
import com.amf.pocsecured.domain.usecase.FetchPublicHolidaysUseCase;
import com.amf.pocsecured.model.PublicHoliday;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

public class CalendarFragmentViewModel extends ViewModel
{

	protected MutableLiveData<List<PublicHoliday>> mPublicHolidays;
	protected MutableLiveData<Boolean> mLoading;
	protected MutableLiveData<String> mError;

	protected FetchPublicHolidaysUseCase mFetchPublicHolidaysUseCase;

	@Inject
	public CalendarFragmentViewModel(FetchPublicHolidaysUseCase fetchUserEventsUseCase)
	{
		mPublicHolidays = new MutableLiveData<>();
		mLoading = new MutableLiveData<>();
		mError = new MutableLiveData<>();

		mFetchPublicHolidaysUseCase = fetchUserEventsUseCase;
	}

	public LiveData<List<PublicHoliday>> getPublicHolidays()
	{
		return mPublicHolidays;
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
	 * Fetch list of {@link PublicHoliday} required by calendar screen.
	 * Post results using {@link LiveData}
	 */
	public void fetchPublicHolidays(Activity activity){
		mLoading.postValue(true);
		mFetchPublicHolidaysUseCase.execute(new DisposableObserver<List<PublicHoliday>>()
		{
			@Override
			public void onNext(@NotNull List<PublicHoliday> events)
			{
				mPublicHolidays.postValue(events);
				mLoading.postValue(false);
			}

			@Override
			public void onError(@NotNull Throwable e)
			{
				mError.postValue(e.getMessage());
				mPublicHolidays.postValue(mPublicHolidays.getValue());
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
		}, new FetchPublicHolidaysUseCase.Params(activity));
	}
}