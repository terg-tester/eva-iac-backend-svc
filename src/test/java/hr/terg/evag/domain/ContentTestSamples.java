package hr.terg.evag.domain;

import java.util.UUID;

public class ContentTestSamples {

    public static Content getContentSample1() {
        return new Content().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).fileName("fileName1");
    }

    public static Content getContentSample2() {
        return new Content().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).fileName("fileName2");
    }

    public static Content getContentRandomSampleGenerator() {
        return new Content().id(UUID.randomUUID()).fileName(UUID.randomUUID().toString());
    }
}
