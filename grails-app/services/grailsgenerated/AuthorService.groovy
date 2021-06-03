package grailsgenerated

import grails.gorm.transactions.Transactional
import grailsgenerated.exceptions.BookNotFoundException

@Transactional
class AuthorService {

    Author get(Serializable id) {
        Author.get(id)
    }

    List<Author> list(Map params) {
        Author.list(params)
    }

    List<Author> listByBookId(Serializable bookId, Map params) throws BookNotFoundException{
        Book book = Book.get(bookId)
        if (book == null) {
            throw new BookNotFoundException()
        }
        Author.findAllByBook(book, params)
    }

    Long count() {
        Author.count()
    }

    Author delete(Serializable id) {
        Author author = get(id)
        if (author == null) {
            return null
        }
        author.book.authors.remove(author)
        author.delete(flush: true)
        author
    }

    Author save(Author author) {
        author.save()
    }
}
