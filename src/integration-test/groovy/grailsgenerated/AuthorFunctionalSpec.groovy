package grailsgenerated

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import grails.testing.spock.OnceBefore
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.grails.datastore.mapping.core.Datastore
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

@Rollback
@Integration
class AuthorFunctionalSpec extends Specification {

    @Shared
    @AutoCleanup
    HttpClient client

    @Autowired Datastore datastore

    BookService bookService

    @OnceBefore
    void init() {
        String baseUrl = "http://localhost:$serverPort"
        this.client  = HttpClient.create(new URL(baseUrl))
    }

//    void cleanup() {
//        assert false, "TODO: Provide a cleanup implementation if using MongoDB"
//    }

    String getResourcePath() {
        "/author"
    }

    Map getValidJson(Long bookId) {
        [name: "some valid name", book: bookId]
    }

    Map getInvalidJson() {
        [name: ""]
    }

    void "Test the index action"() {
        when:"The index action is requested"
        HttpResponse<List<Map>> response = client.toBlocking().exchange(HttpRequest.GET(resourcePath), Argument.of(List, Map))

        then:"The response is correct"
        response.status == HttpStatus.OK
        response.body() == []
    }

//    @Rollback
    void "Test the save action correctly persists an instance"() {
//        given:
        Long bookId = saveBook()

        when:"The save action is executed with no content"
        client.toBlocking().exchange(HttpRequest.POST(resourcePath, ""))

        then:"The response is correct"
        def e = thrown(HttpClientResponseException)
        e.response.status == HttpStatus.UNPROCESSABLE_ENTITY

        when:"The save action is executed with invalid data"
        client.toBlocking().exchange(HttpRequest.POST(resourcePath, invalidJson))

        then:"The response is correct"
        e = thrown(HttpClientResponseException)
        e.response.status == HttpStatus.UNPROCESSABLE_ENTITY

        when:"The save action is executed with valid data"
        HttpResponse<Map> response = client.toBlocking().exchange(HttpRequest.POST(resourcePath, getValidJson(bookId)), Map)

        then:"The response is correct"
        response.status == HttpStatus.CREATED
        response.body().id
        Author.count() == 1

        cleanup:
        def id = response.body().id
        def path = "${resourcePath}/${id}"
        response = client.toBlocking().exchange(HttpRequest.DELETE(path))
        assert response.status() == HttpStatus.NO_CONTENT

//        deleteBook(bookId)
    }

    void "Test the update action correctly updates an instance"() {
//        given:
        Long bookId = saveBook()

        when:"The save action is executed with valid data"
        HttpResponse<Map> response = client.toBlocking().exchange(HttpRequest.POST(resourcePath, getValidJson(bookId)), Map)

        then:"The response is correct"
        response.status == HttpStatus.CREATED
        response.body().id

        when:"The update action is called with invalid data"
        String path = "${resourcePath}/${response.body().id}"
        client.toBlocking().exchange(HttpRequest.PUT(path, invalidJson), Map)

        then: "The response is unprocessable entity"
        path
        def e = thrown(HttpClientResponseException)
        e.response.status == HttpStatus.UNPROCESSABLE_ENTITY

        when: "The update action is called with valid data"
        response = client.toBlocking().exchange(HttpRequest.PUT(path, getValidJson(bookId)), Map)

        then:"The response is correct"
        response.status == HttpStatus.OK
        response.body()

        cleanup:
        response = client.toBlocking().exchange(HttpRequest.DELETE(path))
        assert response.status() == HttpStatus.NO_CONTENT
//        deleteBook(bookId)
    }

    void "Test the show action correctly renders an instance"() {
//        given:
        Long bookId = saveBook()

        when:"The save action is executed with valid data"
        HttpResponse<Map> response = client.toBlocking().exchange(HttpRequest.POST(resourcePath, getValidJson(bookId)), Map)

        then:"The response is correct"
        response.status == HttpStatus.CREATED
        response.body().id

        when:"When the show action is called to retrieve a resource"
        def id = response.body().id
        String path = "${resourcePath}/${id}"
        response = client.toBlocking().exchange(HttpRequest.GET(path), Map)

        then:"The response is correct"
        response.status == HttpStatus.OK
        response.body().id == id

        cleanup:
        client.toBlocking().exchange(HttpRequest.DELETE(path))
//        deleteBook(bookId)
    }

//    @Rollback
    void "Test the delete action correctly deletes an instance"() {
        given:
        Long bookId = saveBook()

        when:"The save action is executed with valid data"
        HttpResponse<Map> response = client.toBlocking().exchange(HttpRequest.POST(resourcePath, getValidJson(bookId)), Map)

        then:"The response is correct"
        response.status == HttpStatus.CREATED
        response.body().id

        when:"When the delete action is executed on an unknown instance"
        def id = response.body().id
        def path = "${resourcePath}/99999"
        client.toBlocking().exchange(HttpRequest.DELETE(path))

        then:"The response is correct"
        def e = thrown(HttpClientResponseException)
        e.response.status == HttpStatus.NOT_FOUND

        when:"When the delete action is executed on an existing instance"
        path = "${resourcePath}/${id}"
        response = client.toBlocking().exchange(HttpRequest.DELETE(path))

        then:"The response is correct"
        response.status == HttpStatus.NO_CONTENT
        datastore.currentSession.flush()
        !Author.get(id)

//        cleanup:
//        deleteBook(bookId)
    }

    private Long saveBook() {
        Book.withNewTransaction {
            return bookService.save(new Book(name: "some valid book name")).id
        }
    }

    private void deleteBook(Long bookId) {
        Book.withNewTransaction {
            datastore.currentSession.flush()
            bookService.delete(bookId)
        }
    }
}
