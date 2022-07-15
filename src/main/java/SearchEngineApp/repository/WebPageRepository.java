package SearchEngineApp.repository;

import SearchEngineApp.models.WebPage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebPageRepository extends CrudRepository<WebPage,Integer>
{
    List<WebPage> findAllBySiteId(int idSite);
    List<WebPage> findAllByIdIn(List<Integer> pageList);
    WebPage findByPathAndSiteId(String pagePath, int idSite);


}
