package SearchEngineApp.utils;

import SearchEngineApp.models.Field;
import SearchEngineApp.models.Index;
import SearchEngineApp.models.Lemma;
import SearchEngineApp.models.WebPage;
import SearchEngineApp.services.FieldService;
import SearchEngineApp.services.IndexService;
import SearchEngineApp.services.LemmaService;
import SearchEngineApp.services.WebPageService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexPagesUtil
{
    private static List<Field> fieldList = new ArrayList<>();

    public static void startIndexing(String urlAdress) {
        try {
            long startTime = System.currentTimeMillis();

            System.out.println(">>> Начало индексации сайта");

            FieldService fieldService = new FieldService();
            if(fieldService.getSize() != 2) {
                writingField(fieldService);
            }

            fieldList = fieldService.getFields();

            WebPageService webPageService = new WebPageService();
            for (WebPage webPage : webPageService.getAllWebPages()) {
                    System.out.println(webPage.getPath());
                    indexPage(webPage);
                    System.out.println("Конец индексации");
            }

            System.out.println(">>> Индексация сайта < " + urlAdress + "/ > завершена.\n" +
                    "Затраченное время = " + (System.currentTimeMillis() - startTime) / 1000 + " сек");

        } catch (Exception iex) {
            iex.printStackTrace();
        }
    }

    private static synchronized void indexPage(WebPage webPage) {
        try {
            System.out.println("Начало индексации " + webPage.getPath());
            ArrayList<String> lemmaList = new ArrayList<>();

            LemmaService lemmaService = new LemmaService();
            IndexService indexService = new IndexService();

            Document doc = Jsoup.parse(webPage.getContent());
            for (Field field : fieldList) {
                Elements el = doc.getElementsByTag(field.getSelector());
                for (Map.Entry<String, Integer> entry : CreateLemmasUtil.createLemmas(el.eachText().get(0)).entrySet()) {
                    if (!lemmaList.contains(entry.getKey())) {
                        lemmaList.add(entry.getKey());
                        Lemma lemma = new Lemma(entry.getKey(), 1);
                        lemmaService.saveLemma(lemma);
                        Index index = new Index(webPage.getId(),lemma.getId(), field.getWeight() * entry.getValue());
                        indexService.saveIndex(index);
                    } else {
                        Lemma lemma = lemmaService.getLemma(entry.getKey());
                        Index index = indexService.getIndex(lemma.getId(), webPage.getId());
                        float rank = index.getRank();
                        index.setRank(rank + entry.getValue() * field.getWeight());
                        indexService.updateIndex(index);
                    }
                }
            }
        } catch (Exception iex) {
            iex.printStackTrace();
        }
    }

    private static void writingField (FieldService fieldService) {
        fieldService.dropTable();
        fieldService.createTable();
        Field fieldTitle = new Field("title", "title", 1.0f);
        Field fieldBody = new Field("body", "body", 0.8f);
        fieldService.saveField(fieldTitle);
        fieldService.saveField(fieldBody);
    }
}
