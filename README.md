Simplernate 
===========

Version 0.2 SNAPSHOT - {Hibernate 3.x supported for now}

If you're in the need of using something like simplernate, then you must have made bad choices :-)
If you like to use a simple and fluent interface to query your RDBMS, then go for something like #jOOQ.

Hibernate is a powerful tool, maybe too much powerful, I would even say that is a dangerous power. If you are forced to use it but you just need a simple way of querying the database and mapping your object without writing any boileplate, dealing with sessions, transactions, proxy objects, etc.. then you could find a project like this one useful.

Simplernate wants to be a simple and minimal Hibernate wrapper, that allow the developer to use a fluent interface to query the database.

It will provide 2 kind of syntax:

+ 1 - A very simple DSL with import static that could to be useful in very simple query. (in development)
+ 2 - A more complete DSL with builder for some more advanced feature (limit, offset etc.) (not yet implemented) This DSL will be a SQL-like DSL.
 
And also a simpler way to make nativeSQL query or HQL query.

Small samples for 1:

```java
User user = select(User.class, eq("id",5) );
user.setCity("Turin");
saveOrUpdate(user);
```

No need to boilerplate your code for the session handling, no need to try and catch and rollback and finally etc.
Just tell simplernate your query, the class where you're gonna map the data, and that's it!
