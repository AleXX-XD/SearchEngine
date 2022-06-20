package SearchEngineApp.services;

import SearchEngineApp.dao.WebPageDao;
import SearchEngineApp.models.WebPage;

import java.util.List;

public class WebPageService
{
    private WebPageDao webPageDao = new WebPageDao();

    public WebPageService() {}

    public void savePage(WebPage webPage) {
        webPageDao.save(webPage);
    }

    public List<WebPage> getAllWebPages() {
        return webPageDao.getAll();
    }

    public WebPage getPage(int pageId) {
        return webPageDao.get(pageId);
    }
}
