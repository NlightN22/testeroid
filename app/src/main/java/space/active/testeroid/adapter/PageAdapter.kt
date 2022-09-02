package space.active.testeroid.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import space.active.testeroid.*
import space.active.testeroid.screens.edittestlist.EditTestListFragment
import space.active.testeroid.screens.score.ScoreFragment
import space.active.testeroid.screens.test.TestFragment
import space.active.testeroid.screens.user.UserFragment
import space.active.testeroid.screens.useredit.UserEditFragment

class PageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return NUMBER_OF_PAGES
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> TestFragment()
            1 -> EditTestListFragment()
            2 -> UserFragment()
            3 -> ScoreFragment()
            else -> TestFragment()
        }
    }
}