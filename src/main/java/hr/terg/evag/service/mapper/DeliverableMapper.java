package hr.terg.evag.service.mapper;

import hr.terg.evag.domain.Deliverable;
import hr.terg.evag.domain.Project;
import hr.terg.evag.domain.User;
import hr.terg.evag.service.dto.DeliverableDTO;
import hr.terg.evag.service.dto.ProjectDTO;
import hr.terg.evag.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Deliverable} and its DTO {@link DeliverableDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeliverableMapper extends EntityMapper<DeliverableDTO, Deliverable> {
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "userId")
    @Mapping(target = "project", source = "project", qualifiedByName = "projectName")
    DeliverableDTO toDto(Deliverable s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("projectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProjectDTO toDtoProjectName(Project project);
}
