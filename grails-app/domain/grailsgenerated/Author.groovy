package grailsgenerated

class Author {

    String name

    static belongsTo = [book: Book]

    static constraints = {
        name nullable: false, blank: false, maxSize: 100
    }

}
