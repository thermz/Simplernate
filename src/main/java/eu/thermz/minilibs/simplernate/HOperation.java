package eu.thermz.minilibs.simplernate;

import org.hibernate.Session;
import org.hibernate.Transaction;

public interface HOperation<T> {

	public T ret(Session session, Transaction tx) throws Exception;
	
}
