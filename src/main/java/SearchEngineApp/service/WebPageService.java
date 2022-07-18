package SearchEngineApp.service;

import SearchEngineApp.models.WebPage;

import java.util.List;

public interface WebPageService
{
    void savePage(WebPage page);
    List<WebPage> getAllBySite(long siteId);
    List<WebPage> getAllWebPages(List<Long> pageList);
    Long pageCount();
    void resetPages(List<WebPage> pageList);
}
