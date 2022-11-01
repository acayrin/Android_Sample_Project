package me.acayrin.sampleproject.activity

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.activity.fragment.*

class HomeActivity : AppCompatActivity() {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var tabsCount = 4
        val currentUser =
            getSharedPreferences("currentUser", Context.MODE_PRIVATE)
                .getString("username", "???")
        val adminUser =
            getSharedPreferences("__secret", Context.MODE_PRIVATE)
                .getString("__username", "__unknown__")
        if (currentUser.equals(adminUser))
            tabsCount = 5

        if (tabLayout == null) tabLayout = findViewById(R.id.ac_fragment_container_navigation)
        if (viewPager == null) viewPager = findViewById(R.id.ac_fragment_container_main)

        viewPager!!.adapter = PagerAdapter(supportFragmentManager, tabsCount)

        tabLayout!!.let {
            it.setSelectedTabIndicatorColor(resources.getColor(R.color.c4))
            it.setupWithViewPager(viewPager)

            for (i in 0 until tabsCount) {
                val ico = ResourcesCompat.getDrawable(
                    resources,
                    when (i) {
                        0 -> R.drawable.ic_baseline_home_36
                        1 -> R.drawable.ic_baseline_backup_table_36
                        2 -> R.drawable.ic_baseline_menu_book_36
                        3 -> R.drawable.ic_baseline_supervised_user_circle_36
                        4 -> R.drawable.ic_baseline_manage_accounts_36
                        else -> R.drawable.ic_baseline_home_36
                    },
                    null
                )
                ico?.let { draw ->
                    draw.setTint(resources.getColor(R.color.c4))

                    val iv = ImageView(this)
                    iv.setImageDrawable(ico)

                    it.getTabAt(i)?.customView = iv
                }
            }
        }
    }
}

class PagerAdapter(fm: FragmentManager, private val tabsCount: Int) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_SET_USER_VISIBLE_HINT) {
    override fun getCount(): Int {
        return tabsCount
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> RentalFragment()
            2 -> BooksFragment()
            3 -> MemberFragment()
            4 -> LibrarianFragment()
            else -> HomeFragment()
        }
    }
}
