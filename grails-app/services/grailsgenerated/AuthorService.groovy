package grailsgenerated

import grails.gorm.transactions.Transactional

@Transactional
class AuthorService {

    Author get(Serializable id) {
        Author.get(id)
    }

    List<Author> list(Map params) {
        Author.list(params)
    }

    List<Author> listByBookId(Serializable bookId, Map params) {
        Author.findAllByBook(Book.get(bookId), params)
    }

    Long count() {
        Author.count()
    }

    Author delete(Serializable id) {
        Author author = get(id)
        author.book.authors.remove(author)
        author.delete(flush: true)
        author
    }

    Author save(Author author) {
        author.save()
    }
}
