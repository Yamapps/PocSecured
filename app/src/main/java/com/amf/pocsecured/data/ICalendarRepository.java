package com.amf.pocsecured.data;

import android.content.Context;

import com.amf.pocsecured.model.PublicHoliday;

import java.util.List;

import io.reactivex.Observable;

/**
 * Interface that defines all existing methods that are responsible for retrieving calendar related data.
 * @author youssefamrani
 */

public interface ICalendarRepository
{
	Observable<List<PublicHoliday>> fetchPublicHolidays(Context context);
}
