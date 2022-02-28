package com.amf.pocsecured.utils;

import com.amf.pocsecured.BuildConfig;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * This class implements Gson's JsonDeserializer.
 * <p>
 * Used to set specific date format when deserializing Time object from json files
 *
 * @author youssefamrani
 */
public class TimeDeserializer implements JsonDeserializer<Time>
{

	private final String[] DATE_FORMATS = new String[] {"yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss"};

	@Override
	public Time deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		String date = element.getAsString();

		for (String it : DATE_FORMATS)
		{
			try
			{
				SimpleDateFormat format = new SimpleDateFormat(it, Locale.getDefault());
				format.setTimeZone(TimeZone.getTimeZone("UTC"));
				Date result = format.parse(date);
				if (result != null)
				{
					return new Time(result.getTime());
				}
			}
			catch (ParseException e)
			{
				if(BuildConfig.DEBUG){
					e.printStackTrace();
				}
			}
		}

		throw new JsonParseException("---- Unparseable date : \"" + element.getAsString() + "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
	}
}
