import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

class Investment {
    private String date;
    private String ticker;
    private double entry;
    private double stopLoss;
    private String notes;
    private double returnPercentage;

    public Investment(String date, String ticker, double entry, double stopLoss, String notes, double returnPercentage) {
        this.date = date;
        this.ticker = ticker;
        this.entry = entry;
        this.stopLoss = stopLoss;
        this.notes = notes;
        this.returnPercentage = returnPercentage;
    }

    public double getReturnPercentage() {
        return returnPercentage;
    }

    public void setReturnPercentage(double returnPercentage) {
        this.returnPercentage = returnPercentage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getEntry() {
        return entry;
    }

    public void setEntry(double entry) {
        this.entry = entry;
    }

    public double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

class InvestmentModel {
    private List<Investment> investments;

    public InvestmentModel() {
        investments = new ArrayList<>();
    }

    public void addInvestment(Investment investment) {
        investments.add(investment);
    }

    public List<Investment> getInvestments() {
        return investments;
    }
}

class InvestmentTableModel extends AbstractTableModel {
    private List<Investment> investments;
    private String[] columnNames = {"Date", "Ticker", "Entry", "Stop Loss", "Notes", "Return (%)"};

    public InvestmentTableModel(List<Investment> investments) {
        this.investments = investments;
    }

    public int getRowCount() {
        return investments.size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Investment investment = investments.get(rowIndex);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        switch (columnIndex) {
            case 0:
                return investment.getDate();
            case 1:
                return investment.getTicker();
            case 2:
                return decimalFormat.format(investment.getEntry());
            case 3:
                return decimalFormat.format(investment.getStopLoss());
            case 4:
                return investment.getNotes();
            case 5:
                return decimalFormat.format(investment.getReturnPercentage());
            default:
                return null;
        }
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }
}

class ReturnColumnRenderer extends DefaultTableCellRenderer {
    private DecimalFormat formatter = new DecimalFormat("0.00%");

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        double returnValue = Double.parseDouble(value.toString());
        if (returnValue < 0) {
            c.setBackground(Color.RED);
        } else if (returnValue > 0) {
            c.setBackground(Color.GREEN);
        } else {
            c.setBackground(Color.WHITE);
        }
        return c;
    }
}

class InvestmentJournalView extends JFrame {
    private InvestmentModel model;
    private JTable table;
    private InvestmentController controller;

    public InvestmentJournalView(InvestmentModel model) {
        this.model = model;
        setTitle("Investment Journal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        table = new JTable(new InvestmentTableModel(model.getInvestments()));
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(5).setCellRenderer(new ReturnColumnRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton sortButton = new JButton("Sort");
        JButton addButton = new JButton("Add");
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> controller.quitProcess());
        buttonPanel.add(sortButton);
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);


        // Add action listener for the sort button
        addButton.addActionListener(e -> {
            // code to open the add investment view
            AddInvestmentView addInvestmentView = new AddInvestmentView(table, model);
            addInvestmentView.setVisible(true);
        });
    }
}

class InvestmentController {
    private InvestmentModel model;
    private InvestmentJournalView view;

    public InvestmentController() {
        this.model = new InvestmentModel();
        this.view = new InvestmentJournalView(model);
        view.setVisible(true);
    }

    public void addInvestment(Investment investment) {
        model.addInvestment(investment);
    }
    public void quitProcess() { System.exit(0);}
}

class AddInvestmentView extends JFrame {
    private JTable table;
    private InvestmentModel model;
    private JTextField dateField = new JTextField(), tickerField = new JTextField(), entryField = new JTextField(), stopLossField = new JTextField(),
            notesField = new JTextField(), returnField = new JTextField();
    private JButton saveButton;
    public AddInvestmentView(JTable table, InvestmentModel model) {
        this.table = table;
        this.model = model;
        setTitle("Add Investment");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Ticker:"));
        inputPanel.add(tickerField);
        inputPanel.add(new JLabel("Entry:"));
        inputPanel.add(entryField);
        inputPanel.add(new JLabel("Stop Loss/Trailing Loss:"));
        inputPanel.add(stopLossField);
        inputPanel.add(new JLabel("Notes:"));
        inputPanel.add(notesField);
        inputPanel.add(new JLabel("Return:"));
        inputPanel.add(returnField);
        this.add(inputPanel, BorderLayout.CENTER);

        // create text fields, labels and button and add them to the frame.
        saveButton = new JButton("Save");
        this.add(saveButton, BorderLayout.SOUTH);
        saveButton.addActionListener(e -> {
            String date = dateField.getText();
            String ticker = tickerField.getText();
            double entry = Double.parseDouble(entryField.getText());
            double stopLoss = Double.parseDouble(stopLossField.getText());
            String notes = notesField.getText();
            double returnPercentage = Double.parseDouble(returnField.getText());
            Investment investment = new Investment(date, ticker, entry, stopLoss, notes, returnPercentage);
            model.addInvestment(investment);
            table.repaint();
            this.setVisible(false);
            this.dispose();
        });
    }
}

class Main {
    public static void main(String[] args) {
        InvestmentController controller = new InvestmentController();
    }
}

