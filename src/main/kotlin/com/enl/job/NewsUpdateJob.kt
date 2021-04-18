package com.enl.job

import com.enl.FundBot
import com.enl.news.News
import com.enl.news.SinaNews
import com.enl.news.Tag
import com.google.gson.Gson
import org.quartz.JobExecutionContext
import java.io.File
import java.net.URL

class NewsUpdateJob : BaseJob() {
    override fun execute(context: JobExecutionContext?) {
        super.execute(context)
        val news = getNonPublishedNews(getFocusedNews())
        if (news.isNullOrEmpty()) {
            logger.debug("No news to publish, return")
            return
        }
        val bot = FundBot()
        news.forEach {
            bot.sendMessage("${it.create_time} ${it.rich_text}")
        }
        logger.info("Published ${news.size} news")
    }

    private fun getFocusedNews(): List<News> {
        // Filter A stock only, add &tag_id=10 param
        val reader = URL("http://zhibo.sina.com.cn/api/zhibo/feed?&page=%251&page_size=10&zhibo_id=152").openStream()
            .bufferedReader()
        val allRecentNewsInfo = Gson().fromJson(reader, SinaNews::class.java)
        return allRecentNewsInfo.result.data.feed.list.filter { news ->
            // 9 is a magic number for highlights
            news.tag.contains(Tag(id = "9", ""))
        }
    }

    private fun getNonPublishedNews(news: List<News>): List<News> {
        val database = File("data.txt")
        if (!database.exists()) {
            logger.info("No data found, create a new one")
            database.createNewFile()
        }
        val publishedNews = database.readLines()
        val nonPublishedNews = news.filter {
            !publishedNews.contains(it.docurl)
        }
        nonPublishedNews.forEach {
            database.appendText(it.docurl + "\n")
        }
        return nonPublishedNews
    }
}