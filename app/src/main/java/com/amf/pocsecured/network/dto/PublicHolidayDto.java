package com.amf.pocsecured.network.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * @author youssefamrani
 */

@JsonIgnoreProperties(ignoreUnknown = true) public class PublicHolidayDto
{
	private Date date;
	private String label;

	public PublicHolidayDto()
	{
	}

	public PublicHolidayDto(Date date, String label)
	{
		this.date = date;
		this.label = label;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}
}
