package ffa.groovy.ee.kata

import groovy.sql.Sql

class PersonDAO {
    Sql sql    
    
    PersonDAO(Sql sql) {
        this.sql = sql
        
        createTable()
        
        addSave()
        addList()
        addFindByName()
        addTruncate()
    }
    
    private void addSave() {
        Person.metaClass.save = {
            sql.execute("insert into person (name, age) values(?, ?)", [delegate.name, delegate.age])
            delegate
        }
    }
    
    private void addList() {
        Person.metaClass.static.list = {
            def persons = []
            sql.eachRow("select name, age from person") { row->
                persons << new Person(name: row.name, age: row.age)
            }
            persons
        }
    }
    
    private void addFindByName() {
        Person.metaClass.static.findByName = { name ->
            def persons = []
            sql.eachRow("select name, age from person where name = ?", [name]) { row->
                persons << new Person(name: row.name, age: row.age)
            }
            persons
        }
    }
    
    private void createTable() {
        sql.execute("create table if not exists person (name varchar(50), age integer)")
    }
    
    private void addTruncate() {
        Person.metaClass.static.truncate = {
            sql.execute("truncate table person")
        }
    }
    
}
