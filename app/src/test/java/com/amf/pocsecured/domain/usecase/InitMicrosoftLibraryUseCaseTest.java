package com.amf.pocsecured.domain.usecase;

import android.content.Context;

import com.amf.pocsecured.NetworkSecurityPolicyWorkaround;
import com.amf.pocsecured.data.IUserRepository;
import com.amf.pocsecured.model.Role;
import com.amf.pocsecured.model.User;
import com.amf.pocsecured.utils.test.CommonTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) //
@Config(shadows = {NetworkSecurityPolicyWorkaround.class}) //
public class InitMicrosoftLibraryUseCaseTest
{
	@Mock
	IUserRepository userRepository;

	InitMicrosoftLoginUseCase mMicrosoftLoginUseCase;

	public User mUser = new User("displayName", "firstName", "lastName", "email", Role.CONTRIBUTOR);

	@Before
	public void beforeTest()
	{
		MockitoAnnotations.initMocks(this);

		mMicrosoftLoginUseCase = new InitMicrosoftLoginUseCase(userRepository);
	}

	@Test
	public void init_OK()
	{
		Context context = ApplicationProvider.getApplicationContext();

		when(userRepository.init(Mockito.any(Context.class))).thenReturn(Observable.just(true));
		when(userRepository.isUserSignedIn()).thenReturn(true);
		when(userRepository.fetchUserInfos()).thenReturn(Observable.just(mUser));

		TestObserver<User> testObserver = TestObserver.create();

		mMicrosoftLoginUseCase.testExecute(testObserver, new InitMicrosoftLoginUseCase.Params(context));

		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(50);

		testObserver.assertComplete();
		testObserver.assertResult(mUser);
	}

	@Test
	public void init_Fails_SignInError()
	{
		Context context = ApplicationProvider.getApplicationContext();

		IOException exception = new IOException("test error");
		when(userRepository.init(Mockito.any(Context.class))).thenReturn(Observable.error(exception));

		TestObserver<User> testObserver = TestObserver.create();

		mMicrosoftLoginUseCase.testExecute(testObserver, new InitMicrosoftLoginUseCase.Params(context));

		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(50);

		testObserver.assertFailureAndMessage(IOException.class, exception.getMessage());
	}

	@Test
	public void init_Fails_NotSignedIn()
	{
		Context context = ApplicationProvider.getApplicationContext();

		when(userRepository.init(Mockito.any(Context.class))).thenReturn(Observable.just(false));
		when(userRepository.fetchUserInfos()).thenReturn(Observable.just(mUser));

		TestObserver<User> testObserver = TestObserver.create();

		mMicrosoftLoginUseCase.testExecute(testObserver, new InitMicrosoftLoginUseCase.Params(context));

		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(2000);

		assertEquals(0, testObserver.valueCount());
	}

	@Test
	public void init_Fails_FetchUserError()
	{
		Context context = ApplicationProvider.getApplicationContext();

		when(userRepository.init(Mockito.any(Context.class))).thenReturn(Observable.just(true));
		when(userRepository.isUserSignedIn()).thenReturn(true);
		IOException exception = new IOException("test error");
		when(userRepository.fetchUserInfos()).thenReturn(Observable.error(exception));

		TestObserver<User> testObserver = TestObserver.create();

		mMicrosoftLoginUseCase.testExecute(testObserver, new InitMicrosoftLoginUseCase.Params(context));

		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(50);

		testObserver.assertFailureAndMessage(IOException.class, exception.getMessage());
	}
}