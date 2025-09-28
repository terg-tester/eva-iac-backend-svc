package hr.terg.evag.service.dto;

import hr.terg.evag.domain.enumeration.DeliverableFormat;
import hr.terg.evag.domain.enumeration.DeliverableType;
import hr.terg.evag.domain.enumeration.DeploymentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link hr.terg.evag.domain.Deliverable} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliverableDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private DeliverableType type;

    private DeliverableFormat format;

    private DeploymentStatus status;

    @Size(max = 500)
    private String packagePath;

    private Long packageSize;

    @Size(max = 128)
    private String checksum;

    private Instant createdDate;

    private Instant lastModifiedDate;

    @Size(max = 4000)
    private String addendum;

    private UserDTO createdBy;

    private ProjectDTO project;

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

    public DeliverableType getType() {
        return type;
    }

    public void setType(DeliverableType type) {
        this.type = type;
    }

    public DeliverableFormat getFormat() {
        return format;
    }

    public void setFormat(DeliverableFormat format) {
        this.format = format;
    }

    public DeploymentStatus getStatus() {
        return status;
    }

    public void setStatus(DeploymentStatus status) {
        this.status = status;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public Long getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(Long packageSize) {
        this.packageSize = packageSize;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
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

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliverableDTO)) {
            return false;
        }

        DeliverableDTO deliverableDTO = (DeliverableDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, deliverableDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliverableDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", type='" + getType() + "'" +
            ", format='" + getFormat() + "'" +
            ", status='" + getStatus() + "'" +
            ", packagePath='" + getPackagePath() + "'" +
            ", packageSize=" + getPackageSize() +
            ", checksum='" + getChecksum() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", addendum='" + getAddendum() + "'" +
            ", createdBy=" + getCreatedBy() +
            ", project=" + getProject() +
            "}";
    }
}
