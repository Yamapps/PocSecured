package com.amf.pocsecured.data;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.amf.pocsecured.ext.MicrosoftLoginHelper;
import com.amf.pocsecured.model.PublicHoliday;
import com.amf.pocsecured.model.mapper.PublicHolidayMapper;
import com.amf.pocsecured.network.DtdPlannerRetrofitApi;
import com.amf.pocsecured.network.dto.PublicHolidayDto;
import com.microsoft.identity.client.exception.MsalUiRequiredException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Repository class that is responsible for retrieving all calendar related data. Implements {@link
 * ICalendarRepository}
 *
 * @author youssefamrani
 */
public class CalendarRepositoryImpl implements ICalendarRepository
{
	private final DtdPlannerRetrofitApi mDtdPlannerRetrofitApi;
	private final MicrosoftLoginHelper mMicrosoftLoginHelper;

	@Inject
	public CalendarRepositoryImpl(DtdPlannerRetrofitApi retrofitApi, MicrosoftLoginHelper microsoftLoginHelper)
	{
		mDtdPlannerRetrofitApi = retrofitApi;
		mMicrosoftLoginHelper = microsoftLoginHelper;
	}

	private Observable<String> acquireAccessTokenSilent()
	{
		return mMicrosoftLoginHelper.acquireTokenSilentAsync(MicrosoftLoginHelper.DTD_PLANNER_SCOPES);
	}

	private Observable<String> acquireAccessTokenInteractive(Activity activity)
	{
		return mMicrosoftLoginHelper.acquireTokenInteractive(activity, MicrosoftLoginHelper.DTD_PLANNER_SCOPES);
	}

	public Observable<List<PublicHoliday>> fetchPublicHolidaysFromRemoteServer(String accessToken)
	{
		return Observable.create(emitter->{
			try
			{
				String authHeader = "Bearer " + accessToken;
				Response<List<PublicHolidayDto>> response = mDtdPlannerRetrofitApi.fetchPublicHolidays(authHeader).execute();

				if (response.isSuccessful())
				{
					assert response.body() != null;
					List<PublicHoliday> holidayList = response.body().stream() //
															  .map(PublicHolidayMapper::mapDto) //
															  .collect(Collectors.toList());

					emitter.onNext(holidayList);
					emitter.onComplete();
				}
				else
				{
					Exception exception;
					if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED)
					{
						exception = new IOException("User access unauthorized");
					}
					else
					{
						exception = new IOException("An error occurred during public holidays request");
					}

					emitter.onError(exception);
				}
			}
			catch (ConnectException | SocketTimeoutException e)
			{
				Timber.e(e);
				emitter.onError(new IOException("A network problem occurred.\nPlease try again later."));
			}
			catch (UnknownHostException e)
			{
				Timber.e(e);
				emitter.onError(new IOException("No network available.\nPlease try again later."));
			}
			catch (Exception e)
			{
				Timber.e(e);
				emitter.onError(new IOException("An error occurred during public holidays request"));
			}
		});
	}

	/**
	 * Fetches list of {@link PublicHoliday} from distant server
	 *
	 * @param context current {@link Context}
	 * @return @return {@link Observable} <list of {@link PublicHoliday}> with informations
	 */
	@Override
	public Observable<List<PublicHoliday>> fetchPublicHolidays(Context context)
	{
		return acquireAccessTokenSilent() //
					   .observeOn(Schedulers.io()).flatMap((Function<String, ObservableSource<List<PublicHoliday>>>) accessToken->{
					Observable<List<PublicHoliday>> result;
					if (!TextUtils.isEmpty(accessToken))
					{
						result = fetchPublicHolidaysFromRemoteServer(accessToken);
					}
					else
					{
						result = Observable.empty();
					}
					return result;
				}).onErrorResumeNext(throwable->{
					Observable<List<PublicHoliday>> result = null;
					if (throwable instanceof MsalUiRequiredException)
					{
						String errorMessage = ((MsalUiRequiredException) throwable).getErrorCode();
						if (errorMessage != null && errorMessage.equals(MicrosoftLoginHelper.ERROR_INVALID_GRANT))
						{
							result = fetchPublicHolidaysInteractive(context);
						}
					}
					if (result == null)
					{
						result = Observable.error(throwable);
					}
					return result;
				});
	}

	public Observable<List<PublicHoliday>> fetchPublicHolidaysInteractive(Context context)
	{
		return acquireAccessTokenInteractive((Activity) context) //
					   .observeOn(Schedulers.io()).flatMap((Function<String, ObservableSource<List<PublicHoliday>>>) accessToken->{
					Observable<List<PublicHoliday>> result;
					if (!TextUtils.isEmpty(accessToken))
					{
						result = fetchPublicHolidaysFromRemoteServer(accessToken);
					}
					else
					{
						result = Observable.empty();
					}
					return result;
				});
	}
}
