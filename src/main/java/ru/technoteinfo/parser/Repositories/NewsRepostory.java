package ru.technoteinfo.parser.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.technoteinfo.parser.Entity.News;

@Repository
public interface NewsRepostory extends JpaRepository<News, Long> {
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM News p WHERE p.name = :name")
    boolean existsByName(@Param("name") String name);
}