package hr.terg.evag.web.rest;

import hr.terg.evag.repository.DeploymentRepository;
import hr.terg.evag.service.DeploymentQueryService;
import hr.terg.evag.service.DeploymentService;
import hr.terg.evag.service.criteria.DeploymentCriteria;
import hr.terg.evag.service.dto.DeploymentDTO;
import hr.terg.evag.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link hr.terg.evag.domain.Deployment}.
 */
@RestController
@RequestMapping("/api/deployments")
public class DeploymentResource {

    private static final Logger LOG = LoggerFactory.getLogger(DeploymentResource.class);

    private static final String ENTITY_NAME = "deployment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DeploymentService deploymentService;

    private final DeploymentRepository deploymentRepository;

    private final DeploymentQueryService deploymentQueryService;

    public DeploymentResource(
        DeploymentService deploymentService,
        DeploymentRepository deploymentRepository,
        DeploymentQueryService deploymentQueryService
    ) {
        this.deploymentService = deploymentService;
        this.deploymentRepository = deploymentRepository;
        this.deploymentQueryService = deploymentQueryService;
    }

    /**
     * {@code POST  /deployments} : Create a new deployment.
     *
     * @param deploymentDTO the deploymentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deploymentDTO, or with status {@code 400 (Bad Request)} if the deployment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DeploymentDTO> createDeployment(@Valid @RequestBody DeploymentDTO deploymentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Deployment : {}", deploymentDTO);
        if (deploymentDTO.getId() != null) {
            throw new BadRequestAlertException("A new deployment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        deploymentDTO = deploymentService.save(deploymentDTO);
        return ResponseEntity.created(new URI("/api/deployments/" + deploymentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, deploymentDTO.getId().toString()))
            .body(deploymentDTO);
    }

    /**
     * {@code PUT  /deployments/:id} : Updates an existing deployment.
     *
     * @param id the id of the deploymentDTO to save.
     * @param deploymentDTO the deploymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deploymentDTO,
     * or with status {@code 400 (Bad Request)} if the deploymentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deploymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeploymentDTO> updateDeployment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DeploymentDTO deploymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Deployment : {}, {}", id, deploymentDTO);
        if (deploymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deploymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deploymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        deploymentDTO = deploymentService.update(deploymentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deploymentDTO.getId().toString()))
            .body(deploymentDTO);
    }

    /**
     * {@code PATCH  /deployments/:id} : Partial updates given fields of an existing deployment, field will ignore if it is null
     *
     * @param id the id of the deploymentDTO to save.
     * @param deploymentDTO the deploymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deploymentDTO,
     * or with status {@code 400 (Bad Request)} if the deploymentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the deploymentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the deploymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DeploymentDTO> partialUpdateDeployment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DeploymentDTO deploymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Deployment partially : {}, {}", id, deploymentDTO);
        if (deploymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deploymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deploymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DeploymentDTO> result = deploymentService.partialUpdate(deploymentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deploymentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /deployments} : get all the deployments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deployments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DeploymentDTO>> getAllDeployments(
        DeploymentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Deployments by criteria: {}", criteria);

        Page<DeploymentDTO> page = deploymentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /deployments/count} : count all the deployments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDeployments(DeploymentCriteria criteria) {
        LOG.debug("REST request to count Deployments by criteria: {}", criteria);
        return ResponseEntity.ok().body(deploymentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /deployments/:id} : get the "id" deployment.
     *
     * @param id the id of the deploymentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deploymentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeploymentDTO> getDeployment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Deployment : {}", id);
        Optional<DeploymentDTO> deploymentDTO = deploymentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(deploymentDTO);
    }

    /**
     * {@code DELETE  /deployments/:id} : delete the "id" deployment.
     *
     * @param id the id of the deploymentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeployment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Deployment : {}", id);
        deploymentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
