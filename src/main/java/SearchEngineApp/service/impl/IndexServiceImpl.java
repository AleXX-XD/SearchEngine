package SearchEngineApp.service.impl;

import SearchEngineApp.repository.IndexRepository;
import SearchEngineApp.models.Index;
import SearchEngineApp.models.Lemma;
import SearchEngineApp.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexServiceImpl implements IndexService
{
    private final IndexRepository indexRepository;

    public IndexServiceImpl(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    @Override
    public void saveIndex(Index index) {
        indexRepository.save(index);
    }

    @Override
    public void resetRanks(List<Integer> idList) {
        indexRepository.deleteAllByLemmaIdIn(idList);
    }

    @Override
    public Index getIndex(int lemmaId, int pageId) {
        return indexRepository.findByLemmaIdAndPageId(lemmaId, pageId);
    }

    @Override
    public void updateIndex(Index index) {

    }

    @Override
    public List<Integer> getPages(int lemmaId) {
        return null;
    }

    @Override
    public List<Index> getIndexes(Lemma lemma, List<Integer> pageList) {
        return null;
    }


}
