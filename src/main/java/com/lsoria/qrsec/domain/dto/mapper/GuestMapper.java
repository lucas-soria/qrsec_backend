package com.lsoria.qrsec.domain.dto.mapper;

import com.lsoria.qrsec.domain.dto.GuestDTO;
import com.lsoria.qrsec.domain.model.Guest;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GuestMapper {

    GuestDTO guestToGuestDTO(Guest guest);

    Guest guestDTOToGuest(GuestDTO guestDTO);

}
