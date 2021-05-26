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

    private Long setupData() {
        addAuthors(new Book(name: "book1"))
        addAuthors(new Book(name: "book2"))
        Book book = addAuthors(new Book(name: "book3"))
        addAuthors(new Book(name: "book4"))
        addAuthors(new Book(name: "book5"))
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
        book.save(flush: true, failOnError: true)
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
        Long bookId = setupData()

        expect:
        bookService.count() == 5
        Author.count() == 25

        when:
        bookService.delete(bookId)
        datastore.currentSession.flush()

        then:
        bookService.count() == 4
        Author.count() == 20
    }

    void "test save"() {
        when:
        Book book = new Book(name: 'some valid name')
        bookService.save(book)

        then:
        book.id != null
    }
}
