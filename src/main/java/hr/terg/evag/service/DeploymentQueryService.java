package hr.terg.evag.service;

import hr.terg.evag.domain.*; // for static metamodels
import hr.terg.evag.domain.Deployment;
import hr.terg.evag.repository.DeploymentRepository;
import hr.terg.evag.service.criteria.DeploymentCriteria;
import hr.terg.evag.service.dto.DeploymentDTO;
import hr.terg.evag.service.mapper.DeploymentMapper;
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
 * Service for executing complex queries for {@link Deployment} entities in the database.
 * The main input is a {@link DeploymentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DeploymentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DeploymentQueryService extends QueryService<Deployment> {

    private static final Logger LOG = LoggerFactory.getLogger(DeploymentQueryService.class);

    private final DeploymentRepository deploymentRepository;

    private final DeploymentMapper deploymentMapper;

    public DeploymentQueryService(DeploymentRepository deploymentRepository, DeploymentMapper deploymentMapper) {
        this.deploymentRepository = deploymentRepository;
        this.deploymentMapper = deploymentMapper;
    }

    /**
     * Return a {@link Page} of {@link DeploymentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DeploymentDTO> findByCriteria(DeploymentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Deployment> specification = createSpecification(criteria);
        return deploymentRepository.findAll(specification, page).map(deploymentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DeploymentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Deployment> specification = createSpecification(criteria);
        return deploymentRepository.count(specification);
    }

    /**
     * Function to convert {@link DeploymentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Deployment> createSpecification(DeploymentCriteria criteria) {
        Specification<Deployment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Deployment_.id),
                buildRangeSpecification(criteria.getDeploymentDate(), Deployment_.deploymentDate),
                buildSpecification(criteria.getStatus(), Deployment_.status),
                buildStringSpecification(criteria.getAddendum(), Deployment_.addendum),
                buildSpecification(criteria.getDeployedById(), root -> root.join(Deployment_.deployedBy, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getDeliverableId(), root ->
                    root.join(Deployment_.deliverable, JoinType.LEFT).get(Deliverable_.id)
                )
            );
        }
        return specification;
    }
}
