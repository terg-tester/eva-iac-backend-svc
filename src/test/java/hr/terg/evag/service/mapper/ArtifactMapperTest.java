package hr.terg.evag.service.mapper;

import static hr.terg.evag.domain.ArtifactAsserts.*;
import static hr.terg.evag.domain.ArtifactTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArtifactMapperTest {

    private ArtifactMapper artifactMapper;

    @BeforeEach
    void setUp() {
        artifactMapper = new ArtifactMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getArtifactSample1();
        var actual = artifactMapper.toEntity(artifactMapper.toDto(expected));
        assertArtifactAllPropertiesEquals(expected, actual);
    }
}
