package com.eddief.android.infiniteaww.ui.main

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddief.android.infiniteaww.R
import com.eddief.android.infiniteaww.databinding.ActivityMainBinding
import com.eddief.android.infiniteaww.model.*
import com.eddief.android.infiniteaww.ui.img.ImgActivity
import com.eddief.android.infiniteaww.ui.main.ImageRecyclerViewAdapter.ItemClickListener
import com.google.android.gms.ads.AdRequest
import java.util.*
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity(),
    ItemClickListener, SubsDialog.OnInteractionListener, ColumnsDialog.OnInteractionListener {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var optionsMenu: Menu
    private lateinit var mAdapter: ImageRecyclerViewAdapter

    private var canFetch = true
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this) { }
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        mAdapter = ImageRecyclerViewAdapter(arrayListOf())
        mAdapter.setClickListener(this)
        binding.rv.apply {
            layoutManager = GridLayoutManager(this@MainActivity, mainViewModel.getColumns())
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        onScrollRefresh()
                    }
                }
            })
            adapter = mAdapter
        }

        mainViewModel.awwImageData.observe(this) { awwImageData ->
            endRefresh()
            val awwImages = when (awwImageData) {
                is Resource.Success -> {
                    awwImageData.data
                }
                is Resource.Error -> {
                    Toast.makeText(
                        this, "ERROR: ${awwImageData.exception}", Toast.LENGTH_LONG
                    ).show()
                    awwImageData.data
                }
            }
            addItems(awwImages)

            if (awwImages.isEmpty()) {
                binding.errorText.apply {
                    setText(if (mainViewModel.isFavorites) R.string.no_favorites else R.string.fetch_error)
                    visibility = View.VISIBLE
                }
            } else {
                binding.errorText.visibility = View.GONE
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (checkConnection() && !mainViewModel.isFavorites) {
                mainViewModel.refreshData()
            }
        }
        checkConnection()
        mainViewModel.getFreshData()
    }

    private fun onScrollRefresh() {
        if (!canFetch || !mainViewModel.canScrollRefresh) {
            return
        }
        if (checkConnection()) {
            mainViewModel.getDataFromRepository()
            canFetch = false
            startRefresh()
        }
    }

    override fun onBackPressed() {
        if (mainViewModel.isFavorites) {
            mainViewModel.toggleFavorites()
        } else {
            super.onBackPressed()
        }
    }

    private fun checkConnection(): Boolean {
        val connManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: NetworkInfo? = null
        networkInfo = connManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            Toast.makeText(this, "Please connect to a stable data connection!", Toast.LENGTH_LONG)
                .show()
            endRefresh()
            return false
        }
        return true
    }

    private fun addItems(awwImages: List<AwwImage>) {
        binding.rv.recycledViewPool.clear()
        mAdapter.updateImages(awwImages)
        endRefresh()
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ImgActivity::class.java)
        intent.putExtra(ImgActivity.IMG_POS_KEY, position)
        intent.putExtra(ImgActivity.IS_FAVS_KEY, mainViewModel.isFavorites)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        optionsMenu = menu
        mainViewModel.isFavoritesLiveData.observe(this) { isFavorites ->
            optionsMenu.findItem(R.id.action_fav).setIcon(
                if (isFavorites) R.drawable.ic_star else R.drawable.ic_save
            )
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_fav -> mainViewModel.toggleFavorites()
            R.id.action_edit_subs -> mainViewModel.parseSubreddits { checked, unchecked ->
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                SubsDialog.newInstance(checked, unchecked).show(fragmentTransaction, "")
            }
            R.id.action_edit_columns -> {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                ColumnsDialog.newInstance(mainViewModel.getColumns()).show(fragmentTransaction, "")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPreferencesSaved(checked: List<Int>, unchecked: List<Int>) {
        mainViewModel.saveSubReddits(checked, unchecked)
        if (!mainViewModel.isFavorites) {
            startRefresh()
        }
        if (checkConnection()) {
            mainViewModel.refreshData()
        }
    }

    override fun onColumnsSaved(columns: Int) {
        mainViewModel.saveColumns(columns)
        (binding.rv.layoutManager as GridLayoutManager).spanCount = columns
    }

    private fun endRefresh() {
        canFetch = true
        binding.swipeRefreshLayout.apply {
            if (isRefreshing) {
                isRefreshing = false
            }
        }
    }

    private fun startRefresh() {
        canFetch = false
        binding.swipeRefreshLayout.apply {
            if (!isRefreshing) {
                isRefreshing = true
            }
        }
    }
}