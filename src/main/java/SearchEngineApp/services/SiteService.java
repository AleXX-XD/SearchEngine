package SearchEngineApp.services;

import SearchEngineApp.dao.SiteDao;
import SearchEngineApp.models.Site;
import SearchEngineApp.models.Status;


public class SiteService {

    private SiteDao siteDao = new SiteDao();

    public SiteService() {}

    public void saveSite(Site site) {
        siteDao.save(site);
    }

    public Site getSite(String name) {
        return siteDao.get(name);
    }

    public void updateStatus(Status status, String error) {
        siteDao.updateStatus(status, error);
    }

    public void updateStatusTime(Site site) {
        siteDao.updateStatusTime(site);
    }

    public void updateSite(Site site) {
        siteDao.update(site);
    }
}
