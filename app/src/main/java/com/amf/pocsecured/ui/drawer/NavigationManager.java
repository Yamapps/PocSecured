package com.amf.pocsecured.ui.drawer;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.amf.pocsecured.R;
import com.amf.pocsecured.domain.usecase.SignOutUseCase;
import com.amf.pocsecured.ui.BaseActivity;
import com.amf.pocsecured.ui.calendar.CalendarActivity;
import com.amf.pocsecured.ui.event.EventActivity;
import com.amf.pocsecured.ui.home.HomeActivity;
import com.amf.pocsecured.utils.UiThreadExecutor;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import androidx.annotation.IdRes;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import io.reactivex.observers.DisposableObserver;

/**
 * @author youssefamrani
 */

public class NavigationManager
{
	private NavigationView mNavigationViewLeft;

	public static int NAVIGATION_BUTTON_LATENCY = 250;
	private View mHeaderView;

	private final SignOutUseCase mSignOutUseCase;

	@Inject
	public NavigationManager(SignOutUseCase signOutUseCase)
	{
		mSignOutUseCase = signOutUseCase;
	}

	/**
	 * Initializes {@link NavigationView} and {@link DrawerLayout}
	 *
	 * @param drawer {@link DrawerLayout}
	 * @param navigationView {@link NavigationView}
	 * @param itemSelectedListener {@link NavigationView.OnNavigationItemSelectedListener} listening to navigation item click
	 */
	public void enableNavigationDrawer(DrawerLayout drawer, //
									   NavigationView navigationView, //
									   NavigationView.OnNavigationItemSelectedListener itemSelectedListener) //
	{
		//Left drawer
		drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

		mNavigationViewLeft = navigationView;
		mNavigationViewLeft.setNavigationItemSelectedListener(itemSelectedListener);
		mHeaderView = mNavigationViewLeft.inflateHeaderView(R.layout.nav_header_main);
	}

	/**
	 * Update navigation header UI with user's infos
	 *
	 * @param userName {@link String} user name
	 * @param email {@link String} user email
	 */
	public void updateHeaderValues(String userName, String email)
	{
		((TextView) mHeaderView.findViewById(R.id.nav_header_username)).setText(userName);
		((TextView) mHeaderView.findViewById(R.id.nav_header_email)).setText(email);
	}

	/**
	 * Toggle {@link DrawerLayout} visibility. Closes it if opened or opens it if closed.
	 *
	 * @param activity owner of {@link DrawerLayout}
	 */
	public void toggleDrawer(Activity activity)
	{
		DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
		if (isDrawerOpen(activity))
		{
			drawer.closeDrawer(GravityCompat.START);
		}
		else
		{
			drawer.openDrawer(GravityCompat.START);
		}
	}

	/**
	 * Hides specified navigation item
	 * @param resId {@link Integer} item resource id
	 */
	public void hideItem(@IdRes int resId)
	{
		mNavigationViewLeft.getMenu().findItem(resId).setVisible(false);
	}

	/**
	 * Shows specified navigation item
	 * @param resId {@link Integer} item resource id
	 */
	public void showItem(@IdRes int resId)
	{
		mNavigationViewLeft.getMenu().findItem(resId).setVisible(true);
	}

	/**
	 * Shows specified navigation group
	 * @param resId {@link Integer} group resource id
	 */
	public void showGroup(@IdRes int resId)
	{
		mNavigationViewLeft.getMenu().setGroupVisible(resId, true);
	}

	/**
	 * Hides specified navigation group
	 * @param resId {@link Integer} group resource id
	 */
	public void hideGroup(@IdRes int resId)
	{
		mNavigationViewLeft.getMenu().setGroupVisible(resId, false);
	}

	/**
	 * Set currently checked navigation item
	 *
	 * @param resId {@link Integer} item resource id
	 */
	public void setCheckedItem(@IdRes int resId)
	{
		mNavigationViewLeft.setCheckedItem(resId);
	}

	private Boolean isDrawerOpen(Activity activity)
	{
		DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
		return drawer.isDrawerOpen(GravityCompat.START);
	}

	private void processClick(BaseActivity activity, Class<? extends BaseActivity> cls)
	{
		activity.toggleDrawer();
		new UiThreadExecutor().execute(()->{
			activity.startActivity(new Intent(activity, cls));
			if (activity instanceof HomeActivity)
			{
				return;
			}
			activity.finish();
		}, NAVIGATION_BUTTON_LATENCY);
	}

	/**
	 * Called when back button is pressed by user {@link NavigationView} while is opened
	 *
	 * @param activity current {@link Activity}
	 * @return true if an action was taken
	 */
	public Boolean onBackPressed(Activity activity)
	{
		if (activity.findViewById(R.id.drawer_layout) != null)
		{
			DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
			if (drawer.isDrawerOpen(GravityCompat.START))
			{
				drawer.closeDrawer(GravityCompat.START);
				return true;
			}
			if (drawer.isDrawerOpen(GravityCompat.END))
			{
				drawer.closeDrawer(GravityCompat.END);
				return true;
			}
		}
		return false;
	}

	/**
	 * Called when an item on {@link NavigationView} is clicked
	 *
	 * @param activity current {@link Activity}
	 * @param itemId {@link Integer} clicked navigation itam
	 */
	@SuppressWarnings("NonConstantResourceId")
	public void handleNavigationClick(BaseActivity activity, @IdRes int itemId)
	{

		switch (itemId)
		{
			case R.id.nav_home:
				if (activity instanceof HomeActivity)
				{
					return;
				}
				toggleDrawer(activity);
				new UiThreadExecutor().execute(activity::finish, NAVIGATION_BUTTON_LATENCY);
				break;
			case R.id.nav_calendar:
				if (activity instanceof CalendarActivity)
				{
					return;
				}
				processClick(activity, CalendarActivity.class);
				break;
			case R.id.nav_event:
				if (activity instanceof EventActivity)
				{
					return;
				}
				processClick(activity, EventActivity.class);
				break;
			case R.id.nav_profile:
				//Do nothing
				break;
			case R.id.nav_sign_in:
				if (activity instanceof HomeActivity)
				{
					((HomeActivity) activity).signIn();
				}
				toggleDrawer(activity);
				break;
			case R.id.nav_sign_out:
				activity.showProgress();
				mSignOutUseCase.execute(new DisposableObserver<Void>()
				{
					@Override
					public void onNext(@NotNull Void unused)
					{
						//Do nothing
					}

					@Override
					public void onError(@NotNull Throwable e)
					{
						activity.dismissProgress();
						activity.alertSnack(e.getMessage());
						toggleDrawer(activity);
					}

					@Override
					public void onComplete()
					{
						activity.dismissProgress();
						activity.updateNavigationUserValues();
						if (activity instanceof HomeActivity)
						{
							((HomeActivity) activity).updateUser();
						}
						else
						{
							activity.finish();
						}
						dispose();
					}
				}, null);
				break;
		}

		DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
	}
}
