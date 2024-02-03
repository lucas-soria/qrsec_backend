package com.lsoria.qrsec.domain.dto.mapper;

import com.lsoria.qrsec.domain.dto.InviteDTO;
import com.lsoria.qrsec.domain.model.Invite;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InviteMapper {

    InviteDTO inviteToInviteDTO(Invite invite);

    Invite inviteDTOToInvite(InviteDTO inviteDTO);

}
