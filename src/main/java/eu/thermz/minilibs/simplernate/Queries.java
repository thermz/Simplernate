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
	
	public static <T,C extends Criterion> List<T> select(Class<T> clazz, Order order, Integer offset, Integer limit, C ... criterions){
		return select( asList(criterions), clazz, order, offset, limit);
	}
	
	public static <T, C extends Criterion> List<T> select(Class<T> clazz, C ... criterions){
		return select( asList(criterions), clazz);
	}
	
	public static <T> List<T> select(List<? extends Criterion> conditions, Class<T> clazz, Order order, Integer offset, Integer limit){
		List<T> resultSet = new ArrayList<T>();
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(clazz);
		try {
			if(order!=null)criteria.addOrder(order);
			if(offset!=null)criteria.setFirstResult(offset);
			if(limit!=null)criteria.setMaxResults(limit);
			
			if(conditions!=null)
				for (Criterion condition : conditions)
					criteria.add(condition);
			
			resultSet = criteria.list();
		} finally {
			if (session.isOpen())
				session.close();
		}
		return resultSet;
	}
	
	public static <T> List<T> select(List<? extends Criterion> conditions, Class<T> clazz){
		return select(conditions, clazz, null, null, null);
	}
	
	public static <T> T selectSingle(List<Criterion> conditions, Class<T> clazz, Integer limit){
		T result = null;
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(clazz);
		try {
			if(limit!=null)criteria.setMaxResults(limit);
			if(conditions!=null)
				for (Criterion condition : conditions)
					criteria.add(condition);
			result = (T) criteria.uniqueResult();
		} finally {
			if (session.isOpen())
				session.close();
		}
		return result;
	}
	
	public static <T> T saveOrUpdate(T object){
		Session sess = getSessionFactory().openSession();
		sess.beginTransaction();
		try {
			sess.saveOrUpdate(object);
			sess.getTransaction().commit();
		} finally {
			if (sess.isOpen())
				sess.close();
		}
		return object;
	}
	
	public static <T> T delete(T object){
		Session sess = getSessionFactory().openSession();
		sess.beginTransaction();
		try {
			sess.delete(object);
			sess.getTransaction().commit();
		} finally {
			if (sess.isOpen())
				sess.close();
		}
		return object;
	}
	
	public static <T> T deleteSingle(List<? extends Criterion> conditions, Class<T> clazz, Integer limit){
		T result = null;
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(clazz);
		try {
			if(limit!=null)criteria.setMaxResults(limit);
			
			if(conditions!=null)
				for (Criterion condition : conditions)
					criteria.add(condition);
			
			result = (T) criteria.uniqueResult();
			session.delete(result);
		} finally {
			if (session.isOpen())
				session.close();
		}
		return result;
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
