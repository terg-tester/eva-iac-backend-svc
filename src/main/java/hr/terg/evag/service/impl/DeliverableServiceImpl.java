package hr.terg.evag.service.impl;

import hr.terg.evag.domain.Deliverable;
import hr.terg.evag.repository.DeliverableRepository;
import hr.terg.evag.service.DeliverableService;
import hr.terg.evag.service.dto.DeliverableDTO;
import hr.terg.evag.service.mapper.DeliverableMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link hr.terg.evag.domain.Deliverable}.
 */
@Service
@Transactional
public class DeliverableServiceImpl implements DeliverableService {

    private static final Logger LOG = LoggerFactory.getLogger(DeliverableServiceImpl.class);

    private final DeliverableRepository deliverableRepository;

    private final DeliverableMapper deliverableMapper;

    public DeliverableServiceImpl(DeliverableRepository deliverableRepository, DeliverableMapper deliverableMapper) {
        this.deliverableRepository = deliverableRepository;
        this.deliverableMapper = deliverableMapper;
    }

    @Override
    public DeliverableDTO save(DeliverableDTO deliverableDTO) {
        LOG.debug("Request to save Deliverable : {}", deliverableDTO);
        Deliverable deliverable = deliverableMapper.toEntity(deliverableDTO);
        deliverable = deliverableRepository.save(deliverable);
        return deliverableMapper.toDto(deliverable);
    }

    @Override
    public DeliverableDTO update(DeliverableDTO deliverableDTO) {
        LOG.debug("Request to update Deliverable : {}", deliverableDTO);
        Deliverable deliverable = deliverableMapper.toEntity(deliverableDTO);
        deliverable = deliverableRepository.save(deliverable);
        return deliverableMapper.toDto(deliverable);
    }

    @Override
    public Optional<DeliverableDTO> partialUpdate(DeliverableDTO deliverableDTO) {
        LOG.debug("Request to partially update Deliverable : {}", deliverableDTO);

        return deliverableRepository
            .findById(deliverableDTO.getId())
            .map(existingDeliverable -> {
                deliverableMapper.partialUpdate(existingDeliverable, deliverableDTO);

                return existingDeliverable;
            })
            .map(deliverableRepository::save)
            .map(deliverableMapper::toDto);
    }

    public Page<DeliverableDTO> findAllWithEagerRelationships(Pageable pageable) {
        return deliverableRepository.findAllWithEagerRelationships(pageable).map(deliverableMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeliverableDTO> findOne(Long id) {
        LOG.debug("Request to get Deliverable : {}", id);
        return deliverableRepository.findOneWithEagerRelationships(id).map(deliverableMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Deliverable : {}", id);
        deliverableRepository.deleteById(id);
    }
}
