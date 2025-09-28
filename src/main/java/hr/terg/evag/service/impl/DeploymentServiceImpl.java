package hr.terg.evag.service.impl;

import hr.terg.evag.domain.Deployment;
import hr.terg.evag.repository.DeploymentRepository;
import hr.terg.evag.service.DeploymentService;
import hr.terg.evag.service.dto.DeploymentDTO;
import hr.terg.evag.service.mapper.DeploymentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link hr.terg.evag.domain.Deployment}.
 */
@Service
@Transactional
public class DeploymentServiceImpl implements DeploymentService {

    private static final Logger LOG = LoggerFactory.getLogger(DeploymentServiceImpl.class);

    private final DeploymentRepository deploymentRepository;

    private final DeploymentMapper deploymentMapper;

    public DeploymentServiceImpl(DeploymentRepository deploymentRepository, DeploymentMapper deploymentMapper) {
        this.deploymentRepository = deploymentRepository;
        this.deploymentMapper = deploymentMapper;
    }

    @Override
    public DeploymentDTO save(DeploymentDTO deploymentDTO) {
        LOG.debug("Request to save Deployment : {}", deploymentDTO);
        Deployment deployment = deploymentMapper.toEntity(deploymentDTO);
        deployment = deploymentRepository.save(deployment);
        return deploymentMapper.toDto(deployment);
    }

    @Override
    public DeploymentDTO update(DeploymentDTO deploymentDTO) {
        LOG.debug("Request to update Deployment : {}", deploymentDTO);
        Deployment deployment = deploymentMapper.toEntity(deploymentDTO);
        deployment = deploymentRepository.save(deployment);
        return deploymentMapper.toDto(deployment);
    }

    @Override
    public Optional<DeploymentDTO> partialUpdate(DeploymentDTO deploymentDTO) {
        LOG.debug("Request to partially update Deployment : {}", deploymentDTO);

        return deploymentRepository
            .findById(deploymentDTO.getId())
            .map(existingDeployment -> {
                deploymentMapper.partialUpdate(existingDeployment, deploymentDTO);

                return existingDeployment;
            })
            .map(deploymentRepository::save)
            .map(deploymentMapper::toDto);
    }

    public Page<DeploymentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return deploymentRepository.findAllWithEagerRelationships(pageable).map(deploymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeploymentDTO> findOne(Long id) {
        LOG.debug("Request to get Deployment : {}", id);
        return deploymentRepository.findOneWithEagerRelationships(id).map(deploymentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Deployment : {}", id);
        deploymentRepository.deleteById(id);
    }
}
