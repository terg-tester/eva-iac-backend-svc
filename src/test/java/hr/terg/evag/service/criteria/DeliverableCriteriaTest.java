package hr.terg.evag.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DeliverableCriteriaTest {

    @Test
    void newDeliverableCriteriaHasAllFiltersNullTest() {
        var deliverableCriteria = new DeliverableCriteria();
        assertThat(deliverableCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void deliverableCriteriaFluentMethodsCreatesFiltersTest() {
        var deliverableCriteria = new DeliverableCriteria();

        setAllFilters(deliverableCriteria);

        assertThat(deliverableCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void deliverableCriteriaCopyCreatesNullFilterTest() {
        var deliverableCriteria = new DeliverableCriteria();
        var copy = deliverableCriteria.copy();

        assertThat(deliverableCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(deliverableCriteria)
        );
    }

    @Test
    void deliverableCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var deliverableCriteria = new DeliverableCriteria();
        setAllFilters(deliverableCriteria);

        var copy = deliverableCriteria.copy();

        assertThat(deliverableCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(deliverableCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var deliverableCriteria = new DeliverableCriteria();

        assertThat(deliverableCriteria).hasToString("DeliverableCriteria{}");
    }

    private static void setAllFilters(DeliverableCriteria deliverableCriteria) {
        deliverableCriteria.id();
        deliverableCriteria.name();
        deliverableCriteria.description();
        deliverableCriteria.type();
        deliverableCriteria.format();
        deliverableCriteria.status();
        deliverableCriteria.packagePath();
        deliverableCriteria.packageSize();
        deliverableCriteria.checksum();
        deliverableCriteria.createdDate();
        deliverableCriteria.lastModifiedDate();
        deliverableCriteria.addendum();
        deliverableCriteria.deploymentId();
        deliverableCriteria.createdById();
        deliverableCriteria.projectId();
        deliverableCriteria.distinct();
    }

    private static Condition<DeliverableCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getFormat()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getPackagePath()) &&
                condition.apply(criteria.getPackageSize()) &&
                condition.apply(criteria.getChecksum()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getAddendum()) &&
                condition.apply(criteria.getDeploymentId()) &&
                condition.apply(criteria.getCreatedById()) &&
                condition.apply(criteria.getProjectId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DeliverableCriteria> copyFiltersAre(DeliverableCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getFormat(), copy.getFormat()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getPackagePath(), copy.getPackagePath()) &&
                condition.apply(criteria.getPackageSize(), copy.getPackageSize()) &&
                condition.apply(criteria.getChecksum(), copy.getChecksum()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getAddendum(), copy.getAddendum()) &&
                condition.apply(criteria.getDeploymentId(), copy.getDeploymentId()) &&
                condition.apply(criteria.getCreatedById(), copy.getCreatedById()) &&
                condition.apply(criteria.getProjectId(), copy.getProjectId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
