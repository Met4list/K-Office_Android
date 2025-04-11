package com.k_office.presentation.base.utils;


import android.content.Context;

import androidx.annotation.AnimRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

public final class FragmentUtil {
    private FragmentUtil() {

    }

    public static void setFragment(Fragment fragment,
                                   AppCompatActivity appCompatActivity,
                                   @IdRes int fragmentPlaceholder) {
        if (!appCompatActivity.isFinishing()) {
            KeyboardHelper.hide(appCompatActivity, appCompatActivity.getWindow().getDecorView());
            FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(fragmentPlaceholder, fragment, fragment.getClass().getSimpleName());
            fragmentTransaction.addToBackStack(getFragmentTag(fragment));
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    public static void setFragmentIfAbsent(Fragment fragment,
                                           AppCompatActivity appCompatActivity,
                                           @IdRes int fragmentPlaceholder) {
        setFragmentIfAbsent(fragment, appCompatActivity, fragmentPlaceholder, false);
    }

    public static void setFragmentIfAbsent(Fragment fragment,
                                           AppCompatActivity appCompatActivity,
                                           @IdRes int fragmentPlaceholder,
                                           boolean setPrimaryNavigation) {
        setFragmentIfAbsent(fragment, appCompatActivity, fragmentPlaceholder, setPrimaryNavigation, -1, -1);
    }

    public static void setFragmentIfAbsent(Fragment fragment,
                                           AppCompatActivity appCompatActivity,
                                           @IdRes int fragmentPlaceholder,
                                           boolean setPrimaryNavigation,
                                           @AnimRes int enterAnim,
                                           @AnimRes int exitAnim) {
        if (!appCompatActivity.isFinishing()) {
            KeyboardHelper.hide(appCompatActivity, appCompatActivity.getWindow().getDecorView());
            String tag = fragment.getClass().getSimpleName();
            FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(tag) == null) {
                FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setReorderingAllowed(true);
                if (enterAnim != -1 && exitAnim != -1) {
                    fragmentTransaction.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim);
                }
                fragmentTransaction.replace(fragmentPlaceholder, fragment, tag);
                if (setPrimaryNavigation) {
                    fragmentTransaction.setPrimaryNavigationFragment(fragment);
                }
                fragmentTransaction.addToBackStack(getFragmentTag(fragment));
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }

    public static void addFragmentIfAbsent(Fragment fragment,
                                           AppCompatActivity appCompatActivity,
                                           @IdRes int fragmentPlaceholder) {
        if (!appCompatActivity.isFinishing()) {
            KeyboardHelper.hide(appCompatActivity, appCompatActivity.getWindow().getDecorView());
            String tag = fragment.getClass().getSimpleName();
            FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(tag) == null) {
                FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(fragmentPlaceholder, fragment, tag);
                fragmentTransaction.addToBackStack(getFragmentTag(fragment));
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }

    public static void addOnFragmentAttachedAction(
            Fragment fragment,
            AppCompatActivity appCompatActivity,
            Runnable block // Runnable instead of Kotlin's lambda
    ) {
        if (!appCompatActivity.isFinishing()) {
            String tag = fragment.getClass().getSimpleName();
            FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();

            fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
                    if (f.getTag() != null && f.getTag().equals(tag)) {
                        block.run();
                    }
                    fm.unregisterFragmentLifecycleCallbacks(this);
                }
            }, false);
        }
    }

    public static void setFragmentIfAbsent(Fragment fragment,
                                           FragmentManager fragmentManager,
                                           @IdRes int fragmentPlaceholder) {
        String tag = getFragmentTag(fragment);
        if (fragmentManager.findFragmentByTag(tag) == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(fragmentPlaceholder, fragment, tag);
            fragmentTransaction.addToBackStack(getFragmentTag(fragment));
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    public static String getFragmentTag(Fragment fragment) {
        return fragment.getClass().getCanonicalName();
    }

    public static String getFragmentTag(Class<? extends Fragment> fragmentClass) {
        return fragmentClass.getCanonicalName();
    }

    /**
     * @param oldFragment         - a previous fragment to hide.
     * @param newFragment         - a next fragment to show.
     * @param fragmentManager     - a fragment manager on which to do transactions.
     * @param fragmentPlaceholder - a resource placeholder to put fragment to.
     * @return - an active fragment.
     */
    public static Fragment hideShowOrAdd(@Nullable Fragment oldFragment, @NonNull Fragment newFragment, @NonNull FragmentManager fragmentManager, @IdRes int fragmentPlaceholder) {
        String tag = getFragmentTag(newFragment);
        Fragment newExistedFragment = fragmentManager.findFragmentByTag(tag);
        if (oldFragment != null) {
            if (newExistedFragment != null) {
                fragmentManager
                        .beginTransaction()
                        .hide(oldFragment)
                        .setMaxLifecycle(oldFragment, Lifecycle.State.STARTED)
                        .show(newExistedFragment)
                        .setMaxLifecycle(newExistedFragment, Lifecycle.State.RESUMED)
                        .setReorderingAllowed(true)
                        .commit();
                return newExistedFragment;
            } else {
                fragmentManager
                        .beginTransaction()
                        .hide(oldFragment)
                        .setMaxLifecycle(oldFragment, Lifecycle.State.STARTED)
                        .add(fragmentPlaceholder, newFragment, tag)
                        .setReorderingAllowed(true)
                        .commit();
                return newFragment;
            }
        } else {
            if (newExistedFragment == null) {
                fragmentManager
                        .beginTransaction()
                        .add(fragmentPlaceholder, newFragment, tag)
                        .setReorderingAllowed(true)
                        .commit();
            } else {
                fragmentManager
                        .beginTransaction()
                        .show(newExistedFragment)
                        .setMaxLifecycle(newExistedFragment, Lifecycle.State.RESUMED)
                        .setReorderingAllowed(true)
                        .commit();
            }
            return newFragment;
        }
    }

    public static void setFragmentWithTarget(Fragment fragment,
                                             Fragment targetFragment,
                                             AppCompatActivity appCompatActivity,
                                             @IdRes int fragmentPlaceholder,
                                             int requestCode) {
        if (!appCompatActivity.isFinishing()) {
            KeyboardHelper.hide(appCompatActivity, appCompatActivity.getWindow().getDecorView());
            FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
            fragment.setTargetFragment(targetFragment, requestCode);
            fragmentTransaction.add(fragmentPlaceholder, fragment, getFragmentTag(fragment));
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    public static void removeFragment(Fragment fragment,
                                      FragmentManager fragmentManager) {
        removeFragment(fragment, fragmentManager, false);
    }

    public static void removeFragment(Fragment fragment,
                                      FragmentManager fragmentManager,
                                      boolean forceSync) {
        String tag = getFragmentTag(fragment);
        Fragment existedFragment = fragmentManager.findFragmentByTag(tag);
        if (existedFragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().remove(existedFragment);
            if (forceSync) {
                fragmentTransaction.commitNow();
            } else {
                fragmentTransaction.commit();
            }
            fragmentManager.popBackStack();
        }
    }
}