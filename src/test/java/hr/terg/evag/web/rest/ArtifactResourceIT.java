package hr.terg.evag.web.rest;

import static hr.terg.evag.domain.ArtifactAsserts.*;
import static hr.terg.evag.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.terg.evag.IntegrationTest;
import hr.terg.evag.domain.Artifact;
import hr.terg.evag.domain.Project;
import hr.terg.evag.domain.User;
import hr.terg.evag.domain.enumeration.ArtifactStatus;
import hr.terg.evag.domain.enumeration.ArtifactType;
import hr.terg.evag.repository.ArtifactRepository;
import hr.terg.evag.repository.UserRepository;
import hr.terg.evag.service.dto.ArtifactDTO;
import hr.terg.evag.service.mapper.ArtifactMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ArtifactResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ArtifactResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ArtifactType DEFAULT_TYPE = ArtifactType.DOCUMENT;
    private static final ArtifactType UPDATED_TYPE = ArtifactType.IMAGE;

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final ArtifactStatus DEFAULT_STATUS = ArtifactStatus.DRAFT;
    private static final ArtifactStatus UPDATED_STATUS = ArtifactStatus.REVIEW;

    private static final Long DEFAULT_FILE_SIZE = 1L;
    private static final Long UPDATED_FILE_SIZE = 2L;
    private static final Long SMALLER_FILE_SIZE = 1L - 1L;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ADDENDUM = "AAAAAAAAAA";
    private static final String UPDATED_ADDENDUM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/artifacts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtifactMapper artifactMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restArtifactMockMvc;

    private Artifact artifact;

    private Artifact insertedArtifact;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artifact createEntity() {
        return new Artifact()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .type(DEFAULT_TYPE)
            .link(DEFAULT_LINK)
            .status(DEFAULT_STATUS)
            .fileSize(DEFAULT_FILE_SIZE)
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
    public static Artifact createUpdatedEntity() {
        return new Artifact()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .link(UPDATED_LINK)
            .status(UPDATED_STATUS)
            .fileSize(UPDATED_FILE_SIZE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .addendum(UPDATED_ADDENDUM);
    }

    @BeforeEach
    void initTest() {
        artifact = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedArtifact != null) {
            artifactRepository.delete(insertedArtifact);
            insertedArtifact = null;
        }
    }

    @Test
    @Transactional
    void createArtifact() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Artifact
        ArtifactDTO artifactDTO = artifactMapper.toDto(artifact);
        var returnedArtifactDTO = om.readValue(
            restArtifactMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(artifactDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ArtifactDTO.class
        );

        // Validate the Artifact in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedArtifact = artifactMapper.toEntity(returnedArtifactDTO);
        assertArtifactUpdatableFieldsEquals(returnedArtifact, getPersistedArtifact(returnedArtifact));

        insertedArtifact = returnedArtifact;
    }

    @Test
    @Transactional
    void createArtifactWithExistingId() throws Exception {
        // Create the Artifact with an existing ID
        artifact.setId(1L);
        ArtifactDTO artifactDTO = artifactMapper.toDto(artifact);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restArtifactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(artifactDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Artifact in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        artifact.setName(null);

        // Create the Artifact, which fails.
        ArtifactDTO artifactDTO = artifactMapper.toDto(artifact);

        restArtifactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(artifactDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllArtifacts() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList
        restArtifactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(artifact.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].fileSize").value(hasItem(DEFAULT_FILE_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].addendum").value(hasItem(DEFAULT_ADDENDUM)));
    }

    @Test
    @Transactional
    void getArtifact() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get the artifact
        restArtifactMockMvc
            .perform(get(ENTITY_API_URL_ID, artifact.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(artifact.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.fileSize").value(DEFAULT_FILE_SIZE.intValue()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.addendum").value(DEFAULT_ADDENDUM));
    }

    @Test
    @Transactional
    void getArtifactsByIdFiltering() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        Long id = artifact.getId();

        defaultArtifactFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultArtifactFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultArtifactFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllArtifactsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where name equals to
        defaultArtifactFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllArtifactsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where name in
        defaultArtifactFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllArtifactsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where name is not null
        defaultArtifactFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllArtifactsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where name contains
        defaultArtifactFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllArtifactsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where name does not contain
        defaultArtifactFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllArtifactsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where description equals to
        defaultArtifactFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllArtifactsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where description in
        defaultArtifactFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllArtifactsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where description is not null
        defaultArtifactFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllArtifactsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where description contains
        defaultArtifactFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllArtifactsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where description does not contain
        defaultArtifactFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllArtifactsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where type equals to
        defaultArtifactFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllArtifactsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where type in
        defaultArtifactFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllArtifactsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where type is not null
        defaultArtifactFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllArtifactsByLinkIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where link equals to
        defaultArtifactFiltering("link.equals=" + DEFAULT_LINK, "link.equals=" + UPDATED_LINK);
    }

    @Test
    @Transactional
    void getAllArtifactsByLinkIsInShouldWork() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where link in
        defaultArtifactFiltering("link.in=" + DEFAULT_LINK + "," + UPDATED_LINK, "link.in=" + UPDATED_LINK);
    }

    @Test
    @Transactional
    void getAllArtifactsByLinkIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where link is not null
        defaultArtifactFiltering("link.specified=true", "link.specified=false");
    }

    @Test
    @Transactional
    void getAllArtifactsByLinkContainsSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where link contains
        defaultArtifactFiltering("link.contains=" + DEFAULT_LINK, "link.contains=" + UPDATED_LINK);
    }

    @Test
    @Transactional
    void getAllArtifactsByLinkNotContainsSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where link does not contain
        defaultArtifactFiltering("link.doesNotContain=" + UPDATED_LINK, "link.doesNotContain=" + DEFAULT_LINK);
    }

    @Test
    @Transactional
    void getAllArtifactsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where status equals to
        defaultArtifactFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllArtifactsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where status in
        defaultArtifactFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllArtifactsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where status is not null
        defaultArtifactFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllArtifactsByFileSizeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where fileSize equals to
        defaultArtifactFiltering("fileSize.equals=" + DEFAULT_FILE_SIZE, "fileSize.equals=" + UPDATED_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllArtifactsByFileSizeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where fileSize in
        defaultArtifactFiltering("fileSize.in=" + DEFAULT_FILE_SIZE + "," + UPDATED_FILE_SIZE, "fileSize.in=" + UPDATED_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllArtifactsByFileSizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where fileSize is not null
        defaultArtifactFiltering("fileSize.specified=true", "fileSize.specified=false");
    }

    @Test
    @Transactional
    void getAllArtifactsByFileSizeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where fileSize is greater than or equal to
        defaultArtifactFiltering("fileSize.greaterThanOrEqual=" + DEFAULT_FILE_SIZE, "fileSize.greaterThanOrEqual=" + UPDATED_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllArtifactsByFileSizeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where fileSize is less than or equal to
        defaultArtifactFiltering("fileSize.lessThanOrEqual=" + DEFAULT_FILE_SIZE, "fileSize.lessThanOrEqual=" + SMALLER_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllArtifactsByFileSizeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where fileSize is less than
        defaultArtifactFiltering("fileSize.lessThan=" + UPDATED_FILE_SIZE, "fileSize.lessThan=" + DEFAULT_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllArtifactsByFileSizeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where fileSize is greater than
        defaultArtifactFiltering("fileSize.greaterThan=" + SMALLER_FILE_SIZE, "fileSize.greaterThan=" + DEFAULT_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllArtifactsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where createdDate equals to
        defaultArtifactFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllArtifactsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where createdDate in
        defaultArtifactFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllArtifactsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where createdDate is not null
        defaultArtifactFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllArtifactsByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where lastModifiedDate equals to
        defaultArtifactFiltering(
            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    @Transactional
    void getAllArtifactsByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where lastModifiedDate in
        defaultArtifactFiltering(
            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    @Transactional
    void getAllArtifactsByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where lastModifiedDate is not null
        defaultArtifactFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllArtifactsByAddendumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where addendum equals to
        defaultArtifactFiltering("addendum.equals=" + DEFAULT_ADDENDUM, "addendum.equals=" + UPDATED_ADDENDUM);
    }

    @Test
    @Transactional
    void getAllArtifactsByAddendumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where addendum in
        defaultArtifactFiltering("addendum.in=" + DEFAULT_ADDENDUM + "," + UPDATED_ADDENDUM, "addendum.in=" + UPDATED_ADDENDUM);
    }

    @Test
    @Transactional
    void getAllArtifactsByAddendumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where addendum is not null
        defaultArtifactFiltering("addendum.specified=true", "addendum.specified=false");
    }

    @Test
    @Transactional
    void getAllArtifactsByAddendumContainsSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where addendum contains
        defaultArtifactFiltering("addendum.contains=" + DEFAULT_ADDENDUM, "addendum.contains=" + UPDATED_ADDENDUM);
    }

    @Test
    @Transactional
    void getAllArtifactsByAddendumNotContainsSomething() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList where addendum does not contain
        defaultArtifactFiltering("addendum.doesNotContain=" + UPDATED_ADDENDUM, "addendum.doesNotContain=" + DEFAULT_ADDENDUM);
    }

    @Test
    @Transactional
    void getAllArtifactsByUploadedByIsEqualToSomething() throws Exception {
        User uploadedBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            artifactRepository.saveAndFlush(artifact);
            uploadedBy = UserResourceIT.createEntity();
        } else {
            uploadedBy = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(uploadedBy);
        em.flush();
        artifact.setUploadedBy(uploadedBy);
        artifactRepository.saveAndFlush(artifact);
        Long uploadedById = uploadedBy.getId();
        // Get all the artifactList where uploadedBy equals to uploadedById
        defaultArtifactShouldBeFound("uploadedById.equals=" + uploadedById);

        // Get all the artifactList where uploadedBy equals to (uploadedById + 1)
        defaultArtifactShouldNotBeFound("uploadedById.equals=" + (uploadedById + 1));
    }

    @Test
    @Transactional
    void getAllArtifactsByProjectIsEqualToSomething() throws Exception {
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            artifactRepository.saveAndFlush(artifact);
            project = ProjectResourceIT.createEntity();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        em.persist(project);
        em.flush();
        artifact.addProject(project);
        artifactRepository.saveAndFlush(artifact);
        Long projectId = project.getId();
        // Get all the artifactList where project equals to projectId
        defaultArtifactShouldBeFound("projectId.equals=" + projectId);

        // Get all the artifactList where project equals to (projectId + 1)
        defaultArtifactShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    private void defaultArtifactFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultArtifactShouldBeFound(shouldBeFound);
        defaultArtifactShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultArtifactShouldBeFound(String filter) throws Exception {
        restArtifactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(artifact.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].fileSize").value(hasItem(DEFAULT_FILE_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].addendum").value(hasItem(DEFAULT_ADDENDUM)));

        // Check, that the count call also returns 1
        restArtifactMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultArtifactShouldNotBeFound(String filter) throws Exception {
        restArtifactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restArtifactMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingArtifact() throws Exception {
        // Get the artifact
        restArtifactMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingArtifact() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the artifact
        Artifact updatedArtifact = artifactRepository.findById(artifact.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedArtifact are not directly saved in db
        em.detach(updatedArtifact);
        updatedArtifact
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .link(UPDATED_LINK)
            .status(UPDATED_STATUS)
            .fileSize(UPDATED_FILE_SIZE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .addendum(UPDATED_ADDENDUM);
        ArtifactDTO artifactDTO = artifactMapper.toDto(updatedArtifact);

        restArtifactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, artifactDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(artifactDTO))
            )
            .andExpect(status().isOk());

        // Validate the Artifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedArtifactToMatchAllProperties(updatedArtifact);
    }

    @Test
    @Transactional
    void putNonExistingArtifact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        artifact.setId(longCount.incrementAndGet());

        // Create the Artifact
        ArtifactDTO artifactDTO = artifactMapper.toDto(artifact);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArtifactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, artifactDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(artifactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Artifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchArtifact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        artifact.setId(longCount.incrementAndGet());

        // Create the Artifact
        ArtifactDTO artifactDTO = artifactMapper.toDto(artifact);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtifactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(artifactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Artifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamArtifact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        artifact.setId(longCount.incrementAndGet());

        // Create the Artifact
        ArtifactDTO artifactDTO = artifactMapper.toDto(artifact);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtifactMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(artifactDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Artifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateArtifactWithPatch() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the artifact using partial update
        Artifact partialUpdatedArtifact = new Artifact();
        partialUpdatedArtifact.setId(artifact.getId());

        partialUpdatedArtifact.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).type(UPDATED_TYPE).fileSize(UPDATED_FILE_SIZE);

        restArtifactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArtifact.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedArtifact))
            )
            .andExpect(status().isOk());

        // Validate the Artifact in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertArtifactUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedArtifact, artifact), getPersistedArtifact(artifact));
    }

    @Test
    @Transactional
    void fullUpdateArtifactWithPatch() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the artifact using partial update
        Artifact partialUpdatedArtifact = new Artifact();
        partialUpdatedArtifact.setId(artifact.getId());

        partialUpdatedArtifact
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .link(UPDATED_LINK)
            .status(UPDATED_STATUS)
            .fileSize(UPDATED_FILE_SIZE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .addendum(UPDATED_ADDENDUM);

        restArtifactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArtifact.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedArtifact))
            )
            .andExpect(status().isOk());

        // Validate the Artifact in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertArtifactUpdatableFieldsEquals(partialUpdatedArtifact, getPersistedArtifact(partialUpdatedArtifact));
    }

    @Test
    @Transactional
    void patchNonExistingArtifact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        artifact.setId(longCount.incrementAndGet());

        // Create the Artifact
        ArtifactDTO artifactDTO = artifactMapper.toDto(artifact);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArtifactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, artifactDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(artifactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Artifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchArtifact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        artifact.setId(longCount.incrementAndGet());

        // Create the Artifact
        ArtifactDTO artifactDTO = artifactMapper.toDto(artifact);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtifactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(artifactDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Artifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamArtifact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        artifact.setId(longCount.incrementAndGet());

        // Create the Artifact
        ArtifactDTO artifactDTO = artifactMapper.toDto(artifact);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtifactMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(artifactDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Artifact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteArtifact() throws Exception {
        // Initialize the database
        insertedArtifact = artifactRepository.saveAndFlush(artifact);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the artifact
        restArtifactMockMvc
            .perform(delete(ENTITY_API_URL_ID, artifact.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return artifactRepository.count();
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

    protected Artifact getPersistedArtifact(Artifact artifact) {
        return artifactRepository.findById(artifact.getId()).orElseThrow();
    }

    protected void assertPersistedArtifactToMatchAllProperties(Artifact expectedArtifact) {
        assertArtifactAllPropertiesEquals(expectedArtifact, getPersistedArtifact(expectedArtifact));
    }

    protected void assertPersistedArtifactToMatchUpdatableProperties(Artifact expectedArtifact) {
        assertArtifactAllUpdatablePropertiesEquals(expectedArtifact, getPersistedArtifact(expectedArtifact));
    }
}
