package com.eddief.android.infiniteaww.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import com.eddief.android.infiniteaww.R
import com.eddief.android.infiniteaww.databinding.DialogColumnsBinding

class ColumnsDialog : DialogFragment() {

    companion object {
        private const val ARG_COLUMNS: String = "columns"

        fun newInstance(columns: Int): ColumnsDialog {
            val fragment = ColumnsDialog()
            val args = Bundle()
            args.putInt(ARG_COLUMNS, columns)
            fragment.arguments = args
            return fragment
        }
    }

    interface OnInteractionListener {
        fun onColumnsSaved(columns: Int)
    }

    private var columns = 3
    private var mListener: OnInteractionListener? = null

    private var _binding: DialogColumnsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        columns = arguments?.getInt(ARG_COLUMNS) ?: 3
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogColumnsBinding.inflate(LayoutInflater.from(context))
        val alertDialog = AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setTitle("Set Columns")
            .setPositiveButton(R.string.save) { _, _ ->
                mListener?.onColumnsSaved(columns)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog?.dismiss() }
            .create()
        alertDialog.show()
        binding.columnsText.text = getString(R.string.columns, columns)
        binding.slider.progress = columns
        binding.slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                columns = progress
                binding.columnsText.text = getString(R.string.columns, columns)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        return alertDialog
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