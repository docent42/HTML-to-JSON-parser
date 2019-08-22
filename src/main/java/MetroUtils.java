import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class MetroUtils
{
    private static String dataFile = "doc/test.json";

    static Metro create()
    {
        Metro metro = new Metro();
        metro.stations = Starter.station_index;
        metro.connections = Starter.connections;
        metro.lines = Starter.lines;
        return metro;
    }

    static void getMetroObjects()
    {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(dataFile));
            lines.forEach(builder::append);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        Metro metroTest = new Gson().fromJson(builder.toString(), Metro.class);
        metroTest.stations.forEach((line,list)->
            System.out.printf("Линия <%s> количество станций: %s%n",line,list.size()));
    }
}
