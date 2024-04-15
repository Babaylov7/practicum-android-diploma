package ru.practicum.android.diploma.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilteringSettingsBinding
import ru.practicum.android.diploma.presentation.filter.FilteringSettingsViewModel

class FilteringSettingsFragment : Fragment() {

    private var binding: FragmentFilteringSettingsBinding? = null

    private val viewModel by viewModel<FilteringSettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFilteringSettingsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createFirstHalfClickListeners()
        createSecondHalfClickListeners()
        createTextWatcher()
        // нужен слушатеть лайф даты с классом фильтра
        // нужен метод который из лайфдаты выставит все данные
        // кнопка применить - ее видимость определяется отдельным методом при сравнении с
        // последним сохраненным фильтром
        binding!!.tietSalary.setOnFocusChangeListener { _, b ->
            if (b) {
                binding!!.tilSalaryLayout.defaultHintTextColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
            } else if (binding!!.tietSalary.text!!.isNotEmpty()) {
                binding!!.tilSalaryLayout.defaultHintTextColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.black)
            } else {
                binding!!.tilSalaryLayout.defaultHintTextColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.gray_white)
            }
        }
        // (binding!!.tietIndustry as TextView).text = "sdfsdfsdfs"
    }

    override fun onResume() {
        super.onResume()
        checkTIETContent()
        installButtonResetVisibility()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun createFirstHalfClickListeners() {
        binding!!.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding!!.ivSalaryClear.setOnClickListener {
            binding!!.tietSalary.text!!.clear()
            binding!!.tilSalaryLayout.defaultHintTextColor =
                ContextCompat.getColorStateList(requireContext(), R.color.gray_white)
            installButtonResetVisibility()
        }
        binding!!.bReset.setOnClickListener {
            binding!!.tietIndustry.text!!.clear()
            binding!!.tietJobPlace.text!!.clear()
            binding!!.tietSalary.text!!.clear()
            binding!!.cbNoSalary.isChecked = false
            binding!!.bReset.isVisible = false
            checkTIETContent()
        }
        binding!!.cbNoSalary.setOnClickListener {
            installButtonResetVisibility()
        }
    }

    private fun createSecondHalfClickListeners() {
        binding!!.ivJobPlaceClear.setOnClickListener {
            binding!!.tietJobPlace.text!!.clear()
            installButtonResetVisibility()
            checkTIETContent()
        }
        binding!!.ivIndustryClear.setOnClickListener {
            binding!!.tietIndustry.text!!.clear()
            installButtonResetVisibility()
            checkTIETContent()
        }
        binding!!.ivArrowRightJobPlace.setOnClickListener {
            findNavController().navigate(
                FilteringSettingsFragmentDirections.actionFilteringSettingsFragmentToPlacesOfWorkFragment()
            )
        }
        binding!!.ivArrowRightIndustry.setOnClickListener {
            findNavController().navigate(
                FilteringSettingsFragmentDirections.actionFilteringSettingsFragmentToIndustrySelectionFragment()
            )
        }
    }

    private fun createTextWatcher() {
        (binding!!.tietSalary as EditText).doOnTextChanged { p0: CharSequence?, start: Int, before: Int, count: Int ->
            binding!!.ivSalaryClear.isVisible = !p0.isNullOrEmpty()
            installButtonResetVisibility()
        }
    }

    private fun installButtonResetVisibility() {
        val buttonsVisibility = binding!!.tietIndustry.text!!.isNotEmpty()
            || binding!!.tietJobPlace.text!!.isNotEmpty()
            || binding!!.tietSalary.text!!.isNotEmpty()
            || binding!!.cbNoSalary.isChecked

        binding!!.bReset.isVisible = buttonsVisibility
    }

    private fun checkTIETContent() {
        if (binding!!.tietJobPlace.text!!.isNotEmpty()) {
            binding!!.ivJobPlaceClear.isVisible = true
            binding!!.ivArrowRightJobPlace.isVisible = false
        } else {
            binding!!.ivJobPlaceClear.isVisible = false
            binding!!.ivArrowRightJobPlace.isVisible = true
        }
        if (binding!!.tietIndustry.text!!.isNotEmpty()) {
            binding!!.ivIndustryClear.isVisible = true
            binding!!.ivArrowRightIndustry.isVisible = false
        } else {
            binding!!.ivIndustryClear.isVisible = false
            binding!!.ivArrowRightIndustry.isVisible = true
        }

    }
}
