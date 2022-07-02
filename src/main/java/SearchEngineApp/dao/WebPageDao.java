package SearchEngineApp.dao;

import SearchEngineApp.models.Index;
import SearchEngineApp.models.Lemma;
import SearchEngineApp.models.WebPage;
import SearchEngineApp.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class WebPageDao
{
    public void save(WebPage webPage) {
        if(get(webPage.getPath(), webPage.getSiteId()) != null) {
            update(webPage);
        }
        else {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            session.save(webPage);
            transaction.commit();
            session.close();
        }
    }

    public List<WebPage> getBySite(int idSite) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<WebPage> query = session.createQuery("from WebPage where code = :code AND siteId = :siteId");
        query.setParameter("code", 200);
        query.setParameter("siteId", idSite);
        List<WebPage> list = query.getResultList();
        transaction.commit();
        session.close();
        return list;
    }

    public List<WebPage> getAll(List<Integer> pageList) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<WebPage> query = session.createQuery("from WebPage where id IN (:pageList)");
        query.setParameterList("pageList", pageList);
        List<WebPage> pages = query.getResultList();
        transaction.commit();
        session.close();
        return pages;
    }

    public WebPage get(String pagePath, int idSite) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<WebPage> query = session.createQuery("from WebPage where path = :path AND siteId = :siteId");
        query.setParameter("path", pagePath);
        query.setParameter("siteId", idSite);
        query.setMaxResults(1);
        WebPage webPage = query.uniqueResult();
        transaction.commit();
        session.close();
        return webPage;
    }

    public void update(WebPage webPage) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("update WebPage set content = :content, code = :code WHERE path = :path AND siteId = :siteId");
        query.setParameter("content", webPage.getContent());
        query.setParameter("code", webPage.getCode());
        query.setParameter("path", webPage.getPath());
        query.setParameter("siteId", webPage.getSiteId());
        query.executeUpdate();
        transaction.commit();
        session.close();
    }

}
