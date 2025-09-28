package hr.terg.evag.web.rest;

import static hr.terg.evag.domain.DeliverableAsserts.*;
import static hr.terg.evag.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.terg.evag.IntegrationTest;
import hr.terg.evag.domain.Deliverable;
import hr.terg.evag.domain.Project;
import hr.terg.evag.domain.User;
import hr.terg.evag.domain.enumeration.DeliverableFormat;
import hr.terg.evag.domain.enumeration.DeliverableType;
import hr.terg.evag.domain.enumeration.DeploymentStatus;
import hr.terg.evag.repository.DeliverableRepository;
import hr.terg.evag.repository.UserRepository;
import hr.terg.evag.service.DeliverableService;
import hr.terg.evag.service.dto.DeliverableDTO;
import hr.terg.evag.service.mapper.DeliverableMapper;
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
 * Integration tests for the {@link DeliverableResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DeliverableResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final DeliverableType DEFAULT_TYPE = DeliverableType.TERRAFORM;
    private static final DeliverableType UPDATED_TYPE = DeliverableType.ANSIBLE;

    private static final DeliverableFormat DEFAULT_FORMAT = DeliverableFormat.ZIP_ARCHIVE;
    private static final DeliverableFormat UPDATED_FORMAT = DeliverableFormat.TAR_GZ;

    private static final DeploymentStatus DEFAULT_STATUS = DeploymentStatus.SUCCESS;
    private static final DeploymentStatus UPDATED_STATUS = DeploymentStatus.FAILED;

    private static final String DEFAULT_PACKAGE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_PACKAGE_PATH = "BBBBBBBBBB";

    private static final Long DEFAULT_PACKAGE_SIZE = 1L;
    private static final Long UPDATED_PACKAGE_SIZE = 2L;
    private static final Long SMALLER_PACKAGE_SIZE = 1L - 1L;

    private static final String DEFAULT_CHECKSUM = "AAAAAAAAAA";
    private static final String UPDATED_CHECKSUM = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ADDENDUM = "AAAAAAAAAA";
    private static final String UPDATED_ADDENDUM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/deliverables";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DeliverableRepository deliverableRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private DeliverableRepository deliverableRepositoryMock;

    @Autowired
    private DeliverableMapper deliverableMapper;

    @Mock
    private DeliverableService deliverableServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeliverableMockMvc;

    private Deliverable deliverable;

    private Deliverable insertedDeliverable;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Deliverable createEntity() {
        return new Deliverable()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .type(DEFAULT_TYPE)
            .format(DEFAULT_FORMAT)
            .status(DEFAULT_STATUS)
            .packagePath(DEFAULT_PACKAGE_PATH)
            .packageSize(DEFAULT_PACKAGE_SIZE)
            .checksum(DEFAULT_CHECKSUM)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .addendum(DEFAULT_ADDENDUM);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Deliverable createUpdatedEntity() {
        return new Deliverable()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .format(UPDATED_FORMAT)
            .status(UPDATED_STATUS)
            .packagePath(UPDATED_PACKAGE_PATH)
            .packageSize(UPDATED_PACKAGE_SIZE)
            .checksum(UPDATED_CHECKSUM)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .addendum(UPDATED_ADDENDUM);
    }

    @BeforeEach
    void initTest() {
        deliverable = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDeliverable != null) {
            deliverableRepository.delete(insertedDeliverable);
            insertedDeliverable = null;
        }
    }

    @Test
    @Transactional
    void createDeliverable() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Deliverable
        DeliverableDTO deliverableDTO = deliverableMapper.toDto(deliverable);
        var returnedDeliverableDTO = om.readValue(
            restDeliverableMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliverableDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DeliverableDTO.class
        );

        // Validate the Deliverable in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDeliverable = deliverableMapper.toEntity(returnedDeliverableDTO);
        assertDeliverableUpdatableFieldsEquals(returnedDeliverable, getPersistedDeliverable(returnedDeliverable));

        insertedDeliverable = returnedDeliverable;
    }

    @Test
    @Transactional
    void createDeliverableWithExistingId() throws Exception {
        // Create the Deliverable with an existing ID
        deliverable.setId(1L);
        DeliverableDTO deliverableDTO = deliverableMapper.toDto(deliverable);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeliverableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliverableDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Deliverable in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        deliverable.setName(null);

        // Create the Deliverable, which fails.
        DeliverableDTO deliverableDTO = deliverableMapper.toDto(deliverable);

        restDeliverableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliverableDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDeliverables() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList
        restDeliverableMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deliverable.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].packagePath").value(hasItem(DEFAULT_PACKAGE_PATH)))
            .andExpect(jsonPath("$.[*].packageSize").value(hasItem(DEFAULT_PACKAGE_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].checksum").value(hasItem(DEFAULT_CHECKSUM)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].addendum").value(hasItem(DEFAULT_ADDENDUM)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeliverablesWithEagerRelationshipsIsEnabled() throws Exception {
        when(deliverableServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDeliverableMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(deliverableServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeliverablesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(deliverableServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDeliverableMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(deliverableRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDeliverable() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get the deliverable
        restDeliverableMockMvc
            .perform(get(ENTITY_API_URL_ID, deliverable.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(deliverable.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.format").value(DEFAULT_FORMAT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.packagePath").value(DEFAULT_PACKAGE_PATH))
            .andExpect(jsonPath("$.packageSize").value(DEFAULT_PACKAGE_SIZE.intValue()))
            .andExpect(jsonPath("$.checksum").value(DEFAULT_CHECKSUM))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.addendum").value(DEFAULT_ADDENDUM));
    }

    @Test
    @Transactional
    void getDeliverablesByIdFiltering() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        Long id = deliverable.getId();

        defaultDeliverableFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDeliverableFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDeliverableFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDeliverablesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where name equals to
        defaultDeliverableFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDeliverablesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where name in
        defaultDeliverableFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDeliverablesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where name is not null
        defaultDeliverableFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliverablesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where name contains
        defaultDeliverableFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDeliverablesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where name does not contain
        defaultDeliverableFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllDeliverablesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where description equals to
        defaultDeliverableFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDeliverablesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where description in
        defaultDeliverableFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDeliverablesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where description is not null
        defaultDeliverableFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliverablesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where description contains
        defaultDeliverableFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDeliverablesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where description does not contain
        defaultDeliverableFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDeliverablesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where type equals to
        defaultDeliverableFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllDeliverablesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where type in
        defaultDeliverableFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllDeliverablesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where type is not null
        defaultDeliverableFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliverablesByFormatIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where format equals to
        defaultDeliverableFiltering("format.equals=" + DEFAULT_FORMAT, "format.equals=" + UPDATED_FORMAT);
    }

    @Test
    @Transactional
    void getAllDeliverablesByFormatIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where format in
        defaultDeliverableFiltering("format.in=" + DEFAULT_FORMAT + "," + UPDATED_FORMAT, "format.in=" + UPDATED_FORMAT);
    }

    @Test
    @Transactional
    void getAllDeliverablesByFormatIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where format is not null
        defaultDeliverableFiltering("format.specified=true", "format.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliverablesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where status equals to
        defaultDeliverableFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDeliverablesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where status in
        defaultDeliverableFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDeliverablesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where status is not null
        defaultDeliverableFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliverablesByPackagePathIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where packagePath equals to
        defaultDeliverableFiltering("packagePath.equals=" + DEFAULT_PACKAGE_PATH, "packagePath.equals=" + UPDATED_PACKAGE_PATH);
    }

    @Test
    @Transactional
    void getAllDeliverablesByPackagePathIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where packagePath in
        defaultDeliverableFiltering(
            "packagePath.in=" + DEFAULT_PACKAGE_PATH + "," + UPDATED_PACKAGE_PATH,
            "packagePath.in=" + UPDATED_PACKAGE_PATH
        );
    }

    @Test
    @Transactional
    void getAllDeliverablesByPackagePathIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where packagePath is not null
        defaultDeliverableFiltering("packagePath.specified=true", "packagePath.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliverablesByPackagePathContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where packagePath contains
        defaultDeliverableFiltering("packagePath.contains=" + DEFAULT_PACKAGE_PATH, "packagePath.contains=" + UPDATED_PACKAGE_PATH);
    }

    @Test
    @Transactional
    void getAllDeliverablesByPackagePathNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where packagePath does not contain
        defaultDeliverableFiltering(
            "packagePath.doesNotContain=" + UPDATED_PACKAGE_PATH,
            "packagePath.doesNotContain=" + DEFAULT_PACKAGE_PATH
        );
    }

    @Test
    @Transactional
    void getAllDeliverablesByPackageSizeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where packageSize equals to
        defaultDeliverableFiltering("packageSize.equals=" + DEFAULT_PACKAGE_SIZE, "packageSize.equals=" + UPDATED_PACKAGE_SIZE);
    }

    @Test
    @Transactional
    void getAllDeliverablesByPackageSizeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where packageSize in
        defaultDeliverableFiltering(
            "packageSize.in=" + DEFAULT_PACKAGE_SIZE + "," + UPDATED_PACKAGE_SIZE,
            "packageSize.in=" + UPDATED_PACKAGE_SIZE
        );
    }

    @Test
    @Transactional
    void getAllDeliverablesByPackageSizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where packageSize is not null
        defaultDeliverableFiltering("packageSize.specified=true", "packageSize.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliverablesByPackageSizeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where packageSize is greater than or equal to
        defaultDeliverableFiltering(
            "packageSize.greaterThanOrEqual=" + DEFAULT_PACKAGE_SIZE,
            "packageSize.greaterThanOrEqual=" + UPDATED_PACKAGE_SIZE
        );
    }

    @Test
    @Transactional
    void getAllDeliverablesByPackageSizeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where packageSize is less than or equal to
        defaultDeliverableFiltering(
            "packageSize.lessThanOrEqual=" + DEFAULT_PACKAGE_SIZE,
            "packageSize.lessThanOrEqual=" + SMALLER_PACKAGE_SIZE
        );
    }

    @Test
    @Transactional
    void getAllDeliverablesByPackageSizeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where packageSize is less than
        defaultDeliverableFiltering("packageSize.lessThan=" + UPDATED_PACKAGE_SIZE, "packageSize.lessThan=" + DEFAULT_PACKAGE_SIZE);
    }

    @Test
    @Transactional
    void getAllDeliverablesByPackageSizeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where packageSize is greater than
        defaultDeliverableFiltering("packageSize.greaterThan=" + SMALLER_PACKAGE_SIZE, "packageSize.greaterThan=" + DEFAULT_PACKAGE_SIZE);
    }

    @Test
    @Transactional
    void getAllDeliverablesByChecksumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where checksum equals to
        defaultDeliverableFiltering("checksum.equals=" + DEFAULT_CHECKSUM, "checksum.equals=" + UPDATED_CHECKSUM);
    }

    @Test
    @Transactional
    void getAllDeliverablesByChecksumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where checksum in
        defaultDeliverableFiltering("checksum.in=" + DEFAULT_CHECKSUM + "," + UPDATED_CHECKSUM, "checksum.in=" + UPDATED_CHECKSUM);
    }

    @Test
    @Transactional
    void getAllDeliverablesByChecksumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where checksum is not null
        defaultDeliverableFiltering("checksum.specified=true", "checksum.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliverablesByChecksumContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where checksum contains
        defaultDeliverableFiltering("checksum.contains=" + DEFAULT_CHECKSUM, "checksum.contains=" + UPDATED_CHECKSUM);
    }

    @Test
    @Transactional
    void getAllDeliverablesByChecksumNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where checksum does not contain
        defaultDeliverableFiltering("checksum.doesNotContain=" + UPDATED_CHECKSUM, "checksum.doesNotContain=" + DEFAULT_CHECKSUM);
    }

    @Test
    @Transactional
    void getAllDeliverablesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where createdDate equals to
        defaultDeliverableFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllDeliverablesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where createdDate in
        defaultDeliverableFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllDeliverablesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where createdDate is not null
        defaultDeliverableFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliverablesByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where lastModifiedDate equals to
        defaultDeliverableFiltering(
            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    @Transactional
    void getAllDeliverablesByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where lastModifiedDate in
        defaultDeliverableFiltering(
            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    @Transactional
    void getAllDeliverablesByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where lastModifiedDate is not null
        defaultDeliverableFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliverablesByAddendumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where addendum equals to
        defaultDeliverableFiltering("addendum.equals=" + DEFAULT_ADDENDUM, "addendum.equals=" + UPDATED_ADDENDUM);
    }

    @Test
    @Transactional
    void getAllDeliverablesByAddendumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where addendum in
        defaultDeliverableFiltering("addendum.in=" + DEFAULT_ADDENDUM + "," + UPDATED_ADDENDUM, "addendum.in=" + UPDATED_ADDENDUM);
    }

    @Test
    @Transactional
    void getAllDeliverablesByAddendumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where addendum is not null
        defaultDeliverableFiltering("addendum.specified=true", "addendum.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliverablesByAddendumContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where addendum contains
        defaultDeliverableFiltering("addendum.contains=" + DEFAULT_ADDENDUM, "addendum.contains=" + UPDATED_ADDENDUM);
    }

    @Test
    @Transactional
    void getAllDeliverablesByAddendumNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        // Get all the deliverableList where addendum does not contain
        defaultDeliverableFiltering("addendum.doesNotContain=" + UPDATED_ADDENDUM, "addendum.doesNotContain=" + DEFAULT_ADDENDUM);
    }

    @Test
    @Transactional
    void getAllDeliverablesByCreatedByIsEqualToSomething() throws Exception {
        User createdBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            deliverableRepository.saveAndFlush(deliverable);
            createdBy = UserResourceIT.createEntity();
        } else {
            createdBy = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(createdBy);
        em.flush();
        deliverable.setCreatedBy(createdBy);
        deliverableRepository.saveAndFlush(deliverable);
        Long createdById = createdBy.getId();
        // Get all the deliverableList where createdBy equals to createdById
        defaultDeliverableShouldBeFound("createdById.equals=" + createdById);

        // Get all the deliverableList where createdBy equals to (createdById + 1)
        defaultDeliverableShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }

    @Test
    @Transactional
    void getAllDeliverablesByProjectIsEqualToSomething() throws Exception {
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            deliverableRepository.saveAndFlush(deliverable);
            project = ProjectResourceIT.createEntity();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        em.persist(project);
        em.flush();
        deliverable.setProject(project);
        deliverableRepository.saveAndFlush(deliverable);
        Long projectId = project.getId();
        // Get all the deliverableList where project equals to projectId
        defaultDeliverableShouldBeFound("projectId.equals=" + projectId);

        // Get all the deliverableList where project equals to (projectId + 1)
        defaultDeliverableShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    private void defaultDeliverableFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDeliverableShouldBeFound(shouldBeFound);
        defaultDeliverableShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDeliverableShouldBeFound(String filter) throws Exception {
        restDeliverableMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deliverable.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].packagePath").value(hasItem(DEFAULT_PACKAGE_PATH)))
            .andExpect(jsonPath("$.[*].packageSize").value(hasItem(DEFAULT_PACKAGE_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].checksum").value(hasItem(DEFAULT_CHECKSUM)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].addendum").value(hasItem(DEFAULT_ADDENDUM)));

        // Check, that the count call also returns 1
        restDeliverableMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDeliverableShouldNotBeFound(String filter) throws Exception {
        restDeliverableMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDeliverableMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDeliverable() throws Exception {
        // Get the deliverable
        restDeliverableMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDeliverable() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deliverable
        Deliverable updatedDeliverable = deliverableRepository.findById(deliverable.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDeliverable are not directly saved in db
        em.detach(updatedDeliverable);
        updatedDeliverable
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .format(UPDATED_FORMAT)
            .status(UPDATED_STATUS)
            .packagePath(UPDATED_PACKAGE_PATH)
            .packageSize(UPDATED_PACKAGE_SIZE)
            .checksum(UPDATED_CHECKSUM)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .addendum(UPDATED_ADDENDUM);
        DeliverableDTO deliverableDTO = deliverableMapper.toDto(updatedDeliverable);

        restDeliverableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliverableDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliverableDTO))
            )
            .andExpect(status().isOk());

        // Validate the Deliverable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDeliverableToMatchAllProperties(updatedDeliverable);
    }

    @Test
    @Transactional
    void putNonExistingDeliverable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliverable.setId(longCount.incrementAndGet());

        // Create the Deliverable
        DeliverableDTO deliverableDTO = deliverableMapper.toDto(deliverable);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliverableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliverableDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliverableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Deliverable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDeliverable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliverable.setId(longCount.incrementAndGet());

        // Create the Deliverable
        DeliverableDTO deliverableDTO = deliverableMapper.toDto(deliverable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliverableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliverableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Deliverable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDeliverable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliverable.setId(longCount.incrementAndGet());

        // Create the Deliverable
        DeliverableDTO deliverableDTO = deliverableMapper.toDto(deliverable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliverableMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliverableDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Deliverable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDeliverableWithPatch() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deliverable using partial update
        Deliverable partialUpdatedDeliverable = new Deliverable();
        partialUpdatedDeliverable.setId(deliverable.getId());

        partialUpdatedDeliverable
            .status(UPDATED_STATUS)
            .packagePath(UPDATED_PACKAGE_PATH)
            .packageSize(UPDATED_PACKAGE_SIZE)
            .checksum(UPDATED_CHECKSUM)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .addendum(UPDATED_ADDENDUM);

        restDeliverableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDeliverable.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDeliverable))
            )
            .andExpect(status().isOk());

        // Validate the Deliverable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliverableUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDeliverable, deliverable),
            getPersistedDeliverable(deliverable)
        );
    }

    @Test
    @Transactional
    void fullUpdateDeliverableWithPatch() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deliverable using partial update
        Deliverable partialUpdatedDeliverable = new Deliverable();
        partialUpdatedDeliverable.setId(deliverable.getId());

        partialUpdatedDeliverable
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .format(UPDATED_FORMAT)
            .status(UPDATED_STATUS)
            .packagePath(UPDATED_PACKAGE_PATH)
            .packageSize(UPDATED_PACKAGE_SIZE)
            .checksum(UPDATED_CHECKSUM)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .addendum(UPDATED_ADDENDUM);

        restDeliverableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDeliverable.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDeliverable))
            )
            .andExpect(status().isOk());

        // Validate the Deliverable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliverableUpdatableFieldsEquals(partialUpdatedDeliverable, getPersistedDeliverable(partialUpdatedDeliverable));
    }

    @Test
    @Transactional
    void patchNonExistingDeliverable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliverable.setId(longCount.incrementAndGet());

        // Create the Deliverable
        DeliverableDTO deliverableDTO = deliverableMapper.toDto(deliverable);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliverableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, deliverableDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliverableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Deliverable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDeliverable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliverable.setId(longCount.incrementAndGet());

        // Create the Deliverable
        DeliverableDTO deliverableDTO = deliverableMapper.toDto(deliverable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliverableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliverableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Deliverable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDeliverable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliverable.setId(longCount.incrementAndGet());

        // Create the Deliverable
        DeliverableDTO deliverableDTO = deliverableMapper.toDto(deliverable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliverableMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(deliverableDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Deliverable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDeliverable() throws Exception {
        // Initialize the database
        insertedDeliverable = deliverableRepository.saveAndFlush(deliverable);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the deliverable
        restDeliverableMockMvc
            .perform(delete(ENTITY_API_URL_ID, deliverable.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return deliverableRepository.count();
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

    protected Deliverable getPersistedDeliverable(Deliverable deliverable) {
        return deliverableRepository.findById(deliverable.getId()).orElseThrow();
    }

    protected void assertPersistedDeliverableToMatchAllProperties(Deliverable expectedDeliverable) {
        assertDeliverableAllPropertiesEquals(expectedDeliverable, getPersistedDeliverable(expectedDeliverable));
    }

    protected void assertPersistedDeliverableToMatchUpdatableProperties(Deliverable expectedDeliverable) {
        assertDeliverableAllUpdatablePropertiesEquals(expectedDeliverable, getPersistedDeliverable(expectedDeliverable));
    }
}
