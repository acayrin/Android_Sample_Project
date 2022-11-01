package me.acayrin.sampleproject.activity.fragment.books

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.activity.adapter.books.BookAdapter
import me.acayrin.sampleproject.database.dao.*
import me.acayrin.sampleproject.database.model.Book

class BookPagerFragment : Fragment(R.layout.fragment_books_books_list) {
    private lateinit var bookDAO: DAOBook
    private lateinit var authorDAO: DAOAuthor
    private lateinit var genreDAO: DAOGenre
    private lateinit var rentalDAO: DAORental
    private lateinit var publisherDAO: DAOPublisher
    private lateinit var adapter: BookAdapter

    // for context, its the same as the author list fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookDAO = DAOBook(requireContext())
        authorDAO = DAOAuthor(requireContext())
        genreDAO = DAOGenre(requireContext())
        rentalDAO = DAORental(requireContext())
        publisherDAO = DAOPublisher(requireContext())

        requireActivity().findViewById<ListView>(R.id.lv_books_books_list)?.let {
            // reassigning DAOs to reduce memory usage?
            adapter = BookAdapter(
                requireContext(),
                bookDAO.all,
                bookDAO,
                genreDAO,
                publisherDAO,
                authorDAO
            )

            it.adapter = adapter
            it.setOnItemClickListener { _, _, i, _ ->
                val book = bookDAO[i]
                val alertDialogBundle = setupDialog("Edit book")

                alertDialogBundle.bookTitle.setText(book.name)
                alertDialogBundle.bookAuthor.setSelection(book.id_author)
                alertDialogBundle.bookPublisher.setSelection(book.id_publisher)
                alertDialogBundle.bookGenre.setSelection(book.id_genre)
                alertDialogBundle.bookPrice.setText(book.price.toString())

                alertDialogBundle.btnYes.setOnClickListener {
                    if (alertDialogBundle.bookTitle.text.toString().isEmpty() || alertDialogBundle.bookPrice.text.toString().isEmpty()) {
                        alertDialogBundle.bookTitle.error = "Title and price cannot be empty"
                    } else {
                        if (bookDAO.update(
                                Book(
                                    book.id,
                                    alertDialogBundle.bookTitle.text.toString(),
                                    alertDialogBundle.bookAuthor.selectedItemPosition,
                                    alertDialogBundle.bookPublisher.selectedItemPosition,
                                    alertDialogBundle.bookGenre.selectedItemPosition,
                                    alertDialogBundle.bookPrice.text.toString().toDouble()
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
                    if (rentalDAO.all.takeWhile { rt -> rt.id_book == i }.isNotEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Unable to delete, this item is linked with other items",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (bookDAO.delete(book.id)) {
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

        requireActivity().findViewById<EditText>(R.id.lv_books_book_search_box)?.let {
            it.doOnTextChanged { _, _, _, _ ->
                val searchText = it.text.toString()

                val listReplace = ArrayList<Book>(
                    bookDAO.all.takeWhile { bk ->
                        bk.name.contains(searchText)
                    }
                )
                adapter.update(listReplace)
            }
        }

        requireActivity().findViewById<Button>(R.id.lv_books_book_btn_add)?.let {
            it.setOnClickListener {
                val alertDialogBundle = setupDialog("New book")

                // stop if one of the spinner is empty
                if (
                    alertDialogBundle.bookAuthor.count == 0 ||
                    alertDialogBundle.bookPublisher.count == 0 ||
                    alertDialogBundle.bookGenre.count == 0
                ) {
                    Toast.makeText(
                        requireContext(),
                        "One or more data list is empty, please add one first",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                alertDialogBundle.btnYes.setOnClickListener {
                    if (alertDialogBundle.bookTitle.text.toString().isEmpty() || alertDialogBundle.bookPrice.text.toString().isEmpty()) {
                        alertDialogBundle.bookTitle.error = "Title and price cannot be empty!"
                    } else {
                        if (bookDAO.insert(
                                Book(
                                    bookDAO.all.size,
                                    alertDialogBundle.bookTitle.text.toString(),
                                    alertDialogBundle.bookAuthor.selectedItemPosition,
                                    alertDialogBundle.bookPublisher.selectedItemPosition,
                                    alertDialogBundle.bookGenre.selectedItemPosition,
                                    alertDialogBundle.bookPrice.text.toString().toDouble()
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
            requireActivity().layoutInflater.inflate(R.layout.dialog_item_book, null)
        val alertDialog = alertDialogBuilder.setView(alertDialogView).create()

        alertDialog.setOnDismissListener { adapter.update(bookDAO.all) }

        header?.let { alertDialogView.findViewById<TextView>(R.id.dialog_header).text = header }

        val bookTitle = alertDialogView.findViewById<EditText>(R.id.dialog_book_title)
        val bookPrice = alertDialogView.findViewById<EditText>(R.id.dialog_book_price)
        val bookAuthor = alertDialogView.findViewById<Spinner>(R.id.dialog_book_author)

        bookAuthor.let { b ->
            val authorList = ArrayList<String>()
            authorDAO.all.forEach { tg ->
                authorList.add(tg.name)
            }
            b.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                authorList
            )
        }

        val bookPublisher = alertDialogView.findViewById<Spinner>(R.id.dialog_book_publisher)
        bookPublisher.let { b ->
            val publisherList = ArrayList<String>()
            publisherDAO.all.forEach { pb ->
                publisherList.add(pb.name)
            }
            b.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                publisherList
            )
        }

        val bookGenre = alertDialogView.findViewById<Spinner>(R.id.dialog_book_genre)
        bookGenre.let { b ->
            val genreList = ArrayList<String>()
            genreDAO.all.forEach { gn ->
                genreList.add(gn.name)
            }
            b.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                genreList
            )
        }

        val btnYes = alertDialogView.findViewById<Button>(R.id.dialog_btn_yes)
        val btnNo = alertDialogView.findViewById<Button>(R.id.dialog_btn_no)
        val btnDel = alertDialogView.findViewById<Button>(R.id.dialog_btn_del)

        btnYes.setOnClickListener { /* TODO */ }
        btnNo.setOnClickListener { alertDialog.dismiss() }
        btnDel.setOnClickListener { /* TODO */ }

        return AlertDialogBundle(
            alertDialog,
            bookTitle,
            bookAuthor,
            bookPublisher,
            bookGenre,
            bookPrice,
            btnYes,
            btnNo,
            btnDel
        )
    }

    private class AlertDialogBundle(
        val alertDialog: AlertDialog,
        val bookTitle: EditText,
        val bookAuthor: Spinner,
        val bookPublisher: Spinner,
        val bookGenre: Spinner,
        val bookPrice: EditText,
        val btnYes: Button,
        val btnNo: Button,
        val btnDel: Button
    )
}
