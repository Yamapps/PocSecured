package com.amf.pocsecured.network;

import com.amf.pocsecured.network.dto.EventListDto;
import com.amf.pocsecured.network.dto.PublicHolidayDto;
import com.amf.pocsecured.network.dto.UserDto;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import static com.amf.pocsecured.network.NetworkConstants.DTD_PLANNER_ENDPOINT;
import static com.amf.pocsecured.network.NetworkConstants.REQUEST_EVENTS_TIMEFRAME_URL;
import static com.amf.pocsecured.network.NetworkConstants.REQUEST_EVENTS_URL;
import static com.amf.pocsecured.network.NetworkConstants.REQUEST_ME_URL;
import static com.amf.pocsecured.network.NetworkConstants.REQUEST_PUBLIC_HOLIDAYS_URL;

/**
 *
 * Interface that defines all available API calls using Retrofit library
 * @author youssefamrani
 */

public interface DtdPlannerRetrofitApi
{
	/**
	 * Fetch user events
	 *
	 * @param headerAuth {@link String} user access token header
	 * @return {@link Call<EventListDto>}
	 */
	@GET(REQUEST_PUBLIC_HOLIDAYS_URL)
	@NotNull
	Call<List<PublicHolidayDto>> fetchPublicHolidays(@Header("Authorization") String headerAuth);
}
