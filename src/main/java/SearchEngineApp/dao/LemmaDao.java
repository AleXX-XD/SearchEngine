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
    public void save(Lemma lemma) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(lemma);
        transaction.commit();
        session.close();
    }

    public Lemma get(String lemmaName) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query<Lemma> query = session.createQuery("from Lemma where lemma = :lemma");
        query.setParameter("lemma", lemmaName);
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
}
