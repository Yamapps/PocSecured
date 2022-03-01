package com.amf.pocsecured.data;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.amf.pocsecured.BuildConfig;
import com.amf.pocsecured.ext.MicrosoftLoginHelper;
import com.amf.pocsecured.model.Event;
import com.amf.pocsecured.model.User;
import com.amf.pocsecured.model.mapper.EventMapper;
import com.amf.pocsecured.model.mapper.UserMapper;
import com.amf.pocsecured.network.MSGraphRetrofitApi;
import com.amf.pocsecured.network.dto.EventListDto;
import com.amf.pocsecured.network.dto.UserDto;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import timber.log.Timber;

import static com.amf.pocsecured.ext.MicrosoftLoginHelper.USER_INFOS_SCOPES;

/**
 * Repository class that is responsible for retrieving all user related data. Implements {@link
 * IUserRepository}
 *
 * @author youssefamrani
 */

public class UserRepositoryImpl implements IUserRepository
{
	private final MSGraphRetrofitApi mGraphRetrofitApi;
	private final MicrosoftLoginHelper mMicrosoftLoginHelper;

	private User mCurrentUser;

	@Inject
	public UserRepositoryImpl(MSGraphRetrofitApi graphRetrofitApi, MicrosoftLoginHelper microsoftLoginHelper)
	{
		mGraphRetrofitApi = graphRetrofitApi;
		mMicrosoftLoginHelper = microsoftLoginHelper;
	}

	/**
	 * Launch MSAL library initialization see {@link MicrosoftLoginHelper#init(Context)}
	 *
	 * @param context current {@link Context}
	 * @return {@link Observable}<{@link Event}>
	 */
	@Override
	public Observable<Boolean> init(Context context)
	{
		return mMicrosoftLoginHelper.init(context);
	}

	/**
	 * Launch MSAL sign in see {@link MicrosoftLoginHelper#signIn(Activity, String[])} (Activity)}
	 *
	 * @param activity current {@link Activity}
	 * @return {@link Observable}<{@link Boolean}> true if sign in is usccessful
	 */
	@Override
	public Observable<String> signIn(Activity activity)
	{
		return mMicrosoftLoginHelper.signIn(activity, USER_INFOS_SCOPES);
	}

	/**
	 * Launch MSAL sign out see {@link MicrosoftLoginHelper#signOut()}
	 *
	 * @return {@link Completable}
	 */
	@Override
	public Completable signOut()
	{
		return mMicrosoftLoginHelper.signOut().doOnComplete(()->mCurrentUser = null);
	}

	/**
	 * Fetches {@link User} specific informations from distant server
	 *
	 * @return {@link Observable}<{@link User}> with informations
	 */
	@Override
	public Observable<User> fetchUserInfos()
	{
		return acquireAccessToken() //
					   .observeOn(Schedulers.io()) //
					   .flatMap((Function<String, ObservableSource<User>>) accessToken->{
						   Observable<User> result;
						   if (!TextUtils.isEmpty(accessToken) && isUserSignedIn())
						   {
							   result = fetchUserInfosFromRemoteServer(accessToken);
						   }
						   else
						   {
							   result = Observable.empty();
						   }
						   return result;
					   });
	}

