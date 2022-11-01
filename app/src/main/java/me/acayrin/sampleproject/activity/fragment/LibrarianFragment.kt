package me.acayrin.sampleproject.activity.fragment

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.activity.adapter.LibrarianAdapter
import me.acayrin.sampleproject.database.dao.DAOLibrarian
import me.acayrin.sampleproject.database.dao.DAORental
import me.acayrin.sampleproject.database.model.Librarian

class LibrarianFragment : Fragment(R.layout.fragment_librarians) {
    private lateinit var rentalDAO: DAORental
    private lateinit var managerDAO: DAOLibrarian
    private lateinit var managerListView: ListView
    private lateinit var managerListViewAdapter: LibrarianAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rentalDAO = DAORental(requireContext())
        managerDAO = DAOLibrarian(requireContext())
        managerListView = requireActivity().findViewById(R.id.lv_librarians)
        managerListViewAdapter =
            LibrarianAdapter(requireContext(), managerDAO.all, null, null, null)

        managerListView.let {
            it.adapter = managerListViewAdapter
            it.setOnItemClickListener { _, _, i, _ ->
                val user = managerDAO[i]
                val alertDialogBundle = setupDialog("Edit librarian")

                alertDialogBundle.etName.setText(user.full_name)
                alertDialogBundle.etEmail.setText(user.email)
                alertDialogBundle.etUsername.setText(user.username)
                // alertDialogBundle.etPassword.setText(user.password)

                alertDialogBundle.btnYes.setOnClickListener {
                    if (alertDialogBundle.etUsername.text.toString()
                            .isEmpty() || alertDialogBundle.etPassword.text.toString().isEmpty()
                    ) {
                        alertDialogBundle.etUsername.error = "Username and password cannot be empty"
                    } else {
                        val updatedLibrarian = Librarian(
                            i,
                            alertDialogBundle.etName.text.toString(),
                            alertDialogBundle.etEmail.text.toString(),
                            user.username,
                            alertDialogBundle.etPassword.text.toString().ifEmpty {
                                user.password
                            }
                        )
                        if (managerDAO.update(updatedLibrarian)) {
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                            alertDialogBundle.alertDialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                alertDialogBundle.btnDel.setOnClickListener {
                    if (rentalDAO.all.takeWhile { rt -> rt.id_librarian == i }.isNotEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Unable to delete, this item is linked with other items",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (managerDAO.delete(i)) {
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

        requireActivity().findViewById<EditText>(R.id.lv_librarians_search_box)?.let {
            it.doOnTextChanged { _, _, _, _ ->
                managerListViewAdapter.update(ArrayList(
                    managerDAO.all.takeWhile { item ->
                        item.username.contains(it.text.toString().trim(), true)
                    }
                ))
            }
        }

        requireActivity().findViewById<Button>(R.id.lv_librarians_btn_add)?.let {
            it.setOnClickListener {
                val alertDialogBundle = setupDialog("New librarian")

                alertDialogBundle.btnYes.setOnClickListener {
                    if (alertDialogBundle.etUsername.text.toString()
                            .isEmpty() || alertDialogBundle.etPassword.text.toString().isEmpty()
                    ) {
                        alertDialogBundle.etUsername.error = "Username and password cannot be empty"
                    } else {
                        val newLibrarian = Librarian(
                            managerDAO.all.size,
                            alertDialogBundle.etName.text.toString(),
                            alertDialogBundle.etEmail.text.toString(),
                            alertDialogBundle.etUsername.text.toString(),
                            alertDialogBundle.etPassword.text.toString()
                        )
                        if (managerDAO.insert(newLibrarian)) {
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
            requireActivity().layoutInflater.inflate(R.layout.dialog_item_librarian, null)
        val alertDialog = alertDialogBuilder.setView(alertDialogView).create()

        alertDialog.setOnDismissListener { managerListViewAdapter.update(managerDAO.all) }

        header?.let { alertDialogView.findViewById<TextView>(R.id.dialog_header).text = it }

        val etName = alertDialogView.findViewById<EditText>(R.id.dialog_librarian_name)
        val etEmail = alertDialogView.findViewById<EditText>(R.id.dialog_librarian_email)
        val etUsername = alertDialogView.findViewById<EditText>(R.id.dialog_librarian_username)
        val etPassword = alertDialogView.findViewById<EditText>(R.id.dialog_librarian_password)

        val btnYes = alertDialogView.findViewById<Button>(R.id.dialog_btn_yes)
        val btnNo = alertDialogView.findViewById<Button>(R.id.dialog_btn_no)
        val btnDel = alertDialogView.findViewById<Button>(R.id.dialog_btn_del)

        btnYes.setOnClickListener { /* TODO */ }
        btnNo.setOnClickListener { alertDialog.dismiss() }
        btnDel.setOnClickListener { /* TODO */ }

        return AlertDialogBundle(
            alertDialog,
            etName,
            etEmail,
            etUsername,
            etPassword,
            btnYes,
            btnNo,
            btnDel
        )
    }

    private class AlertDialogBundle(
        val alertDialog: AlertDialog,
        val etName: EditText,
        val etEmail: EditText,
        val etUsername: EditText,
        val etPassword: EditText,
        val btnYes: Button,
        val btnNo: Button,
        val btnDel: Button
    )
}
