package hr.terg.evag.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import hr.terg.evag.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ArtifactDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ArtifactDTO.class);
        ArtifactDTO artifactDTO1 = new ArtifactDTO();
        artifactDTO1.setId(1L);
        ArtifactDTO artifactDTO2 = new ArtifactDTO();
        assertThat(artifactDTO1).isNotEqualTo(artifactDTO2);
        artifactDTO2.setId(artifactDTO1.getId());
        assertThat(artifactDTO1).isEqualTo(artifactDTO2);
        artifactDTO2.setId(2L);
        assertThat(artifactDTO1).isNotEqualTo(artifactDTO2);
        artifactDTO1.setId(null);
        assertThat(artifactDTO1).isNotEqualTo(artifactDTO2);
    }
}
