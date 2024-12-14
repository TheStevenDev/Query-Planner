import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

public class ScheduledQuery {
    private int IDQuery;
    private static int nextID = 0;

    private String ipAddress, port, user, password, dbName, queryText, type;
    private LocalDate executionDate, startDate, endDate;
    private LocalDateTime nextExecution;
    private boolean loop = false;
    private LocalTime executionTime;
    private int[] weekDays;

    public ScheduledQuery(String ipAddress, String port, String user, String password, String dbName, String queryText, String type, LocalDate executionDate, LocalDate startDate, LocalDate endDate, LocalTime executionTime, int[] weekDays){
        IDQuery = nextID;
        nextID++;

        // connection data
        this.ipAddress = ipAddress;
        this.port=port;
        this.user=user;
        this.password= password;
        this.dbName = dbName;

        //query datas
        this.queryText = queryText;
        this.type = type.toLowerCase();

        //date and time info
        this.executionDate = executionDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.executionTime = executionTime;
        this.weekDays = weekDays;
    }

    public ScheduledQuery(){
        IDQuery = nextID;
        nextID++;
    }

    //useful methods

    public boolean isRepeat(){
        if(this.type.equalsIgnoreCase("repeat")) return true;

        return false;
    }

    //undefined -> it doesn't have an end date
    public boolean isUndefined(){
        if(isRepeat()){
            if (startDate!=null && endDate==null) return true;
        }

        return false;
    }

    //getter & setters

    public int getIDQuery() {
        return IDQuery;
    }

    public void setIDQuery(int IDQuery) {
        this.IDQuery = IDQuery;
    }

    public static int getNextID() {
        return nextID;
    }

    public static void setNextID(int nextID) {
        nextID = nextID;
    }

    public String getIpAdress() {
        return ipAddress;
    }

    public void setIpAdress(String ipAdress) {
        this.ipAddress = ipAdress;
    }
    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDateTime getNextExecution() {
        return nextExecution;
    }

    public LocalDateTime setAndCalcNextExecution() {
        LocalDateTime nextExecutionValue = null;

        if (this.isRepeat()) {
            LocalDate today = LocalDate.now();

            LocalDateTime start = LocalDateTime.of(this.startDate, this.getExecutionTime());
            if (this.startDate.isEqual(today) && this.executionTime.isBefore(LocalTime.now())) start = start.plusDays(1);

            int dayOfWeekZeroBased = (today.getDayOfWeek().getValue() % 7);

            LocalDateTime current = start;

            if(this.endDate == null){
                while (true) {
                    int dayOfWeek = (current.getDayOfWeek().getValue() % 7);


                    if(this.getWeekDays()[dayOfWeek]==1){
                        nextExecutionValue = LocalDateTime.of(current.toLocalDate(), this.getExecutionTime());

                        this.nextExecution = nextExecutionValue;
                        return nextExecutionValue;
                    }
                    else{
                        //increment
                        current = current.plusDays(1);
                    }

                }

            }
            else{
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    int dayOfWeek = (current.getDayOfWeek().getValue() % 7);

                    if(this.getWeekDays()[dayOfWeek]==1){
                        nextExecutionValue = LocalDateTime.of(current.toLocalDate(), this.getExecutionTime());

                        this.nextExecution = nextExecutionValue;
                        return nextExecutionValue;
                    }
                    else{
                        //increment
                        current = current.plusDays(1);
                    }
                }


            }

        }
        else {
            this.nextExecution = LocalDateTime.of(this.getExecutionDate(), this.getExecutionTime());
            return this.nextExecution;
        }

        return null;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalTime executionTime) {
        this.executionTime = executionTime;
    }

    public int[] getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(int[] weekDays) {
        this.weekDays = weekDays;
    }


    //toString method

    @Override
    public String toString() {
        return "ID=" + this.getIDQuery() + ", IPAdress='" + ipAddress + '\'' + ", port='" + port + '\'' + ", user='" + user + '\'' + ", password='" + password + '\'' + ", DBName='" + dbName + '\'' + ", queryText='" + queryText + '\'' + ", type='" + type + '\'' + ", executionDate=" + executionDate + ", startDate=" + startDate + ", endDate=" + endDate + ", executionTime=" + executionTime + ", weekDays=" + Arrays.toString(weekDays);
    }

}
