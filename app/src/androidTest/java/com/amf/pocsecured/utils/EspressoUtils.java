package com.amf.pocsecured.utils;

import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import androidx.annotation.IdRes;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author youssefamrani
 */

@SuppressWarnings("unused") public class EspressoUtils
{

	public static Matcher<View> withIndex(Matcher<View> matcher, int index)
	{
		return new TypeSafeMatcher<View>()
		{

			int currentIndex = 0;
			int viewObjHash = 0;

			@Override
			protected boolean matchesSafely(View item)
			{
				if (matcher.matches(item) && currentIndex++ == index)
				{
					viewObjHash = item.hashCode();
				}
				return item.hashCode() == viewObjHash;
			}

			@Override
			public void describeTo(Description description)
			{
				description.appendText(String.format("with index: %d ", index));
				matcher.describeTo(description);
			}
		};
	}

	public static void checkSnackbar(String message) {
		onView(withId(com.google.android.material.R.id.snackbar_text))
				.check(matches(withText(message)));
	}

	public static void checkSnackbar(@IdRes int resId) {
		onView(withId(com.google.android.material.R.id.snackbar_text))
				.check(matches(withText(resId)));
	}
}
