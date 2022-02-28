package com.amf.pocsecured.di;

import android.content.Context;

import com.amf.pocsecured.MainApplication;
import com.amf.pocsecured.MainApplicationTest;
import com.amf.pocsecured.data.CalendarRepositoryImpl;
import com.amf.pocsecured.data.ICalendarRepository;
import com.amf.pocsecured.data.IUserRepository;
import com.amf.pocsecured.data.UserRepositoryImpl;
import com.amf.pocsecured.ext.MicrosoftLoginHelper;
import com.amf.pocsecured.network.DtdPlannerRetrofitApi;
import com.amf.pocsecured.network.RetrofitApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author youssefamrani
 */

@SuppressWarnings("unused") //
@Module public class AppModuleTest
{

	@Provides
	Context provideContext(MainApplicationTest application)
	{
		return application.getApplicationContext();
	}

	@Provides
	Gson provideGson()
	{
		return new GsonBuilder().create();
	}

	@Singleton
	@Provides
	IUserRepository provideUserRepository(RetrofitApi retrofitApi, MicrosoftLoginHelper microsoftLoginHelper)
	{
		return new UserRepositoryImpl(retrofitApi, microsoftLoginHelper);
	}

	@Singleton
	@Provides
	ICalendarRepository provideCalendarRepository(DtdPlannerRetrofitApi api, MicrosoftLoginHelper microsoftLoginHelper)
	{
		return new CalendarRepositoryImpl(api, microsoftLoginHelper);
	}

}
