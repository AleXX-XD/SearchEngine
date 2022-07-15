package SearchEngineApp.repository;

import SearchEngineApp.models.Index;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexRepository extends CrudRepository<Index, Integer>
{
    void deleteAllByLemmaIdIn(List<Integer> lemmaList);
    Index findByLemmaIdAndPageId(int lemmaId, int pageId);
}
