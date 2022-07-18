package SearchEngineApp.utils;

import SearchEngineApp.models.*;
import SearchEngineApp.service.*;
import SearchEngineApp.service.impl.IndexingServiceImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

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

    public IndexPagesUtil (LemmaService lemmaService, IndexService indexService,
                           SiteService siteService, WebPageService webPageService, FieldService fieldService) {
        IndexPagesUtil.lemmaService = lemmaService;
        IndexPagesUtil.indexService = indexService;
        IndexPagesUtil.siteService = siteService;
        IndexPagesUtil.webPageService = webPageService;
        IndexPagesUtil.fieldService = fieldService;
    }

    public static synchronized void startIndexing(Site transSite) throws Exception {
        Site site = siteService.getSite(transSite.getUrl());
        if(fieldService.getSize() == 0) {
            writingField();
        }
        fieldList = fieldService.getFields();

        List<WebPage> webPageList = webPageService.getAllBySite(site.getId());

        if(webPageList.size() > 0) {
            List<Future<Object>> futures = new ArrayList<>();
//            ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(20);
            ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
            for (WebPage webPage : webPageList) {
                    IndexPageRunnable indexPageRunnable = new IndexPageRunnable(siteService, lemmaService, indexService);
                    indexPageRunnable.setWebPage(webPage);
                    indexPageRunnable.setFieldList(fieldList);
                    futures.add(executor.submit(indexPageRunnable,(Object)null));
            }
            for (Future<Object> future : futures) {
                if(IndexingServiceImpl.isRun) {
                    future.get();
                }
                else {
                    executor.shutdownNow();
                    boolean result = false;
                    while(!result) {
                        result = executor.awaitTermination(30, TimeUnit.SECONDS);
                    }
                    throw new Exception("Процесс остановлен пользователем");
                }
            }
            executor.shutdown();

//                    indexPage(webPage);



            site.setAllParameters(Status.INDEXED, new Date(), null);
        }
        else {
            throw new Exception("Нет страниц для индексации");
        }
        siteService.saveSite(site);
    }

//    private static synchronized void indexPage(WebPage webPage) {
//        try {
//            Site site = siteService.getSite(webPage.getSite().getUrl());
//            site.setStatusTime(new Date());
//            siteService.saveSite(site);
//            List<String> lemmaList = new ArrayList<>();
//            Document doc = Jsoup.parse(webPage.getContent());
//            for (Field field : fieldList) {
//                Elements el = doc.getElementsByTag(field.getSelector());
//                for (Map.Entry<String, Integer> entry : CreateLemmasUtil.createLemmasWithCount(el.eachText().get(0)).entrySet()) {
//                    if (!lemmaList.contains(entry.getKey())) {
//                        lemmaList.add(entry.getKey());
//                        Lemma lemma = new Lemma(entry.getKey(), 1, webPage.getSite());
//                        lemma = lemmaService.saveLemma(lemma);
//                        Index index = new Index(webPage.getId(),lemma.getId(), field.getWeight() * entry.getValue());
//                        indexService.saveIndex(index);
//                    } else {
//                        Lemma lemma = lemmaService.getLemma(entry.getKey(), webPage.getSite().getId());
//                        Index index = indexService.getIndex(lemma.getId(), webPage.getId());
//                        float rank = index.getRank();
//                        index.setRank(rank + entry.getValue() * field.getWeight());
//                        indexService.saveIndex(index);
//                    }
//                }
//            }
//        } catch (Exception iex) {
//            System.out.println("Exception - indexPage");
//            log.warn("Ошибка индексации страницы : '" + webPage.getPath() + "'. " + iex.getMessage());
//            iex.printStackTrace();
//        }
//    }

    private static void writingField () {
        Field fieldTitle = new Field("title", "title", 1.0f);
        Field fieldBody = new Field("body", "body", 0.8f);
        fieldService.saveField(fieldTitle);
        fieldService.saveField(fieldBody);

    }
}
