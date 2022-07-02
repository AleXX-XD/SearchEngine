package SearchEngineApp;

import SearchEngineApp.models.Site;
import SearchEngineApp.models.Status;
import SearchEngineApp.services.SiteService;
import SearchEngineApp.utils.HibernateSessionFactoryUtil;
import SearchEngineApp.utils.IndexPagesUtil;
import SearchEngineApp.utils.ParseSiteUtil;
import SearchEngineApp.utils.SearchTextUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Date;
import java.util.Scanner;

public class Main
{
    /*
    https://www.svetlovka.ru/  http://www.playback.ru/  http://radiomv.ru/  https://et-cetera.ru/mobile/
    https://dombulgakova.ru/
    https://dimonvideo.ru/   - большой
    https://volochek.life/   -
    */

    public static void main(String[] args) {

        try {
            System.out.println("Укажите номер метода :");
            boolean stop = false;
            do {
                System.out.println("1. Создание карты сайта и запись ее в БД\n" +
                        "2. Поиск по запросу\n" +
                        "10 Завершить программу");
                Scanner scanner = new Scanner(System.in);
                String command = scanner.nextLine().trim();

                switch (command) {
                    case ("1"):
                        System.out.println("Введите адрес сайта для начала работы: ");
                        String url = scanner.nextLine().trim();
                            SiteService siteService = new SiteService();
                            Site site;
                            if (siteService.getSite(removeSlash(url)) != null) {
                                site = siteService.getSite(removeSlash(url));
                            } else {
                                site = new Site(removeSlash(url), "Сайт");
                                site.setStatus(Status.INDEXING);
                                site.setStatusTime(new Date());
                                siteService.saveSite(site);
                            }

                            ParseSiteUtil.startParse(site);


                            if (!site.getStatus().equals(Status.FAILED)) {
                                site.setStatus(Status.INDEXED);
                            }
                        break;
                    case ("2"):
                        System.out.println("Введите текст для поиска: ");
                        String text = scanner.nextLine().trim();
                        SearchTextUtil.startSearch(text);
                        break;
                    case ("10"):
                        stop = true;
                        break;
                    default:
                        System.out.println("Введите номер этапа!");
                }
            }
            while (!stop);

            HibernateSessionFactoryUtil.closeSessionFactory();
            System.out.println("******************************************");
            System.out.println("Программа завершена!");
            System.out.println("******************************************");
        } catch (Exception iex) {
            iex.printStackTrace();
        }
    }

    private static String removeSlash (String string) {
        if (string.charAt(string.length()-1) == '/') {
            string = string.substring(0,string.length()-1);
        }
        return string;
    }
}
