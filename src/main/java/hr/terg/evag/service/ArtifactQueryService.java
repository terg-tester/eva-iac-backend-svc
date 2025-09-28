package hr.terg.evag.service;

import hr.terg.evag.domain.*; // for static metamodels
import hr.terg.evag.domain.Artifact;
import hr.terg.evag.repository.ArtifactRepository;
import hr.terg.evag.service.criteria.ArtifactCriteria;
import hr.terg.evag.service.dto.ArtifactDTO;
import hr.terg.evag.service.mapper.ArtifactMapper;
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
 * Service for executing complex queries for {@link Artifact} entities in the database.
 * The main input is a {@link ArtifactCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ArtifactDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ArtifactQueryService extends QueryService<Artifact> {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactQueryService.class);

    private final ArtifactRepository artifactRepository;

    private final ArtifactMapper artifactMapper;

    public ArtifactQueryService(ArtifactRepository artifactRepository, ArtifactMapper artifactMapper) {
        this.artifactRepository = artifactRepository;
        this.artifactMapper = artifactMapper;
    }

    /**
     * Return a {@link Page} of {@link ArtifactDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ArtifactDTO> findByCriteria(ArtifactCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Artifact> specification = createSpecification(criteria);
        return artifactRepository.findAll(specification, page).map(artifactMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ArtifactCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Artifact> specification = createSpecification(criteria);
        return artifactRepository.count(specification);
    }

    /**
     * Function to convert {@link ArtifactCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Artifact> createSpecification(ArtifactCriteria criteria) {
        Specification<Artifact> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Artifact_.id),
                buildStringSpecification(criteria.getName(), Artifact_.name),
                buildStringSpecification(criteria.getDescription(), Artifact_.description),
                buildSpecification(criteria.getType(), Artifact_.type),
                buildStringSpecification(criteria.getLink(), Artifact_.link),
                buildSpecification(criteria.getStatus(), Artifact_.status),
                buildRangeSpecification(criteria.getFileSize(), Artifact_.fileSize),
                buildRangeSpecification(criteria.getCreatedDate(), Artifact_.createdDate),
                buildRangeSpecification(criteria.getLastModifiedDate(), Artifact_.lastModifiedDate),
                buildStringSpecification(criteria.getAddendum(), Artifact_.addendum),
                buildSpecification(criteria.getUploadedById(), root -> root.join(Artifact_.uploadedBy, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getProjectId(), root -> root.join(Artifact_.projects, JoinType.LEFT).get(Project_.id))
            );
        }
        return specification;
    }
}
