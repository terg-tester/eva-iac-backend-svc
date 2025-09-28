package hr.terg.evag.repository;

import hr.terg.evag.domain.Deliverable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Deliverable entity.
 */
@Repository
public interface DeliverableRepository extends JpaRepository<Deliverable, Long>, JpaSpecificationExecutor<Deliverable> {
    @Query("select deliverable from Deliverable deliverable where deliverable.createdBy.login = ?#{authentication.name}")
    List<Deliverable> findByCreatedByIsCurrentUser();

    default Optional<Deliverable> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Deliverable> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Deliverable> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select deliverable from Deliverable deliverable left join fetch deliverable.project",
        countQuery = "select count(deliverable) from Deliverable deliverable"
    )
    Page<Deliverable> findAllWithToOneRelationships(Pageable pageable);

    @Query("select deliverable from Deliverable deliverable left join fetch deliverable.project")
    List<Deliverable> findAllWithToOneRelationships();

    @Query("select deliverable from Deliverable deliverable left join fetch deliverable.project where deliverable.id =:id")
    Optional<Deliverable> findOneWithToOneRelationships(@Param("id") Long id);
}
