package SearchEngineApp.service;

import SearchEngineApp.models.Index;
import SearchEngineApp.models.Lemma;

import java.util.List;

public interface IndexService
{
    void saveIndex(Index index);
    Index getIndex(int lemmaId, int pageId);
    void updateIndex(Index index);
    List<Integer> getPages(int lemmaId);
    List<Index> getIndexes(Lemma lemma, List<Integer> pageList);
    void resetRanks (List<Integer> idList);
}
