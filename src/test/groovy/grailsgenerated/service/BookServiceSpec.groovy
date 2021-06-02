package grailsgenerated.service

import grails.test.hibernate.HibernateSpec
import grails.testing.services.ServiceUnitTest
import grailsgenerated.Book
import grailsgenerated.BookService

class BookServiceSpec extends HibernateSpec implements ServiceUnitTest<BookService>{

    def setup() {
    }

    def cleanup() {
    }

    @Override
    List<Class> getDomainClasses() {
        [Book]
    }

    private Long setupData() {
        new Book(name: "book0").save(flush: true, failOnError: true)
        new Book(name: "book1").save(flush: true, failOnError: true)
        Book book = new Book(name: "book2").save(flush: true, failOnError: true)
        new Book(name: "book3").save(flush: true, failOnError: true)
        new Book(name: "book4").save(flush: true, failOnError: true)
        book.id
    }

    def "test list books"() {
        setupData()

        when:
        List<Book> books = service.list([max: 2, offset: 2])

        then:
        books.size() == 2
        books[0].name == "book2"
        books[1].name == "book3"
    }

    def "test get author by id"() {
        Long bookId = setupData()

        when:
        Book book = service.get(bookId)

        then:
        book
        book.name == "book2"
    }

    def "test count"() {
        setupData()

        expect:
        service.count() == 5
    }

    def "test delete"() {
        Long bookId = setupData()

        when:
        Book book = service.delete(bookId)

        then:
        Book.count() == 4
        book
        book.id == bookId
        book.name == "book2"
    }

    def "test save"() {
        setupData()

        when:
        Book book = service.save(new Book(name: "new book"))

        then:
        book
        Book.count() == 6
        book.name == "new book"
    }
}
