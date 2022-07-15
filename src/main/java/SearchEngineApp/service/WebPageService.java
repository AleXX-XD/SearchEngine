package SearchEngineApp.service;

import SearchEngineApp.models.WebPage;

import java.util.List;

public interface WebPageService
{
    void savePage(WebPage page);
    List<WebPage> getAllBySite(int siteId);
    List<WebPage> getAllWebPages(List<Integer> pageList);
    Long pageCount();
}
