package SearchEngineApp.repository;

import SearchEngineApp.models.Lemma;
import SearchEngineApp.models.Site;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma, Long>
{
    Lemma findByLemmaAndSiteId(String lemma, long siteId);
    List<Lemma> findAllByLemmaIn(List<String> lemmas);
    List<Lemma> findAllBySiteId(long siteId);

}
