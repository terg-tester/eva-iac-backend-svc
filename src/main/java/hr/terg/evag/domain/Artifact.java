package hr.terg.evag.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hr.terg.evag.domain.enumeration.ArtifactStatus;
import hr.terg.evag.domain.enumeration.ArtifactType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Artifact.
 */
@Entity
@Table(name = "artifact")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Artifact implements Serializable {

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
    private ArtifactType type;

    @Size(max = 500)
    @Column(name = "link", length = 500)
    private String link;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ArtifactStatus status;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @Size(max = 4000)
    @Column(name = "addendum", length = 4000)
    private String addendum;

    @ManyToOne(fetch = FetchType.LAZY)
    private User uploadedBy;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "artifacts")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "deliverables", "createdBy", "artifacts" }, allowSetters = true)
    private Set<Project> projects = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Artifact id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Artifact name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Artifact description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArtifactType getType() {
        return this.type;
    }

    public Artifact type(ArtifactType type) {
        this.setType(type);
        return this;
    }

    public void setType(ArtifactType type) {
        this.type = type;
    }

    public String getLink() {
        return this.link;
    }

    public Artifact link(String link) {
        this.setLink(link);
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ArtifactStatus getStatus() {
        return this.status;
    }

    public Artifact status(ArtifactStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ArtifactStatus status) {
        this.status = status;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public Artifact fileSize(Long fileSize) {
        this.setFileSize(fileSize);
        return this;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Artifact createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Artifact lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getAddendum() {
        return this.addendum;
    }

    public Artifact addendum(String addendum) {
        this.setAddendum(addendum);
        return this;
    }

    public void setAddendum(String addendum) {
        this.addendum = addendum;
    }

    public User getUploadedBy() {
        return this.uploadedBy;
    }

    public void setUploadedBy(User user) {
        this.uploadedBy = user;
    }

    public Artifact uploadedBy(User user) {
        this.setUploadedBy(user);
        return this;
    }

    public Set<Project> getProjects() {
        return this.projects;
    }

    public void setProjects(Set<Project> projects) {
        if (this.projects != null) {
            this.projects.forEach(i -> i.removeArtifact(this));
        }
        if (projects != null) {
            projects.forEach(i -> i.addArtifact(this));
        }
        this.projects = projects;
    }

    public Artifact projects(Set<Project> projects) {
        this.setProjects(projects);
        return this;
    }

    public Artifact addProject(Project project) {
        this.projects.add(project);
        project.getArtifacts().add(this);
        return this;
    }

    public Artifact removeProject(Project project) {
        this.projects.remove(project);
        project.getArtifacts().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Artifact)) {
            return false;
        }
        return getId() != null && getId().equals(((Artifact) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Artifact{" +
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
            "}";
    }
}
