package me.acayrin.sampleproject.activity.fragment.books

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.activity.adapter.books.PublisherAdapter
import me.acayrin.sampleproject.database.dao.DAOBook
import me.acayrin.sampleproject.database.dao.DAOPublisher
import me.acayrin.sampleproject.database.model.Publisher

class PublisherPagerFragment : Fragment(R.layout.fragment_books_publishers_list) {
    private lateinit var bookDAO: DAOBook
    private lateinit var publisherDAO: DAOPublisher
    private lateinit var listViewAdapter: PublisherAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookDAO = DAOBook(requireContext())
        publisherDAO = DAOPublisher(requireContext())

        requireActivity().findViewById<ListView>(R.id.lv_books_publisher_list)?.let {
            listViewAdapter = PublisherAdapter(requireContext(), publisherDAO.all)

            it.adapter = listViewAdapter
            it.setOnItemClickListener { _, _, i, _ ->
                val alertDialogBundle = setupDialog("Edit publisher")

                alertDialogBundle.etName.setText(publisherDAO[i].name)

                alertDialogBundle.btnYes.setOnClickListener {
                    if (alertDialogBundle.etName.text.toString().isEmpty()) {
                        alertDialogBundle.etName.error = "Name cannot be empty"
                    } else {
                        if (publisherDAO.update(
                                Publisher(
                                    i,
                                    alertDialogBundle.etName.text.toString()
                                )
                            )
                        ) {
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                            alertDialogBundle.alertDialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                alertDialogBundle.btnDel.setOnClickListener {
                    if (bookDAO.all.takeWhile { book -> book.id_publisher == i }.isNotEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Unable to delete, this item is linked with other items",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (publisherDAO.delete(i)) {
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

        requireActivity().findViewById<EditText>(R.id.lv_books_publisher_search_box)?.let {
            it.doOnTextChanged { _, _, _, _ ->
                val searchText = it.text.toString()

                val listReplace = ArrayList<Publisher>(
                    publisherDAO.all.takeWhile { pb ->
                        pb.name.contains(searchText)
                    }
                )
                listViewAdapter.update(listReplace)
            }
        }

        requireActivity().findViewById<Button>(R.id.lv_books_publisher_btn_add)?.let {
            it.setOnClickListener {
                val alertDialogBundle = setupDialog("New publisher")

                alertDialogBundle.btnYes.setOnClickListener {
                    if (alertDialogBundle.etName.text.toString().isEmpty()) {
                        alertDialogBundle.etName.error = "Name cannot be empty"
                    } else {
                        if (publisherDAO.insert(
                                Publisher(
                                    publisherDAO.all.size,
                                    alertDialogBundle.etName.text.toString()
                                )
                            )
                        ) {
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
            requireActivity().layoutInflater.inflate(R.layout.dialog_item_publisher, null)
        val alertDialog = alertDialogBuilder.setView(alertDialogView).create()

        alertDialog.setOnDismissListener { listViewAdapter.update(publisherDAO.all) }

        header?.let { alertDialogView.findViewById<TextView>(R.id.dialog_header).text = header }

        val etName = alertDialogView.findViewById<EditText>(R.id.dialog_publisher_name)
        val btnYes = alertDialogView.findViewById<Button>(R.id.dialog_btn_yes)
        val btnNo = alertDialogView.findViewById<Button>(R.id.dialog_btn_no)
        val btnDel = alertDialogView.findViewById<Button>(R.id.dialog_btn_del)

        btnYes.setOnClickListener { /* TODO */ }
        btnNo.setOnClickListener { alertDialog.dismiss() }
        btnDel.setOnClickListener { /* TODO */ }

        return AlertDialogBundle(
            alertDialog,
            etName,
            btnYes,
            btnNo,
            btnDel
        )
    }

    private class AlertDialogBundle(
        val alertDialog: AlertDialog,
        val etName: EditText,
        val btnYes: Button,
        val btnNo: Button,
        val btnDel: Button
    )
}
