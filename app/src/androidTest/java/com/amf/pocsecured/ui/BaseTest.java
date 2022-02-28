package com.amf.pocsecured.ui;

import android.text.format.DateUtils;

import com.amf.pocsecured.BuildConfig;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;

/**
 * @author youssefamrani
 */

public abstract class BaseTest
{

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

	protected void registerIdle(IdlingResource idlingResource)
	{
		if (idlingResource != null)
		{
			IdlingRegistry.getInstance().register(idlingResource);
		}
	}
}
