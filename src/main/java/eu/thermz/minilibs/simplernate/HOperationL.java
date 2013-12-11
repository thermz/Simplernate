package eu.thermz.minilibs.simplernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public interface HOperationL<T> {

	public List<T> ret(Session session, Transaction tx) throws Exception;
	
}
