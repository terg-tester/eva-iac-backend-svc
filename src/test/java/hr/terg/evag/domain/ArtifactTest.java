package hr.terg.evag.domain;

import static hr.terg.evag.domain.ArtifactTestSamples.*;
import static hr.terg.evag.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import hr.terg.evag.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ArtifactTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Artifact.class);
        Artifact artifact1 = getArtifactSample1();
        Artifact artifact2 = new Artifact();
        assertThat(artifact1).isNotEqualTo(artifact2);

        artifact2.setId(artifact1.getId());
        assertThat(artifact1).isEqualTo(artifact2);

        artifact2 = getArtifactSample2();
        assertThat(artifact1).isNotEqualTo(artifact2);
    }

    @Test
    void projectTest() {
        Artifact artifact = getArtifactRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        artifact.addProject(projectBack);
        assertThat(artifact.getProjects()).containsOnly(projectBack);
        assertThat(projectBack.getArtifacts()).containsOnly(artifact);

        artifact.removeProject(projectBack);
        assertThat(artifact.getProjects()).doesNotContain(projectBack);
        assertThat(projectBack.getArtifacts()).doesNotContain(artifact);

        artifact.projects(new HashSet<>(Set.of(projectBack)));
        assertThat(artifact.getProjects()).containsOnly(projectBack);
        assertThat(projectBack.getArtifacts()).containsOnly(artifact);

        artifact.setProjects(new HashSet<>());
        assertThat(artifact.getProjects()).doesNotContain(projectBack);
        assertThat(projectBack.getArtifacts()).doesNotContain(artifact);
    }
}
