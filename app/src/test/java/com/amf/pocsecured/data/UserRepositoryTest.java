package com.amf.pocsecured.data;

import android.content.Context;

import com.amf.pocsecured.NetworkSecurityPolicyWorkaround;
import com.amf.pocsecured.ext.MicrosoftLoginHelper;
import com.amf.pocsecured.model.Event;
import com.amf.pocsecured.model.Role;
import com.amf.pocsecured.model.User;
import com.amf.pocsecured.model.mapper.EventMapper;
import com.amf.pocsecured.model.mapper.UserMapper;
import com.amf.pocsecured.network.NetworkConstants;
import com.amf.pocsecured.network.dto.EventListDto;
import com.amf.pocsecured.network.dto.UserDto;
import com.amf.pocsecured.ui.home.HomeActivity;
import com.amf.pocsecured.utils.test.CommonTestUtils;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.SocketPolicy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class) //
@Config(shadows = {NetworkSecurityPolicyWorkaround.class}) //
public class UserRepositoryTest extends BaseRepositoryTest
{
	IUserRepository userRepository;

	@Mock
	MicrosoftLoginHelper mMicrosoftLoginHelper;

	@Override
	public void beforeTest()
	{
		userRepository = new UserRepositoryImpl(mGraphRetrofitApi, mMicrosoftLoginHelper);
	}

	@Test
	public void injectNotNull(){
		assertNotNull(userRepository);
	}

	@Test
	public void init_OK(){

		Context context = ApplicationProvider.getApplicationContext();

		when(mMicrosoftLoginHelper.init(context)).thenReturn(Observable.just(true));

		TestObserver<Boolean> testObserver = TestObserver.create();
		Observable<Boolean> result = userRepository.init(context);

		result.subscribe(testObserver);

		testObserver.assertSubscribed();
		testObserver.assertResult(true);
		testObserver.assertComplete();
	}

	@Test
	public void init_Fails(){

		Context context = ApplicationProvider.getApplicationContext();

		when(mMicrosoftLoginHelper.init(context)).thenReturn(Observable.just(false));

		TestObserver<Boolean> testObserver = TestObserver.create();
		Observable<Boolean> result = userRepository.init(context);

		result.subscribe(testObserver);

		testObserver.assertSubscribed();
		testObserver.assertResult(false);
		testObserver.assertComplete();
	}

	@Test
	public void init_Error(){
		Context context = ApplicationProvider.getApplicationContext();

		Exception initException = new Exception();
		when(mMicrosoftLoginHelper.init(context)).thenReturn(Observable.error(initException));

		TestObserver<Boolean> testObserver = TestObserver.create();
		Observable<Boolean> result = userRepository.init(context);

		result.subscribe(testObserver);

		testObserver.assertSubscribed();
		testObserver.assertError(initException);
		testObserver.assertNotComplete();
	}

	@Test
	public void signIn_OK(){
		HomeActivity homeActivity = Robolectric.buildActivity(HomeActivity.class).create().get();
		String accessToken = "access_token";
		when(mMicrosoftLoginHelper.signIn(homeActivity, MicrosoftLoginHelper.USER_INFOS_SCOPES)).thenReturn(Observable.just(accessToken));

		TestObserver<String> testObserver = TestObserver.create();
		Observable<String> result = userRepository.signIn(homeActivity);

		result.subscribe(testObserver);

		testObserver.assertSubscribed();
		testObserver.assertResult(accessToken);
		testObserver.assertComplete();
	}

	@Test
	public void signIn_Error(){
		HomeActivity homeActivity = Robolectric.buildActivity(HomeActivity.class).create().get();
		Exception exception = new Exception();

		when(mMicrosoftLoginHelper.signIn(homeActivity, MicrosoftLoginHelper.USER_INFOS_SCOPES)).thenReturn(Observable.error(exception));

		TestObserver<String> testObserver = TestObserver.create();
		Observable<String> result = userRepository.signIn(homeActivity);

		result.subscribe(testObserver);

		testObserver.assertSubscribed();
		testObserver.assertError(exception);
	}

