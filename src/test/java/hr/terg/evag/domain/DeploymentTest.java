package hr.terg.evag.domain;

import static hr.terg.evag.domain.DeliverableTestSamples.*;
import static hr.terg.evag.domain.DeploymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import hr.terg.evag.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeploymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Deployment.class);
        Deployment deployment1 = getDeploymentSample1();
        Deployment deployment2 = new Deployment();
        assertThat(deployment1).isNotEqualTo(deployment2);

        deployment2.setId(deployment1.getId());
        assertThat(deployment1).isEqualTo(deployment2);

        deployment2 = getDeploymentSample2();
        assertThat(deployment1).isNotEqualTo(deployment2);
    }

    @Test
    void deliverableTest() {
        Deployment deployment = getDeploymentRandomSampleGenerator();
        Deliverable deliverableBack = getDeliverableRandomSampleGenerator();

        deployment.setDeliverable(deliverableBack);
        assertThat(deployment.getDeliverable()).isEqualTo(deliverableBack);

        deployment.deliverable(null);
        assertThat(deployment.getDeliverable()).isNull();
    }
}
