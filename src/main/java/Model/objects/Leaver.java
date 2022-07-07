package Model.objects;

public class Leaver {

    private String name, discordID, rank;
    private int times, row;

    public Leaver(String name, String discordID, String rank, int times, int row) {
        this.name = name;
        this.discordID = discordID;
        this.rank = rank;
        this.times = times;
        this.row = row;
    }

    public int getRow() {

        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscordID() {
        return discordID;
    }

    public void setDiscordID(String discordID) {
        this.discordID = discordID;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}
