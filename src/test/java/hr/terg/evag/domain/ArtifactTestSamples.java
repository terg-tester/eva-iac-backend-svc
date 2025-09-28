package hr.terg.evag.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ArtifactTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Artifact getArtifactSample1() {
        return new Artifact().id(1L).name("name1").description("description1").link("link1").fileSize(1L).addendum("addendum1");
    }

    public static Artifact getArtifactSample2() {
        return new Artifact().id(2L).name("name2").description("description2").link("link2").fileSize(2L).addendum("addendum2");
    }

    public static Artifact getArtifactRandomSampleGenerator() {
        return new Artifact()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .link(UUID.randomUUID().toString())
            .fileSize(longCount.incrementAndGet())
            .addendum(UUID.randomUUID().toString());
    }
}
