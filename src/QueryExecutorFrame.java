import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

public class QueryExecutorFrame extends JFrame implements ActionListener {
    public Planner planner;

    //fonts and colors
    private Font mainFont = new Font("Open Sans, Arial", Font.PLAIN, 18);

    // Logos and icons
    private ImageIcon logo = new ImageIcon("images/QueryPlanner-logo.png");

    //info, delete and exNow
    private ImageIcon infoIcon = new ImageIcon("images/QueryPlanner_Icons/icons8-info-48.png");
    private ImageIcon deleteIcon = new ImageIcon("images/QueryPlanner_Icons/icons8-bin-26.png");
    private ImageIcon ExecuteIcon = new ImageIcon("images/QueryPlanner_Icons/icons8-run-command-48.png");


    //panels
    private JPanel topPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();

    // buttons
    private JButton openPlannerButton;

    // table
    public JTable table = new JTable();

    public QueryExecutorFrame(Planner planner) {
        this.planner = planner;
        this.setTitle("Query-Planner | Query Executor");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = Messages.showQuitConfirmDialog(null);
                if (result ==0) {
                    planner.saveFile();
                    System.exit(0); // close
                }
            }
        });

        this.setSize(900, 700);
        this.setMinimumSize(new Dimension(600, 600));
        this.getContentPane().setBackground(Color.WHITE);
        this.setLocationRelativeTo(null);
        this.setVisible(false);
        this.setLayout(new BorderLayout(6, 3));

        // Set up the panels
        topPanel.setBackground(Color.WHITE);
        topPanel.setPreferredSize(new Dimension(100, 50));
        topPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 0));

        centerPanel.setBackground(Color.WHITE);
        centerPanel.setLayout(new GridLayout(1, 1));
        centerPanel.setPreferredSize(new Dimension(100, 100));

        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setPreferredSize(new Dimension(100, 70));

        // Set the top panel
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(logo);

        JLabel titleLabel = new JLabel("QueryPlanner | Executor");
        Font titleFont = new Font("Open Sans, Arial", Font.PLAIN, 20);

        titleLabel.setFont(titleFont);

        topPanel.add(logoLabel);
        topPanel.add(titleLabel);

        // Create the table
        String[] columnNames = {"ID", "DB", "Query", "Type", "Next Ex.", "Info", "Ex. now", "Delete"};
        Object[][] rowData = getDatas();

        DefaultTableModel model = new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row,int column) {
                return column >= 5; //only buttons are editable
            }
        };

        table = new JTable(model) {
            @Override
            public TableCellRenderer getCellRenderer(int row,int column) {
                if (column >= 5) {
                    return new ButtonRenderer();
                }
                return super.getCellRenderer(row, column);
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column >= 5) {
                    return new ButtonEditor(new JCheckBox(), row);
                }
                return super.getCellEditor(row, column);
            }
        };

        table.setRowHeight(55);
        table.setFont(mainFont);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);


        centerPanel.add(new JScrollPane(table));

        //set the bottom panel
        bottomPanel.setLayout(new FlowLayout());

        this.openPlannerButton = new JButton("Hide Planner");
        this.openPlannerButton.setPreferredSize(new Dimension(150, 50));
        this.openPlannerButton.addActionListener(this);
        this.openPlannerButton.setFont(mainFont);

        bottomPanel.add(openPlannerButton);


        //add components
        this.add(topPanel,BorderLayout.NORTH);
        this.add(centerPanel,BorderLayout.CENTER);
        this.add(bottomPanel,BorderLayout.SOUTH);

        if (!isInternetAvailable()) Messages.showNoInternetMessage(this);
        startThread();
    }


    //execute a query
    private int execute(ScheduledQuery scheduledQuery){
        String url = "jdbc:mysql://"+scheduledQuery.getIpAdress()+":"+scheduledQuery.getPort()+"/"+scheduledQuery.getDbName().trim();
        String user = scheduledQuery.getUser();
        String password = scheduledQuery.getPassword();

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = scheduledQuery.getQueryText();

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                // type INSERT, UPDATE, DELETE
                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected;  //num. of rows
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;  //error
        }
    }


    private boolean isInternetAvailable() {
        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(2000); // Timeout connessione (ms)
            connection.setReadTimeout(2000);    // Timeout lettura (ms)
            connection.connect();

            // HTTP di response
            return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            return false;
        }
    }

    //threads
    public void startThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    this.update();

                    //execute, in case
                    ArrayList<ScheduledQuery> list = planner.scheduledQueries;

                    for (int i = 0; i < list.size(); i++) {
                        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

                        if(list.get(i).getNextExecution() != null){
                            LocalDateTime queryTime = list.get(i).getNextExecution().truncatedTo(ChronoUnit.MINUTES);

                            if (now.equals(queryTime)){
                                if (execute(list.get(i)) > 0){
                                    Messages.showOkMessage(this, list.get(i).getIDQuery() + "executed!", "Query Status");
                                }
                                else{
                                    Messages.showErrorMessage(this, list.get(i).getIDQuery() + " not executed!", "Query Status (Error)");
                                }

                            }
                        }

                    }


                    Thread.sleep(30000); // sleep of 30 seconds

                } catch (InterruptedException e) {
                    Messages.showErrorMessage(this, "Thread stopped! Error!", "Error");
                    break;
                }
            }


        });

        thread.start();
    }

    //update
    public void update(){
        //update table
        String[] columnNames = {"ID", "DB", "Query", "Type", "Next Ex.", "Info", "Ex. now", "Delete"};
        Object[][] rowData = this.getDatas();

        this.table.setModel(new DefaultTableModel(rowData,columnNames));

        table.setRowHeight(55);
        table.setFont(mainFont);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
    }

    public Object[][] getDatas() {
        ArrayList<ScheduledQuery> list = planner.scheduledQueries;

        for (int i = 0; i < list.size(); i++) {
            planner.scheduledQueries.get(i).setAndCalcNextExecution();
            list.get(i).setAndCalcNextExecution();
        }

        Object[][] rowData = new Object[list.size()][8];

        for (int i = 0; i < list.size(); i++) {
            ScheduledQuery s = list.get(i);

            rowData[i][0]= s.getIDQuery();
            rowData[i][1]= s.getDbName();
            rowData[i][2]= s.getQueryText();
            rowData[i][3]= s.getType();

            if(s.setAndCalcNextExecution() !=null) rowData[i][4]=s.setAndCalcNextExecution();
            else rowData[i][4]= "ALERT";

            //checks
        }

        return rowData;
    }


    //messages
    private void executeQuery(ScheduledQuery scheduledQuery){
        ArrayList<ScheduledQuery> list = planner.scheduledQueries;
        for (int i=0; i<list.size();i++){
            if (list.get(i).getIDQuery() == scheduledQuery.getIDQuery()){
                if (execute(scheduledQuery) > 0){
                    Messages.showOkMessage(this, "Query N: " + list.get(i).getIDQuery() + " executed!", "Query Status");

                    //delete
                    planner.scheduledQueries.remove(scheduledQuery);
                    Messages.showOkMessage(this, "Operation completed", "Delete query");
                    update();
                }
                else{
                    Messages.showErrorMessage(this, "Query N: " + list.get(i).getIDQuery() + " not executed!", "Query Status (Error)");
                }


            }
        }



    }

    private void showInfo(ScheduledQuery scheduledQuery) {
        ArrayList<ScheduledQuery> list = planner.scheduledQueries;
        for (int i=0; i<list.size();i++){
            if (list.get(i).getIDQuery() == scheduledQuery.getIDQuery()){
                Messages.showQueryDetails(this, list.get(i));
            }
        }
    }

    private void deleteQuery(ScheduledQuery scheduledQuery) {
        //check repeat and delete

        ArrayList<ScheduledQuery> list = planner.scheduledQueries;
        for (int i=0; i<list.size();i++){
            if (list.get(i).getIDQuery() == scheduledQuery.getIDQuery()){
                if (scheduledQuery.getType().toLowerCase().equals("repeat")){
                    int result = Messages.showDeletingQueryConfirmDialog(this);

                    if (result==0){
                        planner.scheduledQueries.remove(i);
                        Messages.showOkMessage(this, "Operation completed", "Delete query");
                        update();
                    }
                }
                else{
                    planner.scheduledQueries.remove(i);
                    Messages.showOkMessage(this, "Operation completed", "Delete query");
                    update();
                }

            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==openPlannerButton){
            if (planner.isVisible()){
                openPlannerButton.setText("Show Planner");
                planner.setVisible(false);
            }
            else{
                openPlannerButton.setText("Hide the Planner");
                planner.setVisible(true);
            }
        }


    }



    //buttons classes
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setIcon(deleteIcon);

            switch (column) {
                case 5: //info
                    setIcon(infoIcon);
                    break;
                case 6: //ex now
                    setIcon(ExecuteIcon);
                    break;
                case 7: //delete
                    break;
            }
            setText(""); //nothing
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;
        private int column;

        public ButtonEditor(JCheckBox checkBox,int row) {
            super(checkBox);
            this.row = row;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table,Object value, boolean isSelected, int row, int column) {
            this.row = row;
            this.column = column;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                ScheduledQuery scheduledQuery = planner.scheduledQueries.get(row);
                switch (column) {
                    case 5: //info
                        showInfo(scheduledQuery);
                        break;
                    case 6: //ex now
                        executeQuery(scheduledQuery);
                        break;
                    case 7: //delete
                        deleteQuery(scheduledQuery);
                        break;
                }
            }
            isPushed = false;
            return null;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {

            try {
                super.fireEditingStopped();
            }
            catch (Exception e){

            }

        }


    }
}