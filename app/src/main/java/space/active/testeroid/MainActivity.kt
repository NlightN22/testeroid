package space.active.testeroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import space.active.testeroid.adapter.PageAdapter
import space.active.testeroid.databinding.ActivityMainBinding
import space.active.testeroid.screens.MainActivityViewModelFactory
import space.active.testeroid.screens.MainActivityViewModel
import space.active.testeroid.screens.useredit.UserEditViewModel
import space.active.testeroid.screens.useredit.UserEditViewModelFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainActivityViewModel
    lateinit var viewModelUserEdit: UserEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        APP = this
        viewModel = ViewModelProvider(this, MainActivityViewModelFactory(this)).get(
            MainActivityViewModel::class.java)

        viewModelUserEdit = ViewModelProvider(this, UserEditViewModelFactory(this)).get(
            UserEditViewModel::class.java)

        init()
        testZone() // Need to delete
    }

    // TODO delete
    private fun testZone() {
        class Values(val name: String, val num: Int)

        fun main() {
            val list = listOf(
                Values("qwe", 1),
                Values("rew", 2),
                Values("asd", 3),
            )
            println("list $list")

            list.forEach { if (it.name == "qwe") {
                val position: Int = it.num
            }
            }
        }
    }

    private fun init(){


        //inflate from adapter
        binding.pager.adapter = PageAdapter(this.supportFragmentManager, lifecycle)

        // set icons for tabs
        TabLayoutMediator(binding.tabLayout, binding.pager){ //Mediator open fragment from adapter
            tab, pos ->
            when(pos){
                0 -> tab.setIcon(R.drawable.ic_baseline_test_24)
                1 -> tab.setIcon(R.drawable.ic_baseline_edit_24)
                2 -> tab.setIcon(R.drawable.ic_baseline_child_care_24)
                3 -> tab.setIcon(R.drawable.ic_baseline_star_24)
            }
        }.attach()

        // set icons for tabs end

        // Bottom visible state
        viewModel.bottomTabsVisibility.observe(this){
            binding.tabLayout.visibility = if (it) {View.VISIBLE} else {View.INVISIBLE}
        }
        viewModel.bottomToolBarVisibility.observe(this){
            binding.navigationView.visibility = if (it) {View.VISIBLE} else {View.INVISIBLE}
        }

        viewModel.bottomItemsVisibility.observe(this){
            items ->
            binding.navigationView.menu.findItem(R.id.item_add).isVisible = items.add
            binding.navigationView.menu.findItem(R.id.item_edit).isVisible = items.edit
            binding.navigationView.menu.findItem(R.id.item_delete).isVisible = items.delete
        }

        binding.navigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.item_add -> {
                    viewModel.addOnClick()
                    return@setOnItemSelectedListener true
                }
                R.id.item_edit -> {
                    viewModel.editOnClick()
                    return@setOnItemSelectedListener true
                }
                R.id.item_delete -> {
                    viewModel.deleteOnClick()
                    return@setOnItemSelectedListener true
                }
                else -> {return@setOnItemSelectedListener false}
            }
        }
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager
        viewModelUserEdit
        super.onBackPressed()
    }
}