package com.amf.pocsecured.di;

import android.annotation.SuppressLint;

import com.amf.pocsecured.BuildConfig;
import com.amf.pocsecured.network.DtdPlannerRetrofitApi;
import com.amf.pocsecured.network.NetworkConstants;
import com.amf.pocsecured.network.RetrofitApi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Defines all the network related classes that need to be provided in the scope of the app.
 * @author youssefamrani
 */

@Module public class NetworkModule
{

	@Provides
	public final RetrofitApi provideRetrofitApi(OkHttpClient okHttpClient)
	{
		Retrofit retrofit = new Retrofit.Builder() //
									.client(okHttpClient) //
									.baseUrl(NetworkConstants.MS_GRAPH_ROOT_ENDPOINT) //
									.addConverterFactory(JacksonConverterFactory.create()).build(); //
		return retrofit.create(RetrofitApi.class);
	}

	@Provides
	public final DtdPlannerRetrofitApi provideDtdPlannerRetrofitApi(OkHttpClient okHttpClient)
	{
		Retrofit retrofit = new Retrofit.Builder() //
									.client(okHttpClient) //
									.baseUrl(NetworkConstants.DTD_PLANNER_ENDPOINT) //
									.addConverterFactory(JacksonConverterFactory.create()).build(); //
		return retrofit.create(DtdPlannerRetrofitApi.class);
	}

	@Provides
	@NotNull
	public final OkHttpClient provideOkHttpClient()
	{
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);
		okhttp3.OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder() //
													   .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //
													   .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS); //
		builder.addInterceptor(logging);
		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager()
		{
			@SuppressLint({"TrustAllX509TrustManager"})
			public void checkClientTrusted(@Nullable X509Certificate[] chain, @Nullable String authType)
			{
			}

			@SuppressLint({"TrustAllX509TrustManager"})
			public void checkServerTrusted(@Nullable X509Certificate[] chain, @Nullable String authType)
			{
			}

			@NotNull
			public X509Certificate[] getAcceptedIssuers()
			{
				return new X509Certificate[0];
			}
		}};
		try
		{
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new SecureRandom());
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			TrustManager var10002 = trustAllCerts[0];
			builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) var10002);
			//			builder.hostnameVerifier(null);
		}
		catch (NoSuchAlgorithmException | KeyManagementException e)
		{
			if(BuildConfig.DEBUG){
				e.printStackTrace();
			}
		}
		return builder.build();
	}

	//Connect timeout delay in seconds
	static Long CONNECT_TIMEOUT = 30L;

	//Read timeout delay in seconds
	static Long READ_TIMEOUT = 30L;
}
