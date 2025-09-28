package hr.terg.evag.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DeploymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Deployment getDeploymentSample1() {
        return new Deployment().id(1L).addendum("addendum1");
    }

    public static Deployment getDeploymentSample2() {
        return new Deployment().id(2L).addendum("addendum2");
    }

    public static Deployment getDeploymentRandomSampleGenerator() {
        return new Deployment().id(longCount.incrementAndGet()).addendum(UUID.randomUUID().toString());
    }
}
