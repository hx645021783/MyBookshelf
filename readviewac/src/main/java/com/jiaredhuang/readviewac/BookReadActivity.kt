package com.jiaredhuang.readviewac

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.jaredhuang.readbook.R
import com.jaredhuang.xiao.ReadViewExt
import com.jaredhuang.xiao.bean.BaseChapterBean
import com.jaredhuang.xiao.bean.BookChapterBean
import com.jaredhuang.xiao.bean.BookCollectBean
import com.jaredhuang.xiao.bean.BookContentBean
import com.jaredhuang.xiao.help.BookCollectHelp
import com.jaredhuang.xiao.help.ReadBookControl
import com.jaredhuang.xiao.widget.page.PageLoader
import com.jiaredhuang.readaloudlib.ReadAloudService
import com.zia.easybookmodule.bean.Book
import com.zia.easybookmodule.bean.Catalog
import com.zia.easybookmodule.engine.EasyBook
import com.zia.easybookmodule.rx.StepSubscriber
import com.zia.easybookmodule.rx.Subscriber
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.annotations.NonNull
import kotlinx.android.synthetic.main.activity_book_read.*


class BookReadActivity : AppCompatActivity() {

    companion object {
        @JvmField
        val PAGE_KEY: String = "pageKey"
    }

    private var curBook: Book? = null
    private var mPageLoader: PageLoader? = null
    private var pageKey: String? = null
    lateinit var readBookControl: ReadBookControl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxBus.get().register(this)
        ReadViewExt.getInstance().init(this)
        readBookControl = ReadBookControl.getInstance()
        readBookControl.initTextDrawableIndex()
        setContentView(R.layout.activity_book_read)
        pageKey = intent.getStringExtra(PAGE_KEY)
        pageKey = "/sdcard/aaaaa.txt"
        searchBook();
    }

    override fun onDestroy() {
        mPageLoader?.closeBook()
        RxBus.get().unregister(this)
        super.onDestroy()
    }
    private fun loadPageBook(book: BookCollectBean?) {
        page_view.createPageLoader(this, book?.noteUrl, object : PageLoader.OnPageLoaderCallback {
            override fun onPageChange(chapterIndex: Int, pageIndex: Int, resetReadAloud: Boolean) {
            }

            override fun onCategoryFinish(chapters: MutableList<BookChapterBean>?) {
            }

            override fun onChapterChange(pos: Int) {
                ReadAloudService.readAloud(this@BookReadActivity,book?.bookInfoBean?.name,mPageLoader?.unReadContent,book?.durChapterPage!!)
            }

            override fun onPageCountChange(count: Int) {
            }

            override fun onVipPop() {
            }
        }) {
            mPageLoader = it
            setOnNetLoad();
        }

    }

    private fun setOnNetLoad() {
        mPageLoader?.setOnNetLoaderCallback(object : PageLoader.OnNetLoaderCallback {
            override fun getChapterList(book: BookCollectBean?): Observable<MutableList<BookChapterBean>> {
                return Observable.create(ObservableOnSubscribe<List<Catalog>> { emitter ->
                    EasyBook.getCatalog(curBook).subscribe(object : Subscriber<List<Catalog>> {
                        override fun onFinish(t: List<Catalog>) {
                            emitter.onNext(t)
                            emitter.onComplete()
                        }

                        override fun onError(e: java.lang.Exception) {
                        }

                        override fun onMessage(message: String) {
                        }

                        override fun onProgress(progress: Int) {
                        }
                    })
                }).flatMap { tList ->
                    val booklist = mutableListOf<BookChapterBean>()
                    for (mCatalog in tList) {
                        val mBookChapterBean = BookChapterBean()
                        mBookChapterBean.noteUrl = curBook?.url
                        mBookChapterBean.durChapterUrl = mCatalog.url
                        mBookChapterBean.domain =curBook?.url
                        mBookChapterBean.durChapterIndex = tList.indexOf(mCatalog)
                        mBookChapterBean.durChapterName = mCatalog.chapterName
                        booklist.add(mBookChapterBean)
                    }
                    Observable.just(booklist)
                };
            }

            override fun getBookContent(book: BookCollectBean?, bookChapterBean: BookChapterBean?, nextChapterBean: BaseChapterBean?): Observable<BookContentBean> {
                val catalog = Catalog()
                catalog.chapterName = bookChapterBean?.durChapterName
                catalog.url = bookChapterBean?.durChapterUrl
                return Observable.create(ObservableOnSubscribe<List<String>> {
                    EasyBook.getContent(curBook, catalog).subscribe(object : Subscriber<List<String>> {
                        override fun onFinish(t: List<String>) {
                            it.onNext(t)
                            it.onComplete()
                        }

                        override fun onError(e: java.lang.Exception) {
                        }

                        override fun onMessage(message: String) {
                        }

                        override fun onProgress(progress: Int) {
                        }
                    });
                }).flatMap { tList ->
                    val mBookContentBean = BookContentBean()
                    mBookContentBean.durChapterContent =createBookContent(tList)
                    mBookContentBean.noteUrl = bookChapterBean?.noteUrl
                    mBookContentBean.durChapterUrl = bookChapterBean?.durChapterUrl
                    mBookContentBean.domain = bookChapterBean?.domain
                    mBookContentBean.setDurChapterIndex(bookChapterBean?.durChapterIndex)
                    mBookContentBean.timeMillis = System.currentTimeMillis()
                    Observable.just(mBookContentBean)
                }

            }
        })
        mPageLoader?.loadChapterList()
    }

    private fun createBookContent(tList: List<String>): String? {
        val sb=StringBuilder()
        for (str in tList){
            sb.append(str+"\n\n")
        }
        return sb.toString()
    }


    private fun searchBook() {
        EasyBook.search("反套路系统")
                .subscribe(object : StepSubscriber<List<Book?>> {
                    override fun onFinish(@NonNull books: List<Book?>) { //所有站点小说爬取完后调用这个方法，传入所有站点解析的有序结果
                        for (mbook in books) {
                            if (mbook?.url?.contains("www.biquge.biz")!!) {
                                curBook = mbook;
                            }
                        }
                        if(curBook==null)return@onFinish
                        val bookCollectBean = BookCollectBean()
                        bookCollectBean.domain = curBook?.url
                        bookCollectBean.noteUrl = curBook?.url
                        bookCollectBean.setFinalDate(System.currentTimeMillis())
                        bookCollectBean.setDurChapter(0)
                        bookCollectBean.setDurChapterPage(0)
                        bookCollectBean.variable = null;
                        bookCollectBean.bookInfoBean.name = curBook?.bookName
                        BookCollectHelp.saveBookToShelf(bookCollectBean)
                        loadPageBook(bookCollectBean);

                    }

                    override fun onError(@NonNull e: Exception) {
                        e.printStackTrace()
                    }

                    override fun onMessage(@NonNull message: String) { //一些搜索中的进度消息，错误原因等，可以用toast弹出
                    }

                    override fun onProgress(progress: Int) { //搜索进度
                    }

                    override fun onPart(@NonNull books: List<Book?>) { //某一个站点的小说搜索结果
                    }
                })
    }

    /////////////////////RxBus////////////////////////
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(ReadAloudService.RxBusTag.MEDIA_BUTTON)])
    fun onMediaButton(command: String?) {
//        if (bookShelf != null) {
//            mView.onMediaButton(command)
//        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(ReadAloudService.RxBusTag.ALOUD_STATE)])
    fun upAloudState(state: ReadAloudService.Status?) {
//        mView.upAloudState(state)
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(ReadAloudService.RxBusTag.ALOUD_TIMER)])
    fun upAloudTimer(timer: String?) {
//        mView.upAloudTimer(timer)
    }


    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(ReadAloudService.RxBusTag.READ_ALOUD_START)])
    fun readAloudStart(start: Int?) {
        if (mPageLoader != null) {
            mPageLoader!!.readAloudStart(start!!)
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(ReadAloudService.RxBusTag.READ_ALOUD_NUMBER)])
    fun readAloudLength(start: Int?) {
        mPageLoader!!.readAloudLength(start!!)
    }

}
