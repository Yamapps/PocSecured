package com.amf.pocsecured.domain.usecase;

import android.content.Context;

import com.amf.pocsecured.NetworkSecurityPolicyWorkaround;
import com.amf.pocsecured.data.IUserRepository;
import com.amf.pocsecured.model.Event;
import com.amf.pocsecured.model.mapper.EventMapper;
import com.amf.pocsecured.network.dto.EventListDto;
import com.amf.pocsecured.utils.GsonUtils;
import com.amf.pocsecured.utils.test.CommonTestUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) //
@Config(shadows = {NetworkSecurityPolicyWorkaround.class}) //
public class FetchUserEventsUseCaseTest
{
	@Mock
	IUserRepository userRepository;

	Gson gson = GsonUtils.getGson();

	FetchUserEventsUseCase mFetchUserEventsUseCase;

	@Before
	public void beforeTest()
	{
		//noinspection deprecation
		MockitoAnnotations.initMocks(this);

		mFetchUserEventsUseCase = new FetchUserEventsUseCase(userRepository);
	}

	@Test
	public void fetchUserEvents_OK() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();

		String eventsStr = CommonTestUtils.openFile(context, "json/events_john.json");
		EventListDto eventsDtoFromJson = gson.fromJson(eventsStr, EventListDto.class);
		List<Event> events = EventMapper.mapDto(eventsDtoFromJson);

		when(userRepository.fetchUserEvents()).thenReturn(Observable.just(events));

		TestObserver<List<Event>> testObserver = TestObserver.create();

		mFetchUserEventsUseCase.testExecute(testObserver, null);

		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(50);

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
	public void fetchUserEvents_Fails()
	{
		IOException exception = new IOException("test error");
		when(userRepository.fetchUserEvents()).thenReturn(Observable.error(exception));

		TestObserver<List<Event>> testObserver = TestObserver.create();

		mFetchUserEventsUseCase.testExecute(testObserver, null);

		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(50);

		//noinspection unchecked
		testObserver.assertFailureAndMessage(IOException.class, exception.getMessage());
	}
}