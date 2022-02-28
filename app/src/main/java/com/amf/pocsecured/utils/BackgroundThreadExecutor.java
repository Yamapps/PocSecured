package com.amf.pocsecured.utils;

import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Helper class to execute runnable in Background Thread.
 *
 * @author youssefamrani
 */
public class BackgroundThreadExecutor implements Executor
{
	private boolean isUiThread()
	{
		return Thread.currentThread() == Looper.getMainLooper().getThread();
	}

	@Override
	public void execute(Runnable runnable)
	{
		if (!isUiThread())
		{
			runnable.run();
		}
		else
		{
			new Thread(runnable).start();
		}
	}
}
