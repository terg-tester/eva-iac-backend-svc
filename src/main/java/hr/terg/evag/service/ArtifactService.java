package hr.terg.evag.service;

import hr.terg.evag.service.dto.ArtifactDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link hr.terg.evag.domain.Artifact}.
 */
public interface ArtifactService {
    /**
     * Save a artifact.
     *
     * @param artifactDTO the entity to save.
     * @return the persisted entity.
     */
    ArtifactDTO save(ArtifactDTO artifactDTO);

    /**
     * Updates a artifact.
     *
     * @param artifactDTO the entity to update.
     * @return the persisted entity.
     */
    ArtifactDTO update(ArtifactDTO artifactDTO);

    /**
     * Partially updates a artifact.
     *
     * @param artifactDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ArtifactDTO> partialUpdate(ArtifactDTO artifactDTO);

    /**
     * Get the "id" artifact.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ArtifactDTO> findOne(Long id);

    /**
     * Delete the "id" artifact.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
