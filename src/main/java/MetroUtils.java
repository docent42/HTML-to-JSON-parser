
class MetroUtils

{
    static Metro create()
    {
        Metro metro = new Metro();
        metro.stations = Starter.station_index;
        metro.connections = Starter.connections;
        metro.lines = Starter.lines;
        return metro;
    }
}
