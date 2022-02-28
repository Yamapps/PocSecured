package com.amf.pocsecured.data;

import android.app.Activity;
import android.content.Context;

import com.amf.pocsecured.model.Event;
import com.amf.pocsecured.model.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Interface that defines all existing methods that are responsible for retrieving user related data
 * @author youssefamrani
 */

public interface IUserRepository
{
	Observable<Boolean> init(Context context);
	Observable<String> signIn(Activity activity);

	Completable signOut();
	User getCurrentUser();
	Boolean isUserSignedIn();

	Observable<User> fetchUserInfos();
	Observable<List<Event>> fetchUserEvents();

	void onDestroy();
}
