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
    private static TreeSet<String> lineNumbers = new TreeSet<>();
    static TreeMap<String, ArrayList<String>> station_index = new TreeMap<>();
    static HashSet<TreeSet<Connection>> connections = new HashSet<>();
    static List<Line> lines = new LinkedList<>();

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
                        });
                    }
                });
            });
            gsonToJsonFile();
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
        Metro spbMetro = MetroUtils.create();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        FileWriter file = new FileWriter("doc/test.json");
        file.write(gson.toJson(spbMetro));
        file.flush(); file.close();

    }

}