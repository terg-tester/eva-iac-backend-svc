package hr.terg.evag.service.mapper;

import static hr.terg.evag.domain.DeliverableAsserts.*;
import static hr.terg.evag.domain.DeliverableTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeliverableMapperTest {

    private DeliverableMapper deliverableMapper;

    @BeforeEach
    void setUp() {
        deliverableMapper = new DeliverableMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDeliverableSample1();
        var actual = deliverableMapper.toEntity(deliverableMapper.toDto(expected));
        assertDeliverableAllPropertiesEquals(expected, actual);
    }
}
