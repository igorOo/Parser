package ru.technoteinfo.parser.Services.Interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.technoteinfo.parser.Entity.News;


import java.util.List;

@Service
public interface NewsServiceInterface {
    public void save(News news);
    public boolean isExist(String name);
    public List<News> getNews(Pageable pageable);
}