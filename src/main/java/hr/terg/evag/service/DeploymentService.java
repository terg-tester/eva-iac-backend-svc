package hr.terg.evag.service;

import hr.terg.evag.service.dto.DeploymentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link hr.terg.evag.domain.Deployment}.
 */
public interface DeploymentService {
    /**
     * Save a deployment.
     *
     * @param deploymentDTO the entity to save.
     * @return the persisted entity.
     */
    DeploymentDTO save(DeploymentDTO deploymentDTO);

    /**
     * Updates a deployment.
     *
     * @param deploymentDTO the entity to update.
     * @return the persisted entity.
     */
    DeploymentDTO update(DeploymentDTO deploymentDTO);

    /**
     * Partially updates a deployment.
     *
     * @param deploymentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DeploymentDTO> partialUpdate(DeploymentDTO deploymentDTO);

    /**
     * Get all the deployments with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DeploymentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" deployment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DeploymentDTO> findOne(Long id);

    /**
     * Delete the "id" deployment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
