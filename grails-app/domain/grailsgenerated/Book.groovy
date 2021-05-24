package grailsgenerated

class Book {

    Long id
    String name

    static hasMany = [authors: Author]

    static constraints = {
        name nullable: false, blank: false, maxSize: 100
    }
}
