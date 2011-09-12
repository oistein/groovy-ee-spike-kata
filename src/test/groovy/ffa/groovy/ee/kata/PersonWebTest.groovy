package ffa.groovy.ee.kata

import static org.junit.Assert.*;

import java.security.SecureRandom;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server
import org.mortbay.jetty.servlet.Context
import org.mortbay.jetty.servlet.ServletHandler
import org.mortbay.jetty.servlet.ServletHolder

import geb.junit4.GebTest;

class PersonWebTest extends GebTest {
    def server
    
    @BeforeClass
    static void silence() {
        org.mortbay.log.Log.setLog(null)
    }
    
    @Before
    public void setupContext() {
        int port = new SecureRandom().nextInt(2**16 - 1000) + 1000
        browser.baseUrl = "http://localhost:$port"
        
        
        server = new Server(port)
        Context root = new Context(server,"/",Context.SESSIONS);
        root.addServlet(new ServletHolder(new PersonServlet()), "/*");
        server.start()

        Person.truncate()
    }
    
    @After
    public void shutdown() {
        server.stop()
    }
    
    @Test
    public void shouldCreatePerson() throws Exception {
        go "/person/create"
        
        // Fill in form
        $("input", name: "name").value("Fooson")
        $("input", name: "age").value("59")
        $("input", type: "submit").click()
        
        // Redirected to list page
        assert $("td", text: "Fooson")
        assert $("td", text: "59")
        
        assert Person.list().contains(new Person(name: "Fooson", age: 59))
    }
    
    @Test
    public void shouldNotCreatePersonWithInvalidAge() throws Exception {
        assert Person.list().isEmpty()
        go "/person/create"
        
        $("input", name: "name").value("Fooson")
        $("input", name: "age").value("not an int")
        $("input", type: "submit").click()
        
        assert Person.list().isEmpty()
        assert !$("td")
    }

}
