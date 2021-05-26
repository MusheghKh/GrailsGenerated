package grailsgenerated

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.grails.datastore.mapping.core.Datastore
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@Integration
@Rollback
class BookServiceSpec extends Specification {

    BookService bookService
    @Autowired Datastore datastore

    private static Long setupData() {
        addAuthors(new Book(name: "book1").save(flush: true, failOnError: true))
        addAuthors(new Book(name: "book2").save(flush: true, failOnError: true))
        Book book = addAuthors(new Book(name: "book3").save(flush: true, failOnError: true))
        addAuthors(new Book(name: "book4").save(flush: true, failOnError: true))
        addAuthors(new Book(name: "book5").save(flush: true, failOnError: true))
        book.id
    }

    private static Book addAuthors(Book book) {
        List<Author> authors = []
        (1..5).each {
            authors.add(new Author(name: "${book.name} author$it"))
        }
        authors.each {
            book.addToAuthors(it)
        }
        book
    }

//    void cleanup() {
//        assert false, "TODO: Provide a cleanup implementation if using MongoDB"
//    }

    void "test get"() {
        setupData()

        expect:
        bookService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Book> bookList = bookService.list(max: 2, offset: 2)

        then:
        bookList.size() == 2
        bookList[0].name == 'book3'
        bookList[1].name == 'book4'
    }

    void "test count"() {
        setupData()

        expect:
        bookService.count() == 5
    }

    void "test delete"() {
        given:
        Long bookId = setupData()

        expect:
        bookService.count() == 5

        when:
        bookService.delete(bookId)
        datastore.currentSession.flush()

        then:
        bookService.count() == 4
    }

    void "test save"() {
        when:
        Book book = new Book(name: 'some valid name')
        bookService.save(book)

        then:
        book.id != null
    }
}
