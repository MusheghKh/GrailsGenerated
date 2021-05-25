package grailsgenerated

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class AuthorSpec extends Specification implements DomainUnitTest<Author> {

    def setup() {
    }

    def cleanup() {
    }

    void "test name can not be null"() {
        when:
        domain.name = null

        then:
        !domain.validate(['name'])
        domain.errors['name'].code == 'nullable'
    }

    void "test name can not be blank"() {
        when:
        domain.name = ''

        then:
        !domain.validate(['name'])
        domain.errors['name'].code == 'blank'
    }

    void "test name can have maximum 100 characters"() {
        when: 'for string of 101 characters'
        String str = 'a' * 101
        domain.name = str

        then: 'name validation fails'
        !domain.validate(['name'])
        domain.errors['name'].code == 'maxSize.exceeded'

        when: 'for string of 100 characters'
        str = 'a' * 100
        domain.name = str

        then: 'name validation passes'
        domain.validate(['name'])
    }
}
