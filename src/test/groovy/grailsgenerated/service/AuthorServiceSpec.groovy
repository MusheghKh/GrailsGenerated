package grailsgenerated.service

import grails.test.hibernate.HibernateSpec
import grails.testing.services.ServiceUnitTest
import grails.util.Pair
import grailsgenerated.Author
import grailsgenerated.AuthorService
import grailsgenerated.Book

class AuthorServiceSpec extends HibernateSpec implements ServiceUnitTest<AuthorService>{

    def setup() {
    }

    def cleanup() {
    }

    @Override
    List<Class> getDomainClasses() {
        [Author, Book]
    }

    private Pair<Long, Long> setupData() {
        Book book = new Book(name: "book name")
        [
                new Author(name: "author0"),
                new Author(name: "author1"),
                new Author(name: "author2"),
                new Author(name: "author3"),
                new Author(name: "author4")
        ].each {
            book.addToAuthors(it)
        }
        book = book.save(flush: true, failOnError: true)
        new Pair<Long, Long>(book.id, book.authors[2].id)
    }

    def "test list authors"() {
        setupData()

        when:
        List<Author> authors = service.list([max: 2, offset: 2])

        then:
        authors.size() == 2
        authors[0].name == "author2"
        authors[1].name == "author3"
    }

    def "test get author by id"() {
        Long authorId = setupData().getbValue()

        when:
        Author author = service.get(authorId)

        then:
        author
        author.name == "author2"
    }

    def "test get author by book id"() {
        Long bookId = setupData().getaValue()

        when:
        List<Author> authors = service.listByBookId(bookId, [max: 10, offset: 0])

        then:
        authors.size() == 5
    }

    def "test count"() {
        setupData()

        expect:
        service.count() == 5
    }

    def "test delete"() {
        Long authorId = setupData().getbValue()

        when:
        Author author = service.delete(authorId)

        then:
        Author.count() == 4
        author
        author.id == authorId
        author.name == "author2"
    }

    def "test save"() {
        Long bookId = setupData().getaValue()

        when:
        Author author = service.save(new Author(name: "new author", book: Book.get(bookId)))

        then:
        author
        Author.count() == 6
        author.name == "new author"
        author.book.id == bookId
    }
}
