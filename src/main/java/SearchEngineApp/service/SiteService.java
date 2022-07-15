package SearchEngineApp.service;

import SearchEngineApp.models.Site;
import SearchEngineApp.models.Status;

import java.util.List;

public interface SiteService {

    void saveSite(Site site);
    Site getSite(String url);
    List<Site> getAllSites();
    long siteCount();
    long countStatusIndexing();

    void updateStatus(Status status, String error);
    void updateStatusTime(Site site);
    void updateSite(Site site);

}
