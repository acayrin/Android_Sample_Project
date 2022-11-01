package me.acayrin.sampleproject.activity.fragment.books

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.activity.adapter.books.AuthorAdapter
import me.acayrin.sampleproject.database.dao.DAOAuthor
import me.acayrin.sampleproject.database.dao.DAOBook
import me.acayrin.sampleproject.database.model.Author

class AuthorPagerFragment : Fragment(R.layout.fragment_books_authors_list) {
    private lateinit var authorDAO: DAOAuthor
    private lateinit var bookDAO: DAOBook
    private lateinit var listViewAdapter: AuthorAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set author dao
        authorDAO = DAOAuthor(requireContext())

        // set book dao
        bookDAO = DAOBook(requireContext())

        // set listview adapter
        listViewAdapter = AuthorAdapter(requireContext(), authorDAO.all)

        // run listview related tasks
        requireActivity().findViewById<ListView>(R.id.lv_books_authors_list)?.let {
            // assign adapter
            it.adapter = listViewAdapter

            // on list item click
            it.setOnItemClickListener { _, _, i, _ ->
                // setup alert dialog bundle
                val alertDialogBundle = setupDialog("Edit author")
                alertDialogBundle.etName.setText(authorDAO[i].name)

                // accept button
                alertDialogBundle.btnYes.setOnClickListener {
                    if (alertDialogBundle.etName.text.toString().isEmpty()) {
                        alertDialogBundle.etName.error = "Name cannot be empty"
                    } else {
                        val author = Author(i, alertDialogBundle.etName.text.toString())

                        if (authorDAO.update(author)) {
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                            alertDialogBundle.alertDialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // delete button
                alertDialogBundle.btnDel.setOnClickListener {
                    if (bookDAO.all.takeWhile { book -> book.id_author == i }.isNotEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Unable to delete, this item is linked with other items",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (authorDAO.delete(i)) {
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                            alertDialogBundle.alertDialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // show
                alertDialogBundle.alertDialog.show()
            }
        }

        // search box related tasks
        requireActivity().findViewById<EditText>(R.id.lv_books_authors_search_box)?.let {
            // listen for text change event
            it.doOnTextChanged { _, _, _, _ ->
                val searchText = it.text.toString()

                val listReplace = ArrayList<Author>(
                    authorDAO.all.takeWhile { at ->
                        at.name.contains(searchText)
                    }
                )
                listViewAdapter.update(listReplace)
            }
        }

        // new entry related tasks
        requireActivity().findViewById<Button>(R.id.lv_books_authors_btn_add)?.let {
            // listen for button click event
            it.setOnClickListener {
                // setup alert dialog bundle
                val alertDialogBundle = setupDialog("New author")

                // accept button
                alertDialogBundle.btnYes.setOnClickListener {
                    if (alertDialogBundle.etName.text.toString().isEmpty()) {
                        alertDialogBundle.etName.error = "Name cannot be empty"
                    } else {
                        val author =
                            Author(authorDAO.all.size, alertDialogBundle.etName.text.toString())
                        if (authorDAO.insert(author)) {
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                            alertDialogBundle.alertDialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // hide deny button
                alertDialogBundle.btnDel.visibility = View.GONE

                // show
                alertDialogBundle.alertDialog.show()
            }
        }
    }

    // setup and return alert dialog and views
    private fun setupDialog(header: String?): AlertDialogBundle {
        // builder
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        // custom dialog view
        val alertDialogView =
            requireActivity().layoutInflater.inflate(R.layout.dialog_item_author, null)

        // create dialog object
        val alertDialog = alertDialogBuilder.setView(alertDialogView).create()

        // update listview on dismiss
        alertDialog.setOnDismissListener { listViewAdapter.update(authorDAO.all) }

        // set header text
        header?.let { alertDialogView.findViewById<TextView>(R.id.dialog_header).text = header }

        // get views
        val etName = alertDialogView.findViewById<EditText>(R.id.dialog_author_name)
        val btnYes = alertDialogView.findViewById<Button>(R.id.dialog_btn_yes)
        val btnNo = alertDialogView.findViewById<Button>(R.id.dialog_btn_no)
        val btnDel = alertDialogView.findViewById<Button>(R.id.dialog_btn_del)

        // base button listener
        btnYes.setOnClickListener { /* TODO */ }
        btnNo.setOnClickListener { alertDialog.dismiss() }
        btnDel.setOnClickListener { /* TODO */ }

        // return new alert dialog bundle
        return AlertDialogBundle(
            alertDialog,
            etName,
            btnYes,
            btnNo,
            btnDel
        )
    }

    // alert dialog bundle for this fragment
    private class AlertDialogBundle(
        val alertDialog: AlertDialog,
        val etName: EditText,
        val btnYes: Button,
        val btnNo: Button,
        val btnDel: Button
    )
}
