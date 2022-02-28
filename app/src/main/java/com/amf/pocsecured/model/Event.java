package com.amf.pocsecured.model;

import org.joda.time.DateTime;

/**
 * @author youssefamrani
 */

public class Event
{
	private String subject;
	private String organizerName;
	private String organizerEmail;
	private DateTime start;
	private DateTime end;
	private String address;

	public Event(String subject, String organizerName, String organizerEmail, DateTime start, DateTime end, String address)
	{
		this.subject = subject;
		this.organizerName = organizerName;
		this.organizerEmail = organizerEmail;
		this.start = start;
		this.end = end;
		this.address = address;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getOrganizerName()
	{
		return organizerName;
	}

	public void setOrganizerName(String organizerName)
	{
		this.organizerName = organizerName;
	}

	public String getOrganizerEmail()
	{
		return organizerEmail;
	}

	public void setOrganizerEmail(String organizerEmail)
	{
		this.organizerEmail = organizerEmail;
	}

	public DateTime getStart()
	{
		return start;
	}

	public void setStart(DateTime start)
	{
		this.start = start;
	}

	public DateTime getEnd()
	{
		return end;
	}

	public void setEnd(DateTime end)
	{
		this.end = end;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}
}
