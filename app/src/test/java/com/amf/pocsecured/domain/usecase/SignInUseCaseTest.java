package com.amf.pocsecured.domain.usecase;

import android.app.Activity;

import com.amf.pocsecured.NetworkSecurityPolicyWorkaround;
import com.amf.pocsecured.data.IUserRepository;
import com.amf.pocsecured.model.Role;
import com.amf.pocsecured.model.User;
import com.amf.pocsecured.ui.home.HomeActivity;
import com.amf.pocsecured.utils.test.CommonTestUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) //
@Config(shadows = {NetworkSecurityPolicyWorkaround.class}) //
public class SignInUseCaseTest
{
	@Mock
	IUserRepository userRepository;

	SignInUseCase mSignInUseCase;

	public User mUser = new User("displayName", "firstName", "lastName", "email", Role.CONTRIBUTOR);

	@Before
	public void beforeTest()
	{
		MockitoAnnotations.initMocks(this);

		mSignInUseCase = new SignInUseCase(userRepository);
	}

	@Test
	public void signIn_OK()
	{
		HomeActivity homeActivity = Robolectric.buildActivity(HomeActivity.class).create().get();

		when(userRepository.signIn(Mockito.any(Activity.class))).thenReturn(Observable.just("accessToken"));
		when(userRepository.fetchUserInfos()).thenReturn(Observable.just(mUser));

		TestObserver<User> testObserver = TestObserver.create();

		mSignInUseCase.testExecute(testObserver, new SignInUseCase.Params(homeActivity));

		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(50);

		testObserver.assertComplete();
		testObserver.assertResult(mUser);
	}

	@Test
	public void signIn_NotSignedIn()
	{
		HomeActivity homeActivity = Robolectric.buildActivity(HomeActivity.class).create().get();

		when(userRepository.signIn(Mockito.any(Activity.class))).thenReturn(Observable.error(new Exception("")));
		when(userRepository.fetchUserInfos()).thenReturn(Observable.just(mUser));

		TestObserver<User> testObserver = TestObserver.create();

		mSignInUseCase.testExecute(testObserver, new SignInUseCase.Params(homeActivity));

		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(50);

		Assert.assertEquals(0, testObserver.valueCount());
	}

	@Test
	public void signIn_FetchUserFails()
	{
		HomeActivity homeActivity = Robolectric.buildActivity(HomeActivity.class).create().get();

		when(userRepository.signIn(Mockito.any(Activity.class))).thenReturn(Observable.just("accessToken"));
		IOException exception = new IOException("test error");
		when(userRepository.fetchUserInfos()).thenReturn(Observable.error(exception));

		TestObserver<User> testObserver = TestObserver.create();

		mSignInUseCase.testExecute(testObserver, new SignInUseCase.Params(homeActivity));

		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(50);

		testObserver.assertFailureAndMessage(IOException.class, exception.getMessage());
	}
}