package hr.terg.evag.repository;

import hr.terg.evag.domain.Deployment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Deployment entity.
 */
@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, Long>, JpaSpecificationExecutor<Deployment> {
    @Query("select deployment from Deployment deployment where deployment.deployedBy.login = ?#{authentication.name}")
    List<Deployment> findByDeployedByIsCurrentUser();

    default Optional<Deployment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Deployment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Deployment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select deployment from Deployment deployment left join fetch deployment.deliverable",
        countQuery = "select count(deployment) from Deployment deployment"
    )
    Page<Deployment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select deployment from Deployment deployment left join fetch deployment.deliverable")
    List<Deployment> findAllWithToOneRelationships();

    @Query("select deployment from Deployment deployment left join fetch deployment.deliverable where deployment.id =:id")
    Optional<Deployment> findOneWithToOneRelationships(@Param("id") Long id);
}
