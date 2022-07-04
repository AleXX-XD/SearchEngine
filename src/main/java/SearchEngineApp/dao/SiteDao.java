package SearchEngineApp.dao;

import SearchEngineApp.models.Site;
import SearchEngineApp.models.Status;
import SearchEngineApp.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Date;

public class SiteDao {

    public void save(Site site) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(site);
        transaction.commit();
        session.close();
    }

    public Site get(String urlSite) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Site> query = session.createQuery("from Site where url = :url");
        query.setParameter("url", urlSite);
        query.setMaxResults(1);
        Site site = query.uniqueResult();
        transaction.commit();
        session.close();
        return site;
    }

    public void updateStatus(Status indexStatus, String error) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Site> query = session.createQuery("update Site set status = :status, statusTime = :statusTime, lastError = :lastError");
        query.setParameter("status", indexStatus);
        query.setParameter("statusTime", new Date());
        query.setParameter("lastError", error);
        query.executeUpdate();
        transaction.commit();
        session.close();
    }

    public void updateStatusTime(Site site) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Site> query = session.createQuery("update Site set statusTime = :statusTime WHERE id =: id");
        query.setParameter("statusTime", new Date());
        query.setParameter("id", site.getId());
        query.executeUpdate();
        transaction.commit();
        session.close();
    }

    public void update(Site site) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Site> query = session.createQuery("update Site set status = :status, statusTime = :statusTime, lastError = :lastError WHERE id = :id");
        query.setParameter("status", site.getStatus());
        query.setParameter("statusTime", site.getStatusTime());
        query.setParameter("lastError", site.getLastError());
        query.setParameter("id", site.getId());
        query.executeUpdate();
        transaction.commit();
        session.close();
    }
}
