package ffa.groovy.ee.kata

import static org.junit.Assert.*;
import groovy.sql.Sql;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

class PersonDAOTest {

    
    def sql = Sql.newInstance("jdbc:h2:mem:", "sa", "sa")
    
    @Before
    public void setupPersonDAO() {
        new PersonDAO(sql)
    }
    
    @After
    public void shutdownDB() {
        Person.truncate()
        sql.execute("shutdown immediately")
    }
    
    @Test
    public void shouldStorePerson() throws Exception {
        def fooson = new Person(name: "Fooson", age: 59)
        fooson.save()
        assert Person.list().contains(fooson)
    }
    
    @Test
    public void shouldFindPersonByName() throws Exception {
        def barson = new Person(name: "Barson").save()
        def fooson = new Person(name: "Fooson").save()
        def search = Person.findByName("Fooson")
        
        assert search.contains(fooson)
        assert !search.contains(barson)
    }
    
    @Test
    public void shouldBeAbleToDoTransactionalDAO() throws Exception {
        try {
            sql.withTransaction {
                new Person(name: "foo").save()
                assert !Person.list().isEmpty()
                throw new RuntimeException("oops")
            }
        } catch (RuntimeException e) { } 
        finally {
            assert Person.list().isEmpty()
        }
        
        
    }
}
