package ru.technoteinfo.parser.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;
    public String text;

    public LocalDateTime created_at;

    public News(String name, String text){
        this.name = name;
        this.text = text;
    }
    public News(String name, String text, LocalDateTime dateTime){
        this.name = name;
        this.text = text;
        this.created_at = dateTime;
    }
}