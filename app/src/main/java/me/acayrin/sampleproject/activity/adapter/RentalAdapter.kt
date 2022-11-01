package me.acayrin.sampleproject.activity.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.database.dao.DAOBook
import me.acayrin.sampleproject.database.dao.DAOLibrarian
import me.acayrin.sampleproject.database.dao.DAOMember
import me.acayrin.sampleproject.database.model.Rental

class RentalAdapter(
    private val context: Context,
    private val list: ArrayList<Rental>,
    private var daoMember: DAOMember?,
    private var daoBook: DAOBook?,
    private var daoLibrarian: DAOLibrarian?
) : BaseAdapter() {

    fun update(_list: ArrayList<Rental>?) {
        _list?.let {
            list.clear()
            list.addAll(it)
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
        if (daoMember == null) daoMember = DAOMember(context)
        if (daoBook == null) daoBook = DAOBook(context)
        if (daoLibrarian == null) daoLibrarian = DAOLibrarian(context)

        var view = p1
        if (view == null) {
            view = (context as Activity).layoutInflater.inflate(
                R.layout.listview_item_rental,
                p2,
                false
            )

            view.tag = ViewStore(
                view.findViewById(R.id.lv_rentals_item_member_name),
                view.findViewById(R.id.lv_rentals_item_book_title),
                view.findViewById(R.id.lv_rentals_item_manager_username),
                view.findViewById(R.id.lv_rentals_item_price),
                view.findViewById(R.id.lv_rentals_item_date_start),
                view.findViewById(R.id.lv_rentals_item_date_end),
                view.findViewById(R.id.lv_rentals_item_status)
            )
        }

        val viewStore = view?.tag as ViewStore
        viewStore.tvRentalName.text =
            daoMember?.get(list[p0].id_member)?.full_name
        viewStore.tvRentalTitle.text =
            daoBook?.get(list[p0].id_book)?.name
        viewStore.tvRentalManager.text =
            daoLibrarian?.get(list[p0].id_librarian)?.username
        viewStore.tvRentalPrice.text =
            daoBook?.get(list[p0].id_book)?.price.toString()
        viewStore.tvRentalDateStart.text =
            list[p0].date_start
        viewStore.tvRentalDateEnd.text =
            list[p0].date_end
        viewStore.cbRentalStatus.isChecked =
            list[p0].status == 1

        return view
    }

    private class ViewStore(
        var tvRentalName: TextView,
        var tvRentalTitle: TextView,
        var tvRentalManager: TextView,
        var tvRentalPrice: TextView,
        var tvRentalDateStart: TextView,
        var tvRentalDateEnd: TextView,
        var cbRentalStatus: CheckBox,
    )
}
