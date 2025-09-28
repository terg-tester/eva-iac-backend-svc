package hr.terg.evag.domain;

import static hr.terg.evag.domain.DeliverableTestSamples.*;
import static hr.terg.evag.domain.DeploymentTestSamples.*;
import static hr.terg.evag.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import hr.terg.evag.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DeliverableTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Deliverable.class);
        Deliverable deliverable1 = getDeliverableSample1();
        Deliverable deliverable2 = new Deliverable();
        assertThat(deliverable1).isNotEqualTo(deliverable2);

        deliverable2.setId(deliverable1.getId());
        assertThat(deliverable1).isEqualTo(deliverable2);

        deliverable2 = getDeliverableSample2();
        assertThat(deliverable1).isNotEqualTo(deliverable2);
    }

    @Test
    void deploymentTest() {
        Deliverable deliverable = getDeliverableRandomSampleGenerator();
        Deployment deploymentBack = getDeploymentRandomSampleGenerator();

        deliverable.addDeployment(deploymentBack);
        assertThat(deliverable.getDeployments()).containsOnly(deploymentBack);
        assertThat(deploymentBack.getDeliverable()).isEqualTo(deliverable);

        deliverable.removeDeployment(deploymentBack);
        assertThat(deliverable.getDeployments()).doesNotContain(deploymentBack);
        assertThat(deploymentBack.getDeliverable()).isNull();

        deliverable.deployments(new HashSet<>(Set.of(deploymentBack)));
        assertThat(deliverable.getDeployments()).containsOnly(deploymentBack);
        assertThat(deploymentBack.getDeliverable()).isEqualTo(deliverable);

        deliverable.setDeployments(new HashSet<>());
        assertThat(deliverable.getDeployments()).doesNotContain(deploymentBack);
        assertThat(deploymentBack.getDeliverable()).isNull();
    }

    @Test
    void projectTest() {
        Deliverable deliverable = getDeliverableRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        deliverable.setProject(projectBack);
        assertThat(deliverable.getProject()).isEqualTo(projectBack);

        deliverable.project(null);
        assertThat(deliverable.getProject()).isNull();
    }
}
