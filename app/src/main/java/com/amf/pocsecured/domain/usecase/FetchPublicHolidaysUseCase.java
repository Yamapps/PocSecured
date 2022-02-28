package com.amf.pocsecured.domain.usecase;

import android.app.Activity;

import com.amf.pocsecured.data.ICalendarRepository;
import com.amf.pocsecured.model.PublicHoliday;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Fetches public holidays from distant server
 *
 * @author youssefamrani
 */
public class FetchPublicHolidaysUseCase extends UseCase<List<PublicHoliday>, FetchPublicHolidaysUseCase.Params>
{
	private final ICalendarRepository mCalendarRepository;

	@Inject
	public FetchPublicHolidaysUseCase(ICalendarRepository calendarRepository)
	{
		super();
		mCalendarRepository = calendarRepository;
	}

	@Override
	Observable<List<PublicHoliday>> buildUseCaseObservable(FetchPublicHolidaysUseCase.Params params)
	{
		return mCalendarRepository.fetchPublicHolidays(params.activity);
	}

	@Override
	public void onDestroy()
	{
		dispose();
	}

	public static final class Params
	{

		private final Activity activity;

		public Params(Activity mActivity)
		{
			this.activity = mActivity;
		}

	}
}
