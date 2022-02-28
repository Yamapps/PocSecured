package com.amf.pocsecured.model.mapper;

import com.amf.pocsecured.model.PublicHoliday;
import com.amf.pocsecured.network.dto.PublicHolidayDto;

import org.joda.time.DateTime;

/**
 *
 * Helper class to map {@link PublicHoliday} from {@link PublicHolidayDto}
 *
 * @author youssefamrani
 */
public class PublicHolidayMapper
{
	public static PublicHoliday mapDto(PublicHolidayDto publicHolidayDto)
	{
		return new PublicHoliday(new DateTime(publicHolidayDto.getDate()), publicHolidayDto.getLabel());
	}

}
