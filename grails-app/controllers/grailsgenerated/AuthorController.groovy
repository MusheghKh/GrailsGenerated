package grailsgenerated

import grails.validation.ValidationException
import grailsgenerated.controller.ControllerExtensions

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class AuthorController implements ControllerExtensions{

    AuthorService authorService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond authorService.list(params), model:[authorCount: authorService.count()]
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
    def update(Author author) {
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

        respond author, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id) {
        if (id == null || authorService.delete(id) == null) {
            respondError NOT_FOUND, "author with id $id not found"
            return
        }

        render status: NO_CONTENT
    }
}
