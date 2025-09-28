package hr.terg.evag.service;

import hr.terg.evag.domain.*; // for static metamodels
import hr.terg.evag.domain.Deliverable;
import hr.terg.evag.repository.DeliverableRepository;
import hr.terg.evag.service.criteria.DeliverableCriteria;
import hr.terg.evag.service.dto.DeliverableDTO;
import hr.terg.evag.service.mapper.DeliverableMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Deliverable} entities in the database.
 * The main input is a {@link DeliverableCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DeliverableDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DeliverableQueryService extends QueryService<Deliverable> {

    private static final Logger LOG = LoggerFactory.getLogger(DeliverableQueryService.class);

    private final DeliverableRepository deliverableRepository;

    private final DeliverableMapper deliverableMapper;

    public DeliverableQueryService(DeliverableRepository deliverableRepository, DeliverableMapper deliverableMapper) {
        this.deliverableRepository = deliverableRepository;
        this.deliverableMapper = deliverableMapper;
    }

    /**
     * Return a {@link Page} of {@link DeliverableDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DeliverableDTO> findByCriteria(DeliverableCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Deliverable> specification = createSpecification(criteria);
        return deliverableRepository.findAll(specification, page).map(deliverableMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DeliverableCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Deliverable> specification = createSpecification(criteria);
        return deliverableRepository.count(specification);
    }

    /**
     * Function to convert {@link DeliverableCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Deliverable> createSpecification(DeliverableCriteria criteria) {
        Specification<Deliverable> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Deliverable_.id),
                buildStringSpecification(criteria.getName(), Deliverable_.name),
                buildStringSpecification(criteria.getDescription(), Deliverable_.description),
                buildSpecification(criteria.getType(), Deliverable_.type),
                buildSpecification(criteria.getFormat(), Deliverable_.format),
                buildSpecification(criteria.getStatus(), Deliverable_.status),
                buildStringSpecification(criteria.getPackagePath(), Deliverable_.packagePath),
                buildRangeSpecification(criteria.getPackageSize(), Deliverable_.packageSize),
                buildStringSpecification(criteria.getChecksum(), Deliverable_.checksum),
                buildRangeSpecification(criteria.getCreatedDate(), Deliverable_.createdDate),
                buildRangeSpecification(criteria.getLastModifiedDate(), Deliverable_.lastModifiedDate),
                buildStringSpecification(criteria.getAddendum(), Deliverable_.addendum),
                buildSpecification(criteria.getDeploymentId(), root ->
                    root.join(Deliverable_.deployments, JoinType.LEFT).get(Deployment_.id)
                ),
                buildSpecification(criteria.getCreatedById(), root -> root.join(Deliverable_.createdBy, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getProjectId(), root -> root.join(Deliverable_.project, JoinType.LEFT).get(Project_.id))
            );
        }
        return specification;
    }
}
