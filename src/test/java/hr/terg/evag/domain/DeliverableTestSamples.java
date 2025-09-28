package hr.terg.evag.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DeliverableTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Deliverable getDeliverableSample1() {
        return new Deliverable()
            .id(1L)
            .name("name1")
            .description("description1")
            .packagePath("packagePath1")
            .packageSize(1L)
            .checksum("checksum1")
            .addendum("addendum1");
    }

    public static Deliverable getDeliverableSample2() {
        return new Deliverable()
            .id(2L)
            .name("name2")
            .description("description2")
            .packagePath("packagePath2")
            .packageSize(2L)
            .checksum("checksum2")
            .addendum("addendum2");
    }

    public static Deliverable getDeliverableRandomSampleGenerator() {
        return new Deliverable()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .packagePath(UUID.randomUUID().toString())
            .packageSize(longCount.incrementAndGet())
            .checksum(UUID.randomUUID().toString())
            .addendum(UUID.randomUUID().toString());
    }
}
