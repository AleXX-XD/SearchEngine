package SearchEngineApp.service.impl;

import SearchEngineApp.repository.LemmaRepository;
import SearchEngineApp.models.Lemma;
import SearchEngineApp.service.LemmaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LemmaServiceImpl implements LemmaService {

    private final LemmaRepository lemmaRepository;

    public LemmaServiceImpl(LemmaRepository lemmaRepository){
        this.lemmaRepository = lemmaRepository;
    }

    @Override
    public synchronized Lemma saveLemma(Lemma lemma) {
        Lemma lemmaFromDB = getLemma(lemma.getLemma(), lemma.getSite().getId());
        if(lemmaFromDB != null) {
            lemma = lemmaFromDB;
            lemma.setFrequency(lemma.getFrequency() + 1);
        }
        lemmaRepository.save(lemma);
        return lemma;
    }

    @Override
    public Lemma getLemma(String lemma, long siteId) {
        return lemmaRepository.findByLemmaAndSiteId(lemma, siteId);
    }

    @Override
    public List<Lemma> getLemmas(List<String> nameList) {
        return lemmaRepository.findAllByLemmaIn(nameList);
    }

    @Override
    public synchronized void resetLemmas(List<Lemma> lemmaList) {
        lemmaRepository.deleteAll(lemmaList);
    }

    @Override
    public List<Lemma> getLemmas(long siteId) {
        return lemmaRepository.findAllBySiteId(siteId);
    }

    @Override
    public long lemmaCount() {
        return lemmaRepository.count();
    }
}
