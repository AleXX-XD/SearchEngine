package SearchEngineApp.service.impl;

import SearchEngineApp.repository.LemmaRepository;
import SearchEngineApp.models.Lemma;
import SearchEngineApp.service.LemmaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LemmaServiceImpl implements LemmaService {

    private final LemmaRepository lemmaRepository;

    public LemmaServiceImpl(LemmaRepository lemmaRepository){
        this.lemmaRepository = lemmaRepository;
    }

    @Override
    public Lemma saveLemma(Lemma lemma) {
        if(getLemma(lemma.getLemma(), lemma.getSite().getId()) != null) {
            lemma = getLemma(lemma.getLemma(), lemma.getSite().getId());
            lemma.setFrequency(lemma.getFrequency() + 1);
        }
        lemmaRepository.save(lemma);
        return lemma;
    }

    @Override
    public Lemma getLemma(String lemma, int siteId) {
        return lemmaRepository.findByLemmaAndSiteId(lemma, siteId);
    }

    @Override
    public List<Lemma> getLemmas(List<String> nameList) {
        List<Lemma> lemmaList = new ArrayList<>();
        Iterable<Lemma> lemmas = lemmaRepository.findAllByLemmaIn(nameList);
        lemmas.forEach(lemmaList::add);
        return lemmaList;
    }

    @Override
    public void resetLemmas(int siteId) {
        lemmaRepository.deleteAllBySiteId(siteId);
    }

    @Override
    public List<Integer> getLemmasId(int siteId) {
        List<Integer> listId = new ArrayList<>();
        Iterable<Lemma> lemmas = lemmaRepository.findAllBySiteId(siteId);
        lemmas.forEach(lemma -> {
            listId.add(lemma.getId());
        });
        return listId;
    }

    @Override
    public long lemmaCount() {
        return lemmaRepository.count();
    }
}
