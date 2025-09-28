package hr.terg.evag.service.mapper;

import hr.terg.evag.domain.Deliverable;
import hr.terg.evag.domain.Deployment;
import hr.terg.evag.domain.User;
import hr.terg.evag.service.dto.DeliverableDTO;
import hr.terg.evag.service.dto.DeploymentDTO;
import hr.terg.evag.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Deployment} and its DTO {@link DeploymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeploymentMapper extends EntityMapper<DeploymentDTO, Deployment> {
    @Mapping(target = "deployedBy", source = "deployedBy", qualifiedByName = "userId")
    @Mapping(target = "deliverable", source = "deliverable", qualifiedByName = "deliverableName")
    DeploymentDTO toDto(Deployment s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("deliverableName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DeliverableDTO toDtoDeliverableName(Deliverable deliverable);
}
