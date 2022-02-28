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
import com.amf.pocsecured.model.Role;
import com.amf.pocsecured.model.User;
import com.amf.pocsecured.model.mapper.UserMapper;
import com.amf.pocsecured.network.NetworkConstants;
import com.amf.pocsecured.network.dto.UserDto;
import com.amf.pocsecured.ui.home.HomeActivity;
import com.amf.pocsecured.utils.EspressoUtils;
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

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.amf.pocsecured.utils.EspressoUtils.checkSnackbar;
import static com.amf.pocsecured.utils.EspressoUtils.withIndex;
import static org.hamcrest.CoreMatchers.not;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class) //
public class HomeActivityTest extends BaseTest
{
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
	public void home_SignIn_Error() throws IOException
	{
		String errorMessage = "Init exception message";
		mMockMicrosoftLoginHelper = new MockMicrosoftLoginHelper(false, false, new Exception(errorMessage));
		injectDependencies(mMockMicrosoftLoginHelper);

		mHomeScenario = ActivityScenario.launch(HomeActivity.class);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		EspressoUtils.checkSnackbar(errorMessage);

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on first occurence of 'R.string.please_sign_in' -> displayed on home screen
		 */
		onView(withIndex(withText(R.string.please_sign_in), 0)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on second occurence of 'R.string.please_sign_in' -> written on navigation drawer (but not displayed)
		 */
		onView(withIndex(withText(R.string.please_sign_in), 1)) //
				.check(matches(not(isDisplayed()))); // -> hidden because drawer is closed

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())).perform(click());

		onView(withIndex(withText(R.string.please_sign_in), 1)) //
				.check(matches(isDisplayed())); // -> displayed because drawer is open

