package SearchEngineApp.dao;

import SearchEngineApp.models.Index;
import SearchEngineApp.models.Lemma;
import SearchEngineApp.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class IndexDao
{
    public void save(Index index) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(index);
        transaction.commit();
        session.close();
    }

    public Index get(int idLemma, int idPage) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Index> query = session.createQuery("from Index where lemmaId = :lemmaId and pageId = :pageId");
        query.setParameter("lemmaId", idLemma);
        query.setParameter("pageId", idPage);
        query.setMaxResults(1);
        Index index = query.uniqueResult();
        transaction.commit();
        session.close();
        return index;
    }

    public List<Index> getIndexes(Lemma lemma, List<Integer> pageList) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Index> query = session.createQuery("from Index where lemmaId = :lemmaId AND pageId IN (:pageList)");
        query.setParameter("lemmaId", lemma.getId());
        query.setParameterList("pageList", pageList);
        List<Index> indexes = query.getResultList();
        transaction.commit();
        session.close();
        return indexes;
    }

    public List<Integer> getPages(int idLemma) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Index> query = session.createQuery("from Index where lemmaId = :lemmaId");
        query.setParameter("lemmaId", idLemma);
        List<Index> indexes = query.getResultList();
        transaction.commit();
        session.close();
        List<Integer> pages = new ArrayList<>();
        for(Index index : indexes) {
            pages.add(index.getPageId());
        }
        return pages;
    }

    public void update(Index index) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Index> query = session.createQuery("update Index set rank = :rank" +
                " where lemmaId = :lemmaId and pageId = :pageId");
        query.setParameter("rank", index.getRank());
        query.setParameter("lemmaId", index.getLemmaId());
        query.setParameter("pageId", index.getPageId() );
        query.executeUpdate();
        transaction.commit();
        session.close();
        System.out.println("index.getRank()  DAO = " + index.getRank());
    }

    public void resetRanks(List<Integer> lemmasIdList) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Index> query = session.createQuery("delete Index where lemmaId IN (:lemmaList)");
        query.setParameterList("lemmaList", lemmasIdList);
        query.executeUpdate();
        transaction.commit();
        session.close();
    }
}
