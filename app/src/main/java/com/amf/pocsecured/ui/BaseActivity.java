package com.amf.pocsecured.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.amf.pocsecured.R;
import com.amf.pocsecured.data.IUserRepository;
import com.amf.pocsecured.model.Role;
import com.amf.pocsecured.model.User;
import com.amf.pocsecured.ui.drawer.NavigationManager;
import com.amf.pocsecured.utils.UiThreadExecutor;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import androidx.drawerlayout.widget.DrawerLayout;
import dagger.android.support.DaggerAppCompatActivity;

/**
 *
 * Abstract class extended by all application's activities.
 *
 * @author youssefamrani
 */

public abstract class BaseActivity extends DaggerAppCompatActivity
{
	@Inject
	protected NavigationManager mNavigationManager;

	@Inject
	protected IUserRepository mUserRepository;

	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.please_wait));
		mProgressDialog.setCancelable(false);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		updateNavigationUserValues();
	}

	/**
	 * Display a {@link ProgressDialog} on screen when waiting for a background process completion
	 */
	public void showProgress()
	{
		if (!mProgressDialog.isShowing())
		{
			mProgressDialog.show();
		}
	}

	/*
	 * Dismiss the {@link ProgressDialog} (if displayed on screen)
	 */
	public void dismissProgress()
	{
		if (mProgressDialog.isShowing())
		{
			mProgressDialog.dismiss();
		}
	}

	/**
	 * {@link NavigationManager#enableNavigationDrawer(DrawerLayout, NavigationView, NavigationView.OnNavigationItemSelectedListener)}
	 *
	 * @param drawer {@link DrawerLayout}
	 * @param navigationView {@link NavigationView}
	 * @param itemSelectedListener {@link NavigationView.OnNavigationItemSelectedListener} listening to navigation item click
	 */
	protected void enableNavigationDrawer(DrawerLayout drawer, NavigationView navigationView, NavigationView.OnNavigationItemSelectedListener itemSelectedListener)
	{
		mNavigationManager.enableNavigationDrawer(drawer, navigationView, itemSelectedListener);
	}

	/**
	 * {@link NavigationManager#handleNavigationClick(BaseActivity, int)}
	 * @param itemId {@link Integer} clicked navigation itam
	 */
	protected void handleNavigationClick(int itemId)
	{
		mNavigationManager.handleNavigationClick(this, itemId);
	}

	/**
	 *  Update {@link NavigationView} and {@link DrawerLayout} UI with user's infos (name, email...)
	 */
	public void updateNavigationUserValues()
	{
		User currentUser = mUserRepository.getCurrentUser();
		if (currentUser != null)
		{
			mNavigationManager.showGroup(R.id.group_connected);
			mNavigationManager.hideGroup(R.id.group_disconnected);
			mNavigationManager.updateHeaderValues(currentUser.getNameForScreen(), currentUser.getEmail());

			if(currentUser.getRole().equals(Role.CONTRIBUTOR)){
				mNavigationManager.showItem(R.id.nav_calendar);
			}
			else{
				mNavigationManager.hideItem(R.id.nav_calendar);
			}
		}
		else
		{
			mNavigationManager.showGroup(R.id.group_disconnected);
			mNavigationManager.hideGroup(R.id.group_connected);
			mNavigationManager.updateHeaderValues("", getString(R.string.please_sign_in));
		}
	}

	/**
	 * Toggle {@link DrawerLayout} visibility
	 *
	 * {@link NavigationManager#toggleDrawer}
	 */
	public void toggleDrawer()
	{
		mNavigationManager.toggleDrawer(this);
	}

	/**
	 * Display {@link Snackbar} with specified message
	 * @param message {@link String} message to display
	 */
	public void alertSnack(String message)
	{
		new UiThreadExecutor().execute(()->Snackbar.make(getWindow().getDecorView(), message, Snackbar.LENGTH_LONG).show());
	}

	/**
	 * Display {@link Toast} with specified message
	 * @param message {@link String} message to display
	 */
	public void alertToast(String message)
	{
		new UiThreadExecutor().execute(()->Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
	}

	@Override
	public void onBackPressed()
	{
		if (mNavigationManager.onBackPressed(this))
		{
			return;
		}
		super.onBackPressed();
	}
}
