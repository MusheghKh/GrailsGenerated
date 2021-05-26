package grailsgenerated

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.grails.datastore.mapping.core.Datastore
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@Integration
@Rollback
class AuthorServiceSpec extends Specification {

    AuthorService authorService
    @Autowired Datastore datastore

    private Long setupData() {
        Book book = new Book(name: "book1").save(flush: true, failOnError: true)
        book.addToAuthors(new Author(name: "author1", book: book).save(flush: true, failOnError: true))
        book.addToAuthors(new Author(name: "author2", book: book).save(flush: true, failOnError: true))

        Author author = new Author(name: "author3", book: book).save(flush: true, failOnError: true)
        book.addToAuthors(author)

        book.addToAuthors(new Author(name: "author4", book: book).save(flush: true, failOnError: true))
        book.addToAuthors(new Author(name: "author5", book: book).save(flush: true, failOnError: true))

        author.id
    }

//    void cleanup() {
//        assert false, "TODO: Provide a cleanup implementation if using MongoDB"
//    }

    void "test get"() {
        setupData()

        expect:
        authorService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Author> authorList = authorService.list(max: 2, offset: 2)

        then:
        authorList.size() == 2
        authorList[0].name == "author3"
        authorList[1].name == "author4"
    }

    void "test count"() {
        setupData()

        expect:
        authorService.count() == 5
    }

    void "test delete"() {
        Long authorId = setupData()
        Author author = authorService.get(authorId)

        expect:
        authorService.count() == 5

        when:
        author.book.removeFromAuthors(author)
        authorService.delete(authorId)
        datastore.currentSession.flush()

        then:
        authorService.count() == 4
    }

    void "test save"() {
        Book book = new Book(name: "some book").save(flush: true, failOnError: true)

        when:
        Author author = new Author(name: "some valid name", book: book)
        authorService.save(author)

        then:
        author.id != null
    }
}
