package eu.thermz.minilibs;

import static eu.thermz.minilibs.simplernate.Queries.*;
import static org.junit.Assert.*;
import static org.hibernate.criterion.Restrictions.*;

import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.thermz.minilibs.dto.User;
import eu.thermz.minilibs.simplernate.exceptions.SimplernateException;
import static eu.thermz.minilibs.simplernate.QueriesBuilder.QueryBFactory.*;


/**
 * Test for syntax static and for syntax builder
 * <br/>
 * <br/>
 * 
 * @author Riccardo Muzzi
 *
 */
//TODO use H2 or HSQL embedded for test instead of external MySQL server
public class TestQueries {

	static User u = userFactory();
	
	@BeforeClass
	public static void init(){
		setSessionFactory("test.cfg.xml");
		saveOrUpdate(u);
	}
	
	@AfterClass
	public static void after(){
		delete(u);
	}

	@Test
	public void testParamsCreation(){
		Map<String, ?> params = params( kv("12","value12"),
										kv("42","meaningOfLife"),
										kv("1", "first") );
		assertEquals("meaningOfLife", params.get("42"));
	}
	
	@Test
	public void testHQL(){
		List<User> users = hql( User.class, 
								"where name=:name and age>:age", 
								kv("name","Richard"),
								kv("age",18) );
		assertTrue(users.size()>0);
	}
	
	@Test
	public void testSQL(){
		List<User> users = sql( User.class, 
								"select * from users where name=? and age>?", 
								"Richard",
								18 );
		assertTrue(users.size()>0);
	}

	@Test
	public void testSaveAndDelete(){
		User user = userFactory();
		saveOrUpdate(user);
		delete(user);
	}
	
	@Test(expected=SimplernateException.class)
	public void testFailSaveStaleStateObject(){
		User user = userFactory();
		saveOrUpdate(user);
		delete(user);
		saveOrUpdate(user);
	}
	
	@Test
	public void testBuilderSyntaxBase(){
		List<User> users = select().from(User.class)
								   .where(true)
								   .fetch();
		assertTrue(users.size()>0);
	}
	
	@Test 
	public void testBuilderConditionsFalse(){
		List<User> users = select().from(User.class)
				   				   .where( eq("name", "JesusChrist"), 
				   						   ge("age", 33))
				   				   .fetch();
		assertEquals(0, users.size());
	}
	
	@Test 
	public void testBuilderConditionsTrue(){
		List<User> users = select().from(User.class)
				   				   .where( eq("name", "Richard"), 
				   						   ge("age", 18) )
				   				   .fetch();
		assertTrue(users.size()>0);
	}
	
	@Test 
	public void testBuilderFetchOne(){
		User user = 		select().from(User.class)
									.where( eq("name", "Richard"), 
				   					   	    ge("age", 18) )
				   					.fetchOne();
		assertNotNull(user);
		assertEquals( new Integer(27), user.getAge());
	}
	
	private static User userFactory(){
		User u = new User();
		u.setAge(27);
		u.setName("Richard");
		return u;
	}
}
