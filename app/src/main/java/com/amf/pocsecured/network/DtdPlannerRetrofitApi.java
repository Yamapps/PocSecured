package com.amf.pocsecured.network;

import com.amf.pocsecured.network.dto.PublicHolidayDto;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

import static com.amf.pocsecured.network.NetworkConstants.REQUEST_PUBLIC_HOLIDAYS_URL;

/**
 *
 * Interface that defines all available DTD-Planner API calls using Retrofit library
 * @author youssefamrani
 */

public interface DtdPlannerRetrofitApi
{
	/**
	 * Fetch public holidays
	 *
	 * @param headerAuth {@link String} user access token header
	 * @return {@link Call<PublicHolidayDto>}
	 */
	@GET(REQUEST_PUBLIC_HOLIDAYS_URL)
	@NotNull
	Call<List<PublicHolidayDto>> fetchPublicHolidays(@Header("Authorization") String headerAuth);
}
