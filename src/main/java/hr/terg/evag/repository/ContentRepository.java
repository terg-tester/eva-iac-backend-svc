package hr.terg.evag.repository;

import hr.terg.evag.domain.Content;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Content entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContentRepository extends JpaRepository<Content, UUID>, JpaSpecificationExecutor<Content> {}