	@Test
	public void signOut_OK(){
		when(mMicrosoftLoginHelper.signOut()).thenReturn(Completable.complete());

		TestObserver<Boolean> testObserver = TestObserver.create();
		Completable result = userRepository.signOut();

		result.subscribe(testObserver);

		testObserver.assertSubscribed();
		testObserver.assertComplete();
	}

	@Test
	public void signOut_Error(){
		Exception exception = new Exception();
		when(mMicrosoftLoginHelper.signOut()).thenReturn(Completable.error(exception));

		TestObserver<Boolean> testObserver = TestObserver.create();
		Completable result = userRepository.signOut();

		result.subscribe(testObserver);

		testObserver.assertSubscribed();
		testObserver.assertError(exception);
	}

	@Test
	public void fetchUserInfos_OK() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();

		String userStr = CommonTestUtils.openFile(context, "json/me_john.json");
		UserDto userDtoFromJson = gson.fromJson(userStr, UserDto.class);
		User user = UserMapper.mapDto(userDtoFromJson);

		Role mockedRole = Role.CONTRIBUTOR;
		when(mMicrosoftLoginHelper.getCurrentUserRole()).thenReturn(mockedRole);
		when(mMicrosoftLoginHelper.acquireTokenSilentAsync(Mockito.any())).thenReturn(Observable.just("access_token"));
		when(mMicrosoftLoginHelper.isUserSignedIn()).thenReturn(true);

		MockResponse response = new MockResponse()
							   .setResponseCode(HttpURLConnection.HTTP_OK)
							   .setBody(userStr);

		mockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(response));

		TestObserver<User> testObserver = TestObserver.create();
		Observable<User> result = userRepository.fetchUserInfos();
		result.subscribe(testObserver);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		testObserver.assertSubscribed();
		testObserver.assertComplete();

		List<User> results = testObserver.values();
		assertFalse(results.isEmpty());

		User resultUser = results.get(0);
		assertEquals(user.getDisplayName(), resultUser.getDisplayName());
		assertEquals(user.getEmail(), resultUser.getEmail());
		assertEquals(user.getFirstName(), resultUser.getFirstName());
		assertEquals(user.getLastName(), resultUser.getLastName());
		assertEquals(mockedRole, resultUser.getRole());

		User currentUser = userRepository.getCurrentUser();
		assertNotNull(currentUser);
		assertEquals(user.getDisplayName(), currentUser.getDisplayName());
		assertEquals(user.getEmail(), currentUser.getEmail());
		assertEquals(user.getFirstName(), currentUser.getFirstName());
		assertEquals(user.getLastName(), currentUser.getLastName());
		assertEquals(mockedRole, currentUser.getRole());
	}

	@Test
	public void fetchUserInfos_Fails_Forbidden()
	{
		when(mMicrosoftLoginHelper.acquireTokenSilentAsync(Mockito.any())).thenReturn(Observable.just("access_token"));
		when(mMicrosoftLoginHelper.isUserSignedIn()).thenReturn(true);

		MockResponse response = new MockResponse()
										.setResponseCode(HttpURLConnection.HTTP_FORBIDDEN);

		mockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(response));

		TestObserver<User> testObserver = TestObserver.create();
		Observable<User> result = userRepository.fetchUserInfos();

		result.subscribe(testObserver);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		testObserver.assertSubscribed();
		testObserver.assertFailureAndMessage(IOException.class, "An error occurred during user infos request");
		assertNull(userRepository.getCurrentUser());
	}

	@Test
	public void fetchUserInfos_Fails_Unauthorized()
	{
		when(mMicrosoftLoginHelper.acquireTokenSilentAsync(Mockito.any())).thenReturn(Observable.just("access_token"));
		when(mMicrosoftLoginHelper.isUserSignedIn()).thenReturn(true);

		MockResponse response = new MockResponse()
										.setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED);

		mockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(response));

		TestObserver<User> testObserver = TestObserver.create();
		Observable<User> result = userRepository.fetchUserInfos();

		result.subscribe(testObserver);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		testObserver.assertSubscribed();
		testObserver.assertFailureAndMessage(IOException.class, "User access unauthorized");
		assertNull(userRepository.getCurrentUser());
	}

	@Test
	public void fetchUserInfos_Fails_TimeOut()
	{
		when(mMicrosoftLoginHelper.acquireTokenSilentAsync(Mockito.any())).thenReturn(Observable.just("access_token"));
		when(mMicrosoftLoginHelper.isUserSignedIn()).thenReturn(true);

		MockResponse response = new MockResponse()
										.setSocketPolicy(SocketPolicy.DISCONNECT_AT_START);

		mockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(response));

		TestObserver<User> testObserver = TestObserver.create();
		Observable<User> result = userRepository.fetchUserInfos();

		result.subscribe(testObserver);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY * 3);

		testObserver.assertSubscribed();
		testObserver.assertFailureAndMessage(IOException.class, "A network problem occurred.\nPlease try again later.");
		assertNull(userRepository.getCurrentUser());
	}

	@Test
	public void fetchUserEvents_OK() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();

		String eventsStr = CommonTestUtils.openFile(context, "json/events_john.json");
		EventListDto eventsDtoFromJson = gson.fromJson(eventsStr, EventListDto.class);
		List<Event> events = EventMapper.mapDto(eventsDtoFromJson);

		Role mockedRole = Role.CONTRIBUTOR;
		when(mMicrosoftLoginHelper.getCurrentUserRole()).thenReturn(mockedRole);
		when(mMicrosoftLoginHelper.acquireTokenSilentAsync(any())).thenReturn(Observable.just("access_token"));

		MockResponse response = new MockResponse()
										.setResponseCode(HttpURLConnection.HTTP_OK)
										.setBody(eventsStr);

		mockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_EVENTS_URL, Collections.singletonList(response));

		TestObserver<List<Event>> testObserver = TestObserver.create();
		Observable<List<Event>> result = userRepository.fetchUserEvents();

		result.subscribe(testObserver);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		testObserver.assertSubscribed();
		testObserver.assertComplete();

		List<Event> results = testObserver.values().get(0);
		assertFalse(results.isEmpty());

		try
		{
			JSONAssert.assertEquals(
					gson.toJson(events),
					gson.toJson(results),
					JSONCompareMode.LENIENT
			);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void fetchUserEvents_Fails_Forbidden()
	{
		when(mMicrosoftLoginHelper.acquireTokenSilentAsync(any())).thenReturn(Observable.just("access_token"));

		MockResponse response = new MockResponse()
										.setResponseCode(HttpURLConnection.HTTP_FORBIDDEN);

		mockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_EVENTS_URL, Collections.singletonList(response));

		TestObserver<List<Event>> testObserver = TestObserver.create();
		Observable<List<Event>> result = userRepository.fetchUserEvents();

		result.subscribe(testObserver);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		testObserver.assertSubscribed();
		testObserver.assertFailureAndMessage(IOException.class, "An error occurred during user events request");
		assertNull(userRepository.getCurrentUser());
	}

	@Test
	public void fetchUserEvents_Fails_Unauthorized()
	{
		when(mMicrosoftLoginHelper.acquireTokenSilentAsync(any())).thenReturn(Observable.just("access_token"));

		MockResponse response = new MockResponse()
										.setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED);

		mockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_EVENTS_URL, Collections.singletonList(response));

		TestObserver<List<Event>> testObserver = TestObserver.create();
		Observable<List<Event>> result = userRepository.fetchUserEvents();

		result.subscribe(testObserver);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		testObserver.assertSubscribed();
		testObserver.assertFailureAndMessage(IOException.class, "User access unauthorized");
		assertNull(userRepository.getCurrentUser());
	}

	@Test
	public void fetchUserEvents_Fails_TimeOut()
	{
		when(mMicrosoftLoginHelper.acquireTokenSilentAsync(any())).thenReturn(Observable.just("access_token"));

		MockResponse response = new MockResponse()
										.setSocketPolicy(SocketPolicy.DISCONNECT_AT_START);

		mockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_EVENTS_URL, Collections.singletonList(response));

		TestObserver<List<Event>> testObserver = TestObserver.create();
		Observable<List<Event>> result = userRepository.fetchUserEvents();

		result.subscribe(testObserver);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY * 3);

		testObserver.assertSubscribed();
		testObserver.assertFailureAndMessage(IOException.class, "A network problem occurred.\nPlease try again later.");
		assertNull(userRepository.getCurrentUser());
	}
}