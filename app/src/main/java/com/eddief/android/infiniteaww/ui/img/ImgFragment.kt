package com.eddief.android.infiniteaww.ui.img

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.eddief.android.infiniteaww.R
import com.eddief.android.infiniteaww.databinding.FragmentImgBinding
import com.eddief.android.infiniteaww.model.AwwImage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImgFragment : Fragment() {

    companion object {
        const val POS_KEY = "pos"
        const val IMAGE_KEY = "aww_img"

        fun newInstance(awwImage: AwwImage): ImgFragment {
            val fragment = ImgFragment()
            val args = Bundle()
            args.putParcelable(IMAGE_KEY, awwImage)
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: ImgViewModel by activityViewModels()

    private lateinit var mAwwImage: AwwImage
    private var mVideoPosition = 0

    private var _binding: FragmentImgBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImgBinding.inflate(layoutInflater)

        if (savedInstanceState != null) {
            mVideoPosition = savedInstanceState.getInt(POS_KEY, 0)
        }
        (arguments?.getParcelable(IMAGE_KEY) as? AwwImage)?.let { image ->
            mAwwImage = image
            when {
                image.isVideo -> playVideo(image.fallbackUrl)
                else -> showImage(image.primaryUrl, image.fallbackUrl)
            }
            binding.title.text = image.title
        } ?: activity?.finish()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.Main) {
            updateSaveIcon(!viewModel.isImageSaved(mAwwImage))
        }
        if (mAwwImage.isVideo) binding.vidView.start()
    }

    override fun onPause() {
        super.onPause()
        if (mAwwImage.isVideo) binding.vidView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(POS_KEY, binding.vidView.currentPosition)
    }

    private fun playVideo(vidUrl: String) {
        val mc = MediaController(requireContext(), false)
        binding.imageFull.visibility = View.GONE
        binding.vidView.apply {
            visibility = View.VISIBLE
            setOnPreparedListener { it.isLooping = true }
            setOnErrorListener { _, _, _ -> true }
            mc.setAnchorView(this)
            mc.setMediaPlayer(this)
            setMediaController(mc)
            setVideoURI(Uri.parse(vidUrl))
            seekTo(mVideoPosition)
            start()
        }
    }

    private fun showImage(primaryUrl: String?, fallbackUrl: String?) {
        Picasso.get()
            .load(primaryUrl)
            .placeholder(R.drawable.ic_paw)
            .fit()
            .centerInside()
            .into(binding.imageFull, object : Callback {
                override fun onSuccess() {}
                override fun onError(e: Exception?) {
                    _binding?.let {
                        Picasso.get()
                            .load(fallbackUrl)
                            .fit()
                            .centerInside()
                            .placeholder(R.drawable.ic_paw)
                            .error(R.drawable.ic_broken_img)
                            .into(it.imageFull)
                    }
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send -> sendLink()
            R.id.action_link -> goToLink()
            R.id.action_save -> saveImage()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendLink() {
        val msgIntent = Intent(Intent.ACTION_SEND)
        msgIntent.type = "text/plain"
        msgIntent.putExtra(Intent.EXTRA_TEXT, mAwwImage.link)
        startActivity(Intent.createChooser(msgIntent, "Share with friends!"))
    }

    private fun goToLink() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mAwwImage.link))
        startActivity(browserIntent)
    }

    private fun updateSaveStatus(exists: Boolean) {
        if (exists) {
            viewModel.removeAwwImage(mAwwImage)
        } else {
            viewModel.saveAwwImage(mAwwImage)
        }
        updateSaveIcon(!exists)
    }

    private fun saveImage() {
        lifecycleScope.launch(Dispatchers.Main) {
            updateSaveStatus(!viewModel.isImageSaved(mAwwImage))
        }
    }

    private fun updateSaveIcon(saved: Boolean) {
        (activity as? ImgActivity)?.setSaveIcon(saved)
    }
}