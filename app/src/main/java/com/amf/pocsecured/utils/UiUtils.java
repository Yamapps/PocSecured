package com.amf.pocsecured.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Helper class to get screen and devices informations
 *
 * @author youssefamrani
 */
@SuppressWarnings("unused") //
public class UiUtils
{

	private static final String SIZE_SMALL = "small";
	private static final String SIZE_NORMAL = "normal";
	private static final String SIZE_LARGE = "large";
	private static final String SIZE_XLARGE = "xlarge";

	private static final String DENSITY_LDPI = "ldpi";
	private static final String DENSITY_MDPI = "mdpi";
	private static final String DENSITY_HDPI = "hdpi";
	private static final String DENSITY_XHDPI = "xhdpi";
	private static final String DENSITY_XXHDPI = "xxhdpi";
	private static final String DENSITY_XXXHDPI = "xxxhdpi";
	private static final String DENSITY_TVDPI = "tvdpi";
	private static final String DENSITY_400_DPI = "400-dpi";

	/**
	 * Returns the screen width
	 *
	 * @param context current {@link Context}
	 * @return {@link Integer} screen width
	 */
	public static int getScreenWidth(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}

	/**
	 * Returns the screen height
	 *
	 * @param context current {@link Context}
	 * @return {@link Integer} screen height
	 */
	public static int getScreenHeight(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.y;
	}

	/**
	 * Returns the screen density expressed as dots-per-inch
	 *
	 * @param context current {@link Context}
	 * @return {@link Integer} screen density
	 */
	public static int getScreenDensity(Context context)
	{
		return context.getResources().getDisplayMetrics().densityDpi;
	}

	/**
	 *
	 * Indicates whether the current device has a large screen
	 *
	 * @param context current {@link Context}
	 * @return true if device has a large screen
	 */
	public static boolean isLargeScreen(Context context)
	{
		int screenSize = context.getResources().getConfiguration().screenLayout //
								 & Configuration.SCREENLAYOUT_SIZE_MASK;
		boolean isLargeScreen;
		switch (screenSize)
		{
			case Configuration.SCREENLAYOUT_SIZE_LARGE:
			case Configuration.SCREENLAYOUT_SIZE_XLARGE:
				isLargeScreen = true;
				break;
			default:
				isLargeScreen = false;
				break;
		}

		return isLargeScreen;
	}

	/**
	 * Returns the current device bucket (concatenation of screen size and screen density)
	 * <p>
	 * example : normal-xhdpi
	 *
	 * @param context current {@link Context}
	 * @return {@link String} device bucket
	 */
	public static String getDeviceBucket(Context context)
	{
		int screenSize = context.getResources().getConfiguration().screenLayout //
								 & Configuration.SCREENLAYOUT_SIZE_MASK;
		String size;
		switch (screenSize)
		{
			case Configuration.SCREENLAYOUT_SIZE_SMALL:
				size = SIZE_SMALL;
				break;
			case Configuration.SCREENLAYOUT_SIZE_NORMAL:
				size = SIZE_NORMAL;
				break;
			case Configuration.SCREENLAYOUT_SIZE_LARGE:
				size = SIZE_LARGE;
				break;
			case Configuration.SCREENLAYOUT_SIZE_XLARGE:
				size = SIZE_XLARGE;
				break;
			default:
				size = "??";
				break;
		}

		switch (context.getResources().getDisplayMetrics().densityDpi)
		{
			case DisplayMetrics.DENSITY_LOW:
				size += "- " + DENSITY_LDPI;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				size += "-" + DENSITY_MDPI;
				break;
			case DisplayMetrics.DENSITY_HIGH:
				size += "-" + DENSITY_HDPI;
				break;
			case DisplayMetrics.DENSITY_XHIGH:
				size += "-" + DENSITY_XHDPI;
				break;
			case DisplayMetrics.DENSITY_XXHIGH:
				size += "-" + DENSITY_XXHDPI;
				break;
			case DisplayMetrics.DENSITY_XXXHIGH:
				size += "-" + DENSITY_XXXHDPI;
				break;
			case DisplayMetrics.DENSITY_TV:
				size += "-" + DENSITY_TVDPI;
				break;
			case DisplayMetrics.DENSITY_400:
				size += "-" + DENSITY_400_DPI;
				break;
		}

		return size;
	}
}
