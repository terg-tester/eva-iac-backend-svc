package hr.terg.evag.web.rest;

import static hr.terg.evag.domain.ContentAsserts.*;
import static hr.terg.evag.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.terg.evag.IntegrationTest;
import hr.terg.evag.domain.Content;
import hr.terg.evag.repository.ContentRepository;
import hr.terg.evag.service.dto.ContentDTO;
import hr.terg.evag.service.mapper.ContentMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;
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
 * Integration tests for the {@link ContentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContentResourceIT {

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final byte[] DEFAULT_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CONTENT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CONTENT_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/contents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContentMockMvc;

    private Content content;

    private Content insertedContent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Content createEntity() {
        return new Content()
            .fileName(DEFAULT_FILE_NAME)
            .dateCreated(DEFAULT_DATE_CREATED)
            .content(DEFAULT_CONTENT)
            .contentContentType(DEFAULT_CONTENT_CONTENT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Content createUpdatedEntity() {
        return new Content()
            .fileName(UPDATED_FILE_NAME)
            .dateCreated(UPDATED_DATE_CREATED)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE);
    }

    @BeforeEach
    void initTest() {
        content = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedContent != null) {
            contentRepository.delete(insertedContent);
            insertedContent = null;
        }
    }

    @Test
    @Transactional
    void createContent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);
        var returnedContentDTO = om.readValue(
            restContentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ContentDTO.class
        );

        // Validate the Content in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedContent = contentMapper.toEntity(returnedContentDTO);
        assertContentUpdatableFieldsEquals(returnedContent, getPersistedContent(returnedContent));

        insertedContent = returnedContent;
    }

    @Test
    @Transactional
    void createContentWithExistingId() throws Exception {
        // Create the Content with an existing ID
        insertedContent = contentRepository.saveAndFlush(content);
        ContentDTO contentDTO = contentMapper.toDto(content);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Content in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFileNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        content.setFileName(null);

        // Create the Content, which fails.
        ContentDTO contentDTO = contentMapper.toDto(content);

        restContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreatedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        content.setDateCreated(null);

        // Create the Content, which fails.
        ContentDTO contentDTO = contentMapper.toDto(content);

        restContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllContents() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        // Get all the contentList
        restContentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(content.getId().toString())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())))
            .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_CONTENT))));
    }

    @Test
    @Transactional
    void getContent() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        // Get the content
        restContentMockMvc
            .perform(get(ENTITY_API_URL_ID, content.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(content.getId().toString()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.dateCreated").value(DEFAULT_DATE_CREATED.toString()))
            .andExpect(jsonPath("$.contentContentType").value(DEFAULT_CONTENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.content").value(Base64.getEncoder().encodeToString(DEFAULT_CONTENT)));
    }

    @Test
    @Transactional
    void getContentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        UUID id = content.getId();

        defaultContentFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllContentsByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        // Get all the contentList where fileName equals to
        defaultContentFiltering("fileName.equals=" + DEFAULT_FILE_NAME, "fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllContentsByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        // Get all the contentList where fileName in
        defaultContentFiltering("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME, "fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllContentsByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        // Get all the contentList where fileName is not null
        defaultContentFiltering("fileName.specified=true", "fileName.specified=false");
    }

    @Test
    @Transactional
    void getAllContentsByFileNameContainsSomething() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        // Get all the contentList where fileName contains
        defaultContentFiltering("fileName.contains=" + DEFAULT_FILE_NAME, "fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllContentsByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        // Get all the contentList where fileName does not contain
        defaultContentFiltering("fileName.doesNotContain=" + UPDATED_FILE_NAME, "fileName.doesNotContain=" + DEFAULT_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllContentsByDateCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        // Get all the contentList where dateCreated equals to
        defaultContentFiltering("dateCreated.equals=" + DEFAULT_DATE_CREATED, "dateCreated.equals=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllContentsByDateCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        // Get all the contentList where dateCreated in
        defaultContentFiltering(
            "dateCreated.in=" + DEFAULT_DATE_CREATED + "," + UPDATED_DATE_CREATED,
            "dateCreated.in=" + UPDATED_DATE_CREATED
        );
    }

    @Test
    @Transactional
    void getAllContentsByDateCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        // Get all the contentList where dateCreated is not null
        defaultContentFiltering("dateCreated.specified=true", "dateCreated.specified=false");
    }

    private void defaultContentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultContentShouldBeFound(shouldBeFound);
        defaultContentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultContentShouldBeFound(String filter) throws Exception {
        restContentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(content.getId().toString())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())))
            .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_CONTENT))));

        // Check, that the count call also returns 1
        restContentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultContentShouldNotBeFound(String filter) throws Exception {
        restContentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restContentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingContent() throws Exception {
        // Get the content
        restContentMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingContent() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the content
        Content updatedContent = contentRepository.findById(content.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedContent are not directly saved in db
        em.detach(updatedContent);
        updatedContent
            .fileName(UPDATED_FILE_NAME)
            .dateCreated(UPDATED_DATE_CREATED)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE);
        ContentDTO contentDTO = contentMapper.toDto(updatedContent);

        restContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contentDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Content in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedContentToMatchAllProperties(updatedContent);
    }

    @Test
    @Transactional
    void putNonExistingContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        content.setId(UUID.randomUUID());

        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contentDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Content in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        content.setId(UUID.randomUUID());

        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Content in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        content.setId(UUID.randomUUID());

        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Content in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContentWithPatch() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the content using partial update
        Content partialUpdatedContent = new Content();
        partialUpdatedContent.setId(content.getId());

        partialUpdatedContent.fileName(UPDATED_FILE_NAME);

        restContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContent))
            )
            .andExpect(status().isOk());

        // Validate the Content in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedContent, content), getPersistedContent(content));
    }

    @Test
    @Transactional
    void fullUpdateContentWithPatch() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the content using partial update
        Content partialUpdatedContent = new Content();
        partialUpdatedContent.setId(content.getId());

        partialUpdatedContent
            .fileName(UPDATED_FILE_NAME)
            .dateCreated(UPDATED_DATE_CREATED)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE);

        restContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContent))
            )
            .andExpect(status().isOk());

        // Validate the Content in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContentUpdatableFieldsEquals(partialUpdatedContent, getPersistedContent(partialUpdatedContent));
    }

    @Test
    @Transactional
    void patchNonExistingContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        content.setId(UUID.randomUUID());

        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Content in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        content.setId(UUID.randomUUID());

        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Content in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        content.setId(UUID.randomUUID());

        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(contentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Content in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteContent() throws Exception {
        // Initialize the database
        insertedContent = contentRepository.saveAndFlush(content);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the content
        restContentMockMvc
            .perform(delete(ENTITY_API_URL_ID, content.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return contentRepository.count();
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

    protected Content getPersistedContent(Content content) {
        return contentRepository.findById(content.getId()).orElseThrow();
    }

    protected void assertPersistedContentToMatchAllProperties(Content expectedContent) {
        assertContentAllPropertiesEquals(expectedContent, getPersistedContent(expectedContent));
    }

    protected void assertPersistedContentToMatchUpdatableProperties(Content expectedContent) {
        assertContentAllUpdatablePropertiesEquals(expectedContent, getPersistedContent(expectedContent));
    }
}
