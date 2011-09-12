package ffa.groovy.ee.kata

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse

import org.junit.Before;
import org.junit.Test;

import spock.lang.Specification;

class PersonServletTest extends Specification {

    def req = Mock(HttpServletRequest)
    def res = Mock(HttpServletResponse)
    def servlet
    
    def setup() {
        servlet = new PersonServlet()
        Person.metaClass  = null
    }
    
    def "should create person"() {
        setup: 
        def name = "foo"
        def age = "20"
        def person = null
        Person.metaClass.save = { person = delegate}
        
        when:
        servlet.doPost(req, res)
        
        then:
        1 * res.sendRedirect("/person/list")
        (1.._) * req.getParameter("name") >> name
        (1.._) * req.getParameter("age") >> age
        person == new Person(name: "foo", age: 20)
    }
    
    def "should validate person name input"() {
        expect:
        servlet.validateName(name) == error
        
        where:
        name                                                            |  error
        null                                                            |  "must not be empty"
        ""                                                              |  "must not be empty"
        "foo"                                                           |  null
        "waaaaaay toooooooo long name maaaaan, seriously............."  |  "must be less than 50"
    }
    
    def "should show create page"() {
        setup:
        def writer = new StringWriter()
        
        when:
        servlet.doGet(req, res)
        
        then: 
        1 * res.getWriter() >> new PrintWriter(writer)
        1 * res.setContentType("text/html")
        
        expect:
        def inputs = ["name", "age"]
        new XmlSlurper().parseText(writer.toString()).depthFirst().grep{ it.@name != '' }.'@name'*.text() == inputs
    }
    
    def "should list persons"() {
        setup:
        def writer = new StringWriter()
        def persons = [new Person(name:"foo", age: 10), new Person(name:"bar", age: 40)]
        Person.metaClass.static.list = { persons }
        
        when:
        servlet.doGet(req, res)
        
        then:
        1 * res.getWriter() >> new PrintWriter(writer)
        1 * req.getPathInfo() >> "/person/list"
        
        expect:
        def html = new XmlSlurper().parseText(writer.toString()).
        html.depthFirst().grep{ it.name() == "td" }*.text().containsAll(["10", "bar", "foo", "40"])
    }
    
}
