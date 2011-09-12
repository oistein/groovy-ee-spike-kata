package ffa.groovy.ee.kata

import groovy.sql.Sql;
import groovy.xml.MarkupBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServlet

class PersonServlet extends HttpServlet {

    PersonServlet() {
        new PersonDAO(Sql.newInstance("jdbc:h2:mem:test", "sa", "sa"))
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html")
        if ("/person/list" == req.getPathInfo()) {
            writeListPage(resp)
        } else {
            writeCreatePage(resp, req)
        }
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        if (validateName(req.getParameter("name")) == null &&
            validateAge(req.getParameter("age")) == null) {
            new Person(name: req.getParameter("name"), age: req.getParameter("age") as int).save()
        }
        resp.sendRedirect "/person/list"
    }
    
    String validateName(String name) {
        if (!name) return "must not be empty"
        if (name.length() > 50) return "must be less than 50"
    }
    
    String validateAge(String age) {
        if (!age) return null
        if (!age.isInteger()) return "must be a valid number"
    }

    private writeListPage(HttpServletResponse resp) {
        new MarkupBuilder(resp.getWriter()).
            html {
                head { title("Person List") }
                body {
                    table {
                        thead {
                            tr {
                                th("Name")
                                th("Age")
                            }
                        }
                        tbody {
                            for (def p in Person.list()) {
                                tr {
                                    td(p.name)
                                    td(p.age)
                                }
                            }
                        }
                    }
                }
            }
    }
    
    private writeCreatePage(HttpServletResponse resp, HttpServletRequest req) {
        new MarkupBuilder(resp.getWriter()).
            html {
                head { title("Create new person") }
                body {
                    form(method: "post", action: "create") {
                        input(type: "text", name: "name", value: req.getParameter("name") ?:"")
                        input(type: "text", name: "age",  value: req.getParameter("age")  ?:"")
                        input(type: "submit", value: "Create")
                    }
                }
            }
    }
}
