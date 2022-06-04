package SearchEngineApp.services;

import SearchEngineApp.dao.IndexDao;
import SearchEngineApp.models.Index;

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
}
