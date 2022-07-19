package SearchEngineApp.service;

import SearchEngineApp.models.Lemma;

import java.util.List;

public interface LemmaService
{
    Lemma saveLemma(Lemma lemma);
    Lemma getLemma(String lemma, long siteId);
    List<Lemma> getLemmas(List<String> nameList);
    void resetLemmas(List<Lemma> lemma);
    List<Lemma> getLemmas(long siteId);
    long lemmaCount();
}
