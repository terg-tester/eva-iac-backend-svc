package hr.terg.evag.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hr.terg.evag.domain.enumeration.DeploymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Deployment.
 */
@Entity
@Table(name = "deployment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Deployment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "deployment_date")
    private Instant deploymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeploymentStatus status;

    @Lob
    @Column(name = "logs")
    private String logs;

    @Size(max = 4000)
    @Column(name = "addendum", length = 4000)
    private String addendum;

    @ManyToOne(fetch = FetchType.LAZY)
    private User deployedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "deployments", "createdBy", "project" }, allowSetters = true)
    private Deliverable deliverable;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Deployment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDeploymentDate() {
        return this.deploymentDate;
    }

    public Deployment deploymentDate(Instant deploymentDate) {
        this.setDeploymentDate(deploymentDate);
        return this;
    }

    public void setDeploymentDate(Instant deploymentDate) {
        this.deploymentDate = deploymentDate;
    }

    public DeploymentStatus getStatus() {
        return this.status;
    }

    public Deployment status(DeploymentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(DeploymentStatus status) {
        this.status = status;
    }

    public String getLogs() {
        return this.logs;
    }

    public Deployment logs(String logs) {
        this.setLogs(logs);
        return this;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public String getAddendum() {
        return this.addendum;
    }

    public Deployment addendum(String addendum) {
        this.setAddendum(addendum);
        return this;
    }

    public void setAddendum(String addendum) {
        this.addendum = addendum;
    }

    public User getDeployedBy() {
        return this.deployedBy;
    }

    public void setDeployedBy(User user) {
        this.deployedBy = user;
    }

    public Deployment deployedBy(User user) {
        this.setDeployedBy(user);
        return this;
    }

    public Deliverable getDeliverable() {
        return this.deliverable;
    }

    public void setDeliverable(Deliverable deliverable) {
        this.deliverable = deliverable;
    }

    public Deployment deliverable(Deliverable deliverable) {
        this.setDeliverable(deliverable);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deployment)) {
            return false;
        }
        return getId() != null && getId().equals(((Deployment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Deployment{" +
            "id=" + getId() +
            ", deploymentDate='" + getDeploymentDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", logs='" + getLogs() + "'" +
            ", addendum='" + getAddendum() + "'" +
            "}";
    }
}
