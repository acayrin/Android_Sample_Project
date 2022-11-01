package me.acayrin.sampleproject.activity.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.database.dao.DAOMember
import me.acayrin.sampleproject.database.model.Member

class MemberAdapter(
	private val context: Context,
	private val list: ArrayList<Member>,
	private var daoMember: DAOMember?
) : BaseAdapter() {

	fun update(_list: ArrayList<Member>?) {
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
		if (daoMember == null) daoMember = DAOMember(context)

		var view = p1
		if (view == null) {
			view = (context as Activity).layoutInflater.inflate(
				R.layout.listview_item_member,
				p2,
				false
			)

			view.tag = ViewStore(
				view.findViewById(R.id.lv_members_item_name),
				view.findViewById(R.id.lv_members_item_birthdate),
				view.findViewById(R.id.lv_members_item_address)
			)
		}

		val member = daoMember!![p0]
		val viewStore = view?.tag as ViewStore
		viewStore.tvName.text = member.full_name
		viewStore.tvBirthdate.text = member.birthdate
		viewStore.tvAddress.text = member.address

		return view
	}

	private class ViewStore(
		val tvName: TextView,
		val tvBirthdate: TextView,
		val tvAddress: TextView,
	)
}
