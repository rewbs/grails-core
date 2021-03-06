package org.codehaus.groovy.grails.orm.hibernate

/**
 * @author Graeme Rocher
 * @since 1.0
 *
 * Created: Nov 22, 2007
 */
class DeleteFromCollectionTests extends AbstractGrailsHibernateTests {

    protected void onSetUp() {
        gcl.parseClass '''
import grails.persistence.*

@Entity
class DeleteBook {
    Long id
    Long version
    String title
    DeleteAuthor author
    static belongsTo = DeleteAuthor
}

@Entity
class DeleteAuthor {
    Long id
    Long version
    String name
    Set books
    static hasMany = [books:DeleteBook]
}
'''
    }

    void testDeleteFromCollection() {
        def bookClass = ga.getDomainClass("DeleteBook").clazz
        def authorClass = ga.getDomainClass("DeleteAuthor").clazz

        authorClass.newInstance(name:"Stephen King")
                   .addToBooks(title:"The Stand")
                   .addToBooks(title:"The Shining")
                   .save(flush:true)

        session.clear()

        def author = authorClass.get(1)
        assertNotNull author
        assertEquals 2, author.books.size()

        def book1 = author.books.find { it.title.endsWith("Stand") }
        author.removeFromBooks(book1)
        book1.delete(flush:true)

        session.clear()

        author = authorClass.get(1)
        assertNotNull author
        assertEquals 1, author.books.size()
    }
}
