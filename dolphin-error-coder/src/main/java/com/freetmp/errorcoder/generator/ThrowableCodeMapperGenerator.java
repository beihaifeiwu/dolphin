package com.freetmp.errorcoder.generator;

import com.freetmp.common.util.Assert;
import com.freetmp.common.util.FileCopyUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by LiuPin on 2015/4/22.
 */
public class ThrowableCodeMapperGenerator {

    private static final Logger log = LoggerFactory.getLogger(ThrowableCodeMapperGenerator.class);

    private AtomicLong codeCounter;

    protected void handleSingleClass(Element element, Map<String, Long> map) {
        StringBuilder sb = new StringBuilder();
        sb.append(element.childNode(0).toString())
                .append(element.child(0).child(0).text());
        map.put(sb.toString(), codeCounter.getAndIncrement());
    }

    protected void handleSection(Element li,Map<String, Long> map) {
        handleSingleClass(li,map);
        long delta = 1000 - codeCounter.get() % 1000;
        codeCounter.addAndGet(delta);
        li.select("ul > li").forEach(element -> handleSection(element, map));
    }

    protected String translateReadResult(Map<String, Long> map){
        StringBuilder sb = new StringBuilder();
        map.forEach((key, value) -> sb.append(key).append("=").append(String.format("%1$09d", value)).append("\n"));
        map.clear();
        return sb.toString();
    }

    public void writeJavaSourceFile(String content, String filename) throws IOException {
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + "\\dolphin-error-coder\\src\\main\\resources\\code\\" + filename + ".properties";
        FileWriter writer = new FileWriter(filePath, false);
        FileCopyUtils.copy(content, writer);
    }

    public void parseFromJavadoc(String url, String csspath, String filename) throws IOException {
        Assert.hasText(url);
        Assert.hasText(csspath);
        Assert.hasText(filename);

        Map<String, Long> map = parseFromJavadoc(url, csspath);
        writeJavaSourceFile(translateReadResult(map), filename);
    }

    protected Map<String, Long> parseFromJavadoc(String url, String csspath) throws IOException {
        Map<String, Long> map = new LinkedHashMap<>();
        log.info("Start parsing from {}", url);

        Document doc = null;
        long count = 0;
        while (doc == null && ++count <= 3) {
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                log.warn("Cannot parse from specified url, will be try {}th time",count);
            }
        }
        if(doc != null) {
            Elements elements = doc.select(csspath);
            handleSection(elements.first(), map);
        }else {
            log.warn("Skip parsing from {}",url);
        }
        log.info("Finish parsed from {}",url);
        return map;
    }

    public void parseFromJavadoc(String[] urls, String[] csspaths, String filename) throws IOException {
        Assert.notEmpty(urls);
        Assert.notEmpty(csspaths);
        Assert.hasText(filename);
        Assert.isTrue(urls.length == csspaths.length);

        Map<String, Long> map = new LinkedHashMap<>();
        for(int i = 0; i < urls.length; i++){
            parseFromJavadoc(urls[i],csspaths[i]).forEach(map::putIfAbsent);
        }
        writeJavaSourceFile(translateReadResult(map), filename);
    }

    public ThrowableCodeMapperGenerator(long start){
        if(start < 100000000){
          codeCounter = new AtomicLong(100000000);
        }
    }

    public ThrowableCodeMapperGenerator(){
        codeCounter = new AtomicLong(100000000);
    }

    public static void main(String[] args) throws IOException {

        ThrowableCodeMapperGenerator codeGenerator = new ThrowableCodeMapperGenerator();

        codeGenerator.parseFromJavadoc("https://docs.oracle.com/javase/8/docs/api/overview-tree.html",
                "body > div.contentContainer > ul:nth-child(2) > li > ul > li:nth-child(1257)",
                "jdk_code");
        System.out.println("Done with jdk");
        codeGenerator.parseFromJavadoc("http://docs.spring.io/spring/docs/current/javadoc-api/overview-tree.html",
                "body > div.contentContainer > ul:nth-child(2) > li > ul > li:nth-child(1071)",
                "spring_code");
        System.out.println("Done with spring framework");

        codeGenerator.parseFromJavadoc(new String[]{
            "http://docs.spring.io/spring-boot/docs/current/api/overview-tree.html"
        },new String[]{
            "body > div.contentContainer > ul:nth-child(2) > li > ul > li:nth-child(388)"
        },"spring_others");
    }
}