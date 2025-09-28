package hr.terg.evag.service.mapper;

import static hr.terg.evag.domain.DeploymentAsserts.*;
import static hr.terg.evag.domain.DeploymentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeploymentMapperTest {

    private DeploymentMapper deploymentMapper;

    @BeforeEach
    void setUp() {
        deploymentMapper = new DeploymentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDeploymentSample1();
        var actual = deploymentMapper.toEntity(deploymentMapper.toDto(expected));
        assertDeploymentAllPropertiesEquals(expected, actual);
    }
}
