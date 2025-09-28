package hr.terg.evag.domain;

import static hr.terg.evag.domain.ArtifactTestSamples.*;
import static hr.terg.evag.domain.DeliverableTestSamples.*;
import static hr.terg.evag.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import hr.terg.evag.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Project.class);
        Project project1 = getProjectSample1();
        Project project2 = new Project();
        assertThat(project1).isNotEqualTo(project2);

        project2.setId(project1.getId());
        assertThat(project1).isEqualTo(project2);

        project2 = getProjectSample2();
        assertThat(project1).isNotEqualTo(project2);
    }

    @Test
    void deliverableTest() {
        Project project = getProjectRandomSampleGenerator();
        Deliverable deliverableBack = getDeliverableRandomSampleGenerator();

        project.addDeliverable(deliverableBack);
        assertThat(project.getDeliverables()).containsOnly(deliverableBack);
        assertThat(deliverableBack.getProject()).isEqualTo(project);

        project.removeDeliverable(deliverableBack);
        assertThat(project.getDeliverables()).doesNotContain(deliverableBack);
        assertThat(deliverableBack.getProject()).isNull();

        project.deliverables(new HashSet<>(Set.of(deliverableBack)));
        assertThat(project.getDeliverables()).containsOnly(deliverableBack);
        assertThat(deliverableBack.getProject()).isEqualTo(project);

        project.setDeliverables(new HashSet<>());
        assertThat(project.getDeliverables()).doesNotContain(deliverableBack);
        assertThat(deliverableBack.getProject()).isNull();
    }

    @Test
    void artifactTest() {
        Project project = getProjectRandomSampleGenerator();
        Artifact artifactBack = getArtifactRandomSampleGenerator();

        project.addArtifact(artifactBack);
        assertThat(project.getArtifacts()).containsOnly(artifactBack);

        project.removeArtifact(artifactBack);
        assertThat(project.getArtifacts()).doesNotContain(artifactBack);

        project.artifacts(new HashSet<>(Set.of(artifactBack)));
        assertThat(project.getArtifacts()).containsOnly(artifactBack);

        project.setArtifacts(new HashSet<>());
        assertThat(project.getArtifacts()).doesNotContain(artifactBack);
    }
}
