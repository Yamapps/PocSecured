package com.amf.pocsecured.utils;

import android.view.View;

import org.hamcrest.Matcher;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author youssefamrani
 */

public class RecyclerViewItemCountAssertion implements ViewAssertion
{
	private final Matcher<Integer> matcher;

	public static RecyclerViewItemCountAssertion withItemCount(int expectedCount)
	{
		return withItemCount(is(expectedCount));
	}

	public static RecyclerViewItemCountAssertion withItemCount(Matcher<Integer> matcher)
	{
		return new RecyclerViewItemCountAssertion(matcher);
	}

	private RecyclerViewItemCountAssertion(Matcher<Integer> matcher)
	{
		this.matcher = matcher;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void check(View view, NoMatchingViewException noViewFoundException)
	{
		if (noViewFoundException != null)
		{
			throw noViewFoundException;
		}

		RecyclerView recyclerView = (RecyclerView) view;
		RecyclerView.Adapter adapter = recyclerView.getAdapter();
		assert adapter != null;
		assertThat(adapter.getItemCount(), matcher);
	}
}
