package com.amf.pocsecured.model;

import org.joda.time.DateTime;

/**
 * @author youssefamrani
 */

public class PublicHoliday
{
	private DateTime date;
	private String label;

	public PublicHoliday(DateTime date, String label)
	{
		this.date = date;
		this.label = label;
	}

	public DateTime getDate()
	{
		return date;
	}

	public String getLabel()
	{
		return label;
	}
}
