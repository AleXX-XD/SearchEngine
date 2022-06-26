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
import java.util.*;

public class SearchTextUtil {

    public static void startSearch(String text) throws IOException {
        List<Lemma> lemmas = getLemmas(text);
        List<Integer> pages = getPages(lemmas);
        List<SearchPage> searchPages = getSearchPages(lemmas, pages, text);

        for (SearchPage searchPage : searchPages) {
            System.out.println("Найденные страницы : ");
            System.out.println("URL : " + searchPage.getWebPage().getPath() + "\n"
                    + "Заголовок : " + searchPage.getTitle() + "\n"
                    + "Фрагмент текста : " + searchPage.getSnippet() + "\n"
                    + "Релевантность : " + searchPage.getRelevance());
        }
    }

    private static List<SearchPage> getSearchPages (List<Lemma> searchLemmas, List<Integer> searchPages, String searchText) throws IOException {
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

            String titleText = doc.getElementsByTag("title").text();
            String bodyText = doc.getElementsByTag("body").text();

            searchPage.setTitle(getSnippet(searchText, titleText));
            searchPage.setSnippet(getSnippet(searchText, bodyText));

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

        HashMap<String,Integer> map = CreateLemmasUtil.createLemmasWithCount(text);
        if(map.size() > 0) {
            for (Map.Entry<String, Integer> entry : CreateLemmasUtil.createLemmasWithCount(text).entrySet()) {
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
        }
        else {
            searchLemmas = null;
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

    private static String getSnippet (String searchText, String mainText) throws IOException {

        System.out.println("Исходный текст:\n" + mainText);

        String[] textArray = mainText.split("(?!^)\\b");
        TreeMap<Integer,List<String>> lemmasIndexes = CreateLemmasUtil.getIndexLemmas(textArray);
        List<List<String>> lemmasFromText = CreateLemmasUtil.createLemmas(searchText);

        //TODO: отслеживание действий в консоли. Убрать после доработки
        for (int i = 0; i < textArray.length; i++) {
            System.out.println("Элемент[" + i + "] - " + textArray[i]);
        }
        System.out.println("ЛЕММЫ :");
        for(Map.Entry<Integer,List<String>> entry : lemmasIndexes.entrySet()) {
            System.out.print(entry.getKey() + " -");
            for(String string : entry.getValue()) {
                System.out.print(" " + string);
            }
            System.out.print("\n");
        }

        List<Integer> indexes = new ArrayList<>();
        for(List<String> lemmasSearch : lemmasFromText) {
            for(Map.Entry<Integer,List<String>> lemmasDoc : lemmasIndexes.entrySet()) {
                if(lemmasSearch.equals(lemmasDoc.getValue())) {
                    indexes.add(lemmasDoc.getKey());
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        if(indexes.size() > 0) {
            Collections.sort(indexes);

            //TODO: отслеживание действий в консоли. Убрать после доработки
            System.out.println("Indexes: ");
            indexes.forEach(System.out::println);

            TreeMap<Integer, Integer> startEnd = getStartEndIndexes(indexes);
            for (Map.Entry<Integer,Integer> entry : startEnd.entrySet()) {
                builder.append(getBoldText(entry.getKey(), entry.getValue(), textArray, indexes));
            }
        }
        else {
            builder.append(mainText);
        }
        return builder.toString();
    }

    private static TreeMap<Integer, Integer> getStartEndIndexes (List<Integer> indexes) {
        TreeMap<Integer, Integer> startEnd = new TreeMap<>();
        int start = 0;
        int end = 0;
        for(int i = 0; i < indexes.size(); i++) {
            if(start == 0 && end == 0) {
                start = indexes.get(i);
                end = indexes.get(i);
            }
            if (i + 1 < indexes.size()) {
                if (indexes.get(i + 1) - indexes.get(i) <= 20) {
                    end = indexes.get(i + 1);
                }
                else {
                    startEnd.put(start, end);
                    start = 0;
                    end = 0;
                }
            } else {
                startEnd.put(start,end);
                start = 0;
                end = 0;
            }
        }
        return startEnd;
    }

    private static String getBoldText (int start, int end, String[] stringArray, List<Integer> indexes) {
        int startIndex = Math.max(start - 30, 0);
        int endIndex = Math.min(end + 20, stringArray.length);
        for (int i = start - 1; i > startIndex; i--) {
            if (stringArray[i].matches("[\\p{Punct}\\s]+") && stringArray[i + 1].matches("[A-ZА-Я][a-zа-я]*")) {
                startIndex = i + 1;
                System.out.println("startIndex = " + startIndex);
                break;
            }
        }
        for (int i = end + 1; i < endIndex; i++) {
            if (stringArray[i].matches(".*\\.\\s")){
                endIndex = i-1;
                System.out.println("endIndex = " + endIndex);
                break;
            }
        }
        StringBuilder builder = new StringBuilder();
        List<Integer> indexlist = new ArrayList<>();
        for (Integer index : indexes) {
            if (index >= startIndex && index <= endIndex) {
                indexlist.add(index);
            }
        }

        for(Integer index : indexlist) {
            for (int i = startIndex; i < index; i++) {
                builder.append(stringArray[i]);
            }
            builder.append("<b>");
            builder.append(stringArray[index]);
            builder.append("</b>");
            startIndex = index+1;
        }
        for (int i = startIndex; i < endIndex; i++) {
            builder.append(stringArray[i]);
        }
        if(endIndex == end + 20) {
            builder.append(".... ");
        }
        else {
            builder.append(". ");
        }

        return builder.toString();
    }

}
