Simplernate 
===========

Version 0.2.7 SNAPSHOT - {Hibernate 3.x supported for now}

Hibernate helps you to solve problems you wouldn't have without Hibernate.
Simplernate try to hide (wherever possible) those problems to you.

If you can choose your tools in your projects, and you'd like to have a simple and fluent interface to query your RDBMS, then go for something like jOOQ, and avoid Hibernate.
Hibernate is a powerful tool, maybe too much powerful, I would even say that is a dangerous power. If you are forced to use it (a legacy to maintain and evolve, or a project where you can't choose the tools) when you just need a more simple way to query and map your object without writing boileplate, dealing with sessions, transactions, proxy objects, etc.. then you could find a project like this one useful.

Simplernate will provide 2 syntax, and both them wraps Hibernate Criteria APIs:

+ 1 - Static syntax.
This syntax should be used for the simplest CRUD operations: 

```java
User user = selectFrom(User.class, eq("id",5) );

User richard = new User(); 
richard.setName("Richard");
richard.setCity("Turin");
richard.setAge(27);
saveOrUpdate(richard);

delete(richard);
```

Minimal operations require minimal syntax.

+ 2 - Builder syntax (a SQL-like DSL which is inspired by jOOQ which is inspired by SQL)
For a bit more complex query, but without join, groupby/having and some more advanced features.

```java
List<User> users = select().from(User.class)
                           .where( eq("name", "JesusChrist"), 
                                   ge("age", 33))
                           .limit(5)
                           .offset(1)
                           .fetch();
				   				                
User user =   select().from(User.class)
                      .where( eq("name", "Richard"), 
                              ge("age", 18) )
                      .fetchOne();
```

And also 

+ A simpler API for native SQL Hibernate queries

```java
List<User> users = sql( User.class, 
                        "select * from users where name=? and age>?", 
                        "Richard",
                        18 );
```

+ A simpler API for HQL queries (for this part I'm considering to integrate with Torpedo Query which is a powerful HQL wrapper. I still haven't figure out how).

```java
List<User> users = hql( User.class, 
                        "where name=:name and age>:age", 
                        kv("name","Richard"),
                        kv("age",18) );
```

___

In the ethernal debate about checked exceptions vs unchecked exceptions, Simplernate clearly takes a side, and in Simplernate APIs checked exceptions doesn't exist! Every Simplernate API can throw a SimplernateException (which is a RuntimeException) with a root cause from Hibernate or the DB driver you're using, so you will not lose any informations for the reasons of the error, but you're not forced to catch or declare all the exceptions from the "lower" levels ☺.

No need to boilerplate your code for the session handling, no need to try and catch and rollback and finally etc.
Just tell simplernate your query, the class where you're gonna map the data, and that's it!

___

TODO list:

+ Update and save in static syntax
+ Tests for offset and limit in builder syntax
+ Disjunctions and conjuntions in builder syntax
+ Transactions
+ Delete / Insert / Update in HQL typesafe commands.
