package eu.thermz.minilibs;

import static eu.thermz.minilibs.simplernate.Queries.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.thermz.minilibs.dto.User;

public class TestQueries {

	static User u = new User();
	static {
		u.setAge(27);
		u.setName("Richard");
	}
	
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
		List<User> users = hql(
				User.class, 
				"where name=:name and age>:age", 
				kv("name","Richard"),
				kv("age",18)
		);
		assertTrue(users.size()>0);
	}
	
	@Test
	public void testSQL(){
		List<User> users = sql(
				User.class, 
				"select * from users where name=? and age>?", 
				"Richard",
				18
		);
		assertTrue(users.size()>0);
	}
	
}
