package hr.terg.evag.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ContentCriteriaTest {

    @Test
    void newContentCriteriaHasAllFiltersNullTest() {
        var contentCriteria = new ContentCriteria();
        assertThat(contentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void contentCriteriaFluentMethodsCreatesFiltersTest() {
        var contentCriteria = new ContentCriteria();

        setAllFilters(contentCriteria);

        assertThat(contentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void contentCriteriaCopyCreatesNullFilterTest() {
        var contentCriteria = new ContentCriteria();
        var copy = contentCriteria.copy();

        assertThat(contentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(contentCriteria)
        );
    }

    @Test
    void contentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var contentCriteria = new ContentCriteria();
        setAllFilters(contentCriteria);

        var copy = contentCriteria.copy();

        assertThat(contentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(contentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var contentCriteria = new ContentCriteria();

        assertThat(contentCriteria).hasToString("ContentCriteria{}");
    }

    private static void setAllFilters(ContentCriteria contentCriteria) {
        contentCriteria.id();
        contentCriteria.fileName();
        contentCriteria.dateCreated();
        contentCriteria.distinct();
    }

    private static Condition<ContentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFileName()) &&
                condition.apply(criteria.getDateCreated()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ContentCriteria> copyFiltersAre(ContentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFileName(), copy.getFileName()) &&
                condition.apply(criteria.getDateCreated(), copy.getDateCreated()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
