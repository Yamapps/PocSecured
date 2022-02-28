package com.amf.pocsecured.network.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

/**
 * @author youssefamrani
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto
{
	private String displayName;
	private String surname;
	private String givenName;
	private String id;
	private String userPrincipalName;
	private String[] businessPhones;
	private String jobTitle;
	private String mail;
	private String mobilePhone;
	private String officeLocation;

	public String getDisplayName()
	{
		return displayName;
	}

	public String getSurname()
	{
		return surname;
	}

	public String getGivenName()
	{
		return givenName;
	}

	public String getId()
	{
		return id;
	}

	public String getUserPrincipalName()
	{
		return userPrincipalName;
	}

	public String[] getBusinessPhones()
	{
		return businessPhones;
	}

	public String getJobTitle()
	{
		return jobTitle;
	}

	public String getMail()
	{
		return mail;
	}

	public String getMobilePhone()
	{
		return mobilePhone;
	}

	public String getOfficeLocation()
	{
		return officeLocation;
	}
}
