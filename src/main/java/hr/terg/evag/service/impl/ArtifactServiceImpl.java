package hr.terg.evag.service.impl;

import hr.terg.evag.domain.Artifact;
import hr.terg.evag.repository.ArtifactRepository;
import hr.terg.evag.service.ArtifactService;
import hr.terg.evag.service.dto.ArtifactDTO;
import hr.terg.evag.service.mapper.ArtifactMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link hr.terg.evag.domain.Artifact}.
 */
@Service
@Transactional
public class ArtifactServiceImpl implements ArtifactService {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactServiceImpl.class);

    private final ArtifactRepository artifactRepository;

    private final ArtifactMapper artifactMapper;

    public ArtifactServiceImpl(ArtifactRepository artifactRepository, ArtifactMapper artifactMapper) {
        this.artifactRepository = artifactRepository;
        this.artifactMapper = artifactMapper;
    }

    @Override
    public ArtifactDTO save(ArtifactDTO artifactDTO) {
        LOG.debug("Request to save Artifact : {}", artifactDTO);
        Artifact artifact = artifactMapper.toEntity(artifactDTO);
        artifact = artifactRepository.save(artifact);
        return artifactMapper.toDto(artifact);
    }

    @Override
    public ArtifactDTO update(ArtifactDTO artifactDTO) {
        LOG.debug("Request to update Artifact : {}", artifactDTO);
        Artifact artifact = artifactMapper.toEntity(artifactDTO);
        artifact = artifactRepository.save(artifact);
        return artifactMapper.toDto(artifact);
    }

    @Override
    public Optional<ArtifactDTO> partialUpdate(ArtifactDTO artifactDTO) {
        LOG.debug("Request to partially update Artifact : {}", artifactDTO);

        return artifactRepository
            .findById(artifactDTO.getId())
            .map(existingArtifact -> {
                artifactMapper.partialUpdate(existingArtifact, artifactDTO);

                return existingArtifact;
            })
            .map(artifactRepository::save)
            .map(artifactMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArtifactDTO> findOne(Long id) {
        LOG.debug("Request to get Artifact : {}", id);
        return artifactRepository.findById(id).map(artifactMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Artifact : {}", id);
        artifactRepository.deleteById(id);
    }
}
