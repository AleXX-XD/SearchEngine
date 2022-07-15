package SearchEngineApp.service;

import SearchEngineApp.models.Lemma;

import java.util.List;

public interface LemmaService
{
    Lemma saveLemma(Lemma lemma);
    Lemma getLemma(String lemma, int siteId);
    List<Lemma> getLemmas(List<String> nameList);
    void resetLemmas(int siteId);
    List<Integer> getLemmasId (int siteId);
    long lemmaCount();
}
