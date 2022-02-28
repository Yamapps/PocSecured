package com.amf.pocsecured.ui;

import android.app.Instrumentation;
import android.content.Context;

import com.amf.pocsecured.MainApplicationTest;
import com.amf.pocsecured.R;
import com.amf.pocsecured.di.AppComponentTest;
import com.amf.pocsecured.di.DaggerTestBuilder;
import com.amf.pocsecured.ext.MicrosoftLoginHelper;
import com.amf.pocsecured.mock.MockMicrosoftLoginHelper;
import com.amf.pocsecured.mock.MockedWebServerData;
import com.amf.pocsecured.model.Event;
import com.amf.pocsecured.model.Role;
import com.amf.pocsecured.model.User;
import com.amf.pocsecured.model.mapper.EventMapper;
import com.amf.pocsecured.model.mapper.UserMapper;
import com.amf.pocsecured.network.NetworkConstants;
import com.amf.pocsecured.network.dto.EventListDto;
import com.amf.pocsecured.network.dto.UserDto;
import com.amf.pocsecured.ui.home.HomeActivity;
import com.amf.pocsecured.utils.DateTimeUtils;
import com.amf.pocsecured.utils.GsonUtils;
import com.amf.pocsecured.utils.test.CommonTestUtils;
import com.amf.pocsecured.utils.test.MockServerDispatcher;
import com.google.gson.Gson;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.amf.pocsecured.utils.EspressoUtils.withIndex;
import static com.amf.pocsecured.utils.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class) //
public class EventsActivityTest extends BaseTest
{
	private IdlingResource mIdlingResource;

	private ActivityScenario<HomeActivity> mHomeScenario;

	protected Gson gson = GsonUtils.getGson();

	private MockMicrosoftLoginHelper mMockMicrosoftLoginHelper;
	private MockWebServer mMockServer;
	private MockServerDispatcher mMockServerDispatcher;

	@After
	public void cleanup() throws IOException
	{
		mHomeScenario.close();
		mMockServer.close();
	}

	private void injectDependencies(MicrosoftLoginHelper microsoftLoginHelper) throws IOException
	{
		/*
		 * Init MockWebServer
		 */
		mMockServerDispatcher = new MockServerDispatcher();
		mMockServer = new MockWebServer();
		mMockServer.start(8080);
		mMockServer.setDispatcher(mMockServerDispatcher);

		String url = mMockServer.url("/").toString();

		Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
		MainApplicationTest testApp = (MainApplicationTest) instrumentation.getTargetContext().getApplicationContext();

		AppComponentTest appComponentTest = DaggerTestBuilder.build(testApp, //
																	microsoftLoginHelper, //
																	new MockedWebServerData(url));

		appComponentTest.inject(testApp);
	}

	@Test
	public void events_ThreeEvents_OK() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();
		mMockMicrosoftLoginHelper = new MockMicrosoftLoginHelper(true, true);
		mMockMicrosoftLoginHelper.setRole(Role.CONTRIBUTOR);

		injectDependencies(mMockMicrosoftLoginHelper);

