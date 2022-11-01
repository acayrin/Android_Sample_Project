package me.acayrin.sampleproject.activity.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import me.acayrin.sampleproject.R
import me.acayrin.sampleproject.activity.fragment.books.AuthorPagerFragment
import me.acayrin.sampleproject.activity.fragment.books.BookPagerFragment
import me.acayrin.sampleproject.activity.fragment.books.GenrePagerFragment
import me.acayrin.sampleproject.activity.fragment.books.PublisherPagerFragment

class BooksFragment : Fragment(R.layout.fragment_books) {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = requireActivity().findViewById(R.id.ac_fragment_books_tablayout)
        viewPager = requireActivity().findViewById(R.id.ac_fragment_books_viewpager)

        viewPager.adapter = PagerAdapter(childFragmentManager)

        tabLayout.let {
            it.setTabTextColors(R.color.c2, R.color.c4)
            it.setSelectedTabIndicatorColor(resources.getColor(R.color.c4))
            it.setupWithViewPager(viewPager)
        }
    }
}

class PagerAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_SET_USER_VISIBLE_HINT) {
    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> BookPagerFragment()
            1 -> GenrePagerFragment()
            2 -> AuthorPagerFragment()
            3 -> PublisherPagerFragment()
            else -> BookPagerFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Books"
            1 -> "Genres"
            2 -> "Authors"
            3 -> "Publishers"
            else -> "Books"
        }
    }
}
