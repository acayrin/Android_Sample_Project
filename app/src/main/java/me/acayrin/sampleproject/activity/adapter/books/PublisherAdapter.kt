package me.acayrin.sampleproject.activity.adapter.books

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.database.dao.DAOPublisher
import me.acayrin.sampleproject.database.dao.DAOBook
import me.acayrin.sampleproject.database.model.Publisher

class PublisherAdapter(val context: Context, val list: ArrayList<Publisher>) : BaseAdapter() {
    private var bookDAO: DAOBook? = null
    private var publisherDAO: DAOPublisher? = null

    fun update(_list: ArrayList<Publisher>?) {
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
        if (publisherDAO == null) publisherDAO = DAOPublisher(context)

        var view = p1
        if (view == null) {
            view = (context as Activity).layoutInflater.inflate(
                R.layout.listview_item_publisher,
                p2,
                false
            )

            view.tag = ViewStore(
                view.findViewById(R.id.lv_publishers_item_name),
                view.findViewById(R.id.lv_publishers_item_count)
            )
        }

        val publisher = publisherDAO!![p0]
        val viewStore = view?.tag as ViewStore
        viewStore.tvName.text = publisher.name
        viewStore.tvCount.text =
            bookDAO!!.all.takeWhile { it.id_publisher == publisher.id }.size.toString()

        return view
    }

    private class ViewStore(
        val tvName: TextView,
        val tvCount: TextView
    )
}
