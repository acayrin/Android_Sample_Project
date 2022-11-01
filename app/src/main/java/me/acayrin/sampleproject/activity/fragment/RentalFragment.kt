package me.acayrin.sampleproject.activity.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.activity.adapter.RentalAdapter
import me.acayrin.sampleproject.database.dao.DAOBook
import me.acayrin.sampleproject.database.dao.DAOMember
import me.acayrin.sampleproject.database.dao.DAORental
import me.acayrin.sampleproject.database.model.Rental
import java.util.*

class RentalFragment : Fragment(R.layout.fragment_rentals) {
    private lateinit var listView: ListView
    private lateinit var listViewAdapter: RentalAdapter
    private lateinit var rentalDAO: DAORental
    private lateinit var memberDAO: DAOMember
    private lateinit var bookDAO: DAOBook

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rentalDAO = DAORental(requireContext())
        memberDAO = DAOMember(requireContext())
        bookDAO = DAOBook(requireContext())
        listView = requireActivity().findViewById(R.id.lv_rentals)
        listViewAdapter =
            RentalAdapter(requireContext(), rentalDAO.all, memberDAO, bookDAO, null)

        listView.let {
            it.adapter = listViewAdapter
            it.setOnItemClickListener { _, _, i, _ ->
                val alertDialogBundle = setupDialog("Edit rental details")

                alertDialogBundle.rentalMember.setSelection(rentalDAO[i].id_member)
                alertDialogBundle.rentalBook.setSelection(rentalDAO[i].id_book)
                alertDialogBundle.rentalDateStart.setText(rentalDAO[i].date_start)
                alertDialogBundle.rentalDateEnd.setText(rentalDAO[i].date_end)
                alertDialogBundle.rentalStatus.isChecked = rentalDAO[i].status == 1

                alertDialogBundle.btnYes.let { yes ->
                    yes.setOnClickListener {
                        val pm =
                            Rental(
                                i,
                                rentalDAO[i].id_librarian,
                                alertDialogBundle.rentalMember.selectedItemPosition,
                                alertDialogBundle.rentalBook.selectedItemPosition,
                                alertDialogBundle.rentalDateStart.text.toString().ifEmpty {
                                    "${Calendar.getInstance().get(Calendar.DATE)}/${
                                        Calendar.getInstance().get(Calendar.MONTH) + 1
                                    }/${Calendar.getInstance().get(Calendar.YEAR)}"
                                },
                                if (alertDialogBundle.rentalStatus.isChecked) 1 else 0,
                                alertDialogBundle.rentalDateEnd.text.toString()
                            )

                        if (rentalDAO.update(pm)) {
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                            alertDialogBundle.alertDialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                alertDialogBundle.btnDel.setOnClickListener {
                    if (rentalDAO.delete(i)) {
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        alertDialogBundle.alertDialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                alertDialogBundle.alertDialog.show()
            }
        }

        requireActivity().findViewById<EditText>(R.id.lv_rentals_search_box)?.let {
            it.doOnTextChanged { _, _, _, _ ->
                val searchText = it.text.toString()

                var listReplace = ArrayList(
                    rentalDAO.all.takeWhile { pm ->
                        bookDAO[pm.id_book].name.contains(searchText)
                    }
                )
                if (listReplace.isEmpty()) {
                    listReplace = ArrayList(
                        rentalDAO.all.takeWhile { pm ->
                            memberDAO[pm.id_member].full_name.contains(searchText)
                        }
                    )
                }
                listViewAdapter.update(listReplace)
            }
        }

        requireActivity().findViewById<Button>(R.id.lv_rentals_btn_add)?.let {
            it.setOnClickListener {
                val alertDialogBundle = setupDialog("New rental details")

                if (alertDialogBundle.rentalMember.count == 0 ||
                    alertDialogBundle.rentalBook.count == 0
                ) {
                    Toast.makeText(
                        requireContext(),
                        "One or more data list is empty, please add one first",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                alertDialogBundle.btnDel.visibility = View.GONE

                alertDialogBundle.btnYes.setOnClickListener {
                    val newRental = Rental(
                        rentalDAO.all.size,
                        requireContext().getSharedPreferences(
                            "currentUser",
                            Context.MODE_PRIVATE
                        )
                            .getInt("userId", -1),
                        alertDialogBundle.rentalMember.selectedItemPosition,
                        alertDialogBundle.rentalBook.selectedItemPosition,
                        alertDialogBundle.rentalDateStart.text.toString().ifEmpty {
                            "${Calendar.getInstance().get(Calendar.DATE)}/${
                                Calendar.getInstance().get(Calendar.MONTH) + 1
                            }/${Calendar.getInstance().get(Calendar.YEAR)}"
                        },
                        if (alertDialogBundle.rentalStatus.isChecked) 1 else 0,
                        alertDialogBundle.rentalDateEnd.text.toString()
                    )

                    if (rentalDAO.insert(newRental)) {
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        alertDialogBundle.alertDialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                alertDialogBundle.alertDialog.show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupDialog(header: String?): AlertDialogBundle {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val alertDialogView =
            requireActivity().layoutInflater.inflate(R.layout.dialog_item_rental, null)
        val alertDialog = alertDialogBuilder.setView(alertDialogView).create()

        alertDialog.setOnDismissListener { listViewAdapter.update(rentalDAO.all) }

        header?.let { alertDialogView.findViewById<TextView>(R.id.dialog_header).text = it }

        val rentalMember = alertDialogView.findViewById<Spinner>(R.id.dialog_rental_member)
        val rentalBook = alertDialogView.findViewById<Spinner>(R.id.dialog_rental_book_title)
        val rentalDateStart =
            alertDialogView.findViewById<EditText>(R.id.dialog_rental_book_date_start)
        val rentalDateEnd =
            alertDialogView.findViewById<EditText>(R.id.dialog_rental_book_date_end)
        val rentalStatus =
            alertDialogView.findViewById<CheckBox>(R.id.dialog_rental_book_status)

        val btnYes = alertDialogView.findViewById<Button>(R.id.dialog_btn_yes)
        val btnNo = alertDialogView.findViewById<Button>(R.id.dialog_btn_no)
        val btnDel = alertDialogView.findViewById<Button>(R.id.dialog_btn_del)

        rentalMember?.let { spinner ->
            val spinnerList = ArrayList<String>()
            memberDAO.all.forEach { member -> spinnerList.add(member.full_name) }
            spinner.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerList
            )
        }

        rentalBook?.let { spinner ->
            val spinnerList = ArrayList<String>()
            bookDAO.all.forEach { book -> spinnerList.add(book.name) }
            spinner.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerList
            )
        }

        rentalDateStart?.let { et ->
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

        rentalDateEnd?.let { et ->
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

        btnYes.setOnClickListener { /* TODO */ }
        btnNo.setOnClickListener { alertDialog.dismiss() }
        btnDel.setOnClickListener { /* TODO */ }

        return AlertDialogBundle(
            alertDialog,
            rentalMember,
            rentalBook,
            rentalDateStart,
            rentalDateEnd,
            rentalStatus,
            btnYes,
            btnNo,
            btnDel
        )
    }

    class AlertDialogBundle(
        val alertDialog: AlertDialog,
        val rentalMember: Spinner,
        val rentalBook: Spinner,
        val rentalDateStart: EditText,
        val rentalDateEnd: EditText,
        val rentalStatus: CheckBox,
        val btnYes: Button,
        val btnNo: Button,
        val btnDel: Button
    )
}
