package com.amf.pocsecured.data;

import android.text.format.DateUtils;

import com.amf.pocsecured.BuildConfig;
import com.amf.pocsecured.network.DtdPlannerRetrofitApi;
import com.amf.pocsecured.network.RetrofitApi;
import com.amf.pocsecured.utils.GsonUtils;
import com.amf.pocsecured.utils.test.MockServerDispatcher;
import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author youssefamrani
 */

@SuppressWarnings("unused") abstract class BaseRepositoryTest
{
	//Connect timeout delay in seconds
	static long CONNECT_TIMEOUT = 2;

	//Read timeout delay in seconds
	static int READ_TIMEOUT = 2;

	public abstract void beforeTest();

	RetrofitApi mApi;
	DtdPlannerRetrofitApi mDtdPlannerRetrofitApi;
	protected Gson gson = GsonUtils.getGson();

	protected MockWebServer server;
	protected MockServerDispatcher mockServerDispatcher;

	@Before
	public void initTest() throws IOException
	{
		MockitoAnnotations.initMocks(this);

		/*
		 * Init MockWebServer
		 */
		mockServerDispatcher = new MockServerDispatcher();
		server = new MockWebServer();
		server.start(8080);
		server.setDispatcher(mockServerDispatcher);

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);

		OkHttpClient.Builder builder = new OkHttpClient.Builder() //
											   .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //
											   .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
		builder.addInterceptor(logging);
		OkHttpClient okHttpClient = builder.build();
		Retrofit retrofit = new Retrofit.Builder()
							   .client(okHttpClient)
							   .baseUrl(server.url("/"))
							   .addConverterFactory(JacksonConverterFactory.create())
							   .build();

		mApi = retrofit.create(RetrofitApi.class);

		Retrofit dtdRetrofit = new Retrofit.Builder()
									.client(okHttpClient)
									.baseUrl(server.url("/"))
									.addConverterFactory(JacksonConverterFactory.create())
									.build();

		mDtdPlannerRetrofitApi = dtdRetrofit.create(DtdPlannerRetrofitApi.class);

		beforeTest();
	}

	@After
	public void clean() throws IOException
	{
		server.close();
	}

	static long WAIT_BETWEEN_ACTIONS_DELAY = DateUtils.SECOND_IN_MILLIS;

	public void waitForIdle(Long delay)
	{
		try
		{
			Thread.sleep(delay);
		}
		catch (InterruptedException e)
		{
			if(BuildConfig.DEBUG){
				e.printStackTrace();
			}
		}
	}
}
