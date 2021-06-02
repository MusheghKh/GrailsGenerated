package grailsgenerated

import grails.validation.ValidationException
import grailsgenerated.controller.ControllerExtensions
import org.springframework.web.servlet.support.RequestContextUtils

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class BookController implements ControllerExtensions{

    BookService bookService
    def messageSource

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond bookService.list(params), model:[bookCount: bookService.count()]
    }

    def show(Long id) {
        respond bookService.get(id)
    }

    @Transactional
    def save(Book book) {
        if (book == null) {
            respondError NOT_FOUND
            return
        }
        if (book.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond book.errors
            return
        }

        try {
            bookService.save(book)
        } catch (ValidationException e) {
            respond book.errors
            return
        }

        respond book, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(Long id) {
        Book book = bookService.get(id)
        if (book == null) {
            String message = messageSource.getMessage("default.not.found.message", ["Book", id] as Object[], RequestContextUtils.getLocale(request))
            respondError NOT_FOUND, message
            return
        }
        book.properties = request.JSON
        if (book.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond book.errors
            return
        }

        try {
            bookService.save(book)
        } catch (ValidationException e) {
            respond book.errors
            return
        }

        respond book, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id) {
        if (id == null || bookService.delete(id) == null) {
            String message = messageSource.getMessage("default.not.found.message", ["Book", id] as Object[], RequestContextUtils.getLocale(request))
            respondError NOT_FOUND, message
            return
        }

        render status: NO_CONTENT
    }
}
