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
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper extends EntityMapper<ProjectDTO, Project> {
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "userId")
    @Mapping(target = "artifacts", source = "artifacts", qualifiedByName = "artifactNameSet")
    ProjectDTO toDto(Project s);

    @Mapping(target = "removeArtifact", ignore = true)
    Project toEntity(ProjectDTO projectDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("artifactName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ArtifactDTO toDtoArtifactName(Artifact artifact);

    @Named("artifactNameSet")
    default Set<ArtifactDTO> toDtoArtifactNameSet(Set<Artifact> artifact) {
        return artifact.stream().map(this::toDtoArtifactName).collect(Collectors.toSet());
    }
}
