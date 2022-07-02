package SearchEngineApp.utils;

import SearchEngineApp.models.*;
import SearchEngineApp.services.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.*;

public class IndexPagesUtil
{
    private static List<Field> fieldList = new ArrayList<>();
    private static LemmaService lemmaService = new LemmaService();
    private static IndexService indexService = new IndexService();
    private static SiteService siteService = new SiteService();
    private static WebPageService webPageService = new WebPageService();
    private static FieldService fieldService = new FieldService();
    private static Site site;

    public static void startIndexing(Site transSite) {
        try {
            site = transSite;
            long startTime = System.currentTimeMillis();

            System.out.println(">>> Начало индексации сайта");

            if(fieldService.getSize() != 2) {
                writingField(fieldService);
            }

            fieldList = fieldService.getFields();

            List<WebPage> webPageList = webPageService.getAllBySite(site.getId());
            System.out.println("webPageList.size() = " + webPageList.size());
            if(webPageList.size() > 0) {
                for (WebPage webPage : webPageList) {
                    indexPage(webPage);
                    System.out.println("Конец индексации");
                }
                if(site.getStatus() != Status.FAILED) {
                    site.setStatusTime(new Date());
                    site.setStatus(Status.INDEXED);
                }
            }
            else {
                site.setLastError("Нет доступных страниц для индексации");
                site.setStatusTime(new Date());
                site.setStatus(Status.FAILED);
            }

            siteService.updateSite(site);

            System.out.println(">>> Индексация сайта < " + site.getName() + "/ > завершена.\n" +
                    "Затраченное время = " + (System.currentTimeMillis() - startTime) / 1000 + " сек");

        } catch (Exception iex) {
            site.setStatus(Status.FAILED);
            site.setLastError(iex.getMessage());
            site.setStatusTime(new Date());
            siteService.updateSite(site);
            iex.printStackTrace();
        }
    }

    private static synchronized void indexPage(WebPage webPage) {
        try {
            System.out.println("Начало индексации " + webPage.getPath());
            ArrayList<String> lemmaList = new ArrayList<>();

            Document doc = Jsoup.parse(webPage.getContent());
            for (Field field : fieldList) {
                Elements el = doc.getElementsByTag(field.getSelector());
                for (Map.Entry<String, Integer> entry : CreateLemmasUtil.createLemmasWithCount(el.eachText().get(0)).entrySet()) {
                    if (!lemmaList.contains(entry.getKey())) {
                        lemmaList.add(entry.getKey());
                        Lemma lemma = new Lemma(entry.getKey(), 1, webPage.getSiteId());
                        lemmaService.saveLemma(lemma);
                        Index index = new Index(webPage.getId(),lemma.getId(), field.getWeight() * entry.getValue());
                        indexService.saveIndex(index);
                    } else {
                        Lemma lemma = lemmaService.getLemma(entry.getKey(), webPage.getSiteId());
                        Index index = indexService.getIndex(lemma.getId(), webPage.getId());
                        float rank = index.getRank();
                        index.setRank(rank + entry.getValue() * field.getWeight());
                        indexService.updateIndex(index);
                    }
                }
            }
        } catch (Exception iex) {
            site.setStatus(Status.FAILED);
            site.setLastError("Ошибка индексации: " + iex.getMessage());
            site.setStatusTime(new Date());
            siteService.updateSite(site);
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
