import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Starter
{
    private static TreeSet<String> lineNumbers = new TreeSet<>();// база номеров линий для парсинга линий
    static TreeMap<String, ArrayList<String>> station_index = new TreeMap<>(); // база станций метро для парсинга
    static HashSet<TreeSet<Connection>> connections = new HashSet<>();// база пресадок для парсинга
    static List<Line> lines = new LinkedList<>(); // база линий метро для парсинга

    public static void main(String[] args)
    {
        try {
            File input = new File("doc\\moscow.html");// понеслась душа в рай .....
            Document document = Jsoup.parse(input, "UTF-8");

            // вырезаем из документа все элементы <таблица> атрибут которых содержит:.......
            document.select("table[class='standard sortable']").forEach(table -> {
                Elements rows = table.select("tr"); // получаем каждую строку
                rows.forEach(row -> {
                    Elements cells = row.select("td"); // получаем элементы <ячейки> каждой строки
                    if (cells.size() != 0) // прокладка-защита от заголовка
                    {
                        String station = cells.get(1).text(); // из этой ячейки берем станцию
                        String color = cells.get(0).attr("style").split(":")[1];// это цвет линии

                        // в таблице есть 4 строки, станции в которых сразу приндлежат двум линиям, поэтому цикл
                        cells.get(0).select("span[title]").forEach(item ->{
                            String number = item.previousElementSibling().text();// это номер линии
                            String line = item.attr("title");// это название линии
                            stationFormer(number,station);// отправляем данные для формирования базы станций
                            lineFormer(number,line,color);// а здесь для линий

                            // Марлезонский балет или задание со (*) парсим пересадки
                            if (cells.get(3).select("span[title]").size() != 0)// если в ячейке пересадка есть инфо
                            {
                                TreeSet<Connection> hub = new TreeSet<>();// сюда будем класть пересадки
                                hub.add(new Connection(number,station));// начнем со станции из строки

                                //  ячейки с пересадками:достаем из списка элементов только те, в которых номер линии
                                // и куда ведет переход
                                cells.get(3).select("span[title]").forEach(item3 ->{
                                    String number3 = item3.previousElementSibling().text();// номер линии
                                    String[] transit = item3.attr("title").split("\\s");// режем строку пересадки
                                    if (transit.length > 2) // на сайте ошибка, в двух станциях вместо перехода указана просто линия
                                        // вероятнее всего виноват проклятый копипаст, поэтому такое условие, и эти станции не работают
                                    {
                                        String transitStation;
                                        if (transit[0].equals("Переход")) // если это переход (одно слово / два слова)
                                            transitStation = (transit.length < 7) ? transit[3] : transit[3] + " " + transit[4];
                                        else // если это кросс-платф пересадка (одно слово / два слова)
                                            transitStation = (transit.length < 8) ? transit[4] : transit[4] + " " + transit[5];
                                        hub.add(new Connection(number3,transitStation));// добавляем в список объект связь
                                    }
                                } );
                                connectionsFormer(hub);// проверяем на совпадение и отправляем в базу пересадок
                            }
                        });
                    }
                });
            });
            gsonToJsonFile(); // формируем джейсона и пишем на диск
            MetroUtils.getMetroObjects(); // вторая часть задания получаем объект из джейсона и считаем станции
        }
        catch (Exception ex) {
            ex.printStackTrace(); }

    }
    private static void stationFormer(String number, String station)
    {
        if (station_index.containsKey(number)) station_index.get(number).add(station);
        else
            station_index.put(number,new ArrayList<>());station_index.get(number).add(station);
    }

    private static void lineFormer(String number, String name, String color)
    {
        if (!lineNumbers.contains(number))
        {
            lineNumbers.add(number);
            lines.add(new Line(number, name, color));
        }
    }
    private static void connectionsFormer(TreeSet<Connection> parsedHub)
    {
        boolean flag = connections.stream().noneMatch(hub ->hub.containsAll(parsedHub));
        if (flag) connections.add(parsedHub);
    }

    private static void gsonToJsonFile() throws IOException
    {
        Metro moscowMetro = MetroUtils.create();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        FileWriter file = new FileWriter("doc/test.json");
        file.write(gson.toJson(moscowMetro));
        file.flush(); file.close();

    }

}