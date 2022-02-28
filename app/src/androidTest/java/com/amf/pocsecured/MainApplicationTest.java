package com.amf.pocsecured;

import android.app.Application;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

/**
 * @author youssefamrani
 */

public class MainApplicationTest extends Application implements HasAndroidInjector
{

	@Inject
	DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

	@Override
	public AndroidInjector<Object> androidInjector()
	{
		return dispatchingAndroidInjector;
	}
}
