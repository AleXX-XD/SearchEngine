package SearchEngineApp.utils;

import SearchEngineApp.models.WebPage;
import SearchEngineApp.services.WebPageService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;

public class ParseSiteUtil
{
    private static String url;

    private static CopyOnWriteArraySet<String> allUrl = new CopyOnWriteArraySet<>();
    private static CopyOnWriteArraySet<WebPage> pageData= new CopyOnWriteArraySet<>();

    public static void startParse(String urlAdress) {
        url = removeSlash(urlAdress);
        long startTime = System.currentTimeMillis();

        System.out.println(">>> Запись карты сайта в БД");
        WebPage webPage = new WebPage("/");

        mapOfSiteForkJoinPool(webPage);

        long parseTime = System.currentTimeMillis() - startTime;

        WebPageService pageService = new WebPageService();
        for(WebPage page : pageData) {
            pageService.savePage(page);
        }

        long insertParseTime = System.currentTimeMillis() - startTime;

        pageData = new CopyOnWriteArraySet<>();
        allUrl = new CopyOnWriteArraySet<>();

        System.out.println(">>> Парсинг сайта < " + url + "/ > завершен.\n" +
                "Затраченное время на парсинг = " + parseTime / 1000 + " сек\n" +
                "Затраченное время на запись в БД = " + (insertParseTime - parseTime) / 1000 + " сек");
    }

    private static void mapOfSiteForkJoinPool(WebPage webPage) {
        ForkJoinPool pool = new ForkJoinPool();
        WebPageValue mapOfSite = new WebPageValue(webPage);
        pool.execute(mapOfSite);
        pool.shutdown();
        mapOfSite.join();
    }

    protected static synchronized void parsePage(WebPage webPage) {
        try {
            Random random = new Random();
            String numberUserAgent = String.valueOf(random.nextInt());

            Thread.sleep(150);
            Connection.Response response = Jsoup.connect(getFullLink(webPage.getPath()))
                    .userAgent("SearchEngine" + numberUserAgent)
                    .referrer("https://www.google.com/")
                    .timeout(1000000).maxBodySize(0).ignoreHttpErrors(true).followRedirects(true).execute();
            System.out.println(">>> Начат парсинг страницы < " + webPage.getPath() + " > . Количество потоков = " + Thread.activeCount());

            webPage.setCode(response.statusCode());
            Document doc = response.parse();
            webPage.setContent(doc.toString());
            pageData.add(webPage);

            findLinks(webPage, doc);

        } catch (Exception iex) {
            System.out.println("Страница " + url + webPage.getPath() + " НЕ ДОБАВЛЕНА: " + iex.getMessage());
        }
    }

    private static void findLinks(WebPage webPage, Document doc) {
        Elements links = doc.select("a[href~=^" + url + "|^/.+]");
        for (Element link : links) {
            if(!allUrl.contains(getCorrectLink(link.attributes().get("href")))){
                allUrl.add(getCorrectLink(link.attributes().get("href")));
                if(isRelevant(getCorrectLink(link.attributes().get("href")))) {
                    webPage.setUrlList(getCorrectLink(link.attributes().get("href")));
                    System.out.println("Найдена новая ссылка: " + url + getCorrectLink(link.attributes().get("href")));
                }
                else {
                    System.out.println("----- ССЫЛКА НЕ ДОБАВЛЕНА : " + url + getCorrectLink(link.attributes().get("href")));
                }
            }
        }
    }

    private static String getCorrectLink(String link) {
        if (link.startsWith(url)) {
            link = link.replaceFirst(url,"");
        }
        if (link.contains("#")) {
            link = link.substring(0, link.indexOf('#'));
        } else if (link.contains("?")) {
            link = link.substring(0, link.indexOf('?'));
        }
        return link;
    }

    private static String getFullLink(String link) {
        return url + link;
    }

    private static String removeSlash (String string) {
        if (string.charAt(string.length()-1) == '/') {
            string = string.substring(0,string.length()-1);
        }
        return string;
    }

    private static boolean isRelevant (String string) {
        Pattern filter1 = Pattern.compile(".*/\\S+\\.(x?html?|php|jsp)$");
        Pattern filter2 = Pattern.compile(".*/[^.$&?\\s]*$");
        if (filter1.matcher(string).matches() || filter2.matcher(string).matches()) {
            return true;
        }
        else {
            return false;
        }
    }

}