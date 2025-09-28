package hr.terg.evag.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hr.terg.evag.domain.enumeration.DeliverableFormat;
import hr.terg.evag.domain.enumeration.DeliverableType;
import hr.terg.evag.domain.enumeration.DeploymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Deliverable.
 */
@Entity
@Table(name = "deliverable")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Deliverable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DeliverableType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "format")
    private DeliverableFormat format;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeploymentStatus status;

    @Size(max = 500)
    @Column(name = "package_path", length = 500)
    private String packagePath;

    @Column(name = "package_size")
    private Long packageSize;

    @Size(max = 128)
    @Column(name = "checksum", length = 128)
    private String checksum;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @Size(max = 4000)
    @Column(name = "addendum", length = 4000)
    private String addendum;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "deliverable")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "deployedBy", "deliverable" }, allowSetters = true)
    private Set<Deployment> deployments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "deliverables", "createdBy", "artifacts" }, allowSetters = true)
    private Project project;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Deliverable id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Deliverable name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Deliverable description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DeliverableType getType() {
        return this.type;
    }

    public Deliverable type(DeliverableType type) {
        this.setType(type);
        return this;
    }

    public void setType(DeliverableType type) {
        this.type = type;
    }

    public DeliverableFormat getFormat() {
        return this.format;
    }

    public Deliverable format(DeliverableFormat format) {
        this.setFormat(format);
        return this;
    }

    public void setFormat(DeliverableFormat format) {
        this.format = format;
    }

    public DeploymentStatus getStatus() {
        return this.status;
    }

    public Deliverable status(DeploymentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(DeploymentStatus status) {
        this.status = status;
    }

    public String getPackagePath() {
        return this.packagePath;
    }

    public Deliverable packagePath(String packagePath) {
        this.setPackagePath(packagePath);
        return this;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public Long getPackageSize() {
        return this.packageSize;
    }

    public Deliverable packageSize(Long packageSize) {
        this.setPackageSize(packageSize);
        return this;
    }

    public void setPackageSize(Long packageSize) {
        this.packageSize = packageSize;
    }

    public String getChecksum() {
        return this.checksum;
    }

    public Deliverable checksum(String checksum) {
        this.setChecksum(checksum);
        return this;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Deliverable createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Deliverable lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getAddendum() {
        return this.addendum;
    }

    public Deliverable addendum(String addendum) {
        this.setAddendum(addendum);
        return this;
    }

    public void setAddendum(String addendum) {
        this.addendum = addendum;
    }

    public Set<Deployment> getDeployments() {
        return this.deployments;
    }

    public void setDeployments(Set<Deployment> deployments) {
        if (this.deployments != null) {
            this.deployments.forEach(i -> i.setDeliverable(null));
        }
        if (deployments != null) {
            deployments.forEach(i -> i.setDeliverable(this));
        }
        this.deployments = deployments;
    }

    public Deliverable deployments(Set<Deployment> deployments) {
        this.setDeployments(deployments);
        return this;
    }

    public Deliverable addDeployment(Deployment deployment) {
        this.deployments.add(deployment);
        deployment.setDeliverable(this);
        return this;
    }

    public Deliverable removeDeployment(Deployment deployment) {
        this.deployments.remove(deployment);
        deployment.setDeliverable(null);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public Deliverable createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Deliverable project(Project project) {
        this.setProject(project);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deliverable)) {
            return false;
        }
        return getId() != null && getId().equals(((Deliverable) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Deliverable{" +
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
            "}";
    }
}
