package hr.terg.evag.domain;

import static hr.terg.evag.domain.ContentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import hr.terg.evag.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ContentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Content.class);
        Content content1 = getContentSample1();
        Content content2 = new Content();
        assertThat(content1).isNotEqualTo(content2);

        content2.setId(content1.getId());
        assertThat(content1).isEqualTo(content2);

        content2 = getContentSample2();
        assertThat(content1).isNotEqualTo(content2);
    }
}
