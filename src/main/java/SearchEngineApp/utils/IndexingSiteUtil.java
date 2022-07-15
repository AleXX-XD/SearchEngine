//package SearchEngineApp.utils;
//
//import SearchEngineApp.config.SearchConfig;
//import SearchEngineApp.data.sites.SitesData;
//import SearchEngineApp.models.Site;
//import SearchEngineApp.models.Status;
//import SearchEngineApp.service.IndexService;
//import SearchEngineApp.service.LemmaService;
//import SearchEngineApp.service.SiteService;
//import SearchEngineApp.service.impl.IndexingServiceImpl;
//import SearchEngineApp.service.response.FalseResponse;
//import lombok.Getter;
//import lombok.Setter;
//import org.apache.log4j.Logger;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.List;
//
//@Getter
//@Setter
//public class IndexingSiteUtil implements Runnable {
//
//    private Logger log = Logger.getLogger(IndexingSiteUtil.class);
//    private Site site;
//
//
//    public IndexingSiteUtil(Site site) {
//        this.site = site;
//    }
//
//    @Override
//    public void run() {
//        System.out.println("Данные о парсируемом сайте: " + site.getUrl() + " / " + site.getName());
//        ParseSiteUtil.startParse(site);
//    }
//
//}
