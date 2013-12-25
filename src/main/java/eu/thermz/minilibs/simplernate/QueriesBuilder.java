package eu.thermz.minilibs.simplernate;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;

public class QueriesBuilder<T> {

	protected QueriesBuilder(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	Class<T> clazz = null;
	
	List<? extends Criterion> crits = new ArrayList<Criterion>();
	
	public QueriesBuilder<T> where(Criterion ... criterions){
		crits = asList(criterions);
		return this;
	}
	
	public QueriesBuilder<T> where(boolean b){
		//TODO handle false case to return always NO results
		return this;
	}
	
	public List<T> fetch(){
		return Queries.selectAllFrom(clazz, crits.toArray( new Criterion[crits.size()] ));
	}
	
	public T fetchOne(){
		return Queries.selectFrom(clazz, crits.toArray( new Criterion[crits.size()] ));
	}
	
	public static class QueryBFactory{
		
		public static QueryBFactory select(){
			return new QueryBFactory();
		}
		
		public <C> QueriesBuilder<C> from(Class<C> cl){
			return new QueriesBuilder<C>(cl);
		}
	}
	
}
