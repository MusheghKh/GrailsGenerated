package grailsgenerated

import grails.gorm.transactions.Transactional

@Transactional
class BookService {

    Book get(Serializable id) {
        Book.get(id)
    }

    List<Book> list(Map params) {
        Book.list(params)
    }

    Long count() {
        Book.count()
    }

    Book delete(Serializable id) {
        Book book = Book.get(id)
        if (book == null) {
            return null
        }
        book.delete(flush: true)
        book
    }

    Book save(Book book) {
        book.save()
    }
}
