package hr.terg.evag.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DeploymentCriteriaTest {

    @Test
    void newDeploymentCriteriaHasAllFiltersNullTest() {
        var deploymentCriteria = new DeploymentCriteria();
        assertThat(deploymentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void deploymentCriteriaFluentMethodsCreatesFiltersTest() {
        var deploymentCriteria = new DeploymentCriteria();

        setAllFilters(deploymentCriteria);

        assertThat(deploymentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void deploymentCriteriaCopyCreatesNullFilterTest() {
        var deploymentCriteria = new DeploymentCriteria();
        var copy = deploymentCriteria.copy();

        assertThat(deploymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(deploymentCriteria)
        );
    }

    @Test
    void deploymentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var deploymentCriteria = new DeploymentCriteria();
        setAllFilters(deploymentCriteria);

        var copy = deploymentCriteria.copy();

        assertThat(deploymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(deploymentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var deploymentCriteria = new DeploymentCriteria();

        assertThat(deploymentCriteria).hasToString("DeploymentCriteria{}");
    }

    private static void setAllFilters(DeploymentCriteria deploymentCriteria) {
        deploymentCriteria.id();
        deploymentCriteria.deploymentDate();
        deploymentCriteria.status();
        deploymentCriteria.addendum();
        deploymentCriteria.deployedById();
        deploymentCriteria.deliverableId();
        deploymentCriteria.distinct();
    }

    private static Condition<DeploymentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDeploymentDate()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getAddendum()) &&
                condition.apply(criteria.getDeployedById()) &&
                condition.apply(criteria.getDeliverableId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DeploymentCriteria> copyFiltersAre(DeploymentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDeploymentDate(), copy.getDeploymentDate()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getAddendum(), copy.getAddendum()) &&
                condition.apply(criteria.getDeployedById(), copy.getDeployedById()) &&
                condition.apply(criteria.getDeliverableId(), copy.getDeliverableId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
