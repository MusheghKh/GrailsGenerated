package grailsgenerated

import grails.gorm.services.Service

@Service(Author)
interface AuthorService {

    Author get(Serializable id)

    List<Author> list(Map args)

    Long count()

    Author delete(Serializable id)

    Author save(Author author)

}
