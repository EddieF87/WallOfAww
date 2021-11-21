package com.eddief.android.infiniteaww.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.eddief.android.infiniteaww.R
import java.util.*
import kotlin.collections.ArrayList

class SubsDialog : DialogFragment() {

    companion object {
        private const val ARG_CHECKED: String = "checked"
        private const val ARG_UNCHECKED: String = "unchecked"

        fun newInstance(checked: ArrayList<Int>, unchecked: ArrayList<Int>): SubsDialog {
            val fragment = SubsDialog()
            val args = Bundle()
            args.putIntegerArrayList(ARG_CHECKED, checked)
            args.putIntegerArrayList(ARG_UNCHECKED, unchecked)
            fragment.arguments = args
            return fragment
        }
    }

    interface OnInteractionListener {
        fun onPreferencesSaved(checked: List<Int>, unchecked: List<Int>)
    }

    private var mChecked = ArrayList<Int>()
    private var mUnChecked = ArrayList<Int>()
    private var mListener: OnInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mChecked = arguments?.getIntegerArrayList(ARG_CHECKED) ?: ArrayList()
        mUnChecked = arguments?.getIntegerArrayList(ARG_UNCHECKED) ?: ArrayList()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(activity).inflate(R.layout.dialog_subs, null)
        val context = view.context
        for (id in mChecked) {
            val checkBox = view.findViewById<CheckBox?>(id)
            checkBox.isChecked = true
        }
        MainViewModel.subReddits.forEach {
            setCheckBox(view, it.pref)
        }
        val alertDialog = AlertDialog.Builder(context)
            .setView(view)
            .setTitle("Choose Subreddits")
            .setPositiveButton(R.string.save, DialogInterface.OnClickListener { _, _ ->
                if (mChecked.isEmpty()) {
                    Toast.makeText(context, "Choose at least one subreddit", Toast.LENGTH_SHORT)
                        .show()
                    return@OnClickListener
                }
                onButtonPressed()
            })
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog?.dismiss() }
            .create()
        alertDialog.show()
        return alertDialog
    }

    private fun setCheckBox(view: View, id: Int) {

        val checkBox = view.findViewById<CheckBox?>(id)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mChecked.add(id)
                mUnChecked.remove(id)
            } else {
                mUnChecked.add(id)
                mChecked.remove(id)
            }
        }
    }

    private fun onButtonPressed() {
        mListener?.onPreferencesSaved(mChecked, mUnChecked)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnInteractionListener) {
            context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement OnFragmentInteractionListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
}