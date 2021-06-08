package ru.technoteinfo.parser.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.technoteinfo.parser.Entity.News;
import ru.technoteinfo.parser.Repositories.NewsRepostory;
import ru.technoteinfo.parser.Services.Interfaces.NewsServiceInterface;

import java.util.List;

@Service
public class NewsService implements NewsServiceInterface {
    @Autowired
    private NewsRepostory newsRepostory;

    @Override
    public void save(News news) {
        newsRepostory.save(news);
    }

    @Override
    public boolean isExist(String name) {
        return newsRepostory.existsByName(name);
    }

    @Override
    public List<News> getNews(Pageable page) {
        Page<News> list = newsRepostory.findAll(page);
        return list.getContent();
    }
}
