package me.acayrin.sampleproject.activity.adapter.books

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.database.dao.DAOGenre
import me.acayrin.sampleproject.database.dao.DAOBook
import me.acayrin.sampleproject.database.model.Genre

class GenreAdapter(val context: Context, val list: ArrayList<Genre>) : BaseAdapter() {
    private var bookDAO: DAOBook? = null
    private var genreDAO: DAOGenre? = null

    fun update(_list: ArrayList<Genre>?) {
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

        var view = p1
        if (view == null) {
            view = (context as Activity).layoutInflater.inflate(
                R.layout.listview_item_genre,
                p2,
                false
            )

            view.tag = ViewStore(
                view.findViewById(R.id.lv_genres_item_name),
                view.findViewById(R.id.lv_genres_item_count)
            )
        }

        val genre = genreDAO!![p0]
        val viewStore = view?.tag as ViewStore
        viewStore.tvName.text = genre.name
        viewStore.tvCount.text =
            bookDAO!!.all.takeWhile { it.id_genre == genre.id }.size.toString()

        return view
    }

    private class ViewStore(
        val tvName: TextView,
        val tvCount: TextView
    )
}
