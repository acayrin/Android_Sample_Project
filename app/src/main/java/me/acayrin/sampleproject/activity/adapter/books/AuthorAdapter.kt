package me.acayrin.sampleproject.activity.adapter.books

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.database.dao.DAOBook
import me.acayrin.sampleproject.database.dao.DAOAuthor
import me.acayrin.sampleproject.database.model.Author

class AuthorAdapter(val context: Context, val list: ArrayList<Author>) : BaseAdapter() {
    private var bookDAO: DAOBook? = null
    private var authorDAO: DAOAuthor? = null

    fun update(_list: ArrayList<Author>) {
        list.clear()
        list.addAll(_list)
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
        if (authorDAO == null) authorDAO = DAOAuthor(context)

        var view = p1
        if (view == null) {
            view = (context as Activity).layoutInflater.inflate(
                R.layout.listview_item_author,
                p2,
                false
            )

            view.tag = ViewStore(
                view.findViewById(R.id.lv_authors_item_name),
                view.findViewById(R.id.lv_authors_item_count)
            )
        }

        val author = authorDAO!![p0]
        val viewStore = view?.tag as ViewStore
        viewStore.tvName.text = author.name
        viewStore.tvCount.text = bookDAO!!.all.takeWhile { it.id_author == author.id }.size.toString()

        return view
    }

    private class ViewStore(
        val tvName: TextView,
        val tvCount: TextView
    )
}
