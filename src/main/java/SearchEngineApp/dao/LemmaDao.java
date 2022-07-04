package SearchEngineApp.dao;

import SearchEngineApp.models.Index;
import SearchEngineApp.models.Lemma;
import SearchEngineApp.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class LemmaDao
{
    public Lemma save(Lemma lemma) {
        if(get(lemma.getLemma(), lemma.getSiteId()) != null ) {
            lemma = get(lemma.getLemma(), lemma.getSiteId());
            update(lemma);
        }
        else {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            session.save(lemma);
            transaction.commit();
            session.close();
        }
        return lemma;
    }

    public Lemma get(String lemmaName, int idSite) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Lemma> query = session.createQuery("from Lemma where lemma = :lemma AND siteId = :siteId");
        query.setParameter("lemma", lemmaName);
        query.setParameter("siteId", idSite);
        query.setMaxResults(1);
        Lemma lemma = query.uniqueResult();
        transaction.commit();
        session.close();
        return lemma;
    }

    public List<Lemma> getLemmas(List<String> nameList) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Lemma> query = session.createQuery("from Lemma where lemma IN (:lemmaList)");
        query.setParameterList("lemmaList", nameList);
        List<Lemma> lemmaList = query.getResultList();
        transaction.commit();
        session.close();
        return lemmaList;
    }

    public List<Lemma> getLemmas(int idSite) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Lemma> query = session.createQuery("from Lemma where siteId = :siteId");
        query.setParameter("siteId", idSite);
        List<Lemma> lemmaList = query.getResultList();
        transaction.commit();
        session.close();
        return lemmaList;
    }

    public List<String> getLemmasNames(int siteId) {
        List<String> namesList = new ArrayList<>();
        List<Lemma> lemmaList = getLemmas(siteId);
        for(Lemma lemma : lemmaList) {
            namesList.add(lemma.getLemma());
        }
        return namesList;
    }

    public List<Integer> getLemmasId(int siteId) {
        List<Integer> idList= new ArrayList<>();
        List<Lemma> lemmaList = getLemmas(siteId);
        for(Lemma lemma : lemmaList) {
            idList.add(lemma.getId());
        }
        return idList;
    }

    public void update(Lemma lemma) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        lemma.setFrequency(lemma.getFrequency() + 1);
        session.update(lemma);
        transaction.commit();
        session.close();
    }

    public void reset(int idSite) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Index> query = session.createQuery("delete Lemma where siteId = :siteId");
        query.setParameter("siteId", idSite);
        query.executeUpdate();
        transaction.commit();
        session.close();
    }
}
