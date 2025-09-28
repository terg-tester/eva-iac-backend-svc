package hr.terg.evag.service.criteria;

import hr.terg.evag.domain.enumeration.DeliverableFormat;
import hr.terg.evag.domain.enumeration.DeliverableType;
import hr.terg.evag.domain.enumeration.DeploymentStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link hr.terg.evag.domain.Deliverable} entity. This class is used
 * in {@link hr.terg.evag.web.rest.DeliverableResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /deliverables?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliverableCriteria implements Serializable, Criteria {

    /**
     * Class for filtering DeliverableType
     */
    public static class DeliverableTypeFilter extends Filter<DeliverableType> {

        public DeliverableTypeFilter() {}

        public DeliverableTypeFilter(DeliverableTypeFilter filter) {
            super(filter);
        }

        @Override
        public DeliverableTypeFilter copy() {
            return new DeliverableTypeFilter(this);
        }
    }

    /**
     * Class for filtering DeliverableFormat
     */
    public static class DeliverableFormatFilter extends Filter<DeliverableFormat> {

        public DeliverableFormatFilter() {}

        public DeliverableFormatFilter(DeliverableFormatFilter filter) {
            super(filter);
        }

        @Override
        public DeliverableFormatFilter copy() {
            return new DeliverableFormatFilter(this);
        }
    }

    /**
     * Class for filtering DeploymentStatus
     */
    public static class DeploymentStatusFilter extends Filter<DeploymentStatus> {

        public DeploymentStatusFilter() {}

        public DeploymentStatusFilter(DeploymentStatusFilter filter) {
            super(filter);
        }

        @Override
        public DeploymentStatusFilter copy() {
            return new DeploymentStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private DeliverableTypeFilter type;

    private DeliverableFormatFilter format;

    private DeploymentStatusFilter status;

    private StringFilter packagePath;

    private LongFilter packageSize;

    private StringFilter checksum;

    private InstantFilter createdDate;

    private InstantFilter lastModifiedDate;

    private StringFilter addendum;

    private LongFilter deploymentId;

    private LongFilter createdById;

    private LongFilter projectId;

    private Boolean distinct;

    public DeliverableCriteria() {}

    public DeliverableCriteria(DeliverableCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.type = other.optionalType().map(DeliverableTypeFilter::copy).orElse(null);
        this.format = other.optionalFormat().map(DeliverableFormatFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(DeploymentStatusFilter::copy).orElse(null);
        this.packagePath = other.optionalPackagePath().map(StringFilter::copy).orElse(null);
        this.packageSize = other.optionalPackageSize().map(LongFilter::copy).orElse(null);
        this.checksum = other.optionalChecksum().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.addendum = other.optionalAddendum().map(StringFilter::copy).orElse(null);
        this.deploymentId = other.optionalDeploymentId().map(LongFilter::copy).orElse(null);
        this.createdById = other.optionalCreatedById().map(LongFilter::copy).orElse(null);
        this.projectId = other.optionalProjectId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DeliverableCriteria copy() {
        return new DeliverableCriteria(this);
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

    public DeliverableTypeFilter getType() {
        return type;
    }

    public Optional<DeliverableTypeFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public DeliverableTypeFilter type() {
        if (type == null) {
            setType(new DeliverableTypeFilter());
        }
        return type;
    }

    public void setType(DeliverableTypeFilter type) {
        this.type = type;
    }

    public DeliverableFormatFilter getFormat() {
        return format;
    }

    public Optional<DeliverableFormatFilter> optionalFormat() {
        return Optional.ofNullable(format);
    }

    public DeliverableFormatFilter format() {
        if (format == null) {
            setFormat(new DeliverableFormatFilter());
        }
        return format;
    }

    public void setFormat(DeliverableFormatFilter format) {
        this.format = format;
    }

    public DeploymentStatusFilter getStatus() {
        return status;
    }

    public Optional<DeploymentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public DeploymentStatusFilter status() {
        if (status == null) {
            setStatus(new DeploymentStatusFilter());
        }
        return status;
    }

    public void setStatus(DeploymentStatusFilter status) {
        this.status = status;
    }

    public StringFilter getPackagePath() {
        return packagePath;
    }

    public Optional<StringFilter> optionalPackagePath() {
        return Optional.ofNullable(packagePath);
    }

    public StringFilter packagePath() {
        if (packagePath == null) {
            setPackagePath(new StringFilter());
        }
        return packagePath;
    }

    public void setPackagePath(StringFilter packagePath) {
        this.packagePath = packagePath;
    }

    public LongFilter getPackageSize() {
        return packageSize;
    }

    public Optional<LongFilter> optionalPackageSize() {
        return Optional.ofNullable(packageSize);
    }

    public LongFilter packageSize() {
        if (packageSize == null) {
            setPackageSize(new LongFilter());
        }
        return packageSize;
    }

    public void setPackageSize(LongFilter packageSize) {
        this.packageSize = packageSize;
    }

    public StringFilter getChecksum() {
        return checksum;
    }

    public Optional<StringFilter> optionalChecksum() {
        return Optional.ofNullable(checksum);
    }

    public StringFilter checksum() {
        if (checksum == null) {
            setChecksum(new StringFilter());
        }
        return checksum;
    }

    public void setChecksum(StringFilter checksum) {
        this.checksum = checksum;
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

    public LongFilter getDeploymentId() {
        return deploymentId;
    }

    public Optional<LongFilter> optionalDeploymentId() {
        return Optional.ofNullable(deploymentId);
    }

    public LongFilter deploymentId() {
        if (deploymentId == null) {
            setDeploymentId(new LongFilter());
        }
        return deploymentId;
    }

    public void setDeploymentId(LongFilter deploymentId) {
        this.deploymentId = deploymentId;
    }

    public LongFilter getCreatedById() {
        return createdById;
    }

    public Optional<LongFilter> optionalCreatedById() {
        return Optional.ofNullable(createdById);
    }

    public LongFilter createdById() {
        if (createdById == null) {
            setCreatedById(new LongFilter());
        }
        return createdById;
    }

    public void setCreatedById(LongFilter createdById) {
        this.createdById = createdById;
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
        final DeliverableCriteria that = (DeliverableCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(type, that.type) &&
            Objects.equals(format, that.format) &&
            Objects.equals(status, that.status) &&
            Objects.equals(packagePath, that.packagePath) &&
            Objects.equals(packageSize, that.packageSize) &&
            Objects.equals(checksum, that.checksum) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(addendum, that.addendum) &&
            Objects.equals(deploymentId, that.deploymentId) &&
            Objects.equals(createdById, that.createdById) &&
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
            format,
            status,
            packagePath,
            packageSize,
            checksum,
            createdDate,
            lastModifiedDate,
            addendum,
            deploymentId,
            createdById,
            projectId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliverableCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalFormat().map(f -> "format=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalPackagePath().map(f -> "packagePath=" + f + ", ").orElse("") +
            optionalPackageSize().map(f -> "packageSize=" + f + ", ").orElse("") +
            optionalChecksum().map(f -> "checksum=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalAddendum().map(f -> "addendum=" + f + ", ").orElse("") +
            optionalDeploymentId().map(f -> "deploymentId=" + f + ", ").orElse("") +
            optionalCreatedById().map(f -> "createdById=" + f + ", ").orElse("") +
            optionalProjectId().map(f -> "projectId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
