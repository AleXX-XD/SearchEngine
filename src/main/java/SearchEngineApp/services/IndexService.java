package SearchEngineApp.services;

import SearchEngineApp.dao.IndexDao;
import SearchEngineApp.models.Index;
import SearchEngineApp.models.Lemma;

import java.util.List;

public class IndexService
{
    private IndexDao indexDao = new IndexDao();

    public IndexService() {}

    public void saveIndex(Index index) {
        indexDao.save(index);
    }

    public Index getIndex(int lemmaId, int pageId) {
        return indexDao.get(lemmaId, pageId);
    }

    public void updateIndex(Index index) {
        indexDao.update(index);
    }

    public List<Integer> getPages(int lemmaId) {
        return indexDao.getPages(lemmaId);
    }

    public List<Index> getIndexes(Lemma lemma, List<Integer> pageList) {
        return indexDao.getIndexes(lemma, pageList);
    }
}
