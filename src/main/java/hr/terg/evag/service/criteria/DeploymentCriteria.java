package hr.terg.evag.service.criteria;

import hr.terg.evag.domain.enumeration.DeploymentStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link hr.terg.evag.domain.Deployment} entity. This class is used
 * in {@link hr.terg.evag.web.rest.DeploymentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /deployments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeploymentCriteria implements Serializable, Criteria {

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

    private InstantFilter deploymentDate;

    private DeploymentStatusFilter status;

    private StringFilter addendum;

    private LongFilter deployedById;

    private LongFilter deliverableId;

    private Boolean distinct;

    public DeploymentCriteria() {}

    public DeploymentCriteria(DeploymentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.deploymentDate = other.optionalDeploymentDate().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(DeploymentStatusFilter::copy).orElse(null);
        this.addendum = other.optionalAddendum().map(StringFilter::copy).orElse(null);
        this.deployedById = other.optionalDeployedById().map(LongFilter::copy).orElse(null);
        this.deliverableId = other.optionalDeliverableId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DeploymentCriteria copy() {
        return new DeploymentCriteria(this);
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

    public InstantFilter getDeploymentDate() {
        return deploymentDate;
    }

    public Optional<InstantFilter> optionalDeploymentDate() {
        return Optional.ofNullable(deploymentDate);
    }

    public InstantFilter deploymentDate() {
        if (deploymentDate == null) {
            setDeploymentDate(new InstantFilter());
        }
        return deploymentDate;
    }

    public void setDeploymentDate(InstantFilter deploymentDate) {
        this.deploymentDate = deploymentDate;
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

    public LongFilter getDeployedById() {
        return deployedById;
    }

    public Optional<LongFilter> optionalDeployedById() {
        return Optional.ofNullable(deployedById);
    }

    public LongFilter deployedById() {
        if (deployedById == null) {
            setDeployedById(new LongFilter());
        }
        return deployedById;
    }

    public void setDeployedById(LongFilter deployedById) {
        this.deployedById = deployedById;
    }

    public LongFilter getDeliverableId() {
        return deliverableId;
    }

    public Optional<LongFilter> optionalDeliverableId() {
        return Optional.ofNullable(deliverableId);
    }

    public LongFilter deliverableId() {
        if (deliverableId == null) {
            setDeliverableId(new LongFilter());
        }
        return deliverableId;
    }

    public void setDeliverableId(LongFilter deliverableId) {
        this.deliverableId = deliverableId;
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
        final DeploymentCriteria that = (DeploymentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(deploymentDate, that.deploymentDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(addendum, that.addendum) &&
            Objects.equals(deployedById, that.deployedById) &&
            Objects.equals(deliverableId, that.deliverableId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deploymentDate, status, addendum, deployedById, deliverableId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeploymentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDeploymentDate().map(f -> "deploymentDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalAddendum().map(f -> "addendum=" + f + ", ").orElse("") +
            optionalDeployedById().map(f -> "deployedById=" + f + ", ").orElse("") +
            optionalDeliverableId().map(f -> "deliverableId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
