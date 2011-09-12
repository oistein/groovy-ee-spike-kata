package ffa.groovy.ee.kata

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;

@EqualsAndHashCode(includes="name")
@ToString
class Person {
    String name
    Integer age
}
