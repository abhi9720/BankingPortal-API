package com.webapp.bankingportal.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.webapp.bankingportal.entity.User;

/**
 * MapStruct mapper for updating {@link com.webapp.bankingportal.entity.User User} entities.
 *
 * <p>Registered as a Spring bean ({@code componentModel = "spring"}) so it can be
 * dependency-injected into service classes.</p>
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Copies non-null properties from {@code source} onto {@code target}, leaving
     * existing values in {@code target} intact when the corresponding field in
     * {@code source} is {@code null}.
     *
     * @param source the user object carrying the updated field values
     * @param target the existing user entity to be updated in place
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(User source, @MappingTarget User target);
}
