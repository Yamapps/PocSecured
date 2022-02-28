package com.amf.pocsecured;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

/**
 * @author youssefamrani
 */

@SuppressWarnings("unused") //
public class CustomTestRunner extends AndroidJUnitRunner
{

	@Override
	public Application newApplication(ClassLoader cl, String className, Context context) throws IllegalAccessException, InstantiationException, ClassNotFoundException
	{
		return super.newApplication(cl, MainApplicationTest.class.getName(), context);
	}

}
