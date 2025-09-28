package hr.terg.evag.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import hr.terg.evag.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeploymentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeploymentDTO.class);
        DeploymentDTO deploymentDTO1 = new DeploymentDTO();
        deploymentDTO1.setId(1L);
        DeploymentDTO deploymentDTO2 = new DeploymentDTO();
        assertThat(deploymentDTO1).isNotEqualTo(deploymentDTO2);
        deploymentDTO2.setId(deploymentDTO1.getId());
        assertThat(deploymentDTO1).isEqualTo(deploymentDTO2);
        deploymentDTO2.setId(2L);
        assertThat(deploymentDTO1).isNotEqualTo(deploymentDTO2);
        deploymentDTO1.setId(null);
        assertThat(deploymentDTO1).isNotEqualTo(deploymentDTO2);
    }
}
