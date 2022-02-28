package com.amf.pocsecured;

import android.content.Context;

import com.amf.pocsecured.di.DaggerAppComponent;

import androidx.multidex.MultiDex;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import timber.log.Timber;

/**
 * @author youssefamrani
 */

public class MainApplication extends DaggerApplication
{

	// Reference to the application graph that is used across the whole app
//	ApplicationComponent appComponent = DaggerApplicationComponent.create();

	@Override
	protected void attachBaseContext(Context base)
	{
		super.attachBaseContext(base);
		MultiDex.install(this);

		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		}
	}

	@Override
	protected AndroidInjector<? extends DaggerApplication> applicationInjector()
	{
		return DaggerAppComponent
					   .factory()
					   .create(this);
	}
}
