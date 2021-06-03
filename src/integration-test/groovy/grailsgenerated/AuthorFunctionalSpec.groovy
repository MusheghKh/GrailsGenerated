package grailsgenerated

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import grails.testing.spock.OnceBefore
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.grails.datastore.mapping.core.Datastore
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

@Integration
class AuthorFunctionalSpec extends Specification {

    @Shared
    @AutoCleanup
    HttpClient client

    @Autowired Datastore datastore

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

    String getBookResourcePath() {
        "/book"
    }

    String get

    Map getValidJson(Long bookId) {
        [name: "some valid name", book: bookId]
    }

    Map getInvalidJson() {
        [name: ""]
    }

    void "Test the index action"() {
        Long bookId = requestSaveBook()

        when:"The index action is requested"
        HttpResponse<List<Map>> response = client.toBlocking().exchange(HttpRequest.GET(resourcePath), Argument.of(List, Map))

        then:"The response is correct"
        response.status == HttpStatus.OK
        response.body() == []

        when:"Save some instances and request index action"
        HttpResponse<Map> response1 = client.toBlocking().exchange(HttpRequest.POST(resourcePath, getValidJson(bookId)), Map)
        HttpResponse<Map> response2 = client.toBlocking().exchange(HttpRequest.POST(resourcePath, getValidJson(bookId)), Map)
        response = client.toBlocking().exchange(HttpRequest.GET(resourcePath), Argument.of(List, Map))

        then:"The response is correct"
        response.status == HttpStatus.OK
        response.body().size() == 2

        cleanup:
        def id = response1.body().id
        def path = "${resourcePath}/${id}"
        HttpResponse<Map> deleteResponse = client.toBlocking().exchange(HttpRequest.DELETE(path))
        assert deleteResponse.status() == HttpStatus.NO_CONTENT

        id = response2.body().id
        path = "${resourcePath}/${id}"
        deleteResponse = client.toBlocking().exchange(HttpRequest.DELETE(path))
        assert deleteResponse.status() == HttpStatus.NO_CONTENT

        requestDeleteBook(bookId)
    }

    void "test the listByBookId action"() {
        Long bookId = requestSaveBook()

        when:"The listByBookId action is requested with wrong bookID"
        HttpResponse<List<Map>> response = client.toBlocking().exchange(HttpRequest.GET("${resourcePath}/ofBook/9999"), Argument.of(List, Map))

        then:"The response is not found"
        HttpClientException exception = thrown(HttpClientException)
        exception.response.status == HttpStatus.NOT_FOUND

        when:"The listByBookId action is requested"
        response = client.toBlocking().exchange(HttpRequest.GET("${resourcePath}/ofBook/$bookId"), Argument.of(List, Map))

        then:"The response is correct"
        response.status == HttpStatus.OK
        response.body() == []

        when:"Save some instances and request listByBookId action"
        HttpResponse<Map> response1 = client.toBlocking().exchange(HttpRequest.POST(resourcePath, getValidJson(bookId)), Map)
        HttpResponse<Map> response2 = client.toBlocking().exchange(HttpRequest.POST(resourcePath, getValidJson(bookId)), Map)
        response = client.toBlocking().exchange(HttpRequest.GET("${resourcePath}/ofBook/$bookId"), Argument.of(List, Map))

        then:"The response is correct"
        response.status == HttpStatus.OK
        response.body().size() == 2

        cleanup:
        def id = response1.body().id
        def path = "${resourcePath}/${id}"
        HttpResponse<Map> deleteResponse = client.toBlocking().exchange(HttpRequest.DELETE(path))
        assert deleteResponse.status() == HttpStatus.NO_CONTENT

        id = response2.body().id
        path = "${resourcePath}/${id}"
        deleteResponse = client.toBlocking().exchange(HttpRequest.DELETE(path))
        assert deleteResponse.status() == HttpStatus.NO_CONTENT

        requestDeleteBook(bookId)
    }

    @Rollback
    void "Test the save action correctly persists an instance"() {
        Long bookId = requestSaveBook()

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

        requestDeleteBook(bookId)
    }

    void "Test the update action correctly updates an instance"() {
        Long bookId = requestSaveBook()

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
        requestDeleteBook(bookId)
    }

    void "Test the show action correctly renders an instance"() {
        Long bookId = requestSaveBook()

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
        requestDeleteBook(bookId)
    }

    @Rollback
    void "Test the delete action correctly deletes an instance"() {
        Long bookId = requestSaveBook()

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

        cleanup:
        requestDeleteBook(bookId)
    }

    private Long requestSaveBook() {
        HttpResponse<Map> response = client.toBlocking().exchange(HttpRequest.POST("/book", [name: "book name"]), Map)
        response.body().id
    }

    private void requestDeleteBook(Long bookId) {
        client.toBlocking().exchange(HttpRequest.DELETE("${bookResourcePath}/${bookId}"))
    }
}
