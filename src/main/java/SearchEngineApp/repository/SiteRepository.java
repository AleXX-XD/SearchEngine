package SearchEngineApp.repository;

import SearchEngineApp.models.Site;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends CrudRepository<Site,Integer> {

    Site findByUrl(String url);
    @Query(value = "SELECT count(s) FROM Site s WHERE s.status = 'INDEXING'")
    long countByStatusIndexing();
}
