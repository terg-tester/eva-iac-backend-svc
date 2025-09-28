package hr.terg.evag.service.dto;

import hr.terg.evag.domain.enumeration.DeploymentStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link hr.terg.evag.domain.Deployment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeploymentDTO implements Serializable {

    private Long id;

    private Instant deploymentDate;

    private DeploymentStatus status;

    @Lob
    private String logs;

    @Size(max = 4000)
    private String addendum;

    private UserDTO deployedBy;

    private DeliverableDTO deliverable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDeploymentDate() {
        return deploymentDate;
    }

    public void setDeploymentDate(Instant deploymentDate) {
        this.deploymentDate = deploymentDate;
    }

    public DeploymentStatus getStatus() {
        return status;
    }

    public void setStatus(DeploymentStatus status) {
        this.status = status;
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public String getAddendum() {
        return addendum;
    }

    public void setAddendum(String addendum) {
        this.addendum = addendum;
    }

    public UserDTO getDeployedBy() {
        return deployedBy;
    }

    public void setDeployedBy(UserDTO deployedBy) {
        this.deployedBy = deployedBy;
    }

    public DeliverableDTO getDeliverable() {
        return deliverable;
    }

    public void setDeliverable(DeliverableDTO deliverable) {
        this.deliverable = deliverable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeploymentDTO)) {
            return false;
        }

        DeploymentDTO deploymentDTO = (DeploymentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, deploymentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeploymentDTO{" +
            "id=" + getId() +
            ", deploymentDate='" + getDeploymentDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", logs='" + getLogs() + "'" +
            ", addendum='" + getAddendum() + "'" +
            ", deployedBy=" + getDeployedBy() +
            ", deliverable=" + getDeliverable() +
            "}";
    }
}
