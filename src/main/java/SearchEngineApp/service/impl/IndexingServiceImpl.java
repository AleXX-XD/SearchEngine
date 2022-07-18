package SearchEngineApp.service.impl;

import SearchEngineApp.config.SearchConfig;
import SearchEngineApp.data.sites.SitesData;
import SearchEngineApp.models.Lemma;
import SearchEngineApp.models.Site;
import SearchEngineApp.models.Status;
import SearchEngineApp.models.WebPage;
import SearchEngineApp.service.*;
import SearchEngineApp.service.response.FalseResponse;
import SearchEngineApp.service.response.Response;

import SearchEngineApp.service.response.TrueResponse;
//import SearchEngineApp.utils.IndexingSiteUtil;
import SearchEngineApp.utils.ParseSiteUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@Service
public class IndexingServiceImpl implements IndexingService {

    public static volatile boolean isRun;

    private final Logger log = Logger.getLogger(IndexingServiceImpl.class);

    private final SearchConfig searchConfig;
    private final SiteService siteService;
    private final LemmaService lemmaService;
    private final IndexService indexService;
    private final WebPageService pageService;


    public IndexingServiceImpl(SiteService siteService, LemmaService lemmaService,
                               IndexService indexService, SearchConfig searchConfig, WebPageService pageService) {
        this.searchConfig = searchConfig;
        this.siteService = siteService;
        this.lemmaService = lemmaService;
        this.indexService = indexService;
        this.pageService = pageService;
    }

    @Override
    public Response startAllIndexing() throws Exception {
        if(siteService.countStatusIndexing() > 0) {
            log.warn("Ошибка при запуске индексации. Индексация уже запущена");
            return new FalseResponse("Индексация уже запущена");
        } else {
            IndexingServiceImpl.isRun = true;
            log.info("Запущена индексация ВСЕХ САЙТОВ");
            List<Future<Object>> futures = new ArrayList<>();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
            for(SitesData siteData : searchConfig.getSites()) {
                Site site = siteService.getSite(removeSlash(siteData.getUrl()));
                if(site != null) {
                    log.info("Начата ПЕРЕИНДЕКСАЦИЯ сайта '" + site.getUrl() + "'");
                    List<Lemma> lemmaList = lemmaService.getLemmas(site.getId());
                    List<WebPage> pageList = pageService.getAllBySite(site.getId());
                    pageService.resetPages(pageList);
                    lemmaService.resetLemmas(lemmaList);
                    indexService.resetRanks(lemmaList);
                }
                else {
                    log.info("Начата ИНДЕКСАЦИЯ сайта '" + removeSlash(siteData.getUrl()) + "'");
                    site = new Site(removeSlash(siteData.getUrl()), siteData.getName());
                }
                site.setAllParameters(Status.INDEXING, new Date(), null);
                siteService.saveSite(site);
                try {
                    ParseSiteUtil parseSiteUtil = new ParseSiteUtil(pageService, searchConfig, siteService);
                    parseSiteUtil.setSite(site);
                    futures.add(executor.submit(parseSiteUtil,(Object)null));
                }
                catch (Exception ex) {
                    log.warn("Ошибка при индексации сайта '" + site.getUrl() + "': " + ex.getMessage());
                    site.setAllParameters(Status.FAILED, new Date(), "Ошибка при индексации сайта '" + site.getUrl() + "': " + ex.getMessage());
                    siteService.saveSite(site);
                    ex.printStackTrace();
                }
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
            return new TrueResponse();
        }
    }

    @Override
    public Response stopIndexing() {
        if(siteService.countStatusIndexing() > 0) {
            log.info("Остановка индексации сайтов.");
            try {
                Thread.sleep(500);
                IndexingServiceImpl.isRun = false;
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return new FalseResponse("Не удалось завершить все потоки");
            }
            List<Site> siteList = siteService.getAllIndexingSite();
            for(Site site : siteList) {
                site.setAllParameters(Status.FAILED, new Date(), "Процесс индексации остановлен пользователем");
                siteService.saveSite(site);
            }
            return new TrueResponse();
        }
        else {
            return new FalseResponse("Индексация не запущена");
        }
    }

    private static String removeSlash (String string) {
        if (string.charAt(string.length()-1) == '/') {
            string = string.substring(0,string.length()-1);
        }
        return string;
    }

    private boolean shutdownThreads(ThreadPoolExecutor pool) {
        boolean result = true;
        pool.shutdown();
        try {
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("Потоки не завершены");
                    System.out.println("pool.getTaskCount() = " + pool.getTaskCount());
                    result = false;
                }
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
            return false;
        }
        return result;
    }
}
