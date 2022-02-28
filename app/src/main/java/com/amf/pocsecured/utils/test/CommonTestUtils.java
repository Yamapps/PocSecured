package com.amf.pocsecured.utils.test;

import android.content.Context;

import com.amf.pocsecured.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 *
 * Helper class used in test classes
 *
 * @author youssefamrani
 */
public class CommonTestUtils
{

	/**
	 * Reads file content and returns string.
	 *
	 * @param context current {@link Context}
	 * @param filename {@link String} file name
	 * @return file as {@link String}
	 * @throws IOException If a problem occur when reading file
	 */
	public static String openFile(Context context, String filename) throws IOException
	{
		InputStream inputStream = Objects.requireNonNull(context.getClass().getClassLoader()).getResourceAsStream(filename);
		StringBuilder sb = new StringBuilder();
		String strLine;

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

		while ((strLine = reader.readLine()) != null)
		{
			//process line
			sb.append(strLine);
		}

		return sb.toString();
	}

	@SuppressWarnings("SameParameterValue")
	public static void wait(int duration){
		try
		{
			Thread.sleep(duration);
		}
		catch (InterruptedException e)
		{
			if(BuildConfig.DEBUG){
				e.printStackTrace();
			}
		}
	}
}
