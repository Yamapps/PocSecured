package com.amf.pocsecured.model.mapper;

import com.amf.pocsecured.model.Event;
import com.amf.pocsecured.model.PublicHoliday;
import com.amf.pocsecured.network.dto.EventDto;
import com.amf.pocsecured.network.dto.EventListDto;
import com.amf.pocsecured.network.dto.PublicHolidayDto;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * Helper class to map {@link Event} from {@link EventListDto}
 *
 * @author youssefamrani
 */

public class EventMapper
{
	public static List<Event> mapDto(EventListDto eventListDto)
	{
		Objects.requireNonNull(eventListDto);
		List<Event> events = new ArrayList<>();
		for (EventDto eventDto : eventListDto.getValue())
		{
			Event event = new Event(
					eventDto.getSubject(),
					eventDto.getOrganizer().getEmailAddress().getName(),
					eventDto.getOrganizer().getEmailAddress().getAddress(),
					new DateTime(eventDto.getStart().getDateTime()),
					new DateTime(eventDto.getEnd().getDateTime()),
					eventDto.getLocation().getDisplayName()
			);
			events.add(event);
		}
		return events;
	}

}
