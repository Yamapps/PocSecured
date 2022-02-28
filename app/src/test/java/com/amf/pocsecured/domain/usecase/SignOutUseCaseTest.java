package com.amf.pocsecured.domain.usecase;

import com.amf.pocsecured.NetworkSecurityPolicyWorkaround;
import com.amf.pocsecured.data.IUserRepository;
import com.amf.pocsecured.utils.test.CommonTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) //
@Config(shadows = {NetworkSecurityPolicyWorkaround.class}) //
public class SignOutUseCaseTest
{
	@Mock
	IUserRepository userRepository;

	SignOutUseCase mSignOutUseCase;
	@Before
	public void beforeTest()
	{
		MockitoAnnotations.initMocks(this);

		mSignOutUseCase = new SignOutUseCase(userRepository);
	}

	@Test
	public void signOut_OK()
	{
		when(userRepository.signOut()).thenReturn(Completable.complete());

		TestObserver<Void> testObserver = TestObserver.create();
		mSignOutUseCase.testExecute(testObserver, null);
		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(50);

		testObserver.assertComplete();
	}

	@Test
	public void signOut_Fails()
	{
		Exception exception = new IOException("test error");
		when(userRepository.signOut()).thenReturn(Completable.error(exception));

		TestObserver<Void> testObserver = TestObserver.create();
		mSignOutUseCase.testExecute(testObserver, null);
		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(50);

		testObserver.assertFailureAndMessage(IOException.class, exception.getMessage());
	}
}