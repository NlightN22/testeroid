package space.active.testeroid.screens.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.adapter.PageAdapter
import space.active.testeroid.databinding.ActivityMainBinding

const val DATA_STORE_NAME = "TesteroidPreference"

// At the top level of your kotlin file for the one instance of DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG,"MainActivity created")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, MainActivityViewModelFactory(this)).get(
            MainActivityViewModel::class.java)

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
        viewModel.isFirstStart()
    }

    private fun BottomNavigationView.visible (value: Boolean) {
        this.visibility = if (value) {View.VISIBLE} else {View.INVISIBLE}
    }

    private fun TabLayout.visible (value: Boolean) {
        this.visibility = if (value) {View.VISIBLE} else {View.INVISIBLE}
    }

    private fun ViewPager2.visible (value: Boolean) {
        this.visibility = if (value) {View.VISIBLE} else {View.INVISIBLE}
    }

    private fun observers() {
        // Bottom visible state
        viewModel.form.observe(this){ form->
            Log.e(TAG,"form: $form")
            binding.navigationView.menu.findItem(R.id.item_add).isVisible = form.navigation.add
            binding.navigationView.menu.findItem(R.id.item_edit).isVisible = form.navigation.edit
            binding.navigationView.menu.findItem(R.id.item_delete).isVisible = form.navigation.delete
            binding.tabLayout.visible(form.tabs.visibility)
            binding.navigationView.visible(form.navigation.visibility)
            binding.pager.visible(form.pager.visibility)
        }
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

    override fun onDestroy() {
        Log.e(TAG,"MainActivity destroyed")
        super.onDestroy()
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