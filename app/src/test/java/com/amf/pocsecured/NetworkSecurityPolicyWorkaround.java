package com.amf.pocsecured;

import android.security.NetworkSecurityPolicy;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * @author youssefamrani
 */

@Implements(NetworkSecurityPolicy.class)
public class NetworkSecurityPolicyWorkaround {
	@Implementation
	public static NetworkSecurityPolicy getInstance() {
		//noinspection OverlyBroadCatchBlock
		try {
			Class<?> shadow = Class.forName("android.security.NetworkSecurityPolicy");
			return (NetworkSecurityPolicy) shadow.newInstance();
		} catch (Exception e) {
			throw new AssertionError();
		}
	}

	@SuppressWarnings("unused")
	@Implementation
	public boolean isCleartextTrafficPermitted(String hostname) {
		return true;
	}
}
