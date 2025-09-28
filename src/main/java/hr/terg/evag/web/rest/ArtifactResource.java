package hr.terg.evag.web.rest;

import hr.terg.evag.repository.ArtifactRepository;
import hr.terg.evag.service.ArtifactQueryService;
import hr.terg.evag.service.ArtifactService;
import hr.terg.evag.service.criteria.ArtifactCriteria;
import hr.terg.evag.service.dto.ArtifactDTO;
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
 * REST controller for managing {@link hr.terg.evag.domain.Artifact}.
 */
@RestController
@RequestMapping("/api/artifacts")
public class ArtifactResource {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactResource.class);

    private static final String ENTITY_NAME = "artifact";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ArtifactService artifactService;

    private final ArtifactRepository artifactRepository;

    private final ArtifactQueryService artifactQueryService;

    public ArtifactResource(
        ArtifactService artifactService,
        ArtifactRepository artifactRepository,
        ArtifactQueryService artifactQueryService
    ) {
        this.artifactService = artifactService;
        this.artifactRepository = artifactRepository;
        this.artifactQueryService = artifactQueryService;
    }

    /**
     * {@code POST  /artifacts} : Create a new artifact.
     *
     * @param artifactDTO the artifactDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new artifactDTO, or with status {@code 400 (Bad Request)} if the artifact has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ArtifactDTO> createArtifact(@Valid @RequestBody ArtifactDTO artifactDTO) throws URISyntaxException {
        LOG.debug("REST request to save Artifact : {}", artifactDTO);
        if (artifactDTO.getId() != null) {
            throw new BadRequestAlertException("A new artifact cannot already have an ID", ENTITY_NAME, "idexists");
        }
        artifactDTO = artifactService.save(artifactDTO);
        return ResponseEntity.created(new URI("/api/artifacts/" + artifactDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, artifactDTO.getId().toString()))
            .body(artifactDTO);
    }

    /**
     * {@code PUT  /artifacts/:id} : Updates an existing artifact.
     *
     * @param id the id of the artifactDTO to save.
     * @param artifactDTO the artifactDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated artifactDTO,
     * or with status {@code 400 (Bad Request)} if the artifactDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the artifactDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArtifactDTO> updateArtifact(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ArtifactDTO artifactDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Artifact : {}, {}", id, artifactDTO);
        if (artifactDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, artifactDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!artifactRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        artifactDTO = artifactService.update(artifactDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, artifactDTO.getId().toString()))
            .body(artifactDTO);
    }

    /**
     * {@code PATCH  /artifacts/:id} : Partial updates given fields of an existing artifact, field will ignore if it is null
     *
     * @param id the id of the artifactDTO to save.
     * @param artifactDTO the artifactDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated artifactDTO,
     * or with status {@code 400 (Bad Request)} if the artifactDTO is not valid,
     * or with status {@code 404 (Not Found)} if the artifactDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the artifactDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ArtifactDTO> partialUpdateArtifact(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ArtifactDTO artifactDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Artifact partially : {}, {}", id, artifactDTO);
        if (artifactDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, artifactDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!artifactRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ArtifactDTO> result = artifactService.partialUpdate(artifactDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, artifactDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /artifacts} : get all the artifacts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of artifacts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ArtifactDTO>> getAllArtifacts(
        ArtifactCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Artifacts by criteria: {}", criteria);

        Page<ArtifactDTO> page = artifactQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /artifacts/count} : count all the artifacts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countArtifacts(ArtifactCriteria criteria) {
        LOG.debug("REST request to count Artifacts by criteria: {}", criteria);
        return ResponseEntity.ok().body(artifactQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /artifacts/:id} : get the "id" artifact.
     *
     * @param id the id of the artifactDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the artifactDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtifactDTO> getArtifact(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Artifact : {}", id);
        Optional<ArtifactDTO> artifactDTO = artifactService.findOne(id);
        return ResponseUtil.wrapOrNotFound(artifactDTO);
    }

    /**
     * {@code DELETE  /artifacts/:id} : delete the "id" artifact.
     *
     * @param id the id of the artifactDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtifact(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Artifact : {}", id);
        artifactService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
