package com.k_office.presentation.base.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.k_office.domain.base.UIText
import timber.log.Timber

abstract class BaseFragment : Fragment() {

    protected val navController: NavController by lazy { initNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClicks()
        setupViewModelCallbacks()
    }

    protected open fun setupClicks() = Unit

    protected open fun setupViewModelCallbacks() = Unit

    protected fun showMessage(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), message, duration).show()
    }

    protected fun showMessage(uIText: UIText?, duration: Int = Toast.LENGTH_SHORT) {
        if (uIText != null) {
            showMessage(uIText.getString(requireContext()), duration)
        }
    }

    protected fun showMessage(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
        showMessage(requireContext().getString(id), duration)
    }

    protected open fun initNavController(): NavController = findNavController()

    // Не падаємо, якщо action вже не з поточного екрана (фрагмент у back stack ще слухає flow)
    protected fun navigateSafely(directions: NavDirections) {
        val current = navController.currentDestination
        if (current?.getAction(directions.actionId) != null) {
            navController.navigate(directions)
        } else {
            Timber.w("Skip navigate ${directions.actionId} from ${current?.label}")
        }
    }
}