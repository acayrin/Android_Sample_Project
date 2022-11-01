package me.acayrin.sampleproject.activity.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.database.dao.DAOBook
import me.acayrin.sampleproject.database.dao.DAOLibrarian
import me.acayrin.sampleproject.database.dao.DAORental
import me.acayrin.sampleproject.database.model.Librarian
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var rentalDAO: DAORental
    private lateinit var managerDAO: DAOLibrarian
    private lateinit var bookDAO: DAOBook

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rentalDAO = DAORental(requireContext())
        managerDAO = DAOLibrarian(requireContext())
        bookDAO = DAOBook(requireContext())

        val user = managerDAO.get(
            requireContext().getSharedPreferences("currentUser", Context.MODE_PRIVATE)
                .getInt("userId", -1)
        )
        requireActivity()
            .findViewById<TextView>(R.id.fragment_account_greeting).text = user?.username

        let {
            val tvIncome = requireActivity().findViewById<TextView>(R.id.fragment_account_income)
            val tvIncomeTotal =
                requireActivity().findViewById<TextView>(R.id.fragment_account_income_total)

            var accountIncome = 0.0
            var totalIncome = 0.0
            rentalDAO.all.forEach {
                val price = bookDAO[it.id_book].price

                if (it.id_librarian == user?.id) {
                    accountIncome += price
                }
                totalIncome += price
            }

            tvIncome.text = accountIncome.toString()
            tvIncomeTotal.text = totalIncome.toString()
        }

        let {
            val accountInfoView =
                requireActivity().findViewById<LinearLayout>(R.id.fragment_account_expand)
            val accountInfoViewBtn =
                requireActivity().findViewById<TextView>(R.id.fragment_account_expand_btn)
            val accountInfoViewArea =
                requireActivity().findViewById<LinearLayout>(R.id.fragment_account_expand_area)
            accountInfoViewArea
                .setOnClickListener {
                    accountInfoView.visibility =
                        if (accountInfoView.visibility == View.GONE) View.VISIBLE else View.GONE
                    accountInfoViewBtn.text =
                        if (accountInfoView.visibility == View.GONE) "+" else "-"
                }
        }

        // handle account income chart
        let {
            val valueMap = HashMap<String, Double>()
            for (i in 1..12) {
                if (!valueMap.contains("M$i"))
                    valueMap["M$i"] = 0.0

                rentalDAO.all
                    .takeWhile { rt ->
                        rt.id_librarian == user?.id && rt.date_start.split("/")[2] == (Date().year + 1900).toString()
                    }
                    .forEach { pm ->
                        if (pm.date_start.split("/")[1] == "$i") {
                            valueMap["M$i"]?.plus(bookDAO[pm.id_book].price)
                                ?.let { it1 -> valueMap.put("M$i", it1) }
                        }
                    }
            }

            val incomeChart =
                requireActivity().findViewById<AAChartView>(R.id.fragment_account_income_chart)
            val incomeChartModel: AAChartModel = AAChartModel()
                .chartType(AAChartType.Area)
                .backgroundColor(resources.getColor(android.R.color.transparent))
                .dataLabelsEnabled(false)
                .categories(valueMap.keys.toTypedArray())
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Income")
                            .data(valueMap.values.toTypedArray())
                    )
                )
            incomeChart.aa_drawChartWithChartModel(incomeChartModel)
        }

        // handle total income chart
        let {
            val valueMap = HashMap<String, Double>()
            for (i in 1..12) {
                if (!valueMap.contains("M$i"))
                    valueMap["M$i"] = 0.0

                rentalDAO.all.forEach { pm ->
                    if (pm.date_start.split("/")[1] == "$i") {
                        valueMap["M$i"]!!.plus(bookDAO[pm.id_book].price)
                            .let { newValue -> valueMap.put("M$i", newValue) }
                    }
                }
            }

            val incomeChart =
                requireActivity().findViewById<AAChartView>(R.id.fragment_account_income_total_chart)
            val incomeChartModel: AAChartModel = AAChartModel()
                .chartType(AAChartType.Area)
                .backgroundColor(resources.getColor(android.R.color.transparent))
                .dataLabelsEnabled(false)
                .categories(valueMap.keys.toTypedArray())
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Monthly Income")
                            .data(valueMap.values.toTypedArray())
                    )
                )
            incomeChart.aa_drawChartWithChartModel(incomeChartModel)
        }

        let {
            val etFullName =
                requireActivity().findViewById<EditText>(R.id.fragment_account_fullname)
            val etUsername =
                requireActivity().findViewById<EditText>(R.id.fragment_account_username)
            val etEmail = requireActivity().findViewById<EditText>(R.id.fragment_account_email)
            val etPassword =
                requireActivity().findViewById<EditText>(R.id.fragment_account_password)
            val btnSave = requireActivity().findViewById<Button>(R.id.fragment_account_btn_save)

            user.let { user ->
                user!!.full_name?.let { etFullName.setText(it) }
                user.email?.let { etEmail.setText(it) }
                etUsername.setText(user.username)
                etPassword.setText(user.password)

                val updatedManager = Librarian(
                    user.id,
                    etFullName.text.toString(),
                    etEmail.text.toString(),
                    etUsername.text.toString(),
                    etPassword.text.toString()
                )

                btnSave.setOnClickListener {
                    if (etUsername.text.toString().isEmpty() || etPassword.text.toString()
                            .isEmpty()
                    ) {
                        etUsername.error = "Username and password cannot be empty!"
                    } else {
                        if (managerDAO.update(updatedManager)) {
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
