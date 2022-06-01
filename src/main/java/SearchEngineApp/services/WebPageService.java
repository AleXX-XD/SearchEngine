package SearchEngineApp.services;

import SearchEngineApp.dao.WebPageDao;
import SearchEngineApp.models.WebPage;

public class WebPageService
{
    private WebPageDao webPageDao = new WebPageDao();

    public WebPageService() {}

    public void savePage(WebPage webPage) {
        webPageDao.save(webPage);
    }
}
