package com.amf.pocsecured.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.sql.Time;
import java.util.Date;

/**
 *
 * Helper class to centralize the Gson used in the application
 *
 * @author youssefamrani
 */
public class GsonUtils
{

	/**
	 * Returns an instance of Gson used to parse/generate json files
	 * @return Gson instance
	 */
	public static Gson getGson()
	{
		GsonBuilder gsonBuilder = new GsonBuilder();

		gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
		gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer());
		gsonBuilder.registerTypeAdapter(Time.class, new TimeDeserializer());

		gsonBuilder.disableHtmlEscaping();
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.setLenient();

		return gsonBuilder.create();
	}
}
