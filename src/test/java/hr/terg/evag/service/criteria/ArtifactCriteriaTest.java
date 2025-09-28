package hr.terg.evag.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ArtifactCriteriaTest {

    @Test
    void newArtifactCriteriaHasAllFiltersNullTest() {
        var artifactCriteria = new ArtifactCriteria();
        assertThat(artifactCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void artifactCriteriaFluentMethodsCreatesFiltersTest() {
        var artifactCriteria = new ArtifactCriteria();

        setAllFilters(artifactCriteria);

        assertThat(artifactCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void artifactCriteriaCopyCreatesNullFilterTest() {
        var artifactCriteria = new ArtifactCriteria();
        var copy = artifactCriteria.copy();

        assertThat(artifactCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(artifactCriteria)
        );
    }

    @Test
    void artifactCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var artifactCriteria = new ArtifactCriteria();
        setAllFilters(artifactCriteria);

        var copy = artifactCriteria.copy();

        assertThat(artifactCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(artifactCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var artifactCriteria = new ArtifactCriteria();

        assertThat(artifactCriteria).hasToString("ArtifactCriteria{}");
    }

    private static void setAllFilters(ArtifactCriteria artifactCriteria) {
        artifactCriteria.id();
        artifactCriteria.name();
        artifactCriteria.description();
        artifactCriteria.type();
        artifactCriteria.link();
        artifactCriteria.status();
        artifactCriteria.fileSize();
        artifactCriteria.createdDate();
        artifactCriteria.lastModifiedDate();
        artifactCriteria.addendum();
        artifactCriteria.uploadedById();
        artifactCriteria.projectId();
        artifactCriteria.distinct();
    }

    private static Condition<ArtifactCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getLink()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getFileSize()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getAddendum()) &&
                condition.apply(criteria.getUploadedById()) &&
                condition.apply(criteria.getProjectId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ArtifactCriteria> copyFiltersAre(ArtifactCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getLink(), copy.getLink()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getFileSize(), copy.getFileSize()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getAddendum(), copy.getAddendum()) &&
                condition.apply(criteria.getUploadedById(), copy.getUploadedById()) &&
                condition.apply(criteria.getProjectId(), copy.getProjectId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
