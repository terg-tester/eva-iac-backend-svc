package hr.terg.evag.web.rest;

import hr.terg.evag.repository.DeliverableRepository;
import hr.terg.evag.service.DeliverableQueryService;
import hr.terg.evag.service.DeliverableService;
import hr.terg.evag.service.criteria.DeliverableCriteria;
import hr.terg.evag.service.dto.DeliverableDTO;
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
 * REST controller for managing {@link hr.terg.evag.domain.Deliverable}.
 */
@RestController
@RequestMapping("/api/deliverables")
public class DeliverableResource {

    private static final Logger LOG = LoggerFactory.getLogger(DeliverableResource.class);

    private static final String ENTITY_NAME = "deliverable";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DeliverableService deliverableService;

    private final DeliverableRepository deliverableRepository;

    private final DeliverableQueryService deliverableQueryService;

    public DeliverableResource(
        DeliverableService deliverableService,
        DeliverableRepository deliverableRepository,
        DeliverableQueryService deliverableQueryService
    ) {
        this.deliverableService = deliverableService;
        this.deliverableRepository = deliverableRepository;
        this.deliverableQueryService = deliverableQueryService;
    }

    /**
     * {@code POST  /deliverables} : Create a new deliverable.
     *
     * @param deliverableDTO the deliverableDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deliverableDTO, or with status {@code 400 (Bad Request)} if the deliverable has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DeliverableDTO> createDeliverable(@Valid @RequestBody DeliverableDTO deliverableDTO) throws URISyntaxException {
        LOG.debug("REST request to save Deliverable : {}", deliverableDTO);
        if (deliverableDTO.getId() != null) {
            throw new BadRequestAlertException("A new deliverable cannot already have an ID", ENTITY_NAME, "idexists");
        }
        deliverableDTO = deliverableService.save(deliverableDTO);
        return ResponseEntity.created(new URI("/api/deliverables/" + deliverableDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, deliverableDTO.getId().toString()))
            .body(deliverableDTO);
    }

    /**
     * {@code PUT  /deliverables/:id} : Updates an existing deliverable.
     *
     * @param id the id of the deliverableDTO to save.
     * @param deliverableDTO the deliverableDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliverableDTO,
     * or with status {@code 400 (Bad Request)} if the deliverableDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deliverableDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeliverableDTO> updateDeliverable(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DeliverableDTO deliverableDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Deliverable : {}, {}", id, deliverableDTO);
        if (deliverableDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deliverableDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deliverableRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        deliverableDTO = deliverableService.update(deliverableDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deliverableDTO.getId().toString()))
            .body(deliverableDTO);
    }

    /**
     * {@code PATCH  /deliverables/:id} : Partial updates given fields of an existing deliverable, field will ignore if it is null
     *
     * @param id the id of the deliverableDTO to save.
     * @param deliverableDTO the deliverableDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliverableDTO,
     * or with status {@code 400 (Bad Request)} if the deliverableDTO is not valid,
     * or with status {@code 404 (Not Found)} if the deliverableDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the deliverableDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DeliverableDTO> partialUpdateDeliverable(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DeliverableDTO deliverableDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Deliverable partially : {}, {}", id, deliverableDTO);
        if (deliverableDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deliverableDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deliverableRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DeliverableDTO> result = deliverableService.partialUpdate(deliverableDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deliverableDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /deliverables} : get all the deliverables.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deliverables in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DeliverableDTO>> getAllDeliverables(
        DeliverableCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Deliverables by criteria: {}", criteria);

        Page<DeliverableDTO> page = deliverableQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /deliverables/count} : count all the deliverables.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDeliverables(DeliverableCriteria criteria) {
        LOG.debug("REST request to count Deliverables by criteria: {}", criteria);
        return ResponseEntity.ok().body(deliverableQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /deliverables/:id} : get the "id" deliverable.
     *
     * @param id the id of the deliverableDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deliverableDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeliverableDTO> getDeliverable(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Deliverable : {}", id);
        Optional<DeliverableDTO> deliverableDTO = deliverableService.findOne(id);
        return ResponseUtil.wrapOrNotFound(deliverableDTO);
    }

    /**
     * {@code DELETE  /deliverables/:id} : delete the "id" deliverable.
     *
     * @param id the id of the deliverableDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliverable(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Deliverable : {}", id);
        deliverableService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
