package SearchEngineApp.service.impl;

import SearchEngineApp.repository.SiteRepository;
import SearchEngineApp.models.Site;
import SearchEngineApp.models.Status;
import SearchEngineApp.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SiteServiceImpl implements SiteService
{
    private final SiteRepository siteRepository;

    public SiteServiceImpl(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Override
    public void saveSite(Site site) {
        siteRepository.save(site);
    }

    @Override
    public Site getSite(String url) {
        return siteRepository.findByUrl(url);
    }

    @Override
    public List<Site> getAllSites() {
        List<Site> siteList = new ArrayList<>();
        Iterable<Site> sites = siteRepository.findAll();
        sites.forEach(siteList::add);
        return siteList;
    }

    @Override
    public long countStatusIndexing() {
        return siteRepository.countByStatusIndexing();
    }

    @Override
    public long siteCount() {
        return siteRepository.count();
    }



    @Override
    public void updateStatus(Status status, String error) {

    }

    @Override
    public void updateStatusTime(Site site) {

    }

    @Override
    public void updateSite(Site site) {
    }
}
