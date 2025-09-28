package hr.terg.evag.repository;

import hr.terg.evag.domain.Artifact;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Artifact entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArtifactRepository extends JpaRepository<Artifact, Long>, JpaSpecificationExecutor<Artifact> {
    @Query("select artifact from Artifact artifact where artifact.uploadedBy.login = ?#{authentication.name}")
    List<Artifact> findByUploadedByIsCurrentUser();
}
