package ru.practicum.android.diploma.ui.job_vacancy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentJobVacancyBinding
import ru.practicum.android.diploma.presentation.job_vacancy.JobVacancyViewModel

class JobVacancyFragment() : Fragment() {

    private var binding: FragmentJobVacancyBinding? = null

    private val viewModel by viewModel<JobVacancyViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentJobVacancyBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
