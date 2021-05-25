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
        List<Author> authors = []
        (1..5).each {
            authors.add(new Author(name: "name$it"))
        }

        addAuthors(authors, new Book(name: "name1").save(flush: true, failOnError: true))
        addAuthors(authors, new Book(name: "name2").save(flush: true, failOnError: true))
        Book book = new Book(name: "name3").save(flush: true, failOnError: true)
        addAuthors(authors, book)
        addAuthors(authors, new Book(name: "name4").save(flush: true, failOnError: true))
        addAuthors(authors, new Book(name: "name5").save(flush: true, failOnError: true))
        book.id
    }

    private static addAuthors(List<Author> authors, Book book) {
        authors.each {
            book.addToAuthors(it)
        }
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
        bookList[0].name == 'name3'
        bookList[1].name == 'name4'
    }

    void "test count"() {
        setupData()

        expect:
        bookService.count() == 5
    }

    void "test delete"() {
        given:
        Long bookId = setupData()
        Book book = bookService.get(bookId)
        book.authors = null

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
