package ru.technoteinfo.parser.Controllers;

import org.ini4j.Ini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Controller
public class ConfigController {
    public static String imgDir;

    private ApplicationContext appContext;

    public ConfigController(ApplicationContext appContext){
        Ini ini = new Ini();
        File file = new File("./config.ini");
        if (file.exists()){
            try(FileReader fileReader = new FileReader(file)) {
                ini.load(fileReader);
                imgDir = ini.get("path", "imgPath");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                FileWriter fileWriter = new FileWriter(file);
                ini.put("path", "imgPath", "/tmp");
                ini.store(file);
            }catch (IOException e){
                e.printStackTrace();
            }
            System.out.println("Файл настроек создан. Необходимо его заполнить правильными значениями");
            SpringApplication.exit(appContext, ()->0);

        }


    }
}
