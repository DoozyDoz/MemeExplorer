package com.example.memeexplorer.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.os.ResultReceiver
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memeexplorer.R
import com.example.memeexplorer.common.presentation.AdapterGridBasic
import com.example.memeexplorer.helpers.OcrDetectorProcessor
import com.example.memeexplorer.memeClasses.Meme
import com.example.memeexplorer.memeClasses.MemeLab
import com.example.memeexplorer.utilities.*
import com.example.memeexplorer.widgets.SpacingItemDecoration
import com.google.android.gms.ads.AdView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import java.util.stream.Collectors

class DebugActivity : AppCompatActivity() {
    private var parent_view: View? = null
    private lateinit var recyclerView: RecyclerView
    private var mMemes: List<Meme>? = null
    private var mMemeLab: MemeLab? = null
    private lateinit var mTextRecognizer: TextRecognizer
    var pathsArray = ArrayList<String>()
    var searchView: SearchView? = null
    private var adView: AdView? = null
    private var adapter: AdapterGridBasic? = null

    companion object{
        fun convertPathToBitmap(filepath: String?): Bitmap? {
            val sd = Environment.getExternalStorageDirectory()
            val image = File(filepath)
            val bmOptions = BitmapFactory.Options()
            val bitmap = BitmapFactory.decodeFile(image.absolutePath, bmOptions)
            return if (bitmap != null) {
                Bitmap.createScaledBitmap(bitmap, 120, 120, true)
            } else null
        }
    }

    fun detectText(context: Context?, frame: Frame?, location: String) {
        mTextRecognizer = TextRecognizer.Builder(context).build()
        mTextRecognizer.setProcessor(OcrDetectorProcessor())
        val items = mTextRecognizer.detect(frame)
        var s = ""
        for (i in 0 until items.size()) {
            val item = items.valueAt(i)
            if (item != null) {
                s += item.value + " "
            }
        }
        Log.i("textss :", s)
        saveInDb(location, s)
    }

    private fun saveInDb(location: String, tag: String) {
        val m = Meme(location, tag)
        mMemeLab!!.addMeme(m)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_basic)
        parent_view = findViewById(android.R.id.content)
        initToolbar()
        initComponent()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "OCR Gallery"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Tools.setSystemBarColor(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initComponent() {
        Tools.APP_DIR = Environment.getExternalStorageDirectory().path +
                File.separator + "OCRGallery"
        val dst = File(Tools.APP_DIR)
        if (!dst.exists()) {
            if (!dst.mkdir()) {
                Toast.makeText(this@DebugActivity, "Failed to create save path", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        recyclerView = findViewById(R.id.recyclerView)
        adView = findViewById(R.id.adView)
        AdController.loadBannerAd(this@DebugActivity, adView)
        AdController.loadInterAd(this@DebugActivity)
        pathsArray = ArrayListSaverInterface(applicationContext).unFilteredImageListPaths
        setListLayoutManager()

        val firsthundredPaths: List<String?> =
            pathsArray.stream().limit(100).collect(Collectors.toList())
        adapter = AdapterGridBasic(
            this@DebugActivity,
            firsthundredPaths as ArrayList<String?>
        )
        adapter!!.setOnItemClickListener { view: View?, meme: Meme?, position: Int ->
            AdController.adCounter++
            AdController.showInterAd(this@DebugActivity, null, 0)
            viewSingleImage(position, pathsArray)
        }
        recyclerView.setAdapter(adapter)
        mMemeLab = MemeLab.get(this@DebugActivity)
        mTextRecognizer = TextRecognizer.Builder(applicationContext).build()
        mTextRecognizer.setProcessor(OcrDetectorProcessor())
        val i = TranslatorJob.newIntent(applicationContext)
        i!!.putExtra("receiver", DownReceiver(Handler()))
        applicationContext.startService(i)
    }

    private fun viewSingleImage(position: Int, paths: ArrayList<String>) {
        val intent = Intent(this@DebugActivity, FullScreenImage::class.java)
        intent.putExtra(FullScreenImage.EXTRA_POS, position)
        intent.putStringArrayListExtra(FullScreenImage.EXTRA_IMGS, paths)
        startActivity(intent)
    }

    private fun setListLayoutManager() {
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.addItemDecoration(SpacingItemDecoration(3, Tools.dpToPx(this, 2), true))
        recyclerView.setHasFixedSize(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val myActionMenuItem = menu.findItem(R.id.menu_item_search)
        searchView = myActionMenuItem.actionView as SearchView
        val listener: SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onQueryTextChange(query: String): Boolean {
                if (query.isEmpty()) {
                    val firsthundredPaths: List<String?> =
                        pathsArray.stream().limit(100).collect(Collectors.toList())
                    adapter = AdapterGridBasic(
                        this@DebugActivity,
                        firsthundredPaths as ArrayList<String?>
                    )
                    adapter!!.setOnItemClickListener { view: View?, meme: Meme?, position: Int ->
                        AdController.adCounter++
                        AdController.showInterAd(this@DebugActivity, null, 0)
                        viewSingleImage(position, pathsArray)
                    }
                    recyclerView!!.adapter = adapter
                } else {
                    val matchingPics = getMatchingPics(query)
                }
                return true
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.e("queryTextSubmit", query)
                if (query.isEmpty()) {
                    val firsthundredPaths: List<String?> =
                        pathsArray.stream().limit(100).collect(Collectors.toList())
                    adapter = AdapterGridBasic(
                        this@DebugActivity,
                        firsthundredPaths as ArrayList<String?>
                    )
                    adapter!!.setOnItemClickListener { view: View?, meme: Meme?, position: Int ->
                        AdController.adCounter++
                        AdController.showInterAd(this@DebugActivity, null, 0)
                        viewSingleImage(position, pathsArray)
                    }
                    recyclerView!!.adapter = adapter
                } else {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                }
                return true
            }
        }
        searchView!!.setOnQueryTextListener(listener)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getMatchingPics(query: String) {
        GlobalScope.async {
            val newPaths = withContext(Dispatchers.Default) {
                Log.e("queryText", query)
                val newPaths = ArrayList<String?>()
                if (query.isNotEmpty()) {
                    mMemes = mMemeLab?.getMemes(query)
                    var tc = ""
                    for (m in mMemes!!.indices) {
                        Log.e("was called ", "$m ")
                        newPaths.add(mMemes?.get(m)?.location)
                        tc += (mMemes?.get(m)?.location) + "\n"
                    }
                }
                newPaths
            }


            // on post execute
            adapter = AdapterGridBasic(this@DebugActivity, newPaths)
            adapter!!.setOnItemClickListener { _: View?, _: Meme?, position: Int ->
                AdController.adCounter++
                AdController.showInterAd(this@DebugActivity, null, 0)
                viewSingleImage(position, newPaths as ArrayList<String>)
            }
            recyclerView.adapter = adapter
        }
    }

    @SuppressLint("RestrictedApi")
    private class DownReceiver(handler: Handler?) : ResultReceiver(handler) {
        public override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            super.onReceiveResult(resultCode, resultData)
            if (resultCode == Constants.NEW_PROGRESS) {
                val progress = resultData.getInt("progress")
                if (progress == 100) {
//                    getProgressBar().setVisibility(View.GONE);
                } else {
//                    getProgressBar().setProgress(progress);
                }
            }
        }
    }
}