		String userStr = CommonTestUtils.openFile(context, "json/me_john.json");
		UserDto userDtoFromJson = gson.fromJson(userStr, UserDto.class);
		User user = UserMapper.mapDto(userDtoFromJson);
		MockResponse userResponse = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(userStr);
		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(userResponse));

		String eventsStr = CommonTestUtils.openFile(context, "json/events_john.json");
		EventListDto eventsDtoFromJson = gson.fromJson(eventsStr, EventListDto.class);
		List<Event> events = EventMapper.mapDto(eventsDtoFromJson);
		MockResponse eventsResponse = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(eventsStr);

		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_EVENTS_URL, Collections.singletonList(eventsResponse));

		mHomeScenario = ActivityScenario.launch(HomeActivity.class);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on first occurence of user name -> displayed on home screen
		 */
		onView(withIndex(withText(user.getNameForScreen()), 0)) //
				.check(matches(isDisplayed()));

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		onView(withId(R.id.nav_event)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withId(R.id.calendar_recycler)) //
				.check(matches(isDisplayed())) //
				.check(withItemCount(events.size()));

		for (Event event : events)
		{
			//Check subject displayed
			onView(withText(event.getSubject())) //
					.check(matches(isDisplayed()));

			//Check organizer displayed
			onView(withText(event.getOrganizerName())) //
					.check(matches(isDisplayed()));

			//Check start date displayed
			String startDate = context.getString(R.string.events_start, DateTimeUtils.toString(event.getStart(), DateTimeUtils.DATE_PATTERN_WITH_HOUR));
			onView(withText(startDate)) //
					.check(matches(isDisplayed()));

			//Check end date displayed
			String endDate = context.getString(R.string.events_end, DateTimeUtils.toString(event.getEnd(), DateTimeUtils.DATE_PATTERN_WITH_HOUR));
			onView(withText(endDate)) //
					.check(matches(isDisplayed()));
		}
	}

	@Test
	public void events_NoEvents_OK() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();
		mMockMicrosoftLoginHelper = new MockMicrosoftLoginHelper(true, true);
		mMockMicrosoftLoginHelper.setRole(Role.CONTRIBUTOR);

		injectDependencies(mMockMicrosoftLoginHelper);

		String userStr = CommonTestUtils.openFile(context, "json/me_john.json");
		UserDto userDtoFromJson = gson.fromJson(userStr, UserDto.class);
		User user = UserMapper.mapDto(userDtoFromJson);
		MockResponse userResponse = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(userStr);
		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(userResponse));

		String eventsStr = CommonTestUtils.openFile(context, "json/events_empty.json");
		MockResponse eventsResponse = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(eventsStr);

		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_EVENTS_URL, Collections.singletonList(eventsResponse));

		mHomeScenario = ActivityScenario.launch(HomeActivity.class);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on first occurence of user name -> displayed on home screen
		 */
		onView(withIndex(withText(user.getNameForScreen()), 0)) //
				.check(matches(isDisplayed()));

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		onView(withId(R.id.nav_event)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withId(R.id.calendar_recycler)) //
				.check(matches(not(isDisplayed())));

		onView(withText(R.string.no_events)) //
				.check(matches(isDisplayed()));
	}

	@Test
	public void events_Fails_Unauthorized() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();
		mMockMicrosoftLoginHelper = new MockMicrosoftLoginHelper(true, true);
		mMockMicrosoftLoginHelper.setRole(Role.CONTRIBUTOR);

		injectDependencies(mMockMicrosoftLoginHelper);

		String userStr = CommonTestUtils.openFile(context, "json/me_john.json");
		UserDto userDtoFromJson = gson.fromJson(userStr, UserDto.class);
		User user = UserMapper.mapDto(userDtoFromJson);
		MockResponse userResponse = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(userStr);
		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(userResponse));

		MockResponse eventsResponse = new MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED);

		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_EVENTS_URL, Collections.singletonList(eventsResponse));

		mHomeScenario = ActivityScenario.launch(HomeActivity.class);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on first occurence of user name -> displayed on home screen
		 */
		onView(withIndex(withText(user.getNameForScreen()), 0)) //
				.check(matches(isDisplayed()));

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		onView(withId(R.id.nav_event)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withId(R.id.calendar_recycler)) //
				.check(matches(not(isDisplayed())));

		onView(withText("User access unauthorized")) //
				.check(matches(isDisplayed()));

	}

	@Test
	public void events_Fails_OtherException() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();
		mMockMicrosoftLoginHelper = new MockMicrosoftLoginHelper(true, true);
		mMockMicrosoftLoginHelper.setRole(Role.CONTRIBUTOR);

		injectDependencies(mMockMicrosoftLoginHelper);

		String userStr = CommonTestUtils.openFile(context, "json/me_john.json");
		UserDto userDtoFromJson = gson.fromJson(userStr, UserDto.class);
		User user = UserMapper.mapDto(userDtoFromJson);
		MockResponse userResponse = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(userStr);
		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(userResponse));

		MockResponse eventsResponse = new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);

		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_EVENTS_URL, Collections.singletonList(eventsResponse));

		mHomeScenario = ActivityScenario.launch(HomeActivity.class);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on first occurence of user name -> displayed on home screen
		 */
		onView(withIndex(withText(user.getNameForScreen()), 0)) //
				.check(matches(isDisplayed()));

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		onView(withId(R.id.nav_event)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withId(R.id.calendar_recycler)) //
				.check(matches(not(isDisplayed())));

		onView(withText("An error occurred during user events request")) //
				.check(matches(isDisplayed()));

	}

	@Test
	public void events_Fails_TimeOut() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();
		mMockMicrosoftLoginHelper = new MockMicrosoftLoginHelper(true, true);
		mMockMicrosoftLoginHelper.setRole(Role.CONTRIBUTOR);

		injectDependencies(mMockMicrosoftLoginHelper);

		String userStr = CommonTestUtils.openFile(context, "json/me_john.json");
		UserDto userDtoFromJson = gson.fromJson(userStr, UserDto.class);
		User user = UserMapper.mapDto(userDtoFromJson);
		MockResponse userResponse = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(userStr);
		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(userResponse));

		MockResponse eventsResponse = new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START);

		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_EVENTS_URL, Collections.singletonList(eventsResponse));

		mHomeScenario = ActivityScenario.launch(HomeActivity.class);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on first occurence of user name -> displayed on home screen
		 */
		onView(withIndex(withText(user.getNameForScreen()), 0)) //
				.check(matches(isDisplayed()));

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		onView(withId(R.id.nav_event)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY * 2);

		onView(withId(R.id.calendar_recycler)) //
				.check(matches(not(isDisplayed())));

		onView(withText("A network problem occurred.\nPlease try again later.")) //
				.check(matches(isDisplayed()));
	}
}