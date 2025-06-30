package com.k_office.presentation.base.utils

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment

interface Args : Parcelable
interface FragmentArgs<ARGS : Args>

private const val BUNDLE_ARGUMENTS_KEY = "argument"

fun <FRAGMENT, ARGS : Args> FRAGMENT.setArgs(args: ARGS): FRAGMENT where FRAGMENT : Fragment, FRAGMENT : FragmentArgs<ARGS> {
    return apply {
        arguments = Bundle().apply {
            putParcelable(BUNDLE_ARGUMENTS_KEY, args)
        }
    }
}

@MainThread
inline fun <FRAGMENT, reified ARGS : Args> FRAGMENT.args(): ArgsLazy<ARGS> where FRAGMENT : Fragment, FRAGMENT : FragmentArgs<ARGS> =
    ArgsLazy {
        arguments ?: throw IllegalStateException("Fragment $this has null arguments")
    }

class ArgsLazy<ARGS : Args>(
    private val argumentsProducer: () -> Bundle
) : Lazy<ARGS> {

    private var cached: ARGS? = null

    override val value: ARGS
        get() {
            var args = cached
            if (args == null) {
                val arguments = argumentsProducer()

                args = arguments.getParcelable(BUNDLE_ARGUMENTS_KEY)
                    ?: throw IllegalStateException("You should set the arguments manually via setArgs")

                cached = args
            }
            return args
        }

    override fun isInitialized(): Boolean = cached != null
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}