	/**
	 * Fetches {@link User} specific informations from distant server
	 *
	 * @param accessToken {@link User} retrieve using MSAL library with specific scopes
	 * @return {@link Observable}<{@link User}> with informations
	 */
	private Observable<User> fetchUserInfosFromRemoteServer(String accessToken)
	{

		return Observable.create(emitter->{
			try
			{
				String authHeader = "Bearer " + accessToken;
				/*
				 * Launch HTTP request
				 */
				Response<UserDto> response = mGraphRetrofitApi.me(authHeader).execute();

				/*
				 * Check if the call was successful
				 */
				if (response.isSuccessful())
				{
					UserDto userDto = response.body();
					/*
					 * Map the DTO object into our data model object
					 */
					mCurrentUser = UserMapper.mapDto(userDto);

					/*
					 * Extract user role from the Account instance retrieved during user sign-in
					 */
					mCurrentUser.setRole(mMicrosoftLoginHelper.getCurrentUserRole());

					emitter.onNext(mCurrentUser);
					emitter.onComplete();
				}
				else
				{
					/*
					 * Handle HTTP error returned by remote server
					 */
					Exception exception;
					if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED)
					{
						exception = new IOException("User access unauthorized");
					}
					else
					{
						exception = new IOException("An error occurred during user infos request");
					}

					emitter.onError(exception);
				}
			}
			/*
			 * Handle generic exceptions thrown by the HTTP call (TimeOut, No Network...)
			 */
			catch (ConnectException | SocketTimeoutException e)
			{
				/*
				 * A time-out occured
				 */
				Timber.e(e);
				emitter.onError(new IOException("A network problem occurred.\nPlease try again later."));
			}
			catch (UnknownHostException e)
			{
				/*
				 * No network available
				 */
				Timber.e(e);
				emitter.onError(new IOException("No network available.\nPlease try again later."));
			}
			catch (Exception e)
			{
				/*
				 * Any other generic exception
				 */
				Timber.e(e);
				emitter.onError(new IOException("An error occurred during user infos request"));
			}
		});
	}

	public Observable<String> acquireAccessToken()
	{
		return mMicrosoftLoginHelper.acquireTokenSilentAsync(MicrosoftLoginHelper.USER_INFOS_SCOPES);
	}

	/**
	 * {@link MicrosoftLoginHelper#isUserSignedIn()}
	 *
	 * @return {@link Boolean} true if user signed in
	 */
	@Override
	public Boolean isUserSignedIn()
	{
		return mMicrosoftLoginHelper.isUserSignedIn();
	}

	/**
	 * @return current {@link User} if exists
	 */
	public User getCurrentUser()
	{
		return mCurrentUser;
	}

	/**
	 * Fetches {@link User} list of {@link Event} from distant server
	 *
	 * @return {@link Observable} <list of {@link Event}> with informations
	 */
	@Override
	public Observable<List<Event>> fetchUserEvents()
	{
		return acquireAccessToken() //
					   .observeOn(Schedulers.io()) //
					   .flatMap((Function<String, ObservableSource<List<Event>>>) accessToken->{
						   Observable<List<Event>> result;
						   if (!TextUtils.isEmpty(accessToken))
						   {
							   result = fetchUserEventsFromRemoteServer(accessToken);
						   }
						   else
						   {
							   result = Observable.empty();
						   }
						   return result;
					   });
	}

	/**
	 * Fetches list of {@link Event}  from distant server
	 *
	 * @param accessToken {@link User} retrieve using MSAL library with specific scopes
	 * @return {@link Observable}<{@link Event}>
	 */
	private Observable<List<Event>> fetchUserEventsFromRemoteServer(String accessToken)
	{
		return Observable.create(emitter->{
			try
			{
				String authHeader = "Bearer " + accessToken;

				Response<EventListDto> response = mGraphRetrofitApi.events(authHeader).execute();

				if (response.isSuccessful())
				{
					EventListDto eventListDto = response.body();
					List<Event> events = EventMapper.mapDto(eventListDto);

					//Sort event by start date -> more recent first
					events.sort((event1, event2)->-event1.getStart().compareTo(event2.getStart()));

					emitter.onNext(events);
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
						exception = new IOException("An error occurred during user events request");
					}

					emitter.onError(exception);
				}
			}
			catch (ConnectException | SocketTimeoutException e)
			{
				Timber.e(e);
				emitter.onError(new IOException("A network problem occurred.\nPlease try again later."));
				if (BuildConfig.DEBUG)
				{
					e.printStackTrace();
				}
			}
			catch (UnknownHostException e)
			{
				Timber.e(e);
				emitter.onError(new IOException("No network available.\nPlease try again later."));
				if (BuildConfig.DEBUG)
				{
					e.printStackTrace();
				}
			}
			catch (Exception e)
			{
				Timber.e(e);
				emitter.onError(new IOException("An error occurred during user infos request"));
				if (BuildConfig.DEBUG)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Clean no longer used resources
	 */
	@Override
	public void onDestroy()
	{
		mMicrosoftLoginHelper.onDestroy();
	}
}
