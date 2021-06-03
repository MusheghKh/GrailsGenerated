package grailsgenerated

import grails.validation.ValidationException
import grailsgenerated.controller.ControllerExtensions
import grailsgenerated.exceptions.BookNotFoundException
import org.springframework.web.servlet.support.RequestContextUtils

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class AuthorController implements ControllerExtensions{

    AuthorService authorService
    def messageSource

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond authorService.list(params)
    }

    def listByBookId(Long bookId, Integer max) {
        params.max = Math.min(max ?: 10, 100)
        try {
            respond authorService.listByBookId(bookId, params)
        } catch(BookNotFoundException e) {
            String message = messageSource.getMessage("default.not.found.message", ["Book", bookId] as Object[], RequestContextUtils.getLocale(request))
            respondError NOT_FOUND, message
        }
    }

    def show(Long id) {
        respond authorService.get(id)
    }

    @Transactional
    def save(Author author) {
        if (author == null) {
            respondError NOT_FOUND
            return
        }
        if (author.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond author.errors
            return
        }

        try {
            authorService.save(author)
        } catch (ValidationException e) {
            respond author.errors
            return
        }

        respond author, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(Long id) {
        Author author = authorService.get(id)
        if (author == null) {
            respondError NOT_FOUND
            return
        }
        author.properties = request.JSON
        if (author.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond author.errors
            return
        }

        try {
            authorService.save(author)
        } catch (ValidationException e) {
            respond author.errors
            return
        }

        respond author, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id) {
        if (id == null || authorService.delete(id) == null) {
            String message = messageSource.getMessage("default.not.found.message", ["Author", id] as Object[], RequestContextUtils.getLocale(request))
            respondError NOT_FOUND, message
            return
        }

        render status: NO_CONTENT
    }
}
