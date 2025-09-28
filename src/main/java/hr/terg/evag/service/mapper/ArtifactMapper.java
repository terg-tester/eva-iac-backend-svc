package hr.terg.evag.service.mapper;

import hr.terg.evag.domain.Artifact;
import hr.terg.evag.domain.Project;
import hr.terg.evag.domain.User;
import hr.terg.evag.service.dto.ArtifactDTO;
import hr.terg.evag.service.dto.ProjectDTO;
import hr.terg.evag.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Artifact} and its DTO {@link ArtifactDTO}.
 */
@Mapper(componentModel = "spring")
public interface ArtifactMapper extends EntityMapper<ArtifactDTO, Artifact> {
    @Mapping(target = "uploadedBy", source = "uploadedBy", qualifiedByName = "userId")
    @Mapping(target = "projects", source = "projects", qualifiedByName = "projectNameSet")
    ArtifactDTO toDto(Artifact s);

    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "removeProject", ignore = true)
    Artifact toEntity(ArtifactDTO artifactDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("projectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProjectDTO toDtoProjectName(Project project);

    @Named("projectNameSet")
    default Set<ProjectDTO> toDtoProjectNameSet(Set<Project> project) {
        return project.stream().map(this::toDtoProjectName).collect(Collectors.toSet());
    }
}
