package com.amf.pocsecured.network.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * @author youssefamrani
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventListDto
{
	private List<EventDto> value;

	public List<EventDto> getValue()
	{
		return value;
	}
}
