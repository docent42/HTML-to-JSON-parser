public class Connection implements Comparable<Connection>
{
    private String line;
    private String station;

    Connection(String line, String station)
    {
        this.line = line; this.station = station;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    @Override // чтобы добавить объект в трисэт сделал их сравнимыми
    public int compareTo(Connection o) {
        return line.compareTo(o.line);
    }
}
