package com.jiaredhuang.readviewac

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaredhuang.readbook.R
import com.jaredhuang.xiao.ReadViewExt
import com.jaredhuang.xiao.bean.BookChapterBean
import com.jaredhuang.xiao.bean.BookCollectBean
import com.jaredhuang.xiao.help.ReadBookControl
import com.jaredhuang.xiao.widget.page.PageLoader
import kotlinx.android.synthetic.main.activity_book_read.*

class BookReadActivity : AppCompatActivity() {

    companion object {
        @JvmField
        val PAGE_KEY: String = "pageKey"
    }

    private var pageKey: String? = null
    lateinit var readBookControl: ReadBookControl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReadViewExt.getInstance().init(this)
        readBookControl = ReadBookControl.getInstance()
        readBookControl.initTextDrawableIndex()
        setContentView(R.layout.activity_book_read)
        pageKey = intent.getStringExtra(PAGE_KEY)
        pageKey = "/sdcard/aaaaa.txt"
        page_view.createPageLoader(this, pageKey, object : PageLoader.OnPageLoaderCallback {
            override fun onPageChange(chapterIndex: Int, pageIndex: Int, resetReadAloud: Boolean) {
            }

            override fun onCategoryFinish(chapters: MutableList<BookChapterBean>?) {
            }

            override fun onChapterChange(pos: Int) {
            }

            override fun onPageCountChange(count: Int) {
            }

            override fun onVipPop() {
            }
        }) {  }
    }
}
