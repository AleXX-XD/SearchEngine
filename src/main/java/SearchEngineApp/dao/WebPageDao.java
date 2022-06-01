package SearchEngineApp.dao;

import SearchEngineApp.models.WebPage;
import SearchEngineApp.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class WebPageDao
{
    public void save(WebPage webPage) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(webPage);
        transaction.commit();
        session.close();
    }
}