		onView(withId(R.id.nav_sign_in)) //
				.check(matches(isDisplayed()));
		onView(withId(R.id.nav_sign_out)) //
				.check(doesNotExist());
	}

	@Test
	public void home_SignIn_NoUser_OK() throws IOException
	{
		mMockMicrosoftLoginHelper = new MockMicrosoftLoginHelper(true, false);
		injectDependencies(mMockMicrosoftLoginHelper);

		mHomeScenario = ActivityScenario.launch(HomeActivity.class);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on first occurence of 'R.string.please_sign_in' -> displayed on home screen
		 */
		onView(withIndex(withText(R.string.please_sign_in), 0)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on second occurence of 'R.string.please_sign_in' -> written on navigation drawer (but not displayed)
		 */
		onView(withIndex(withText(R.string.please_sign_in), 1)) //
				.check(matches(not(isDisplayed()))); // -> hidden because drawer is closed

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())).perform(click());

		onView(withIndex(withText(R.string.please_sign_in), 1)) //
				.check(matches(isDisplayed())); // -> displayed because drawer is open

		onView(withId(R.id.nav_sign_in)) //
				.check(matches(isDisplayed()));
		onView(withId(R.id.nav_sign_out)) //
				.check(doesNotExist());
	}

	@Test
	public void home_SignedIn_WithUser_RoleReader_OK() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();
		mMockMicrosoftLoginHelper = new MockMicrosoftLoginHelper(true, true);
		mMockMicrosoftLoginHelper.setRole(Role.CALENDAR_READER);

		injectDependencies(mMockMicrosoftLoginHelper);

		String userStr = CommonTestUtils.openFile(context, "json/me_john.json");
		UserDto userDtoFromJson = gson.fromJson(userStr, UserDto.class);
		User user = UserMapper.mapDto(userDtoFromJson);
		MockResponse response = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(userStr);
		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(response));

		mHomeScenario = ActivityScenario.launch(HomeActivity.class);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on first occurence of user name -> displayed on home screen
		 */
		onView(withIndex(withText(user.getNameForScreen()), 0)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on second occurence of user name -> written on navigation drawer (but not displayed)
		 */
		onView(withIndex(withText(user.getNameForScreen()), 1)) //
				.check(matches(not(isDisplayed()))); // -> hidden because drawer is closed

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())).perform(click());

		onView(withIndex(withText(user.getNameForScreen()), 1)) //
				.check(matches(isDisplayed())); // -> displayed because drawer is open

		onView(withText(user.getEmail())) //
				.check(matches(isDisplayed()));

		onView(withId(R.id.nav_sign_out)) //
				.check(matches(isDisplayed()));
		onView(withId(R.id.nav_calendar)) //
				.check(doesNotExist());
		onView(withId(R.id.nav_event)) //
				.check(matches(isDisplayed()));
		onView(withId(R.id.nav_profile)) //
				.check(matches(isDisplayed()));
		onView(withId(R.id.nav_sign_in)) //
				.check(doesNotExist());
	}

	@Test
	public void home_SignedIn_WithUser_RoleContributor_OK() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();
		mMockMicrosoftLoginHelper = new MockMicrosoftLoginHelper(true, true);
		mMockMicrosoftLoginHelper.setRole(Role.CONTRIBUTOR);

		injectDependencies(mMockMicrosoftLoginHelper);

		String userStr = CommonTestUtils.openFile(context, "json/me_john.json");
		UserDto userDtoFromJson = gson.fromJson(userStr, UserDto.class);
		User user = UserMapper.mapDto(userDtoFromJson);
		MockResponse response = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(userStr);
		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(response));

		mHomeScenario = ActivityScenario.launch(HomeActivity.class);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on first occurence of user name -> displayed on home screen
		 */
		onView(withIndex(withText(user.getNameForScreen()), 0)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on second occurence of user name -> written on navigation drawer (but not displayed)
		 */
		onView(withIndex(withText(user.getNameForScreen()), 1)) //
				.check(matches(not(isDisplayed()))); // -> hidden because drawer is closed

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())).perform(click());

		onView(withIndex(withText(user.getNameForScreen()), 1)) //
				.check(matches(isDisplayed())); // -> displayed because drawer is open

		onView(withText(user.getEmail())) //
				.check(matches(isDisplayed()));

		onView(withId(R.id.nav_sign_out)) //
				.check(matches(isDisplayed()));
		onView(withId(R.id.nav_calendar)) //
				.check(matches(isDisplayed()));
		onView(withId(R.id.nav_event)) //
				.check(matches(isDisplayed()));
		onView(withId(R.id.nav_profile)) //
				.check(matches(isDisplayed()));
		onView(withId(R.id.nav_sign_in)) //
				.check(doesNotExist());
	}

	@Test
	public void home_SignOut_OK() throws IOException
	{

		Context context = ApplicationProvider.getApplicationContext();
		mMockMicrosoftLoginHelper = new MockMicrosoftLoginHelper(true, true, true);

		injectDependencies(mMockMicrosoftLoginHelper);

		String userStr = CommonTestUtils.openFile(context, "json/me_john.json");
		UserDto userDtoFromJson = gson.fromJson(userStr, UserDto.class);
		User user = UserMapper.mapDto(userDtoFromJson);
		MockResponse response = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(userStr);
		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(response));

		mHomeScenario = ActivityScenario.launch(HomeActivity.class);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		//Check user signedIn
		onView(withIndex(withText(user.getNameForScreen()), 1)) //
				.check(matches(not(isDisplayed())));

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())).perform(click());

		onView(withId(R.id.nav_sign_out)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on first occurence of 'R.string.please_sign_in' -> displayed on home screen
		 */
		onView(withIndex(withText(R.string.please_sign_in), 0)) //
				.check(matches(isDisplayed()));
	}

	@Test
	public void home_SignOut_FromEventScreen_OK() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();
		mMockMicrosoftLoginHelper = new MockMicrosoftLoginHelper(true, true, true);
		mMockMicrosoftLoginHelper.setRole(Role.CONTRIBUTOR);

		injectDependencies(mMockMicrosoftLoginHelper);

		String userStr = CommonTestUtils.openFile(context, "json/me_john.json");
		UserDto userDtoFromJson = gson.fromJson(userStr, UserDto.class);
		User user = UserMapper.mapDto(userDtoFromJson);
		MockResponse response = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(userStr);
		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(response));

		String eventsStr = CommonTestUtils.openFile(context, "json/events_john.json");
		MockResponse eventsResponse = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(eventsStr);
		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_EVENTS_URL, Collections.singletonList(eventsResponse));

		mHomeScenario = ActivityScenario.launch(HomeActivity.class);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		//Check user signedIn
		onView(withIndex(withText(user.getNameForScreen()), 1)) //
				.check(matches(not(isDisplayed())));

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())).perform(click());

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withId(R.id.nav_event)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		onView(withIndex(withText(R.string.events_title), 0)) //
				.check(matches(isDisplayed()));

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())).perform(click());

		onView(withId(R.id.nav_sign_out)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		onView(withText(R.string.welcome)) //
				.check(matches(isDisplayed()));

		/*
		 * Check on first occurence of 'R.string.please_sign_in' -> displayed on home screen
		 */
		onView(withIndex(withText(R.string.please_sign_in), 0)) //
				.check(matches(isDisplayed()));
	}

	@Test
	public void home_SignOut_Error() throws IOException
	{

		Context context = ApplicationProvider.getApplicationContext();
		mMockMicrosoftLoginHelper = new MockMicrosoftLoginHelper(true, true, false);

		injectDependencies(mMockMicrosoftLoginHelper);

		String userStr = CommonTestUtils.openFile(context, "json/me_john.json");
		UserDto userDtoFromJson = gson.fromJson(userStr, UserDto.class);
		User user = UserMapper.mapDto(userDtoFromJson);
		MockResponse response = new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(userStr);
		mMockServerDispatcher.addMockedResponseList(NetworkConstants.REQUEST_ME_URL, Collections.singletonList(response));

		mHomeScenario = ActivityScenario.launch(HomeActivity.class);

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		//Check user signedIn
		onView(withIndex(withText(user.getNameForScreen()), 1)) //
				.check(matches(not(isDisplayed())));

		onView(withId(R.id.toolbar_drawer_icon)) //
				.check(matches(isDisplayed())).perform(click());

		onView(withId(R.id.nav_sign_out)) //
				.check(matches(isDisplayed())) //
				.perform(click());

		waitForIdle(WAIT_BETWEEN_ACTIONS_DELAY);

		checkSnackbar(MockMicrosoftLoginHelper.SIGN_OUT_ERROR_MESSAGE);

		onView(withIndex(withText(user.getNameForScreen()), 0)) //
				.check(matches(isDisplayed()));
	}
}