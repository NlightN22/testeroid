package space.active.testeroid.screens.score

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import space.active.testeroid.APP
import space.active.testeroid.TAG
import space.active.testeroid.databinding.FragmentScoreBinding
import space.active.testeroid.screens.edittest.EditTestViewModel
import space.active.testeroid.screens.edittest.EditTestViewModelFactory

class ScoreFragment : Fragment() {
    lateinit var binding: FragmentScoreBinding
    lateinit var viewModel: EditTestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScoreBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(APP, EditTestViewModelFactory(APP.applicationContext)).get(
            EditTestViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        Log.e(TAG, "ScoreFragment end initialization")
    }

}