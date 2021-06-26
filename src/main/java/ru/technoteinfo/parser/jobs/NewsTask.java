package ru.technoteinfo.parser.jobs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.technoteinfo.parser.Controllers.ConfigController;
import ru.technoteinfo.parser.Entity.News;
import ru.technoteinfo.parser.Services.NewsService;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Component
public class NewsTask {
    public final String imgUrl = "https://img.technoteinfo.ru";

    @Autowired
    private NewsService newsService;


    @Scheduled(fixedDelay = 1000*60*20)
    public void parse(){
        String imgPath = ConfigController.imgDir;

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
                        Document subDoc = null;
                        try {
                            subDoc = Jsoup.connect(domain+"/"+link.attr("href"))
                                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0")
                                    .timeout(50000)
                                    .get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (subDoc == null) {
                            return;
                        }
                        Element box = subDoc.getElementById("article-box");
                        String dateCreate = box.select("h1").select(".meta-data").text();
                        box.select("h1").select(".meta-data").remove();
                        final String title = box.select("h1").eachText().get(0);
                        box.select("h1").remove();
                        box.lastElementSibling().remove();
                        List<Element> nodes = box.children();
                        StringBuilder text = new StringBuilder();
                        for (Element node: nodes) {
                            Elements imgs = node.getElementsByTag("img");
                            if (!imgs.isEmpty()){
                                for (Element img: imgs){
                                    String urlImg = img.attr("src");
                                    String[] parsedUrl = urlImg.split("\\.");
                                    URL website = null;
                                    try {
                                        website = new URL(urlImg);
                                        try (InputStream stream = website.openStream()) {
                                            String filename = generateRandomString(12)+".jpg";
                                            File outputfile = new File(imgPath+filename);
                                            OutputStream outStream = new FileOutputStream(outputfile);
                                            outStream.write(stream.readAllBytes());
                                            img.attr("src", imgUrl+"/parser/"+filename);
                                        }catch (IOException error){
                                            error.printStackTrace();
                                        }
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            text.append(node.toString().trim());
                        }
//                    final String text = ((TextNode) box.childNodes()).text();
                        if (!newsService.isExist(title)){
                            News news = new News(
                                    title,
                                    text.toString(),
                                    LocalDateTime.parse(dateCreate, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                            );
                            newsService.save(news);
                        }
                }
            }
            System.out.println("Конец парсинга");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String generateRandomString(int length) {
        if(length == 0){
            length = 12;
        }
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();

    }
}
