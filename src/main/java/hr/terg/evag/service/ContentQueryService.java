package hr.terg.evag.service;

import hr.terg.evag.domain.*; // for static metamodels
import hr.terg.evag.domain.Content;
import hr.terg.evag.repository.ContentRepository;
import hr.terg.evag.service.criteria.ContentCriteria;
import hr.terg.evag.service.dto.ContentDTO;
import hr.terg.evag.service.mapper.ContentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Content} entities in the database.
 * The main input is a {@link ContentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ContentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ContentQueryService extends QueryService<Content> {

    private static final Logger LOG = LoggerFactory.getLogger(ContentQueryService.class);

    private final ContentRepository contentRepository;

    private final ContentMapper contentMapper;

    public ContentQueryService(ContentRepository contentRepository, ContentMapper contentMapper) {
        this.contentRepository = contentRepository;
        this.contentMapper = contentMapper;
    }

    /**
     * Return a {@link Page} of {@link ContentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ContentDTO> findByCriteria(ContentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Content> specification = createSpecification(criteria);
        return contentRepository.findAll(specification, page).map(contentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ContentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Content> specification = createSpecification(criteria);
        return contentRepository.count(specification);
    }

    /**
     * Function to convert {@link ContentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Content> createSpecification(ContentCriteria criteria) {
        Specification<Content> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildSpecification(criteria.getId(), Content_.id),
                buildStringSpecification(criteria.getFileName(), Content_.fileName),
                buildRangeSpecification(criteria.getDateCreated(), Content_.dateCreated)
            );
        }
        return specification;
    }
}
