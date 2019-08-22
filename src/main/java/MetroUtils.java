import java.util.LinkedList;
import java.util.List;

public class MetroUtils
{
    public static Metro create()
    {
        //================ stations ===========================================
        Metro metro = new Metro();
        metro.stations = Starter.station_index;
        metro.connections = Starter.connections;
        metro.lines = Starter.lines;
        return metro;
    }
}
