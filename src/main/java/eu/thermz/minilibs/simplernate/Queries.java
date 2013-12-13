package eu.thermz.minilibs.simplernate;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import eu.thermz.minilibs.simplernate.exceptions.SimplernateException;

@SuppressWarnings("unchecked")
public class Queries {

	static Logger log = Logger.getLogger(Queries.class);

	private Queries(){	}
	
	private static SessionFactory sessionFactory = null;
	
	public static SessionFactory buildSessionFactory(String cfgFile) {
		SessionFactory sessionFactory = null;
		try {
			Configuration cfg = new Configuration();
			cfg.configure(cfgFile);
			sessionFactory = cfg.buildSessionFactory();
		} catch (Throwable ex) {
			log.error("Initial SessionFactory creation failed.",ex);
		}
		return sessionFactory;
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static void setSessionFactory(String cfgFile){
		sessionFactory = buildSessionFactory(cfgFile);
	}
	
	public static <T, C extends Criterion> List<T> select(final Class<T> clazz,final C ... conditions){
		return hibernateOp(new HOperationL<T>() {
			public List<T> ret(Session session, Transaction tx) throws Exception {
				Criteria criteria = session.createCriteria(clazz);
				if(conditions!=null)
					for (Criterion condition : conditions)
						criteria.add(condition);
				return (List<T>) criteria.list();
			}
		}, true);
	}
	
	public static <T, C extends Criterion> T selectSingle(final Class<T> clazz, final C ... conditions){
		return hibernateOp(new HOperation<T>() {
			public T ret(Session session, Transaction tx) throws Exception {
				Criteria criteria = session.createCriteria(clazz);
				if(conditions!=null)
					for (Criterion condition : conditions)
						criteria.add(condition);
				criteria.setMaxResults(1);
				return (T) criteria.uniqueResult();
			}
		}, true);
	}
	
	public static <T> T saveOrUpdate(final T object){
		return hibernateOp(new HOperation<T>() {
			public T ret(Session session, Transaction tx) throws Exception {
				session.saveOrUpdate(object);
				session.getTransaction().commit();
				return object;
			}
		}, true);
	}
	
	public static <T> T delete(final T object){
		return hibernateOp(new HOperation<T>() {
			public T ret(Session session, Transaction tx) throws Exception {
				session.delete(object);
				session.getTransaction().commit();
				return object;
			}
		}, true);
	}
	
	public static <T> T deleteSingle(List<? extends Criterion> conditions, final Class<T> clazz, Integer limit){
		return hibernateOp(new HOperation<T>() {
			public T ret(Session session, Transaction tx) throws Exception {
				T result = (T) session.createCriteria(clazz).uniqueResult();
				session.delete(result);
				return result;
			}
		}, true);
	}
	
	public static <T> List<T> hibernateOp(HOperationL<T> hopList, boolean silent){
		List<T> retVal = new ArrayList<T>();
		final Session session = getSessionFactory().openSession();
		final Transaction tx = session.beginTransaction();
		
		try{
			retVal = hopList.ret(session, tx);
		}catch(Exception e){
			tx.rollback();
			if(silent)
				log.error("failed hibernate operation: "+e.getMessage(),e);
			else
				throw new SimplernateException(e);
		}finally{
			session.close();
		}
		return retVal;
	}
	
	public static <T> T hibernateOp(HOperation<T> hop, boolean silent){
		T retVal = null;
		final Session session = getSessionFactory().openSession();
		final Transaction tx = session.beginTransaction();
		
		try{
			retVal = hop.ret(session, tx);
		}catch(Exception e){
			tx.rollback();
			if(silent)
				log.error("failed hibernate operation",e);
			else
				throw new SimplernateException();
		}finally{
			session.close();
		}
		return retVal;
	}
	
	public static <T> List<T> nativeSQL(final Class<T> clazz, final String query, final Object ... par){
		return hibernateOp(new HOperationL<T>() {
			public List<T> ret(Session session, Transaction tx) throws Exception {
				return new NativeSQLOperation<T>(){{
					_par = par;
					_query = query;
					_clazz = clazz;
				}}.ret(session, tx);
			}
		}, true);
	}
	
	public static <T> List<T> nativeSQL$(final Class<T> clazz, final String query, final Object ... par){
		return hibernateOp(new HOperationL<T>() {
			public List<T> ret(Session session, Transaction tx) throws Exception {
				return new NativeSQLOperation<T>(){{
					_par = par;
					_query = query;
					_clazz = clazz;
				}}.ret(session, tx);
			}
		}, false);
	}
	
	protected static class NativeSQLOperation<T> implements HOperationL<T> {
		public Object[] _par;
		public String _query;
		public Class<T> _clazz;
		
		public List<T> ret(Session session, Transaction tx) throws Exception {
			List<?> params = asList(_par);
			Query q = session.createSQLQuery(_query).addEntity(_clazz);
			for (int i = 0; i < params.size(); i++)
				q.setParameter(i, params.get(i));
			List<T> result = q.list();
			return result;
		}
	}
	
}
