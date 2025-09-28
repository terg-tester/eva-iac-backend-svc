package hr.terg.evag.service;

import hr.terg.evag.domain.*; // for static metamodels
import hr.terg.evag.domain.Project;
import hr.terg.evag.repository.ProjectRepository;
import hr.terg.evag.service.criteria.ProjectCriteria;
import hr.terg.evag.service.dto.ProjectDTO;
import hr.terg.evag.service.mapper.ProjectMapper;
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
 * Service for executing complex queries for {@link Project} entities in the database.
 * The main input is a {@link ProjectCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ProjectDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectQueryService extends QueryService<Project> {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectQueryService.class);

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    public ProjectQueryService(ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    /**
     * Return a {@link Page} of {@link ProjectDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectDTO> findByCriteria(ProjectCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Project> specification = createSpecification(criteria);
        return projectRepository.fetchBagRelationships(projectRepository.findAll(specification, page)).map(projectMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Project> specification = createSpecification(criteria);
        return projectRepository.count(specification);
    }

    /**
     * Function to convert {@link ProjectCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Project> createSpecification(ProjectCriteria criteria) {
        Specification<Project> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Project_.id),
                buildStringSpecification(criteria.getName(), Project_.name),
                buildStringSpecification(criteria.getDescription(), Project_.description),
                buildSpecification(criteria.getStatus(), Project_.status),
                buildSpecification(criteria.getPriority(), Project_.priority),
                buildRangeSpecification(criteria.getStartDate(), Project_.startDate),
                buildRangeSpecification(criteria.getEndDate(), Project_.endDate),
                buildRangeSpecification(criteria.getCreatedDate(), Project_.createdDate),
                buildRangeSpecification(criteria.getLastModifiedDate(), Project_.lastModifiedDate),
                buildStringSpecification(criteria.getAddendum(), Project_.addendum),
                buildSpecification(criteria.getDeliverableId(), root -> root.join(Project_.deliverables, JoinType.LEFT).get(Deliverable_.id)
                ),
                buildSpecification(criteria.getCreatedById(), root -> root.join(Project_.createdBy, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getArtifactId(), root -> root.join(Project_.artifacts, JoinType.LEFT).get(Artifact_.id))
            );
        }
        return specification;
    }
}
