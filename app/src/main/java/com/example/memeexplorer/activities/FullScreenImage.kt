package com.example.memeexplorer.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.memeexplorer.R
import com.example.memeexplorer.adapter.AdapterFullScreenImage
import com.example.memeexplorer.utilities.AdController
import com.example.memeexplorer.utilities.Tools
import java.io.File
import java.io.IOException

class FullScreenImage : AppCompatActivity() {

    companion object {
        val EXTRA_POS = "key.EXTRA_POS"
        val EXTRA_IMGS = "key.EXTRA_IMGS"
    }

    private var adapter: AdapterFullScreenImage? = null
    private lateinit var viewPager: ViewPager
    private lateinit var text_page: TextView

    private var position = 0
    private var activePosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)
        viewPager = findViewById(R.id.pager)
        text_page = findViewById(R.id.text_page)
        val items: ArrayList<String>
        val i = intent
        position = i.getIntExtra(EXTRA_POS, 0)
        items = i.getStringArrayListExtra(EXTRA_IMGS)!!
        activePosition = position
        adapter = AdapterFullScreenImage(this@FullScreenImage, items)
        val total = adapter!!.count
        viewPager.setAdapter(adapter)
        text_page.setText(String.format(getString(R.string.image_of), position + 1, total))

        // displaying selected image first
        viewPager.setCurrentItem(position)
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                pos: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(pos: Int) {
                activePosition = pos
                text_page.setText(String.format(getString(R.string.image_of), pos + 1, total))
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        findViewById<View>(R.id.btnClose).setOnClickListener { finish() }
        findViewById<View>(R.id.btnSave).setOnClickListener {
            AdController.adCounter++
            AdController.showInterAd(this@FullScreenImage, null, 0)
            try {
                Tools.exportFile(
                    this@FullScreenImage,
                    File(items[activePosition])
                )
                Tools.toastIconInfo(this@FullScreenImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        findViewById<View>(R.id.btnShare).setOnClickListener {
            AdController.adCounter++
            AdController.showInterAd(this@FullScreenImage, null, 0)
            Tools.share(this@FullScreenImage, File(items[activePosition]))
        }

        // for system bar in lollipop
        Tools.systemBarLolipop(this)
        Tools.RTLMode(window)
    }


}