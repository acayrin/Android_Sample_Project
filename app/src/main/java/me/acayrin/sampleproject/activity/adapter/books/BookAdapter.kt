package me.acayrin.sampleproject.activity.adapter.books

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.database.dao.DAOGenre
import me.acayrin.sampleproject.database.dao.DAOPublisher
import me.acayrin.sampleproject.database.dao.DAOBook
import me.acayrin.sampleproject.database.dao.DAOAuthor
import me.acayrin.sampleproject.database.model.Book

class BookAdapter(
    val context: Context,
    val list: ArrayList<Book>,
    private var bookDAO: DAOBook?,
    private var genreDAO: DAOGenre?,
    private var publisherDAO: DAOPublisher?,
    private var authorDAO: DAOAuthor?
) : BaseAdapter() {

    fun update(_list: ArrayList<Book>?) {
        if (_list != null) {
            list.clear()
            list.addAll(_list)
        }
        notifyDataSetChanged()
    }
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        if (bookDAO == null) bookDAO = DAOBook(context)
        if (genreDAO == null) genreDAO = DAOGenre(context)
        if (publisherDAO == null) publisherDAO = DAOPublisher(context)
        if (authorDAO == null) authorDAO = DAOAuthor(context)

        var view = p1
        if (view == null) {
            view =
                (context as Activity).layoutInflater.inflate(R.layout.listview_item_book, p2, false)

            view.tag = ViewStore(
                view.findViewById(R.id.lv_books_item_title),
                view.findViewById(R.id.lv_books_item_author),
                view.findViewById(R.id.lv_books_item_publisher),
                view.findViewById(R.id.lv_books_item_genres),
                view.findViewById(R.id.lv_books_item_price)
            )
        }

        val book = bookDAO!![p0]
        val viewStore = view?.tag as ViewStore
        viewStore.tvTitle.text = book.name
        viewStore.tvAuthor.text = authorDAO!![book.id_author].name
        viewStore.tvPublisher.text = publisherDAO!![book.id_publisher].name
        viewStore.tvGenre.text = genreDAO!![book.id_genre].name
        viewStore.tvPrice.text = book.price.toString()

        return view
    }

    private class ViewStore(
        val tvTitle: TextView,
        val tvAuthor: TextView,
        val tvPublisher: TextView,
        val tvGenre: TextView,
        val tvPrice: TextView
    )
}
