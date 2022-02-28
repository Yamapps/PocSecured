package com.amf.pocsecured.data;

import android.content.Context;

import com.amf.pocsecured.NetworkSecurityPolicyWorkaround;
import com.amf.pocsecured.ext.MicrosoftLoginHelper;
import com.amf.pocsecured.model.PublicHoliday;
import com.amf.pocsecured.model.mapper.PublicHolidayMapper;
import com.amf.pocsecured.network.NetworkConstants;
import com.amf.pocsecured.network.dto.PublicHolidayDto;
import com.amf.pocsecured.utils.test.CommonTestUtils;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import androidx.test.core.app.ApplicationProvider;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.SocketPolicy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class) //
@Config(shadows = {NetworkSecurityPolicyWorkaround.class}) //
public class CalendarRepositoryTest extends BaseRepositoryTest
{
	ICalendarRepository mCalendarRepository;

	@Mock
	MicrosoftLoginHelper mMicrosoftLoginHelper;

	@Override
	public void beforeTest()
	{
		mCalendarRepository = new CalendarRepositoryImpl(mDtdPlannerRetrofitApi, mMicrosoftLoginHelper);
	}

	@Test
	public void injectNotNull()
	{
		assertNotNull(mCalendarRepository);
	}

	@Test
	public void fetchPublicHolidays_OK() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();

		when(mMicrosoftLoginHelper.acquireTokenSilentAsync(any())).thenReturn(Observable.just("access_token"));

		String holidaysStr = CommonTestUtils.openFile(context, "json/calendar_file.json");
		Type publicHolidayListType = new TypeToken<ArrayList<PublicHolidayDto>>()
		{
		}.getType();
		List<PublicHolidayDto> holidaysDtoFromJson = gson.fromJson(holidaysStr, publicHolidayListType);
		List<PublicHoliday> holidayList = holidaysDtoFromJson.stream() //
												  .map(PublicHolidayMapper::mapDto) //
												  .collect(Collectors.toList());

		MockResponse response = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(holidaysStr);

		mockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_PUBLIC_HOLIDAYS_URL, Collections.singletonList(response));

		TestObserver<List<PublicHoliday>> testObserver = TestObserver.create();
		Observable<List<PublicHoliday>> result = mCalendarRepository.fetchPublicHolidays(context);

		result.subscribe(testObserver);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		testObserver.assertSubscribed();
		testObserver.assertComplete();

		List<List<PublicHoliday>> results = testObserver.values();
		assertFalse(results.isEmpty());

		List<PublicHoliday> resultUser = results.get(0);
		assertEquals(holidayList.size(), resultUser.size());
	}

	@Test
	public void fetchPublicHolidays_Fails_Forbidden() throws IOException
	{
		when(mMicrosoftLoginHelper.acquireTokenSilentAsync(any())).thenReturn(Observable.just("access_token"));

		MockResponse response = new MockResponse().setResponseCode(HttpURLConnection.HTTP_FORBIDDEN);

		mockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_PUBLIC_HOLIDAYS_URL, Collections.singletonList(response));

		Context context = ApplicationProvider.getApplicationContext();

		TestObserver<List<PublicHoliday>> testObserver = TestObserver.create();
		Observable<List<PublicHoliday>> result = mCalendarRepository.fetchPublicHolidays(context);

		result.subscribe(testObserver);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		testObserver.assertFailureAndMessage(IOException.class, "An error occurred during public holidays request");
	}

	@Test
	public void fetchPublicHolidays_Fails_Unauthorized()
	{
		when(mMicrosoftLoginHelper.acquireTokenSilentAsync(any())).thenReturn(Observable.just("access_token"));

		MockResponse response = new MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED);

		mockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_PUBLIC_HOLIDAYS_URL, Collections.singletonList(response));

		Context context = ApplicationProvider.getApplicationContext();

		TestObserver<List<PublicHoliday>> testObserver = TestObserver.create();
		Observable<List<PublicHoliday>> result = mCalendarRepository.fetchPublicHolidays(context);

		result.subscribe(testObserver);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		testObserver.assertSubscribed();
		testObserver.assertFailureAndMessage(IOException.class, "User access unauthorized");
	}

	@Test
	public void fetchPublicHolidays_Fails_TimeOut()
	{
		when(mMicrosoftLoginHelper.acquireTokenSilentAsync(any())).thenReturn(Observable.just("access_token"));

		MockResponse response = new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START);

		mockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_PUBLIC_HOLIDAYS_URL, Collections.singletonList(response));

		Context context = ApplicationProvider.getApplicationContext();

		TestObserver<List<PublicHoliday>> testObserver = TestObserver.create();
		Observable<List<PublicHoliday>> result = mCalendarRepository.fetchPublicHolidays(context);

		result.subscribe(testObserver);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY * 3);

		testObserver.assertSubscribed();
		testObserver.assertFailureAndMessage(IOException.class, "A network problem occurred.\nPlease try again later.");
	}
}