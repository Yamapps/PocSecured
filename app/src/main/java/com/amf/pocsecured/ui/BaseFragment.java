package com.amf.pocsecured.ui;

import dagger.android.support.DaggerFragment;

/**
 *
 * Abstract class extended by all application's fragments.
 *
 * @author youssefamrani
 */

public abstract class BaseFragment extends DaggerFragment
{

	protected static final int NORMAL_SCREEN_RECYCLER_ROW_COUNT = 1;
	protected static final int LARGE_SCREEN_RECYCLER_ROW_COUNT = 2;


	/**
	 * {@link BaseActivity#showProgress()}
	 */
	protected void showProgress()
	{
		((BaseActivity) requireActivity()).showProgress();
	}

	/**
	 * {@link BaseActivity#dismissProgress()} ()}
	 */
	public void dismissProgress()
	{
		((BaseActivity) requireActivity()).dismissProgress();
	}

	/**
	 * {@link BaseActivity#alertSnack(String)} ()}
	 */
	public void alertSnack(String message)
	{
		((BaseActivity) requireActivity()).alertSnack(message);
	}

	/**
	 * {@link BaseActivity#alertToast(String)}
	 */
	public void alertToast(String message)
	{
		((BaseActivity) requireActivity()).alertToast(message);
	}
}
