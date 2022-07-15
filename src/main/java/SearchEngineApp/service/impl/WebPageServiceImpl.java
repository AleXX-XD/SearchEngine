package SearchEngineApp.service.impl;

import SearchEngineApp.models.WebPage;
import SearchEngineApp.repository.WebPageRepository;
import SearchEngineApp.service.WebPageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebPageServiceImpl implements WebPageService {

    private final WebPageRepository pageRepository;

    public WebPageServiceImpl(WebPageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Override
    public void savePage(WebPage page) {
        pageRepository.save(page);
    }

    @Override
    public List<WebPage> getAllBySite(int siteId) {
        return pageRepository.findAllBySiteId(siteId);
    }

    @Override
    public List<WebPage> getAllWebPages(List<Integer> pageList) {
        return pageRepository.findAllByIdIn(pageList);
    }

    @Override
    public Long pageCount() {
        return pageRepository.count();
    }
}
