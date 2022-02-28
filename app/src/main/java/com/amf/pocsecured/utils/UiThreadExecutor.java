package com.amf.pocsecured.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

/**
 *
 * Helper class to execute runnable in UI Thread.
 *
 * @author youssefamrani
 */
public class UiThreadExecutor implements Executor
{
	private static final Handler sUiHandler;

	static
	{
		Looper uiLooper = Looper.getMainLooper();
		sUiHandler = new Handler(uiLooper);
	}

	private boolean isUiThread(){
		return Thread.currentThread() == Looper.getMainLooper().getThread();
	}

	@Override
	public void execute(Runnable runnable)
	{
		if (isUiThread())
		{
			runnable.run();
		}
		else
		{
			sUiHandler.post(runnable);
		}
	}
	public void execute(Runnable runnable, long delay)
	{
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				execute(runnable);
			}
		}, delay);
	}
}
