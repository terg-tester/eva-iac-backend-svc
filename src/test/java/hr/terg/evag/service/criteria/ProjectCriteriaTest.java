package hr.terg.evag.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProjectCriteriaTest {

    @Test
    void newProjectCriteriaHasAllFiltersNullTest() {
        var projectCriteria = new ProjectCriteria();
        assertThat(projectCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void projectCriteriaFluentMethodsCreatesFiltersTest() {
        var projectCriteria = new ProjectCriteria();

        setAllFilters(projectCriteria);

        assertThat(projectCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void projectCriteriaCopyCreatesNullFilterTest() {
        var projectCriteria = new ProjectCriteria();
        var copy = projectCriteria.copy();

        assertThat(projectCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(projectCriteria)
        );
    }

    @Test
    void projectCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var projectCriteria = new ProjectCriteria();
        setAllFilters(projectCriteria);

        var copy = projectCriteria.copy();

        assertThat(projectCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(projectCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var projectCriteria = new ProjectCriteria();

        assertThat(projectCriteria).hasToString("ProjectCriteria{}");
    }

    private static void setAllFilters(ProjectCriteria projectCriteria) {
        projectCriteria.id();
        projectCriteria.name();
        projectCriteria.description();
        projectCriteria.status();
        projectCriteria.priority();
        projectCriteria.startDate();
        projectCriteria.endDate();
        projectCriteria.createdDate();
        projectCriteria.lastModifiedDate();
        projectCriteria.addendum();
        projectCriteria.deliverableId();
        projectCriteria.createdById();
        projectCriteria.artifactId();
        projectCriteria.distinct();
    }

    private static Condition<ProjectCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getPriority()) &&
                condition.apply(criteria.getStartDate()) &&
                condition.apply(criteria.getEndDate()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getAddendum()) &&
                condition.apply(criteria.getDeliverableId()) &&
                condition.apply(criteria.getCreatedById()) &&
                condition.apply(criteria.getArtifactId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProjectCriteria> copyFiltersAre(ProjectCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getPriority(), copy.getPriority()) &&
                condition.apply(criteria.getStartDate(), copy.getStartDate()) &&
                condition.apply(criteria.getEndDate(), copy.getEndDate()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getAddendum(), copy.getAddendum()) &&
                condition.apply(criteria.getDeliverableId(), copy.getDeliverableId()) &&
                condition.apply(criteria.getCreatedById(), copy.getCreatedById()) &&
                condition.apply(criteria.getArtifactId(), copy.getArtifactId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
