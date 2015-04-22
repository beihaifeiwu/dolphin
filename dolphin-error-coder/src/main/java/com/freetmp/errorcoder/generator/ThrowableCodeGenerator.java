package com.freetmp.errorcoder.generator;

import com.freetmp.common.util.FileCopyUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by LiuPin on 2015/4/22.
 */
public class ThrowableCodeGenerator {

    private static AtomicLong codeCounter = new AtomicLong(100000000);

    public static void handleSingleClass(Element element, Map<String, Long> map) {
        StringBuilder sb = new StringBuilder();
        sb.append(element.childNode(0).toString())
                .append(element.child(0).child(0).text());
        map.put(sb.toString(), codeCounter.getAndIncrement());
    }

    public static void handleSection(Element li,Map<String, Long> map) {
        handleSingleClass(li,map);
        codeCounter.addAndGet(1000L);
        li.select("ul > li").forEach(element -> handleSection(element, map));
    }

    public static String translateReadResult(Map<String, Long> map){
        StringBuilder sb = new StringBuilder();
        map.forEach((key, value) -> sb.append(key).append("=").append(String.format("%1$09d", value)).append("\n"));
        map.clear();
        return sb.toString();
    }

    public static void parseFromJavadoc(String url, String csspath, String filename) throws IOException {
        Map<String, Long> map = new LinkedHashMap<>();
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(csspath);
        handleSection(elements.first(),map);
        writeAsPropertyFile(translateReadResult(map), filename);
    }

    public static void writeAsPropertyFile(String content, String filename) throws IOException {
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + "\\dolphin-error-coder\\src\\main\\resources\\code\\" + filename + ".properties";
        FileWriter writer = new FileWriter(filePath, false);
        FileCopyUtils.copy(content, writer);
    }

    public static void main(String[] args) throws IOException {

        parseFromJavadoc("https://docs.oracle.com/javase/8/docs/api/overview-tree.html",
                "body > div.contentContainer > ul:nth-child(2) > li > ul > li:nth-child(1257)",
                "jdk_code");
        System.out.println("Done with jdk");
        parseFromJavadoc("http://docs.spring.io/spring/docs/current/javadoc-api/overview-tree.html",
                "body > div.contentContainer > ul:nth-child(2) > li > ul > li:nth-child(1071)",
                "spring_code");
        System.out.println("Done with spring framework");
    }
}
