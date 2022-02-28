package com.amf.pocsecured.utils;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * Helper class used to manipulate DateTime objects
 * @author youssefamrani
 */

@SuppressWarnings("unused") public class DateTimeUtils
{

    public static String DATE_PATTERN = "yyyy-MM-dd";
	public static String DATE_PATTERN_WITH_HOUR = "yyyy-MM-dd  HH:mm";
	public static String DATE_PATTERN_LABEL = "EEE, d MMM yyyy";
	public static String DATE_PATTERN_DAY_OF_WEEK = "EEE";
	public static String DATE_PATTERN_DAY_OF_MONTH = "d";
	public static String DATE_PATTERN_MONTH = "MMM yyyy";

	static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());

	/**
	 * Format String from specified Date
	 *
	 * @param date Date object
	 * @return String from specified Date
	 */
	public static String toString(Date date){
		return simpleDateFormat.format(date);
	}

	/**
	 * Format String from specififed DateTime
	 *
	 * @param date DateTime object
	 * @return String from specified DateTime
	 */
	public static String toString(DateTime date){
		return simpleDateFormat.format(date.toDate());
	}

	/**
	 * Format String from DateTime using specified date pattern
	 *
	 * @param date Date object
	 * @param pattern Date pattern
	 * @return String from specified DateTime
	 */
	public static String toString(DateTime date, String pattern){
		return new SimpleDateFormat(pattern, Locale.getDefault()).format(date.toDate());
	}

	/**
	 * Parses Date from specified String
	 *
	 * @param date String describing a date
	 * @return Date from specified String
	 *
	 * @throws ParseException @see {@link java.text.DateFormat#parse(String)}
	 */
	public static Date toDate(String date) throws ParseException
	{
		return simpleDateFormat.parse(date);
	}

	/**
	 * Parses DateTime from specified String
	 *
	 * @param date String describing a date
	 * @return Date from specified String
	 *
	 * @throws ParseException @see {@link java.text.DateFormat#parse(String)}
	 */
	public static DateTime toDateTime(String date) throws ParseException
	{
		return new DateTime(simpleDateFormat.parse(date));
	}
}
