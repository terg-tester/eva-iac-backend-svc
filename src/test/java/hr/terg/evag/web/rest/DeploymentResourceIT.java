package hr.terg.evag.web.rest;

import static hr.terg.evag.domain.DeploymentAsserts.*;
import static hr.terg.evag.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.terg.evag.IntegrationTest;
import hr.terg.evag.domain.Deliverable;
import hr.terg.evag.domain.Deployment;
import hr.terg.evag.domain.User;
import hr.terg.evag.domain.enumeration.DeploymentStatus;
import hr.terg.evag.repository.DeploymentRepository;
import hr.terg.evag.repository.UserRepository;
import hr.terg.evag.service.DeploymentService;
import hr.terg.evag.service.dto.DeploymentDTO;
import hr.terg.evag.service.mapper.DeploymentMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DeploymentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DeploymentResourceIT {

    private static final Instant DEFAULT_DEPLOYMENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DEPLOYMENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final DeploymentStatus DEFAULT_STATUS = DeploymentStatus.SUCCESS;
    private static final DeploymentStatus UPDATED_STATUS = DeploymentStatus.FAILED;

    private static final String DEFAULT_LOGS = "AAAAAAAAAA";
    private static final String UPDATED_LOGS = "BBBBBBBBBB";

    private static final String DEFAULT_ADDENDUM = "AAAAAAAAAA";
    private static final String UPDATED_ADDENDUM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/deployments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DeploymentRepository deploymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private DeploymentRepository deploymentRepositoryMock;

    @Autowired
    private DeploymentMapper deploymentMapper;

    @Mock
    private DeploymentService deploymentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeploymentMockMvc;

    private Deployment deployment;

    private Deployment insertedDeployment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Deployment createEntity() {
        return new Deployment()
            .deploymentDate(DEFAULT_DEPLOYMENT_DATE)
            .status(DEFAULT_STATUS)
            .logs(DEFAULT_LOGS)
            .addendum(DEFAULT_ADDENDUM);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Deployment createUpdatedEntity() {
        return new Deployment()
            .deploymentDate(UPDATED_DEPLOYMENT_DATE)
            .status(UPDATED_STATUS)
            .logs(UPDATED_LOGS)
            .addendum(UPDATED_ADDENDUM);
    }

    @BeforeEach
    void initTest() {
        deployment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDeployment != null) {
            deploymentRepository.delete(insertedDeployment);
            insertedDeployment = null;
        }
    }

    @Test
    @Transactional
    void createDeployment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Deployment
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);
        var returnedDeploymentDTO = om.readValue(
            restDeploymentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deploymentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DeploymentDTO.class
        );

        // Validate the Deployment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDeployment = deploymentMapper.toEntity(returnedDeploymentDTO);
        assertDeploymentUpdatableFieldsEquals(returnedDeployment, getPersistedDeployment(returnedDeployment));

        insertedDeployment = returnedDeployment;
    }

    @Test
    @Transactional
    void createDeploymentWithExistingId() throws Exception {
        // Create the Deployment with an existing ID
        deployment.setId(1L);
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeploymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deploymentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Deployment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDeployments() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList
        restDeploymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deployment.getId().intValue())))
            .andExpect(jsonPath("$.[*].deploymentDate").value(hasItem(DEFAULT_DEPLOYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].logs").value(hasItem(DEFAULT_LOGS)))
            .andExpect(jsonPath("$.[*].addendum").value(hasItem(DEFAULT_ADDENDUM)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeploymentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(deploymentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDeploymentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(deploymentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeploymentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(deploymentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDeploymentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(deploymentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDeployment() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get the deployment
        restDeploymentMockMvc
            .perform(get(ENTITY_API_URL_ID, deployment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(deployment.getId().intValue()))
            .andExpect(jsonPath("$.deploymentDate").value(DEFAULT_DEPLOYMENT_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.logs").value(DEFAULT_LOGS))
            .andExpect(jsonPath("$.addendum").value(DEFAULT_ADDENDUM));
    }

    @Test
    @Transactional
    void getDeploymentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        Long id = deployment.getId();

        defaultDeploymentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDeploymentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDeploymentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDeploymentsByDeploymentDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where deploymentDate equals to
        defaultDeploymentFiltering("deploymentDate.equals=" + DEFAULT_DEPLOYMENT_DATE, "deploymentDate.equals=" + UPDATED_DEPLOYMENT_DATE);
    }

    @Test
    @Transactional
    void getAllDeploymentsByDeploymentDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where deploymentDate in
        defaultDeploymentFiltering(
            "deploymentDate.in=" + DEFAULT_DEPLOYMENT_DATE + "," + UPDATED_DEPLOYMENT_DATE,
            "deploymentDate.in=" + UPDATED_DEPLOYMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllDeploymentsByDeploymentDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where deploymentDate is not null
        defaultDeploymentFiltering("deploymentDate.specified=true", "deploymentDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDeploymentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where status equals to
        defaultDeploymentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDeploymentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where status in
        defaultDeploymentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDeploymentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where status is not null
        defaultDeploymentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllDeploymentsByAddendumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where addendum equals to
        defaultDeploymentFiltering("addendum.equals=" + DEFAULT_ADDENDUM, "addendum.equals=" + UPDATED_ADDENDUM);
    }

    @Test
    @Transactional
    void getAllDeploymentsByAddendumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where addendum in
        defaultDeploymentFiltering("addendum.in=" + DEFAULT_ADDENDUM + "," + UPDATED_ADDENDUM, "addendum.in=" + UPDATED_ADDENDUM);
    }

    @Test
    @Transactional
    void getAllDeploymentsByAddendumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where addendum is not null
        defaultDeploymentFiltering("addendum.specified=true", "addendum.specified=false");
    }

    @Test
    @Transactional
    void getAllDeploymentsByAddendumContainsSomething() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where addendum contains
        defaultDeploymentFiltering("addendum.contains=" + DEFAULT_ADDENDUM, "addendum.contains=" + UPDATED_ADDENDUM);
    }

    @Test
    @Transactional
    void getAllDeploymentsByAddendumNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList where addendum does not contain
        defaultDeploymentFiltering("addendum.doesNotContain=" + UPDATED_ADDENDUM, "addendum.doesNotContain=" + DEFAULT_ADDENDUM);
    }

    @Test
    @Transactional
    void getAllDeploymentsByDeployedByIsEqualToSomething() throws Exception {
        User deployedBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            deploymentRepository.saveAndFlush(deployment);
            deployedBy = UserResourceIT.createEntity();
        } else {
            deployedBy = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(deployedBy);
        em.flush();
        deployment.setDeployedBy(deployedBy);
        deploymentRepository.saveAndFlush(deployment);
        Long deployedById = deployedBy.getId();
        // Get all the deploymentList where deployedBy equals to deployedById
        defaultDeploymentShouldBeFound("deployedById.equals=" + deployedById);

        // Get all the deploymentList where deployedBy equals to (deployedById + 1)
        defaultDeploymentShouldNotBeFound("deployedById.equals=" + (deployedById + 1));
    }

    @Test
    @Transactional
    void getAllDeploymentsByDeliverableIsEqualToSomething() throws Exception {
        Deliverable deliverable;
        if (TestUtil.findAll(em, Deliverable.class).isEmpty()) {
            deploymentRepository.saveAndFlush(deployment);
            deliverable = DeliverableResourceIT.createEntity();
        } else {
            deliverable = TestUtil.findAll(em, Deliverable.class).get(0);
        }
        em.persist(deliverable);
        em.flush();
        deployment.setDeliverable(deliverable);
        deploymentRepository.saveAndFlush(deployment);
        Long deliverableId = deliverable.getId();
        // Get all the deploymentList where deliverable equals to deliverableId
        defaultDeploymentShouldBeFound("deliverableId.equals=" + deliverableId);

        // Get all the deploymentList where deliverable equals to (deliverableId + 1)
        defaultDeploymentShouldNotBeFound("deliverableId.equals=" + (deliverableId + 1));
    }

    private void defaultDeploymentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDeploymentShouldBeFound(shouldBeFound);
        defaultDeploymentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDeploymentShouldBeFound(String filter) throws Exception {
        restDeploymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deployment.getId().intValue())))
            .andExpect(jsonPath("$.[*].deploymentDate").value(hasItem(DEFAULT_DEPLOYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].logs").value(hasItem(DEFAULT_LOGS)))
            .andExpect(jsonPath("$.[*].addendum").value(hasItem(DEFAULT_ADDENDUM)));

        // Check, that the count call also returns 1
        restDeploymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDeploymentShouldNotBeFound(String filter) throws Exception {
        restDeploymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDeploymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDeployment() throws Exception {
        // Get the deployment
        restDeploymentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDeployment() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deployment
        Deployment updatedDeployment = deploymentRepository.findById(deployment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDeployment are not directly saved in db
        em.detach(updatedDeployment);
        updatedDeployment.deploymentDate(UPDATED_DEPLOYMENT_DATE).status(UPDATED_STATUS).logs(UPDATED_LOGS).addendum(UPDATED_ADDENDUM);
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(updatedDeployment);

        restDeploymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deploymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deploymentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Deployment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDeploymentToMatchAllProperties(updatedDeployment);
    }

    @Test
    @Transactional
    void putNonExistingDeployment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deployment.setId(longCount.incrementAndGet());

        // Create the Deployment
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeploymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deploymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deploymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Deployment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDeployment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deployment.setId(longCount.incrementAndGet());

        // Create the Deployment
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeploymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deploymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Deployment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDeployment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deployment.setId(longCount.incrementAndGet());

        // Create the Deployment
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeploymentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deploymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Deployment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDeploymentWithPatch() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deployment using partial update
        Deployment partialUpdatedDeployment = new Deployment();
        partialUpdatedDeployment.setId(deployment.getId());

        partialUpdatedDeployment.deploymentDate(UPDATED_DEPLOYMENT_DATE).logs(UPDATED_LOGS);

        restDeploymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDeployment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDeployment))
            )
            .andExpect(status().isOk());

        // Validate the Deployment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeploymentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDeployment, deployment),
            getPersistedDeployment(deployment)
        );
    }

    @Test
    @Transactional
    void fullUpdateDeploymentWithPatch() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deployment using partial update
        Deployment partialUpdatedDeployment = new Deployment();
        partialUpdatedDeployment.setId(deployment.getId());

        partialUpdatedDeployment
            .deploymentDate(UPDATED_DEPLOYMENT_DATE)
            .status(UPDATED_STATUS)
            .logs(UPDATED_LOGS)
            .addendum(UPDATED_ADDENDUM);

        restDeploymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDeployment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDeployment))
            )
            .andExpect(status().isOk());

        // Validate the Deployment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeploymentUpdatableFieldsEquals(partialUpdatedDeployment, getPersistedDeployment(partialUpdatedDeployment));
    }

    @Test
    @Transactional
    void patchNonExistingDeployment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deployment.setId(longCount.incrementAndGet());

        // Create the Deployment
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeploymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, deploymentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deploymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Deployment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDeployment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deployment.setId(longCount.incrementAndGet());

        // Create the Deployment
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeploymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deploymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Deployment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDeployment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deployment.setId(longCount.incrementAndGet());

        // Create the Deployment
        DeploymentDTO deploymentDTO = deploymentMapper.toDto(deployment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeploymentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(deploymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Deployment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDeployment() throws Exception {
        // Initialize the database
        insertedDeployment = deploymentRepository.saveAndFlush(deployment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the deployment
        restDeploymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, deployment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return deploymentRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Deployment getPersistedDeployment(Deployment deployment) {
        return deploymentRepository.findById(deployment.getId()).orElseThrow();
    }

    protected void assertPersistedDeploymentToMatchAllProperties(Deployment expectedDeployment) {
        assertDeploymentAllPropertiesEquals(expectedDeployment, getPersistedDeployment(expectedDeployment));
    }

    protected void assertPersistedDeploymentToMatchUpdatableProperties(Deployment expectedDeployment) {
        assertDeploymentAllUpdatablePropertiesEquals(expectedDeployment, getPersistedDeployment(expectedDeployment));
    }
}
