package me.acayrin.sampleproject.activity.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.activity.adapter.MemberAdapter
import me.acayrin.sampleproject.database.dao.DAOMember
import me.acayrin.sampleproject.database.dao.DAORental
import me.acayrin.sampleproject.database.model.Member

class MemberFragment : Fragment(R.layout.fragment_members) {
    private lateinit var rentalDAO: DAORental
    private lateinit var memberDAO: DAOMember
    private lateinit var memberListView: ListView
    private lateinit var memberListViewAdapter: MemberAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rentalDAO = DAORental(requireContext())
        memberDAO = DAOMember(requireContext())
        memberListView = requireActivity().findViewById(R.id.lv_members)
        memberListViewAdapter =
            MemberAdapter(requireContext(), memberDAO.all, memberDAO)

        memberListView.let {
            it.adapter = memberListViewAdapter
            it.setOnItemClickListener { _, _, i, _ ->
                val member = memberDAO[i]
                val alertDialogBundle = setupDialog("Edit member")

                alertDialogBundle.etName.setText(member.full_name)
                alertDialogBundle.etBirthdate.setText(member.birthdate)
                alertDialogBundle.etAddress.setText(member.address)

                alertDialogBundle.btnYes.setOnClickListener {
                    if (alertDialogBundle.etName.text.toString()
                            .isEmpty() || alertDialogBundle.etBirthdate.text.toString()
                            .isEmpty() || alertDialogBundle.etAddress.text.toString().isEmpty()
                    ) {
                        alertDialogBundle.etName.error =
                            "Name, birthdate and address cannot be empty"
                    } else {
                        val updatedMember = Member(
                            i,
                            alertDialogBundle.etName.text.toString(),
                            alertDialogBundle.etBirthdate.text.toString(),
                            alertDialogBundle.etAddress.text.toString(),
                        )
                        if (memberDAO.update(updatedMember)) {
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                            alertDialogBundle.alertDialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                alertDialogBundle.btnDel.setOnClickListener {
                    if (rentalDAO.all.takeWhile { rt -> rt.id_member == i }.isNotEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Unable to delete, this item is linked with other items",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (memberDAO.delete(i)) {
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                            alertDialogBundle.alertDialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                alertDialogBundle.alertDialog.show()
            }
        }

        requireActivity().findViewById<EditText>(R.id.lv_members_search_box)?.let {
            it.doOnTextChanged { _, _, _, _ ->
                val searchText = it.text.toString()

                val listReplace = ArrayList<Member>(
                    memberDAO.all.takeWhile { mb ->
                        mb.full_name.contains(searchText)
                    }
                )
                memberListViewAdapter.update(listReplace)
            }
        }

        requireActivity().findViewById<Button>(R.id.lv_members_btn_add)?.let {
            it.setOnClickListener {
                val alertDialogBundle = setupDialog("New member")

                alertDialogBundle.btnYes.setOnClickListener {
                    if (alertDialogBundle.etName.text.toString()
                            .isEmpty() || alertDialogBundle.etBirthdate.text.toString()
                            .isEmpty() || alertDialogBundle.etAddress.text.toString().isEmpty()
                    ) {
                        alertDialogBundle.etName.error =
                            "Name, birthdate and address cannot be empty"
                    } else {
                        val newMember = Member(
                            memberDAO.all.size,
                            alertDialogBundle.etName.text.toString(),
                            alertDialogBundle.etBirthdate.text.toString(),
                            alertDialogBundle.etAddress.text.toString()
                        )
                        if (memberDAO.insert(newMember)) {
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                            alertDialogBundle.alertDialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                alertDialogBundle.btnDel.visibility = View.GONE

                alertDialogBundle.alertDialog.show()
            }
        }
    }

    private fun setupDialog(header: String?): AlertDialogBundle {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val alertDialogView =
            requireActivity().layoutInflater.inflate(R.layout.dialog_item_member, null)
        val alertDialog = alertDialogBuilder.setView(alertDialogView).create()

        alertDialog.setOnDismissListener { memberListViewAdapter.update(memberDAO.all) }

        header?.let { alertDialogView.findViewById<TextView>(R.id.dialog_header).text = it }

        val etName = alertDialogView.findViewById<EditText>(R.id.dialog_member_name)
        val etBirthdate = alertDialogView.findViewById<EditText>(R.id.dialog_member_birthdate)
        val etAddress = alertDialogView.findViewById<EditText>(R.id.dialog_member_address)

        etBirthdate.let { et ->
            et.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    val datePicker = DatePickerDialog(requireContext())
                    datePicker.show()
                    datePicker.setOnDateSetListener { _, i, i2, i3 ->
                        et.setText("$i3/$i2/$i")
                    }
                }
            }
        }

        val btnYes = alertDialogView.findViewById<Button>(R.id.dialog_btn_yes)
        val btnNo = alertDialogView.findViewById<Button>(R.id.dialog_btn_no)
        val btnDel = alertDialogView.findViewById<Button>(R.id.dialog_btn_del)

        btnYes.setOnClickListener { /* TODO */ }
        btnNo.setOnClickListener { alertDialog.dismiss() }
        btnDel.setOnClickListener { /* TODO */ }

        return AlertDialogBundle(
            alertDialog,
            etName,
            etBirthdate,
            etAddress,
            btnYes,
            btnNo,
            btnDel
        )
    }

    private class AlertDialogBundle(
        val alertDialog: AlertDialog,
        val etName: EditText,
        val etBirthdate: EditText,
        val etAddress: EditText,
        val btnYes: Button,
        val btnNo: Button,
        val btnDel: Button
    )
}
