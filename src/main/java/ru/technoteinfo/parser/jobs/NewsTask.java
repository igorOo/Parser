package ru.technoteinfo.parser.jobs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.technoteinfo.parser.Entity.News;
import ru.technoteinfo.parser.Services.NewsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Component
public class NewsTask {
    @Autowired
    private NewsService newsService;


    @Scheduled(fixedDelay = 10000)
    public void parse(){
        System.out.println("Начало парсинга");

        String domain = "https://pcnews.ru";
        String url = "/news.html";
        try {
            Document doc;
            URLConnection con = new URL(domain+url).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder html = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                html.append(line);
            }


            doc = Jsoup.parse(html.toString());

            Elements elements = doc.select(".title");
            for (Element element: elements ) {
                Elements links = element.getElementsByTag("a");
                for (Element link: links){
                    Document subDoc = Jsoup.connect(domain+"/"+link.attr("href"))
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0")
                            .timeout(10000)
                            .get();
                    Element box = subDoc.getElementById("article-box");
                    final String title = box.select("h1").text();
                    box.select("h1").remove();
                    box.lastElementSibling().remove();
                    List<Node> nodes = box.childNodes();
                    StringBuilder text = new StringBuilder();
                    for (Node node: nodes) {
                        text.append(node.toString());
                    }
//                    final String text = ((TextNode) box.childNodes()).text();
                    if (!newsService.isExist(title)){
                        News news = new News(title, text.toString());
                        newsService.save(news);
                    }
                }
            }
            System.out.println("Конец парсинга");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
