package space.active.testeroid.screens.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import space.active.testeroid.APP
import space.active.testeroid.DATA_STORE_NAME
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.adapter.PageAdapter
import space.active.testeroid.databinding.ActivityMainBinding
import space.active.testeroid.screens.useredit.UserEditViewModel
import space.active.testeroid.screens.useredit.UserEditViewModelFactory


// At the top level of your kotlin file for the one instance of DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainActivityViewModel
    lateinit var viewModelUserEdit: UserEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        APP = this
        viewModel = ViewModelProvider(this, MainActivityViewModelFactory(this)).get(
            MainActivityViewModel::class.java)

        viewModelUserEdit = ViewModelProvider(this, UserEditViewModelFactory(this)).get(
            UserEditViewModel::class.java)

        init()
    }

    private fun init(){
        //inflate from adapter
        binding.pager.adapter = PageAdapter(this.supportFragmentManager, lifecycle)

        // set icons for tabs start
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
        handleExternalData()
        observers()
        listeners()
    }

    private fun handleExternalData() {

    }

    private fun BottomNavigationView.visible (value: Boolean) {
        this.visibility = if (value) {View.VISIBLE} else {View.INVISIBLE}
    }

    private fun TabLayout.visible (value: Boolean) {
        this.visibility = if (value) {View.VISIBLE} else {View.INVISIBLE}
    }

    private fun observers() {
        // Bottom visible state
        viewModel.form.observe(this){ form->
            Log.e(TAG,"form: $form")
            binding.tabLayout.visible(form.tabs.visibility)
            binding.navigationView.visible(form.navigation.visibility)
            binding.navigationView.menu.findItem(R.id.item_add).isVisible = form.navigation.add
            binding.navigationView.menu.findItem(R.id.item_edit).isVisible = form.navigation.edit
            binding.navigationView.menu.findItem(R.id.item_delete).isVisible = form.navigation.delete
        }

//        viewModel.bottomTabsVisibility.observe(this){
//            binding.tabLayout.visibility = if (it) {View.VISIBLE} else {View.INVISIBLE}
//        }
//        viewModel.bottomToolBarVisibility.observe(this){
//            binding.navigationView.visibility = if (it) {View.VISIBLE} else {View.INVISIBLE}
//        }
//
//        viewModel.bottomItemsVisibility.observe(this){
//                items ->
//            binding.navigationView.menu.findItem(R.id.item_add).isVisible = items.add
//            binding.navigationView.menu.findItem(R.id.item_edit).isVisible = items.edit
//            binding.navigationView.menu.findItem(R.id.item_delete).isVisible = items.delete
//        }
    }

    private fun listeners() {
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

    var lastPress: Long = 0
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPress > 5000 && binding.pager.visibility == View.VISIBLE) {
            Toast.makeText(baseContext, getString(R.string.main_exit_toast), Toast.LENGTH_LONG).show()
            lastPress = currentTime
        } else {
            super.onBackPressed()
        }
    }
}