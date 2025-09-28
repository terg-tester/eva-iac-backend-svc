package hr.terg.evag.service.dto;

import hr.terg.evag.domain.enumeration.ArtifactStatus;
import hr.terg.evag.domain.enumeration.ArtifactType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link hr.terg.evag.domain.Artifact} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ArtifactDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private ArtifactType type;

    @Size(max = 500)
    private String link;

    private ArtifactStatus status;

    private Long fileSize;

    private Instant createdDate;

    private Instant lastModifiedDate;

    @Size(max = 4000)
    private String addendum;

    private UserDTO uploadedBy;

    private Set<ProjectDTO> projects = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArtifactType getType() {
        return type;
    }

    public void setType(ArtifactType type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ArtifactStatus getStatus() {
        return status;
    }

    public void setStatus(ArtifactStatus status) {
        this.status = status;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getAddendum() {
        return addendum;
    }

    public void setAddendum(String addendum) {
        this.addendum = addendum;
    }

    public UserDTO getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(UserDTO uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Set<ProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(Set<ProjectDTO> projects) {
        this.projects = projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArtifactDTO)) {
            return false;
        }

        ArtifactDTO artifactDTO = (ArtifactDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, artifactDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ArtifactDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", type='" + getType() + "'" +
            ", link='" + getLink() + "'" +
            ", status='" + getStatus() + "'" +
            ", fileSize=" + getFileSize() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", addendum='" + getAddendum() + "'" +
            ", uploadedBy=" + getUploadedBy() +
            ", projects=" + getProjects() +
            "}";
    }
}
