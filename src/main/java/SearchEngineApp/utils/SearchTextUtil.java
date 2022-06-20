package SearchEngineApp.utils;

import SearchEngineApp.models.Index;
import SearchEngineApp.models.Lemma;
import SearchEngineApp.models.SearchPage;
import SearchEngineApp.services.IndexService;
import SearchEngineApp.services.LemmaService;
import SearchEngineApp.services.WebPageService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchTextUtil {

    public static void startSearch(String text) throws IOException {
        List<Lemma> lemmas = getLemmas(text);
        List<Integer> pages = getPages(lemmas);
        List<SearchPage> searchPages = getSearchPages(lemmas, pages);

        for (SearchPage searchPage : searchPages) {
            System.out.println("Найденные страницы : ");
            System.out.println("URL - " + searchPage.getWebPage().getPath() + "\n"
                    + "Заголовок - " + searchPage.getTitle() + "\n"
                    + "Релевантность - " + searchPage.getRelevance());
        }
    }

    private static List<SearchPage> getSearchPages (List<Lemma> searchLemmas, List<Integer> searchPages) {
        IndexService indexService = new IndexService();
        WebPageService webPageService = new WebPageService();
        List<SearchPage> searchPageList = new ArrayList<>();
        float maxRelevance = 0;
        for (int pageId : searchPages) {
            float rank = 0;
            for (Lemma lemma : searchLemmas) {
                Index index = indexService.getIndex(lemma.getId(), pageId);
                rank += index.getRank();
            }
            SearchPage searchPage = new SearchPage();
            searchPage.setWebPage(webPageService.getPage(pageId));
            searchPage.setAbsoluteRelevance(rank);

            Document doc = Jsoup.parse(searchPage.getWebPage().getContent());
            searchPage.setTitle(doc.getElementsByTag("title").text());

            searchPageList.add(searchPage);
            maxRelevance = Math.max(maxRelevance, rank);
        }

        List<SearchPage> searchPagesSort = new ArrayList<>();
        for (SearchPage searchPage : searchPageList) {
            searchPage.setRelevance(searchPage.getAbsoluteRelevance() / maxRelevance);
            if (searchPagesSort.isEmpty()) {
                searchPagesSort.add(searchPage);
            } else {
                for (int i = 0; i < searchPagesSort.size(); i++) {
                    if (searchPagesSort.get(i).getRelevance() < searchPage.getRelevance()) {
                        searchPagesSort.add(i, searchPage);
                        break;
                    } else if (i == searchPagesSort.size() - 1) {
                        searchPagesSort.add(searchPage);
                        break;
                    }
                }
            }
        }
        return searchPagesSort;
    }

    private static List<Lemma> getLemmas (String text) throws IOException {
        LemmaService lemmaService = new LemmaService();
        List<Lemma> searchLemmas = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : CreateLemmasUtil.createLemmas(text).entrySet()) {
            Lemma lemma = lemmaService.getLemma(entry.getKey());
            if (lemma == null) {
                searchLemmas = null;
                break;
            } else if (searchLemmas.isEmpty()) {
                searchLemmas.add(lemma);
            } else {
                for (int i = 0; i < searchLemmas.size(); i++) {
                    if (searchLemmas.get(i).getFrequency() > lemma.getFrequency()) {
                        searchLemmas.add(i, lemma);
                        break;
                    } else if (i == searchLemmas.size() - 1) {
                        searchLemmas.add(lemma);
                        break;
                    }
                }
            }
        }
        return searchLemmas;
    }

    private static List<Integer> getPages (List<Lemma> searchLemmas) {
        IndexService indexService = new IndexService();
        List<Integer> searchPages = new ArrayList<>();

        if (searchLemmas!=null) {
            for (Lemma lemma : searchLemmas) {
                List<Integer> pages = indexService.getIndex(lemma.getId());
                if (searchPages.isEmpty()) {
                    searchPages.addAll(pages);
                } else {
                    searchPages.removeIf(pageId -> !pages.contains(pageId));
                    if (searchPages.isEmpty()) {
                        break;
                    }
                }
            }
        } else {
            System.out.println("Поиск не дал результатов!!!");
        }
        return searchPages;
    }

}
