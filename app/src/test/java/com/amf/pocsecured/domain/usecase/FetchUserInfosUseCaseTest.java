package com.amf.pocsecured.domain.usecase;

import android.content.Context;

import com.amf.pocsecured.NetworkSecurityPolicyWorkaround;
import com.amf.pocsecured.data.IUserRepository;
import com.amf.pocsecured.model.User;
import com.amf.pocsecured.model.mapper.UserMapper;
import com.amf.pocsecured.network.dto.UserDto;
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

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) //
@Config(shadows = {NetworkSecurityPolicyWorkaround.class}) //
public class FetchUserInfosUseCaseTest
{
	@Mock
	IUserRepository userRepository;

	Gson gson = GsonUtils.getGson();

	FetchUserInfosUseCase mFetchUserInfosUseCase;

	@Before
	public void beforeTest()
	{
		//noinspection deprecation
		MockitoAnnotations.initMocks(this);

		mFetchUserInfosUseCase = new FetchUserInfosUseCase(userRepository);
	}

	@Test
	public void fetchUserInfos_OK() throws IOException
	{
		Context context = ApplicationProvider.getApplicationContext();

		String userStr = CommonTestUtils.openFile(context, "json/me_john.json");
		UserDto userDtoFromJson = gson.fromJson(userStr, UserDto.class);
		User user = UserMapper.mapDto(userDtoFromJson);

		when(userRepository.fetchUserInfos()).thenReturn(Observable.just(user));

		TestObserver<User> testObserver = TestObserver.create();

		mFetchUserInfosUseCase.testExecute(testObserver, null);

		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(50);

		testObserver.assertComplete();
		testObserver.assertComplete();
		User result = testObserver.values().get(0);
		assertNotNull(result);

		try
		{
			JSONAssert.assertEquals(
					gson.toJson(user),
					gson.toJson(result),
					JSONCompareMode.LENIENT
			);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void fetchUserInfos_Fails()
	{
		IOException exception = new IOException("test error");
		when(userRepository.fetchUserInfos()).thenReturn(Observable.error(exception));

		TestObserver<User> testObserver = TestObserver.create();

		mFetchUserInfosUseCase.testExecute(testObserver, null);

		testObserver.assertSubscribed();

		/*
		 * Wait for observable completion
		 */
		CommonTestUtils.wait(50);

		testObserver.assertFailureAndMessage(IOException.class, exception.getMessage());
	}
}