package com.amf.pocsecured.di;

import android.content.Context;

import com.amf.pocsecured.MainApplication;
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
 * Defines all the classes that need to be provided in the scope of the app.
 * <p>
 * Define here all objects that are shared throughout the app. If some of those objects are
 * singletons, they should be annotated with `@Singleton`.
 */
@Module public class AppModule
{

	@Provides
	Context provideContext(MainApplication application)
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
