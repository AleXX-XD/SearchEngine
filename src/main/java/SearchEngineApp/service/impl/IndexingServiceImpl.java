package SearchEngineApp.service.impl;

import SearchEngineApp.config.SearchConfig;
import SearchEngineApp.data.sites.SitesData;
import SearchEngineApp.models.Site;
import SearchEngineApp.models.Status;
import SearchEngineApp.service.*;
import SearchEngineApp.service.response.FalseResponse;
import SearchEngineApp.service.response.Response;

import SearchEngineApp.service.response.TrueResponse;
//import SearchEngineApp.utils.IndexingSiteUtil;
import SearchEngineApp.utils.ParseSiteUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexingServiceImpl implements IndexingService {

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
    public Response startAllIndexing() {
        if(siteService.countStatusIndexing() > 0) {
            log.warn("Ошибка при запуске индексации. Индексация уже запущена");
            return new FalseResponse("Индексация уже запущена");
        } else {
            log.info("Запущена индексация ВСЕХ САЙТОВ");
            for(SitesData siteData : searchConfig.getSites()) {
                Site site = siteService.getSite(removeSlash(siteData.getUrl()));
                if(site != null) {
                    log.info("Начата ПЕРЕИНДЕКСАЦИЯ сайта '" + site.getUrl() + "'");
                    site.setAllParameters(Status.INDEXING, new Date(), null);
                    List<Integer> lemmasId = lemmaService.getLemmasId(site.getId());
                    lemmaService.resetLemmas(site.getId());
                    indexService.resetRanks(lemmasId);
                }
                else {
                    log.info("Начата ИНДЕКСАЦИЯ сайта '" + removeSlash(siteData.getUrl()) + "'");
                    site = new Site(removeSlash(siteData.getUrl()), siteData.getName());
                    site.setAllParameters(Status.INDEXING, new Date(), null);
                }
                siteService.saveSite(site);
                ParseSiteUtil parseSiteUtil = new ParseSiteUtil(pageService, searchConfig, siteService);
                parseSiteUtil.setSite(site);
                new Thread(parseSiteUtil).start();
            }
            return new TrueResponse();
        }
    }

    @Override
    public Response stopIndexing() {
        return null;
    }

    private static String removeSlash (String string) {
        if (string.charAt(string.length()-1) == '/') {
            string = string.substring(0,string.length()-1);
        }
        return string;
    }

}
