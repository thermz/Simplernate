Simplernate 
===========

Version 0.2 SNAPSHOT

Simple and minimal Hibernate wrapper, that allow you to use a minimal DSL to query the database.

Small samples:

```java
User user = select(User.class, eq("id",5) );
user.setCity("Turin");
saveOrUpdate(user);
```

No need to boilerplate your code for the session handling, no need to try and catch and rollback and finally etc.
Just tell simplernate your query, the class where you're gonna map the data, and that's it!
