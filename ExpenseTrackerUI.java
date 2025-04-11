import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class ExpenseTrackerUI extends JFrame {
    private ArrayList<Expense> expObjects;

    public ExpenseTrackerUI() {
        expObjects = ExpenseStorage.readExpense();

        setTitle("Expense Tracker");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10));

        JButton addBtn = new JButton("Add Expense");
        JButton deleteBtn = new JButton("Delete Expense");
        JButton manageBtn = new JButton("Manage Categories");
        JButton viewBtn = new JButton("View Expenses");
        JButton modifyBtn = new JButton("Modify Expense");
        JButton reportBtn = new JButton("Generate Report");
        JButton visualizeBtn = new JButton("Visualize Expenses"); // NEW
        JButton exitBtn = new JButton("Exit");

        panel.add(addBtn);
        panel.add(deleteBtn);
        panel.add(manageBtn);
        panel.add(viewBtn);
        panel.add(modifyBtn);
        panel.add(reportBtn);
        panel.add(visualizeBtn); // NEW
        panel.add(exitBtn);

        add(panel);

        addBtn.addActionListener(e -> addExpenseDialog());
        deleteBtn.addActionListener(e -> deleteExpenseDialog());
        manageBtn.addActionListener(e -> CategoryManager.manageCategories());
        viewBtn.addActionListener(e -> viewExpensesDialog());
        modifyBtn.addActionListener(e -> modifyExpenseDialog());
        reportBtn.addActionListener(e -> generateReportDialog());
        visualizeBtn.addActionListener(e -> showPieChart()); // NEW
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void addExpenseDialog() {
        JTextField idField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField descField = new JTextField();
        JTextField dateField = new JTextField();

        Object[] fields = {
            "ID:", idField,
            "Amount:", amountField,
            "Category:", categoryField,
            "Description:", descField,
            "Date:", dateField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add New Expense", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText());
                int amount = Integer.parseInt(amountField.getText());
                String cat = categoryField.getText();
                String desc = descField.getText();
                String date = dateField.getText();

                Expense newExpense = new Expense(id, amount, cat, desc, date);
                expObjects.add(newExpense);
                ExpenseStorage.storeExpense(expObjects);

                JOptionPane.showMessageDialog(this, "Expense Added Successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please try again.");
            }
        }
    }

    private void deleteExpenseDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter Expense ID to delete:");
        if (input != null) {
            try {
                int id = Integer.parseInt(input);
                expObjects.removeIf(exp -> exp.getId() == id);
                ExpenseStorage.storeExpense(expObjects);
                JOptionPane.showMessageDialog(this, "Deleted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid ID.");
            }
        }
    }

    private void viewExpensesDialog() {
        StringBuilder sb = new StringBuilder();
        for (Expense exp : expObjects) {
            sb.append(exp).append("\n");
        }
        JTextArea area = new JTextArea(sb.toString(), 15, 40);
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "All Expenses", JOptionPane.INFORMATION_MESSAGE);
    }

    private void modifyExpenseDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter Expense ID to modify:");
        if (input != null) {
            try {
                int id = Integer.parseInt(input);
                for (Expense exp : expObjects) {
                    if (exp.getId() == id) {
                        JTextField amountField = new JTextField(String.valueOf(exp.getAmount()));
                        JTextField catField = new JTextField(exp.getCategory());
                        JTextField descField = new JTextField(exp.getDescription());
                        JTextField dateField = new JTextField(exp.getDate());

                        Object[] fields = {
                            "Amount:", amountField,
                            "Category:", catField,
                            "Description:", descField,
                            "Date:", dateField
                        };

                        int option = JOptionPane.showConfirmDialog(this, fields, "Modify Expense", JOptionPane.OK_CANCEL_OPTION);

                        if (option == JOptionPane.OK_OPTION) {
                            exp.setAmount(Integer.parseInt(amountField.getText()));
                            exp.setCategory(catField.getText());
                            exp.setDescription(descField.getText());
                            exp.setDate(dateField.getText());
                            ExpenseStorage.storeExpense(expObjects);
                            JOptionPane.showMessageDialog(this, "Modified successfully!");
                        }
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "Expense not found.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }

    private void generateReportDialog() {
        int total = 0;
        for (Expense exp : expObjects) {
            total += exp.getAmount();
        }
        JOptionPane.showMessageDialog(this, "Total Expense: ₹" + total);
    }

    private void showPieChart() {
        Map<String, Integer> categoryTotals = new HashMap<>();
        for (Expense exp : expObjects) {
            categoryTotals.put(exp.getCategory(),
                categoryTotals.getOrDefault(exp.getCategory(), 0) + exp.getAmount());
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : categoryTotals.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
            "Expense Breakdown by Category",
            dataset,
            true, true, false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 400));

        JFrame chartFrame = new JFrame("Expense Pie Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setContentPane(chartPanel);
        chartFrame.pack();
        chartFrame.setLocationRelativeTo(null);
        chartFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ExpenseTrackerUI().setVisible(true);
        });
    }
}
  