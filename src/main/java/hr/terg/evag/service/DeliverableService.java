package hr.terg.evag.service;

import hr.terg.evag.service.dto.DeliverableDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link hr.terg.evag.domain.Deliverable}.
 */
public interface DeliverableService {
    /**
     * Save a deliverable.
     *
     * @param deliverableDTO the entity to save.
     * @return the persisted entity.
     */
    DeliverableDTO save(DeliverableDTO deliverableDTO);

    /**
     * Updates a deliverable.
     *
     * @param deliverableDTO the entity to update.
     * @return the persisted entity.
     */
    DeliverableDTO update(DeliverableDTO deliverableDTO);

    /**
     * Partially updates a deliverable.
     *
     * @param deliverableDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DeliverableDTO> partialUpdate(DeliverableDTO deliverableDTO);

    /**
     * Get all the deliverables with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DeliverableDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" deliverable.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DeliverableDTO> findOne(Long id);

    /**
     * Delete the "id" deliverable.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
