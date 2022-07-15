package SearchEngineApp.repository;

import SearchEngineApp.models.Lemma;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma, Integer>
{
    Lemma findByLemmaAndSiteId(String lemma, int siteId);
    Iterable<Lemma> findAllByLemmaIn(List<String> lemmas);
    void deleteAllBySiteId(int siteId);
    Iterable<Lemma> findAllBySiteId(int siteId);

}
