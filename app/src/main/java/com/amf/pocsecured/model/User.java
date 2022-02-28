package com.amf.pocsecured.model;

/**
 * @author youssefamrani
 */

public class User
{
	private String displayName;
	private String firstName;
	private String lastName;
	private String email;
	private Role role;

	public User(String displayName, String firstName, String lastName, String email, Role role)
	{
		this.displayName = displayName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.role = role;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getNameForScreen(){
		return lastName + ", " + firstName;
	}

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}
}
