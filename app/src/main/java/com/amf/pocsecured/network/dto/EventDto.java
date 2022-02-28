package com.amf.pocsecured.network.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * @author youssefamrani
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class EventDto
{
	private Date createdDateTime;
	private String subject;
	private String bodyPreview;
	private DateDto start;
	private DateDto end;
	private LocationDto location;
	private OrganizerDto organizer;

	public EventDto()
	{
	}

	public Date getCreatedDateTime()
	{
		return createdDateTime;
	}

	public String getSubject()
	{
		return subject;
	}

	public String getBodyPreview()
	{
		return bodyPreview;
	}

	public DateDto getStart()
	{
		return start;
	}

	public DateDto getEnd()
	{
		return end;
	}

	public LocationDto getLocation()
	{
		return location;
	}

	public OrganizerDto getOrganizer()
	{
		return organizer;
	}

	@JsonIgnoreProperties(ignoreUnknown = true) public static class DateDto
	{
		private Date dateTime;
		private String timeZone;

		public DateDto()
		{
		}

		public Date getDateTime()
		{
			return dateTime;
		}

		public String getTimeZone()
		{
			return timeZone;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true) public static class LocationDto
	{
		private String displayName;
		private AddressDto address;

		public LocationDto()
		{
		}

		public String getDisplayName()
		{
			return displayName;
		}

		public AddressDto getAddress()
		{
			return address;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true) public static class AddressDto
	{
		private String street;
		private String city;
		private String state;
		private String countryOrRegion;
		private String postalCode;

		public AddressDto()
		{
		}

		public AddressDto(String street, String city, String state, String countryOrRegion, String postalCode)
		{
			this.street = street;
			this.city = city;
			this.state = state;
			this.countryOrRegion = countryOrRegion;
			this.postalCode = postalCode;
		}

		public String getStreet()
		{
			return street;
		}

		public String getCity()
		{
			return city;
		}

		public String getState()
		{
			return state;
		}

		public String getCountryOrRegion()
		{
			return countryOrRegion;
		}

		public String getPostalCode()
		{
			return postalCode;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true) public static class OrganizerDto
	{
		private EmailAddressDto emailAddress;

		public OrganizerDto()
		{
		}

		public EmailAddressDto getEmailAddress()
		{
			return emailAddress;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true) public static class EmailAddressDto
	{
		private String name;
		private String address;

		public EmailAddressDto()
		{
		}

		public String getName()
		{
			return name;
		}

		public String getAddress()
		{
			return address;
		}
	}
}
