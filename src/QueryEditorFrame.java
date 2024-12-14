import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class QueryEditorFrame extends JFrame {
    private Planner planner;
    private JTextArea textArea;
    private JTextArea lineNumbers;

    public QueryEditorFrame(Planner planner) {
        this.planner = planner;

        // set frame properties
        this.setTitle("Query Editor");
        this.setSize(600, 400);
        this.setMinimumSize(new Dimension(300, 150));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);


        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.getDocument().addDocumentListener(new LineNumberUpdater());

        // line numbers area
        lineNumbers = new JTextArea("1");
        lineNumbers.setFont(new Font("Monospaced", Font.PLAIN, 14));
        lineNumbers.setEditable(false);
        lineNumbers.setBackground(Color.LIGHT_GRAY);

        // add text area and line numbers to a scroll pane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumbers);

        // save button
        JButton saveButton = new JButton("salva");
        saveButton.addActionListener(new SaveButtonListener());

        // set main panel layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(saveButton, BorderLayout.SOUTH);

        // add panel to frame
        add(mainPanel);

        //adding the text
        textArea.setText(this.planner.queryArea.getText());
    }

    // updates line numbers when text changes
    private class LineNumberUpdater implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLineNumbers();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLineNumbers();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLineNumbers();
        }

        private void updateLineNumbers() {
            int lines = textArea.getLineCount();
            StringBuilder numbers = new StringBuilder();
            for (int i = 1; i <= lines; i++) {
                numbers.append(i).append("\n");
            }
            lineNumbers.setText(numbers.toString());
        }
    }

    // saves text to a file when save button is clicked
    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            planner.queryArea.setText(textArea.getText());
            dispose();
        }
    }


}
