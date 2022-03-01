package com.amf.pocsecured.network;

import com.amf.pocsecured.network.dto.EventListDto;
import com.amf.pocsecured.network.dto.UserDto;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import static com.amf.pocsecured.network.NetworkConstants.REQUEST_EVENTS_TIMEFRAME_URL;
import static com.amf.pocsecured.network.NetworkConstants.REQUEST_EVENTS_URL;
import static com.amf.pocsecured.network.NetworkConstants.REQUEST_ME_URL;

/**
 *
 * Interface that defines all available MSGraph API calls using Retrofit library
 * @author youssefamrani
 */

public interface MSGraphRetrofitApi
{

	/**
	 * Fetch user infos
	 *
	 * @param headerAuth {@link String} user access token header
	 * @return {@link Call<UserDto>}
	 */
	@GET(REQUEST_ME_URL)
	@NotNull
	Call<UserDto> me(@Header("Authorization") String headerAuth);

	/**
	 * Fetch user events
	 *
	 * @param headerAuth {@link String} user access token header
	 * @return {@link Call<EventListDto>}
	 */
	@GET(REQUEST_EVENTS_URL)
	@NotNull
	Call<EventListDto> events(@Header("Authorization") String headerAuth);

	/**
	 * Fetch user events with limited time frame
	 *
	 * @param headerAuth {@link String} user access token header
	 * @param startTime {@link String} Start date for events
	 * @param endTime {@link String} End date for events
	 * @return {@link Call<EventListDto>}
	 */
	@GET(REQUEST_EVENTS_TIMEFRAME_URL)
	@NotNull
	Call<EventListDto> eventsWithTimeFrame(@Header("Authorization") String headerAuth, @Query("startdatetime") String startTime, @Query("enddatetime") String endTime);

}
