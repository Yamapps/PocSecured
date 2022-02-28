package com.amf.pocsecured.ui.home;

import android.os.Bundle;
import android.view.MenuItem;

import com.amf.pocsecured.R;
import com.amf.pocsecured.databinding.ActivityHomeBinding;
import com.amf.pocsecured.ui.BaseActivity;
import com.amf.pocsecured.ui.viewmodel.HomeActivityViewModel;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener
{
	@Inject
	public ViewModelProvider.Factory viewModelFactory;

	protected HomeActivityViewModel viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Init view model
		viewModel = viewModelFactory.create(HomeActivityViewModel.class);

		// Init view binding
		ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		// Init Navigation
		DrawerLayout drawer = binding.drawerLayout;
		NavigationView navigationView = binding.navView;
		enableNavigationDrawer(drawer, navigationView, this);
		mNavigationManager.showGroup(R.id.group_disconnected);
		mNavigationManager.hideGroup(R.id.group_connected);

		// Init Toolbar
		binding.appBarHome.toolbarDrawerIcon.setOnClickListener(view->mNavigationManager.toggleDrawer(HomeActivity.this));
		binding.appBarHome.toolbarDrawerText.setText(getTitle());
		setSupportActionBar(binding.appBarHome.toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

		displayFragmentContent();

		viewModel.initLogin(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mNavigationManager.setCheckedItem(R.id.nav_home);
		updateUser();
	}

	/**
	 * Updates {@link com.amf.pocsecured.model.User} in current {@link androidx.lifecycle.ViewModel}
	 */
	public void updateUser()
	{
		viewModel.setUser(mUserRepository.getCurrentUser());
	}

	public void signIn()
	{
		viewModel.signIn(this);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item)
	{
		handleNavigationClick(item.getItemId());
		return true;
	}

	protected void displayFragmentContent()
	{
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		HomeFragment homeFragment = HomeFragment.newInstance(0);
		ft.add(R.id.fragment_container, homeFragment);
		ft.commit();
	}

	@Override
	protected void onDestroy()
	{
		mUserRepository.onDestroy();
		viewModel.onDestroy();
		super.onDestroy();
	}
}