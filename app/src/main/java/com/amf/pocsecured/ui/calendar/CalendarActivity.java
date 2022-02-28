package com.amf.pocsecured.ui.calendar;

import android.os.Bundle;
import android.view.MenuItem;

import com.amf.pocsecured.R;
import com.amf.pocsecured.databinding.ActivityCalendarBinding;
import com.amf.pocsecured.ui.BaseActivity;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

public class CalendarActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Init view binding
		ActivityCalendarBinding binding = ActivityCalendarBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		// Init Navigation
		DrawerLayout drawer = binding.drawerLayout;
		NavigationView navigationView = binding.navView;
		enableNavigationDrawer(drawer, navigationView, this);
		mNavigationManager.showGroup(R.id.group_connected);
		mNavigationManager.hideGroup(R.id.group_disconnected);

		// Init Toolbar
		binding.appBarCalendar.toolbarDrawerIcon.setOnClickListener(view->mNavigationManager.toggleDrawer(CalendarActivity.this));
		binding.appBarCalendar.toolbarDrawerText.setText(getTitle());
		setSupportActionBar(binding.appBarCalendar.toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

		displayFragmentContent();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		mNavigationManager.setCheckedItem(R.id.nav_calendar);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item)
	{
		handleNavigationClick(item.getItemId());
		return true;
	}

	protected void displayFragmentContent() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		CalendarFragment fragment = CalendarFragment.newInstance(0);
		ft.add(R.id.fragment_container, fragment);
		ft.commit();
	}
}