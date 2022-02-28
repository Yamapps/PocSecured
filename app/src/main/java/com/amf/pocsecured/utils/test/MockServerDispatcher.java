package com.amf.pocsecured.utils.test;

import com.amf.pocsecured.network.NetworkConstants;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;

/**
 *
 * Implementation of {@link Dispatcher}
 * <p>
 * Used in test to mock http webservices calls
 * @author youssefamrani
 */
public class MockServerDispatcher extends Dispatcher
{
	private final HashMap<String, List<MockResponse>> mockedResponseList = new HashMap<>();

	@Override
	public MockResponse dispatch(RecordedRequest request) throws InterruptedException
	{
		if (!mockedResponseList.isEmpty())
		{
			try
			{
				if (request.getRequestLine().contains(NetworkConstants.REQUEST_EVENTS_URL))
				{
					return getResponse(NetworkConstants.REQUEST_EVENTS_URL);
				}
				if (request.getRequestLine().contains(NetworkConstants.REQUEST_ME_URL))
				{
					return getResponse(NetworkConstants.REQUEST_ME_URL);
				}
				if (request.getRequestLine().contains(NetworkConstants.REQUEST_PUBLIC_HOLIDAYS_URL))
				{
					return getResponse(NetworkConstants.REQUEST_PUBLIC_HOLIDAYS_URL);
				}
			}
			catch (Exception exception)
			{
				throw new InterruptedException();
			}
		}

		return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
	}

	private MockResponse getResponse(String key) throws Exception
	{
		List<MockResponse> responseList = mockedResponseList.get(key);
		assert responseList != null;
		MockResponse response = responseList.get(0);
		List<MockResponse> updated = responseList.stream().filter(mockResponse->mockResponse != response) //
										  .collect(Collectors.toList());

		if (response.getSocketPolicy() == SocketPolicy.DISCONNECT_AT_START)
		{
			throw new SocketTimeoutException();
		}
		mockedResponseList.put(key, updated);
		return response;
	}

	/**
	 * Add a mocked response to webserver
	 * @param key mocked url
	 * @param value responses to specified mocked url
	 */
	public void addMockedResponseList(String key, List<MockResponse> value)
	{
		mockedResponseList.put(key, value);
	}
}
