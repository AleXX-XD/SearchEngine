package SearchEngineApp.utils;

import SearchEngineApp.models.*;
import SearchEngineApp.service.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import org.apache.log4j.Logger;

@Component
public class IndexPagesUtil
{
    private static final Logger log = Logger.getLogger(IndexPagesUtil.class);
    private static List<Field> fieldList = new ArrayList<>();
    private static LemmaService lemmaService;
    private static IndexService indexService;
    private static SiteService siteService;
    private static WebPageService webPageService;
    private static FieldService fieldService;
    private static Site site;

    public IndexPagesUtil (LemmaService lemmaService, IndexService indexService,
                           SiteService siteService, WebPageService webPageService, FieldService fieldService) {
        IndexPagesUtil.lemmaService = lemmaService;
        IndexPagesUtil.indexService = indexService;
        IndexPagesUtil.siteService = siteService;
        IndexPagesUtil.webPageService = webPageService;
        IndexPagesUtil.fieldService = fieldService;
    }

    public static synchronized void startIndexing(Site transSite) {
        try {
            site = transSite;

            if(fieldService.getSize() == 0) {
                writingField();
            }

            fieldList = fieldService.getFields();

            List<WebPage> webPageList = webPageService.getAllBySite(site.getId());
            if(webPageList.size() > 0) {
                for (WebPage webPage : webPageList) {
                    indexPage(webPage);
                }
                if(site.getStatus() != Status.FAILED) {
                    site.setStatusTime(new Date());
                    site.setStatus(Status.INDEXED);
                }
            }
            else {
                log.warn("Ошибка индексации сайта '" + site.getUrl() + "'. Нет страниц для индексации");
                site.setStatusTime(new Date());
                site.setStatus(Status.FAILED);
                site.setLastError("Ошибка индексации. Нет страниц для индексации");
            }
            siteService.updateSite(site);

        } catch (Exception iex) {
            siteService.updateStatus(Status.FAILED, "Ошибка индексации. " + iex.getMessage());
            log.warn("Ошибка индексации сайта '" + site.getUrl() + "'. " + iex.getMessage());
            iex.printStackTrace();
        }
    }

    private static synchronized void indexPage(WebPage webPage) {
        try {
            siteService.updateStatusTime(site);
            List<String> lemmaList = new ArrayList<>();
            Document doc = Jsoup.parse(webPage.getContent());
            for (Field field : fieldList) {
                Elements el = doc.getElementsByTag(field.getSelector());
                for (Map.Entry<String, Integer> entry : CreateLemmasUtil.createLemmasWithCount(el.eachText().get(0)).entrySet()) {
                    if (!lemmaList.contains(entry.getKey())) {
                        lemmaList.add(entry.getKey());
                        Lemma lemma = new Lemma(entry.getKey(), 1, webPage.getSite());
                        lemma = lemmaService.saveLemma(lemma);
                        Index index = new Index(webPage.getId(),lemma.getId(), field.getWeight() * entry.getValue());
                        indexService.saveIndex(index);
                    } else {
                        Lemma lemma = lemmaService.getLemma(entry.getKey(), webPage.getSite().getId());
                        System.out.println("lemma.getId() = " + lemma.getId() + " / webPage.getId() = " +webPage.getId());

                        Index index = indexService.getIndex(lemma.getId(), webPage.getId());
                        float rank = index.getRank();
                        index.setRank(rank + entry.getValue() * field.getWeight());
                        indexService.updateIndex(index);
                    }
                }
            }
        } catch (Exception iex) {
            log.warn("Ошибка индексации страницы : '" + webPage.getPath() + "'. " + iex.getMessage());
            iex.printStackTrace();
        }
    }

    private static void writingField () {
        Field fieldTitle = new Field("title", "title", 1.0f);
        Field fieldBody = new Field("body", "body", 0.8f);
        fieldService.saveField(fieldTitle);
        fieldService.saveField(fieldBody);

    }
}
