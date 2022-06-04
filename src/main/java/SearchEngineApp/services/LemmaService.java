package SearchEngineApp.services;

import SearchEngineApp.dao.LemmaDao;
import SearchEngineApp.models.Lemma;

public class LemmaService
{
    private LemmaDao lemmaDao = new LemmaDao();

    public LemmaService() {}

    public void saveLemma(Lemma lemma) {
        lemmaDao.save(lemma);
    }

    public Lemma getLemma(String lemma) {
        return lemmaDao.get(lemma);
    }
}
