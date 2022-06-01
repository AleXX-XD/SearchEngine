package SearchEngineApp;

import SearchEngineApp.utils.HibernateSessionFactoryUtil;
import SearchEngineApp.utils.ParseSiteUtil;

import java.util.Scanner;

public class Main
{
    /*
    https://www.svetlovka.ru/  http://www.playback.ru/  http://radiomv.ru/  https://et-cetera.ru/mobile/
    https://dimonvideo.ru/   - большой
    https://volochek.life/   - не доступен
    */

    public static void main(String[] args) {

        try {
            System.out.println("Укажите номер метода :");
            boolean stop = false;
            do {
                System.out.println("0. Обнулить все таблицы\n" +
                        "1. Создание карты сайта и запись ее в БД\n" +
                        "10 Завершить программу");
                Scanner scanner = new Scanner(System.in);
                String command = scanner.nextLine().trim();

                switch (command) {
                    case ("0"):
                        System.out.println("ХЗ надо или нет");
                        break;
                    case ("1"):
                        System.out.println("Введите адрес сайта для начала работы: ");
                        String url = scanner.nextLine().trim();
                        ParseSiteUtil.startParse(url);
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
            System.out.println("Ошибка : " + iex.getMessage());
        }

    }
}