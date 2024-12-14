import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Messages{

    private static ImageIcon okIcon = new ImageIcon("images/QueryPlanner_Icons/icons8-check-mark-48.png");
    private static ImageIcon errorIcon = new ImageIcon("images/QueryPlanner_Icons/icons8-error-48.png");
    private static ImageIcon quitIcon = new ImageIcon("images/QueryPlanner_Icons/icons8-logout-rounded-50.png");
    private static ImageIcon binIcon = new ImageIcon("images/QueryPlanner_Icons/icons8-bin-52.png");
    private static ImageIcon infoIcon = new ImageIcon("images/QueryPlanner_Icons/icons8-info-48.png");
    private static ImageIcon wifiIcon = new ImageIcon("images/QueryPlanner_Icons/icons8-no-wifi-68.png");


    public static void showOkMessage(Component component,String text, String title){
        JOptionPane.showMessageDialog(component, text,title,JOptionPane.PLAIN_MESSAGE,okIcon);
    }

    public static void showErrorMessage(Component component,String text, String title){
        JOptionPane.showMessageDialog(component, text,title,JOptionPane.PLAIN_MESSAGE,errorIcon);
    }

    public static int showQuitConfirmDialog(Component component){
        return JOptionPane.showConfirmDialog(component,"Are you sure you want to close the program? Unsaved changes will be lost.", "Confirm Program Exit", JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,quitIcon);
    }

    public static int showDeletingQueryConfirmDialog(Component component){
        return JOptionPane.showConfirmDialog(component,"Are you sure you want to delete this query of type 'repeat'? All subsequent queries will not be executed", "Confirm delete query", JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,binIcon);
    }

    public static void showProjectDetails(Component component){
        String message = "The project allows scheduling the execution of server queries, either once or repeatedly, with specified times and days of the week.\n" + "It saves scheduling details in a CSV file, making the process easy to manage.\n" + "You can check the project and its source code on GitHub and learn more through my website.\n\n" + "GitHub: https://github.com/TheStevenDev\n" + "Website: https://stevendamore.it";
        JOptionPane.showMessageDialog(component, message,"About the Project",JOptionPane.PLAIN_MESSAGE,infoIcon);
    }

    public static void showNoInternetMessage(Component component){
        JOptionPane.showMessageDialog(component, "No Internet connection. The program may not function as expected","No Internet Connection",JOptionPane.PLAIN_MESSAGE,wifiIcon);
    }

    public static void showQueryDetails(Component component, ScheduledQuery scheduledQuery){
        String message = "";
        if (scheduledQuery != null) {
            message += "Scheduled Query Details:\n";
            message += "---------------------------------\n";
            message += "ID Query: " + scheduledQuery.getIDQuery() + "\n";
            message += "IP Address: " + scheduledQuery.getIpAdress() + "\n";
            message += "Port: " + scheduledQuery.getPort() + "\n";
            message += "User: " + scheduledQuery.getUser() + "\n";
            message += "Database Name: " + scheduledQuery.getDbName() + "\n";
            message += "Query Text: " + scheduledQuery.getQueryText() + "\n";
            message += "Type: " + scheduledQuery.getType() + "\n";
            message += "Execution Date: " + (scheduledQuery.getExecutionDate() != null ? scheduledQuery.getExecutionDate() : "N/A") + "\n";
            message += "Start Date: " + (scheduledQuery.getStartDate() != null ? scheduledQuery.getStartDate() : "N/A") + "\n";
            message += "End Date: " + (scheduledQuery.getEndDate() != null ? scheduledQuery.getEndDate() : "N/A") + "\n";
            message += "Execution Time: " + (scheduledQuery.getExecutionTime() != null ? scheduledQuery.getExecutionTime() : "N/A") + "\n";
            message += "Week Days: " + (scheduledQuery.getWeekDays() != null ? Arrays.toString(scheduledQuery.getWeekDays()) : "N/A") + "\n";
            message += "Loop: " + (scheduledQuery.isLoop() ? "Enabled" : "Disabled") + "\n";
            message += "---------------------------------\n";
        } else {
            message += "No scheduled query details available.\n";
        }

        JOptionPane.showMessageDialog(component, message,"Query Details",JOptionPane.PLAIN_MESSAGE,infoIcon);
    }




}
