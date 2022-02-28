package com.amf.pocsecured.model;

import androidx.annotation.NonNull;

/**
 * @author youssefamrani
 */

public enum Role
{
	DEFAULT_ACCESS(0, "Default Access"),
	CALENDAR_READER(1, "Calendar.Reader"),
	CONTRIBUTOR(2, "Calendar.Writer");

	private int mKey;
	private String mValue;

	Role(int key, String value)
	{
		mKey = key;
		mValue = value;
	}

	public int getKey()
	{
		return mKey;
	}

	public String getValue()
	{
		return mValue;
	}

	@NonNull
	public static Role findByKey(int key)
	{
		for (Role role : values())
		{
			if (role.getKey() == key)
			{
				return role;
			}
		}
		return DEFAULT_ACCESS;
	}

	@NonNull
	public static Role findByValue(@NonNull String value)
	{
		for (Role role : values())
		{
			if (role.getValue().equals(value))
			{
				return role;
			}
		}
		return DEFAULT_ACCESS;
	}
}
