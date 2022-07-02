package SearchEngineApp.services;

import SearchEngineApp.dao.LemmaDao;
import SearchEngineApp.models.Lemma;

import java.util.List;

public class LemmaService
{
    private LemmaDao lemmaDao = new LemmaDao();

    public LemmaService() {}

    public void saveLemma(Lemma lemma) {
        lemmaDao.save(lemma);
    }

    public Lemma getLemma(String lemma, int siteId) {
        return lemmaDao.get(lemma, siteId);
    }

    public List<Lemma> getLemmas(List<String> nameList) {
        return lemmaDao.getLemmas(nameList);
    }
}
