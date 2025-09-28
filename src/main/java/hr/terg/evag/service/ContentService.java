package hr.terg.evag.service;

import hr.terg.evag.domain.Content;
import hr.terg.evag.repository.ContentRepository;
import hr.terg.evag.service.dto.ContentDTO;
import hr.terg.evag.service.mapper.ContentMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link hr.terg.evag.domain.Content}.
 */
@Service
@Transactional
public class ContentService {

    private static final Logger LOG = LoggerFactory.getLogger(ContentService.class);

    private final ContentRepository contentRepository;

    private final ContentMapper contentMapper;

    public ContentService(ContentRepository contentRepository, ContentMapper contentMapper) {
        this.contentRepository = contentRepository;
        this.contentMapper = contentMapper;
    }

    /**
     * Save a content.
     *
     * @param contentDTO the entity to save.
     * @return the persisted entity.
     */
    public ContentDTO save(ContentDTO contentDTO) {
        LOG.debug("Request to save Content : {}", contentDTO);
        Content content = contentMapper.toEntity(contentDTO);
        content = contentRepository.save(content);
        return contentMapper.toDto(content);
    }

    /**
     * Update a content.
     *
     * @param contentDTO the entity to save.
     * @return the persisted entity.
     */
    public ContentDTO update(ContentDTO contentDTO) {
        LOG.debug("Request to update Content : {}", contentDTO);
        Content content = contentMapper.toEntity(contentDTO);
        content = contentRepository.save(content);
        return contentMapper.toDto(content);
    }

    /**
     * Partially update a content.
     *
     * @param contentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ContentDTO> partialUpdate(ContentDTO contentDTO) {
        LOG.debug("Request to partially update Content : {}", contentDTO);

        return contentRepository
            .findById(contentDTO.getId())
            .map(existingContent -> {
                contentMapper.partialUpdate(existingContent, contentDTO);

                return existingContent;
            })
            .map(contentRepository::save)
            .map(contentMapper::toDto);
    }

    /**
     * Get one content by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ContentDTO> findOne(UUID id) {
        LOG.debug("Request to get Content : {}", id);
        return contentRepository.findById(id).map(contentMapper::toDto);
    }

    /**
     * Delete the content by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        LOG.debug("Request to delete Content : {}", id);
        contentRepository.deleteById(id);
    }
}
