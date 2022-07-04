package SearchEngineApp.dao;

import SearchEngineApp.models.Field;
import SearchEngineApp.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class FieldDao
{
    public void create() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        String sql = "CREATE TABLE IF NOT EXISTS Field " +
                "(id integer not null auto_increment," +
                "name varchar(255)," +
                "selector varchar(255)," +
                "weight float," +
                "primary key (id))";
        Query<Field> query = session.createSQLQuery(sql).addEntity(Field.class);
        query.executeUpdate();
        transaction.commit();
        session.close();
    }

    public void drop() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        String sql = "DROP TABLE IF EXISTS Field";
        Query<Field> query = session.createSQLQuery(sql).addEntity(Field.class);
        query.executeUpdate();
        transaction.commit();
        session.close();
    }

    public void save(Field field) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(field);
        transaction.commit();
        session.close();
    }

    public List<Field> get() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Field> query = builder.createQuery(Field.class);
        Root<Field> root = query.from(Field.class);
        query.select(root);
        Query<Field> q = session.createQuery(query);
        List<Field> list = q.getResultList();
        transaction.commit();
        session.close();
        return list;
    }

    public int getSize() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Long count = ((Long) session.createQuery("select count(*) from Field").uniqueResult());
        transaction.commit();
        session.close();
        return count.intValue();
    }
}
