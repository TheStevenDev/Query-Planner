import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Planner extends JFrame implements ActionListener {

    //database file
    File databaseFile;
    ArrayList<ScheduledQuery> scheduledQueries = new ArrayList<>();
    //menu-bar
    private JMenuBar menuBar = new JMenuBar();

    //file menu
    private JMenu fileMenu = new JMenu("File");
    private JMenuItem importQueryItem = new JMenuItem("Import query from a file");
    private JMenuItem exportQueryItem = new JMenuItem("Export query in a file");

    //actions menu
    private JMenu actionsMenu = new JMenu("Actions");
    private JMenuItem showQE = new JMenuItem("Show The Q.E");
    private JMenuItem editQueryItem = new JMenuItem("Edit the Query");
    private JMenuItem quitItem = new JMenuItem("Quit");

    //help-menu
    private JMenu helpMenu = new JMenu("Help");
    private JMenuItem aboutTheProjectItem = new JMenuItem("About the Project");
    private JMenuItem supportMeItem = new JMenuItem("Support me!");

    //panels
    private JPanel topPanel = new JPanel();
    private JPanel leftPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();

    //query settings panel
    private JPanel querySettingsPanel;

    //static query datas
    private JTextField exTimeField;
    private JTextField exDateField;

    //repeat query datas
    private JTextField fromDateRepeat;
    private JTextField toDateRepeat;
    private JCheckBox noEndButtonRepeat = new JCheckBox();
    private JCheckBox[] daysOfTheWeekRepeat = new JCheckBox[7]; // 0 = sunday 6 = sat
    private JTextField exTimeFieldRepeat;

    //JTextArea
    JTextArea queryArea;

    //Fields
    private JTextField ipAddressField;
    private JTextField portField;
    private JTextField usernameField;
    private JTextField dbNameField;
    private JPasswordField passwordField;


    //Buttons
    private JButton editQueryButton;
    private JButton addToQeButton;
    private JButton showQeButton;
    private JButton modeChangerButton = new JButton();


    //fonts and colors
    private Font mainFont = new Font("Open Sans, Arial", Font.PLAIN, 18);

    //logos and icons
    private ImageIcon logo = new ImageIcon("images/QueryPlanner-logo.png");

    //mode
    private JComboBox<String> comboBoxMode;
    private String[] modes = {"Single","Repeat"};
    private String mode = "Single";

    //Q.E
    private QueryExecutorFrame queryExecutorFrame;

    public Planner(){
        this.databaseFile  = new File("datas/queryDatasFile.qpl");

        this.setTitle("Query-Planner | Homepage");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = Messages.showQuitConfirmDialog(null);
                if (result == 0) {
                    saveFile();
                    System.exit(0); // close
                }

            }
        });

        this.setSize(900,700);
        this.setMinimumSize(new Dimension(600,600));
        this.getContentPane().setBackground(Color.WHITE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setLayout(new BorderLayout(6,3));


        //read file
        this.readDB();

        //open Q.E
        SwingUtilities.invokeLater(() -> {
            queryExecutorFrame = new QueryExecutorFrame(this);
            queryExecutorFrame.setVisible(false);
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {e1.printStackTrace();}


        //set up the panels
        topPanel.setBackground(Color.WHITE);
        topPanel.setPreferredSize(new Dimension(100,50));
        topPanel.setLayout(new FlowLayout(FlowLayout.LEADING,5,0));

        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(180,100));

        centerPanel.setBackground(Color.WHITE);
        centerPanel.setLayout(new BorderLayout(6,3));
        centerPanel.setPreferredSize(new Dimension(100,100));

        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setPreferredSize(new Dimension(100,70));

        //filling the panels
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(logo);

        JLabel titleLabel = new JLabel("QueryPlanner");
        Font titleFont = new Font("Open Sans, Arial", Font.PLAIN, 20);

        titleLabel.setFont(titleFont);
        topPanel.add(logoLabel);
        topPanel.add(titleLabel);

        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(new LineBorder(Color.BLACK, 2));

        //borderLayout left and south panels
        JPanel topPanelLeft = new JPanel();
        topPanelLeft.setLayout(new FlowLayout(FlowLayout.CENTER));

        JPanel southPanelLeft = new JPanel();
        southPanelLeft.setLayout(new FlowLayout(FlowLayout.CENTER));
        southPanelLeft.setPreferredSize(new Dimension(200,80));

        //ip adress
        JLabel ipAddressLabel = new JLabel("IP Address:");
        ipAddressField = new JTextField("localhost");
        ipAddressField.setPreferredSize(new Dimension(150,20));
        topPanelLeft.add(ipAddressLabel);
        topPanelLeft.add(ipAddressField);

        //port
        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField("3306");
        portField.setPreferredSize(new Dimension(150,20));
        topPanelLeft.add(portLabel);
        topPanelLeft.add(portField);

        //username & password
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField("root");
        usernameField.setPreferredSize(new Dimension(150,20));
        topPanelLeft.add(usernameLabel);
        topPanelLeft.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(150,20));

        topPanelLeft.setBackground(Color.WHITE);
        topPanelLeft.add(passwordLabel);
        topPanelLeft.add(passwordField);

        //DB name
        JLabel dbNameLabel = new JLabel("Database name:");
        dbNameField = new JTextField();
        dbNameField.setPreferredSize(new Dimension(150,20));
        topPanelLeft.add(dbNameLabel);
        topPanelLeft.add(dbNameField);

        //mode selector
        JLabel modeTextLabel = new JLabel("Mode:");
        modeTextLabel.setFont(new Font("Open Sans, Arial", Font.BOLD, 18));
        southPanelLeft.add(modeTextLabel);

        comboBoxMode = new JComboBox<>(modes);
        comboBoxMode.addActionListener(this);

        modeChangerButton.setText("Change Mode");
        modeChangerButton.setPreferredSize(new Dimension(120,30));
        modeChangerButton.setForeground(Color.BLACK);
        modeChangerButton.addActionListener(this);

        southPanelLeft.add(comboBoxMode);
        southPanelLeft.add(modeChangerButton);
        southPanelLeft.setBackground(Color.WHITE);

        leftPanel.add(topPanelLeft,BorderLayout.CENTER);
        leftPanel.add(southPanelLeft,BorderLayout.SOUTH);

        //set the center panel
        JPanel queryBodyPanel = new JPanel();
        queryBodyPanel.setLayout(new BorderLayout(3,3));
        queryBodyPanel.setBackground(Color.WHITE);
        queryBodyPanel.setPreferredSize(new Dimension(100, 230));

        JLabel queryTextLabel =new JLabel("Query text:");
        queryTextLabel.setFont(mainFont);

        queryArea = new JTextArea("");
        queryArea.setBorder(new LineBorder(Color.BLACK, 2));


        //edit button
        editQueryButton = new JButton();
        editQueryButton.setSize(new Dimension(100,100));
        editQueryButton.setText("Edit");
        editQueryButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(editQueryButton);


        queryBodyPanel.add(queryTextLabel,BorderLayout.NORTH);
        queryBodyPanel.add(queryArea,BorderLayout.CENTER);
        queryBodyPanel.add(buttonPanel,BorderLayout.SOUTH);


        //query settings panel
        querySettingsPanel = new JPanel();
        querySettingsPanel.setBackground(Color.WHITE);
        querySettingsPanel.setBorder(new LineBorder(Color.BLACK, 3));
        querySettingsPanel.setLayout(new BoxLayout(querySettingsPanel, BoxLayout.PAGE_AXIS));

        this.setStaticQuery();

        centerPanel.add(queryBodyPanel,BorderLayout.NORTH);
        centerPanel.add(querySettingsPanel,BorderLayout.CENTER);

        //menu bar
        importQueryItem.addActionListener(this);
        fileMenu.add(importQueryItem);
        exportQueryItem.addActionListener(this);
        fileMenu.add(exportQueryItem);

        showQE.addActionListener(this);
        actionsMenu.add(showQE);
        editQueryItem.addActionListener(this);
        actionsMenu.add(editQueryItem);
        quitItem.addActionListener(this);
        actionsMenu.add(quitItem);


        aboutTheProjectItem.addActionListener(this);
        helpMenu.add(aboutTheProjectItem);
        supportMeItem.addActionListener(this);
        helpMenu.add(supportMeItem);

        menuBar.add(fileMenu);
        menuBar.add(actionsMenu);
        menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);

        //set the bottom panel
        bottomPanel.setLayout(new FlowLayout());

        this.showQeButton = new JButton("Show the Q.E.");
        this.showQeButton.setPreferredSize(new Dimension(150,50));
        this.showQeButton.addActionListener(this);
        this.showQeButton.setFont(mainFont);


        this.addToQeButton = new JButton("Add query to the Q.E.");
        this.addToQeButton.setPreferredSize(new Dimension(200,50));
        this.addToQeButton.addActionListener(this);
        this.addToQeButton.setFont(mainFont);


        bottomPanel.add(showQeButton);
        bottomPanel.add(addToQeButton);


        //add components
        this.add(topPanel,BorderLayout.NORTH);
        this.add(leftPanel,BorderLayout.WEST);
        this.add(centerPanel,BorderLayout.CENTER);
        this.add(bottomPanel,BorderLayout.SOUTH);

    }


    public void saveFile(){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(databaseFile));

            for (ScheduledQuery s : scheduledQueries){
                String days="";
                if(s.getWeekDays()!=null){
                    for (int i=0; i<s.getWeekDays().length;i++){
                        if (s.getWeekDays()[i]==1) days += "1";
                        else days += "0";

                        if(i<6) days+=",";
                    }
                }
                else{
                    days = null;
                }

                writer.write(s.getIDQuery() +"|"+ s.getIpAdress()+"|"+s.getPort()+"|"+s.getUser()+"|"+s.getPassword()+"|"+s.getDbName()+"|"+s.getQueryText()+"|"+s.getType()+"|"+s.getExecutionDate()+"|"+s.getStartDate()+"|"+s.getEndDate()+"|"+s.getExecutionTime()+"|"+days+"\n");
            }
            writer.close();


        } catch (Exception ex) {throw new RuntimeException(ex);}
    }


    public void setStaticQuery(){
        //exec date
        JPanel exDatePanel = new JPanel();
        exDatePanel.setBackground(Color.WHITE);
        exDatePanel.setPreferredSize(new Dimension(200,-150));

        JLabel exDateLabel = new JLabel("Execution date: (MM/dd/yyyy format)");
        exDateLabel.setFont(mainFont);
        this.exDateField = new JTextField();

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String date = formatter.format(new Date());
        this.exDateField.setText(date);
        this.exDateField.setPreferredSize(new Dimension(100, 25));


        exDatePanel.add(exDateLabel);
        exDatePanel.add(this.exDateField);

        //exec time
        JPanel exTimePanel = new JPanel();
        exTimePanel.setBackground(Color.WHITE);


        JLabel exTimeLabel = new JLabel("Execution time (24h format): ");
        exTimeLabel.setFont(mainFont);
        this.exTimeField = new JTextField();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String currentTime = simpleDateFormat.format(new Date());

        this.exTimeField.setText(currentTime);
        this.exTimeField.setPreferredSize(new Dimension(100, 25));

        exTimePanel.add(exTimeLabel);
        exTimePanel.add(this.exTimeField);

        querySettingsPanel.add(exDatePanel);
        querySettingsPanel.add(exTimePanel);

    }

    //repeat mode query settings
    public void setRepeatQuery(){
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(200,-130));
        topPanel.setBackground(Color.WHITE);

        //from-to
        JLabel fromLabel = new JLabel("From (MM/dd/yyyy format): ");
        fromLabel.setFont(mainFont);

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String date = formatter.format(new Date());


        this.fromDateRepeat = new JTextField();
        this.fromDateRepeat.setText(date);
        this.fromDateRepeat.setPreferredSize(new Dimension(100, 25));

        JLabel toLabel = new JLabel("To: ");
        toLabel.setFont(mainFont);

        this.toDateRepeat = new JTextField();
        this.toDateRepeat.setText(date);
        this.toDateRepeat.setPreferredSize(new Dimension(100, 25));

        this.noEndButtonRepeat = new JCheckBox("No-end");
        this.noEndButtonRepeat.setPreferredSize(new Dimension(95,25));
        noEndButtonRepeat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(noEndButtonRepeat.isSelected()){
                    toDateRepeat.setText("");
                    toDateRepeat.setEditable(false);
                    toDateRepeat.setBorder(new LineBorder(Color.RED, 1));


                }
                if(! noEndButtonRepeat.isSelected()){
                    toDateRepeat.setEditable(true);
                    toDateRepeat.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                    toDateRepeat.setText(formatter.format(new Date()));
                }


            }
        });


        topPanel.add(fromLabel);
        topPanel.add(fromDateRepeat);
        topPanel.add(toLabel);
        topPanel.add(toDateRepeat);
        topPanel.add(noEndButtonRepeat);

        //days panel
        JPanel midPanel = new JPanel(new FlowLayout());
        midPanel.setPreferredSize(new Dimension(200,-120));
        midPanel.setBackground(Color.WHITE);

        for (int i =0; i<7;i++){
            daysOfTheWeekRepeat[i] = new JCheckBox();
            daysOfTheWeekRepeat[i].setSelected(true);
        }

        //days of the week
        daysOfTheWeekRepeat[0].setText("Sun");
        daysOfTheWeekRepeat[1].setText("Mon");
        daysOfTheWeekRepeat[2].setText("Tue");
        daysOfTheWeekRepeat[3].setText("Wed");
        daysOfTheWeekRepeat[4].setText("Thu");
        daysOfTheWeekRepeat[5].setText("Fri");
        daysOfTheWeekRepeat[6].setText("Sat");

        for (int i =0; i<7;i++){
            midPanel.add(daysOfTheWeekRepeat[i]);
        }

        //bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(200,-120));
        bottomPanel.setBackground(Color.WHITE);

        JLabel exTimeLabel = new JLabel("Execution time (24h format):");
        exTimeLabel.setFont(mainFont);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String currentTime = simpleDateFormat.format(new Date());

        this.exTimeFieldRepeat = new JTextField(currentTime);
        this.exTimeFieldRepeat.setPreferredSize(new Dimension(100, 25));

        bottomPanel.add(exTimeLabel);
        bottomPanel.add(this.exTimeFieldRepeat);


        querySettingsPanel.add(topPanel);
        querySettingsPanel.add(midPanel);
        querySettingsPanel.add(bottomPanel);

    }


    public void clearPanel(JPanel selectedPanel){
        Component[] list = selectedPanel.getComponents();
        for (Component component: list) selectedPanel.remove(component);

        selectedPanel.revalidate();
        selectedPanel.repaint();
    }

    //read the DB at the start
    public void readDB(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(databaseFile));

            String data="";
            String[] datas;

            while ( (data=reader.readLine()) !=null){
                datas = data.split("\\|");

                ScheduledQuery query = new ScheduledQuery();
                query.setIDQuery(Integer.parseInt(datas[0])); //set ID


                if(!datas[12].equals("null")){
                    String[] total = datas[12].split(",");
                    int[] days = new int[7];
                    for (int i=0; i<days.length;i++){
                        days[i] = Integer.parseInt(total[i]);
                    }

                    query.setWeekDays(days);
                }
                else query.setWeekDays(null);

                query.setIpAdress(datas[1]);
                query.setPort(datas[2]);
                query.setUser(datas[3]);
                query.setPassword(datas[4]);
                query.setDbName(datas[5]);
                query.setQueryText(datas[6]);
                query.setType(datas[7]);

                //from 8 to 11
                try {
                    query.setExecutionDate(LocalDate.parse(datas[8]));
                }catch (Exception e){query.setExecutionDate(null);}
                try {
                    query.setStartDate(LocalDate.parse(datas[9]));
                }catch (Exception e){query.setStartDate(null);}
                try {
                    query.setEndDate(LocalDate.parse(datas[10]));
                }catch (Exception e){query.setEndDate(null);}
                try {
                    query.setExecutionTime(LocalTime.parse(datas[11]));
                }catch (Exception e){query.setExecutionTime(null);}


                if( ((!query.isRepeat()) && query.getExecutionDate().isBefore(LocalDate.now())) || (query.isRepeat() && query.getEndDate()!=null && query.getEndDate().isBefore(LocalDate.now()))){

                }
                else{
                    scheduledQueries.add(query);
                }


            }


        } catch (IOException e) { throw new RuntimeException(e);}



    }



    @Override
    public void actionPerformed(ActionEvent e) {

        // mode
        if(e.getSource() == modeChangerButton){
            if(comboBoxMode.getSelectedIndex()==0){
                clearPanel(querySettingsPanel);
                mode = "Single";
                setStaticQuery();
            }
            if(comboBoxMode.getSelectedIndex()==1){
                clearPanel(querySettingsPanel);
                mode = "Repeat";
                setRepeatQuery();
            }
        }


        //Send a query
        if(e.getSource() == addToQeButton){
            boolean flagCorrect = true;
            ScheduledQuery queryToSend = new ScheduledQuery();

            String ipAddress = ipAddressField.getText();
            String portNumber = portField.getText();
            String password = String.valueOf(passwordField.getPassword());

            //incorrect datas
            if( (!Validator.vaildIpAdress(ipAddress)) || (!Validator.vaildPort(portNumber)) || dbNameField.getText().trim().equals("") || usernameField.getText().trim().equals("") || queryArea.getText().trim().equals("")) flagCorrect = false;

            if (flagCorrect){
                //set the ID
                if(scheduledQueries.size()!=0)queryToSend.setIDQuery(scheduledQueries.get(scheduledQueries.size()-1).getIDQuery()+1);

                //connection datas
                queryToSend.setIpAdress(ipAddress);
                queryToSend.setPort(portNumber);
                queryToSend.setUser(usernameField.getText());
                queryToSend.setPassword(password);
                queryToSend.setDbName(dbNameField.getText());


                //set up query
                if(mode.equals("Single")){
                    queryToSend.setType("Single");

                    //date and time
                    String dateString = exDateField.getText();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate date = null;

                    try {
                        date = LocalDate.parse(dateString, formatter);
                        if (date.isBefore(LocalDate.now())) flagCorrect = false;

                    } catch (Exception e1) {
                        flagCorrect = false;
                    }


                    String timeString = exTimeField.getText();
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime time = null;
                    try {
                        time = LocalTime.parse(timeString, timeFormatter);

                        if ((time.isBefore(LocalTime.now()) || time.equals(LocalTime.now())) && date.isEqual(LocalDate.now())) {
                            flagCorrect = false;
                        }
                    } catch (Exception e1) {
                        flagCorrect = false;
                    }


                    String text = queryArea.getText().replace("\n","").strip();


                    queryToSend.setQueryText(text);
                    queryToSend.setExecutionDate(date);
                    queryToSend.setExecutionTime(time);

                }


                if (mode.equals("Repeat")){
                    queryToSend.setType("Repeat");

                    if(noEndButtonRepeat.isSelected()) queryToSend.setLoop(true);

                    //date and time
                    String dateString = fromDateRepeat.getText();
                    String dateStringTo = null;
                    if (!queryToSend.isLoop())  dateStringTo = toDateRepeat.getText();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate fromDate= null;
                    LocalDate toDate = null;

                    try {
                        fromDate = LocalDate.parse(dateString, formatter);
                        if (fromDate.isBefore(LocalDate.now())) flagCorrect = false;

                        if (!queryToSend.isLoop()){
                            toDate = LocalDate.parse(dateStringTo, formatter);

                            if (toDate.isBefore(LocalDate.now())) flagCorrect = false;
                            if (toDate.isBefore(fromDate)) flagCorrect = false;
                        }


                    } catch (Exception e1) {
                        flagCorrect = false;
                    }

                    //time ex
                    String timeString = exTimeFieldRepeat.getText();
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime time = null;
                    try {
                        time = LocalTime.parse(timeString, timeFormatter);

                    } catch (Exception e1) {
                        flagCorrect = false;
                    }


                    int [] weekDaysRepeat = new int[7];
                    int count=0;
                    for (int i=0; i<daysOfTheWeekRepeat.length;i++){
                        if (daysOfTheWeekRepeat[i].isSelected()){
                            weekDaysRepeat[i] = 1;
                            count++;
                        }
                    }

                    if (count==0) flagCorrect = false;


                    String text = queryArea.getText().replace("\n","").strip();


                    queryToSend.setQueryText(text);
                    queryToSend.setStartDate(fromDate);
                    queryToSend.setEndDate(toDate);
                    queryToSend.setWeekDays(weekDaysRepeat);
                    queryToSend.setExecutionTime(time);
                }


            }
            else{
                Messages.showErrorMessage(this, "Error during data entry.", "Error in data entry.");
            }



            if(flagCorrect){
                Messages.showOkMessage(this, "Query added successfully!","Operation completed");

                //add to database
                scheduledQueries.add(queryToSend);
                queryExecutorFrame.update(); //update

                //write in DB
                saveFile();

            }
            else Messages.showErrorMessage(this, "Error during data entry.", "Error in data entry.");
        }


        //open the q.e
        if (e.getSource()==showQE || e.getSource()==showQeButton){
            if (queryExecutorFrame.isVisible()){
                showQeButton.setText("Show the Q.E.");
                queryExecutorFrame.setVisible(false);
            }
            else{
                showQeButton.setText("Hide the Q.E.");
                queryExecutorFrame.setVisible(true);
            }

        }

        //menu bar - files
        if (e.getSource()==importQueryItem){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));

            int result = fileChooser.showOpenDialog(this);

            if(result==JFileChooser.APPROVE_OPTION){
                File selectedFile = fileChooser.getSelectedFile();

                String finalQuery="";

                try {
                    FileReader reader = new FileReader(selectedFile);
                    int data;
                    while((data=reader.read())!=-1){
                        finalQuery += (char) data;
                    }
                    reader.close();

                } catch (Exception ex) {throw new RuntimeException(ex);}

                this.queryArea.setText(finalQuery);
                Messages.showOkMessage(this, "Data import completed successfully!" , "Operation completed!");

            }else{
                Messages.showErrorMessage(this,"File import failed! Please try again","Operation not completed! Error");
            }


        }

        if (e.getSource()==exportQueryItem){
            if(! (this.queryArea.getText().trim().equals("") )) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));

                fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files (.txt)", "txt"));
                fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL Files (.sql)", "sql"));

                fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[1]);


                int result = fileChooser.showSaveDialog(this);

                if(result==JFileChooser.APPROVE_OPTION){
                    File selectedFile = fileChooser.getSelectedFile();


                    if (selectedFile != null) {
                        String extension = "";
                        if (fileChooser.getFileFilter() instanceof javax.swing.filechooser.FileNameExtensionFilter) {
                            javax.swing.filechooser.FileNameExtensionFilter filter = (javax.swing.filechooser.FileNameExtensionFilter) fileChooser.getFileFilter();
                            String[] extensions = filter.getExtensions();

                            if (extensions[0].equals("sql")) {
                                extension = ".sql";
                            } else if (extensions[0].equals("txt")) {
                                extension = ".txt";
                            }
                        }

                        //if no extension is provided add the correct one
                        if (!selectedFile.getName().endsWith(extension)) {
                            selectedFile = new File(selectedFile.getAbsolutePath() + extension);
                        }

                        try {
                            FileWriter writer = new FileWriter(selectedFile);
                            writer.write(this.queryArea.getText());
                            writer.close();

                        } catch (Exception ex) {throw new RuntimeException(ex);}

                        Messages.showOkMessage(this, "Data export completed successfully!" , "Operation completed!");

                    }
                }else{
                    Messages.showErrorMessage(this,"File export failed! Please try again","Operation not completed! Error");
                }
            }
            else{
                Messages.showErrorMessage(this, "Error! The query is empty", "Error in exporting!");
            }
        }


        //edit query button
        if(e.getSource()==editQueryButton || e.getSource() == editQueryItem){
            SwingUtilities.invokeLater(() -> {
                QueryEditorFrame queryEditorFrame = new QueryEditorFrame(this);
                queryEditorFrame.setVisible(true);
            });
            try {
                Thread.sleep(100);}
            catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        if (e.getSource()==quitItem){
            if (Messages.showQuitConfirmDialog(this)==0){
                saveFile();
                System.exit(0);
            }
        }

        //help section
        if(e.getSource()==aboutTheProjectItem){
            Messages.showProjectDetails(this);
        }

        if(e.getSource()==supportMeItem) {
            try {
                Desktop.getDesktop().browse(new URI("https://stevendamore.it"));
            } catch (IOException ex) {throw new RuntimeException(ex);
            } catch (URISyntaxException ex) {throw new RuntimeException(ex);}
        }


    }

}