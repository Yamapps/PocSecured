package com.amf.pocsecured.model.mapper;

import com.amf.pocsecured.model.Role;
import com.amf.pocsecured.model.User;
import com.amf.pocsecured.network.dto.UserDto;

import java.util.Objects;

/**
 *
 * Helper class to map {@link User} from {@link UserDto}
 *
 * @author youssefamrani
 */

public class UserMapper
{
	public static User mapDto(UserDto userDto)
	{
		Objects.requireNonNull(userDto);
		return new User( //
						 userDto.getDisplayName(), //
						 userDto.getGivenName(),  //
						 userDto.getSurname(),  //
						 userDto.getUserPrincipalName(),
						 Role.DEFAULT_ACCESS);
	}

}
