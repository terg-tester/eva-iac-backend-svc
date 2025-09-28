package hr.terg.evag.service.criteria;

import hr.terg.evag.domain.enumeration.ArtifactStatus;
import hr.terg.evag.domain.enumeration.ArtifactType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link hr.terg.evag.domain.Artifact} entity. This class is used
 * in {@link hr.terg.evag.web.rest.ArtifactResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /artifacts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ArtifactCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ArtifactType
     */
    public static class ArtifactTypeFilter extends Filter<ArtifactType> {

        public ArtifactTypeFilter() {}

        public ArtifactTypeFilter(ArtifactTypeFilter filter) {
            super(filter);
        }

        @Override
        public ArtifactTypeFilter copy() {
            return new ArtifactTypeFilter(this);
        }
    }

    /**
     * Class for filtering ArtifactStatus
     */
    public static class ArtifactStatusFilter extends Filter<ArtifactStatus> {

        public ArtifactStatusFilter() {}

        public ArtifactStatusFilter(ArtifactStatusFilter filter) {
            super(filter);
        }

        @Override
        public ArtifactStatusFilter copy() {
            return new ArtifactStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private ArtifactTypeFilter type;

    private StringFilter link;

    private ArtifactStatusFilter status;

    private LongFilter fileSize;

    private InstantFilter createdDate;

    private InstantFilter lastModifiedDate;

    private StringFilter addendum;

    private LongFilter uploadedById;

    private LongFilter projectId;

    private Boolean distinct;

    public ArtifactCriteria() {}

    public ArtifactCriteria(ArtifactCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.type = other.optionalType().map(ArtifactTypeFilter::copy).orElse(null);
        this.link = other.optionalLink().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ArtifactStatusFilter::copy).orElse(null);
        this.fileSize = other.optionalFileSize().map(LongFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.addendum = other.optionalAddendum().map(StringFilter::copy).orElse(null);
        this.uploadedById = other.optionalUploadedById().map(LongFilter::copy).orElse(null);
        this.projectId = other.optionalProjectId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ArtifactCriteria copy() {
        return new ArtifactCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public ArtifactTypeFilter getType() {
        return type;
    }

    public Optional<ArtifactTypeFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public ArtifactTypeFilter type() {
        if (type == null) {
            setType(new ArtifactTypeFilter());
        }
        return type;
    }

    public void setType(ArtifactTypeFilter type) {
        this.type = type;
    }

    public StringFilter getLink() {
        return link;
    }

    public Optional<StringFilter> optionalLink() {
        return Optional.ofNullable(link);
    }

    public StringFilter link() {
        if (link == null) {
            setLink(new StringFilter());
        }
        return link;
    }

    public void setLink(StringFilter link) {
        this.link = link;
    }

    public ArtifactStatusFilter getStatus() {
        return status;
    }

    public Optional<ArtifactStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ArtifactStatusFilter status() {
        if (status == null) {
            setStatus(new ArtifactStatusFilter());
        }
        return status;
    }

    public void setStatus(ArtifactStatusFilter status) {
        this.status = status;
    }

    public LongFilter getFileSize() {
        return fileSize;
    }

    public Optional<LongFilter> optionalFileSize() {
        return Optional.ofNullable(fileSize);
    }

    public LongFilter fileSize() {
        if (fileSize == null) {
            setFileSize(new LongFilter());
        }
        return fileSize;
    }

    public void setFileSize(LongFilter fileSize) {
        this.fileSize = fileSize;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public Optional<InstantFilter> optionalCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            setCreatedDate(new InstantFilter());
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Optional<InstantFilter> optionalLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            setLastModifiedDate(new InstantFilter());
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public StringFilter getAddendum() {
        return addendum;
    }

    public Optional<StringFilter> optionalAddendum() {
        return Optional.ofNullable(addendum);
    }

    public StringFilter addendum() {
        if (addendum == null) {
            setAddendum(new StringFilter());
        }
        return addendum;
    }

    public void setAddendum(StringFilter addendum) {
        this.addendum = addendum;
    }

    public LongFilter getUploadedById() {
        return uploadedById;
    }

    public Optional<LongFilter> optionalUploadedById() {
        return Optional.ofNullable(uploadedById);
    }

    public LongFilter uploadedById() {
        if (uploadedById == null) {
            setUploadedById(new LongFilter());
        }
        return uploadedById;
    }

    public void setUploadedById(LongFilter uploadedById) {
        this.uploadedById = uploadedById;
    }

    public LongFilter getProjectId() {
        return projectId;
    }

    public Optional<LongFilter> optionalProjectId() {
        return Optional.ofNullable(projectId);
    }

    public LongFilter projectId() {
        if (projectId == null) {
            setProjectId(new LongFilter());
        }
        return projectId;
    }

    public void setProjectId(LongFilter projectId) {
        this.projectId = projectId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ArtifactCriteria that = (ArtifactCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(type, that.type) &&
            Objects.equals(link, that.link) &&
            Objects.equals(status, that.status) &&
            Objects.equals(fileSize, that.fileSize) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(addendum, that.addendum) &&
            Objects.equals(uploadedById, that.uploadedById) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            description,
            type,
            link,
            status,
            fileSize,
            createdDate,
            lastModifiedDate,
            addendum,
            uploadedById,
            projectId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ArtifactCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalLink().map(f -> "link=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalFileSize().map(f -> "fileSize=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalAddendum().map(f -> "addendum=" + f + ", ").orElse("") +
            optionalUploadedById().map(f -> "uploadedById=" + f + ", ").orElse("") +
            optionalProjectId().map(f -> "projectId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
