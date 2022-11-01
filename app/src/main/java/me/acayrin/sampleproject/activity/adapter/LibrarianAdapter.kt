package me.acayrin.sampleproject.activity.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.database.dao.DAOBook
import me.acayrin.sampleproject.database.dao.DAOLibrarian
import me.acayrin.sampleproject.database.dao.DAORental
import me.acayrin.sampleproject.database.model.Librarian

class LibrarianAdapter(
    private val context: Context,
    private val list: ArrayList<Librarian>,
    private var daoRental: DAORental?,
    private var daoBook: DAOBook?,
    private var daoLibrarian: DAOLibrarian?
) : BaseAdapter() {

    fun update(_list: ArrayList<Librarian>?) {
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
        if (daoRental == null) daoRental = DAORental(context)
        if (daoBook == null) daoBook = DAOBook(context)
        if (daoLibrarian == null) daoLibrarian = DAOLibrarian(context)

        var view = p1
        if (view == null) {
            view = (context as Activity).layoutInflater.inflate(
                R.layout.listview_item_librarian,
                p2,
                false
            )

            view.tag = ViewStore(
                view.findViewById(R.id.lv_librarians_item_name),
                view.findViewById(R.id.lv_librarians_item_username),
                view.findViewById(R.id.lv_librarians_item_email),
                view.findViewById(R.id.lv_librarians_item_income)
            )
        }

        val viewStore = view?.tag as ViewStore
        val user = daoLibrarian!!.get(p0)
        var income = 0.0
        daoRental!!.all.takeWhile { it.id_librarian == user?.id }.forEach {
            income += daoBook!![it.id_book].price
        }

        viewStore.tvName.text = user?.full_name
        viewStore.tvUsername.text = user?.username
        viewStore.tvEmail.text = user?.email
        viewStore.tvIncome.text = income.toString()

        return view
    }

    private class ViewStore(
        val tvName: TextView,
        val tvUsername: TextView,
        val tvEmail: TextView,
        val tvIncome: TextView
    )
}
