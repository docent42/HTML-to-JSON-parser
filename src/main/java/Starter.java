import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Starter
{
    public static TreeSet<String> lineNumbers = new TreeSet<>();
    public static TreeMap<String, ArrayList<String>> station_index = new TreeMap<>();
    public static HashSet<TreeSet<Connection>> connections = new HashSet<>();
    public static List<Line> lines = new LinkedList<>();

    public static void main(String[] args)
    {
        try {
            File input = new File("doc\\moscow.html");
            Document document = Jsoup.parse(input, "UTF-8");
            document.select("table[class='standard sortable']").forEach(table -> {
                Elements rows = table.select("tr");
                rows.forEach(row -> {
                    Elements cells = row.select("td");
                    if (cells.size() != 0)
                    {
                        String station = cells.get(1).text();
                        String color = cells.get(0).attr("style").split(":")[1];
                        cells.get(0).select("span[title]").forEach(item ->{
                            String number = item.previousElementSibling().text();
                            String line = item.attr("title");
                            stationFormer(number,station);
                            lineFormer(number,line,color);
                            if (cells.get(3).select("span[title]").size() != 0)
                            {
                                TreeSet<Connection> hub = new TreeSet<>();
                                hub.add(new Connection(number,station));
                                cells.get(3).select("span[title]").forEach(item3 ->{
                                    String number3 = item3.previousElementSibling().text();
                                    System.out.println(item3.attr("title"));
                                    String[] transit = item3.attr("title").split("\\s");
                                    if (transit.length > 2)
                                    {
                                        String transitStation;
                                        if (transit[0].equals("Переход"))
                                            transitStation = (transit.length < 7) ? transit[3] : transit[3] + " " + transit[4];
                                        else
                                            transitStation = (transit.length < 8) ? transit[4] : transit[4] + " " + transit[5];
                                        hub.add(new Connection(number3,transitStation));
                                    }
                                } );
                                connectionsFormer(hub);
                            }

                            //System.out.println(item.previousElementSibling().text() + " " + item.attr("title") + " " + nameStation);
                        });
                    }
                });
            });

        }
        catch (Exception ex) {
            ex.printStackTrace(); }

        station_index.forEach((key,value) ->{
            System.out.println("<"+ key + ">");value.forEach(System.out::println);
        });
        lines.forEach(line ->{
            System.out.printf("%s %s %s%n",line.getNumber(),line.getName(),line.getColor());
        });

        connections.forEach(hub -> { System.out.println("-------------------"+ hub.hashCode());hub.forEach(connection ->
                System.out.println(connection.getLine() + " - " + connection.getStation()));
        });
        gsonTest();
        System.out.println("\nComplete !!!\n");
    }
    public static void stationFormer(String number,String station)
    {
        if (station_index.containsKey(number)) station_index.get(number).add(station);
        else
        {
            station_index.put(number,new ArrayList<>());station_index.get(number).add(station);
        }
    }
    public static void lineFormer(String number,String name,String color)
    {
        if (!lineNumbers.contains(number))
        {
            lineNumbers.add(number);
            lines.add(new Line(number, name, color));
        }
    }
    public static void connectionsFormer(TreeSet<Connection> parsedHub)
    {
        boolean flag = connections.stream().noneMatch(hub ->hub.containsAll(parsedHub));
        if (flag) connections.add(parsedHub);

    }

    public static void gsonTest()
    {
        Metro spbMetro = MetroUtils.create();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(spbMetro);

        try {
            FileWriter file = new FileWriter("doc/test.json");
            file.write(gson.toJson(spbMetro));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(json);
    }

}