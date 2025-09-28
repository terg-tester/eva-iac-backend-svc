package hr.terg.evag.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import hr.terg.evag.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeliverableDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeliverableDTO.class);
        DeliverableDTO deliverableDTO1 = new DeliverableDTO();
        deliverableDTO1.setId(1L);
        DeliverableDTO deliverableDTO2 = new DeliverableDTO();
        assertThat(deliverableDTO1).isNotEqualTo(deliverableDTO2);
        deliverableDTO2.setId(deliverableDTO1.getId());
        assertThat(deliverableDTO1).isEqualTo(deliverableDTO2);
        deliverableDTO2.setId(2L);
        assertThat(deliverableDTO1).isNotEqualTo(deliverableDTO2);
        deliverableDTO1.setId(null);
        assertThat(deliverableDTO1).isNotEqualTo(deliverableDTO2);
    }
}
