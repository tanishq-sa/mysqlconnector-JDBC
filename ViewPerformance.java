import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class ViewPerformance extends JFrame {
    JTextField studentIdField;
    JTextArea resultArea;
    JButton fetch;

    public ViewPerformance() {
        setTitle("Student Performance Analyzer");
        setSize(600, 500);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("📊 Student Performance Analyzer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Enter Student ID:"));
        studentIdField = new JTextField(15);
        inputPanel.add(studentIdField);
        fetch = new JButton("Fetch Results");
        inputPanel.add(fetch);

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JPanel topCombined = new JPanel(new BorderLayout());
        topCombined.add(titlePanel, BorderLayout.NORTH);
        topCombined.add(inputPanel, BorderLayout.SOUTH);

        mainPanel.add(topCombined, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        fetch.addActionListener(e -> fetchResults());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    void fetchResults() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_perf", "root", "tanishqsaini");

            String studentId = studentIdField.getText().trim();
            if (studentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Student ID.");
                return;
            }

            String query = "SELECT student_name, subject, score, max_marks FROM results WHERE student_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();

            java.util.List<Double> percentages = new ArrayList<>();
            java.util.List<String> subjectLines = new ArrayList<>();
            Map<String, Double> percentMap = new HashMap<>();
            String studentName = null;

            while (rs.next()) {
                if (studentName == null) studentName = rs.getString("student_name");

                String subject = rs.getString("subject");
                int score = rs.getInt("score");
                int max = rs.getInt("max_marks");
                double percent = (score / (double) max) * 100;

                percentages.add(percent);
                subjectLines.add(String.format("Subject: %s | Score: %d/%d (%.2f%%)", subject, score, max, percent));
                percentMap.put(subject + "_" + score + "_" + max, percent);
            }

            if (percentages.isEmpty()) {
                resultArea.setText("No results found for Student ID: " + studentId);
                return;
            }

            double avg = percentages.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double max = Collections.max(percentages);
            double min = Collections.min(percentages);

            StringBuilder sb = new StringBuilder();
            sb.append("📄 Results for Student: ").append(studentName).append(" (ID: ").append(studentId).append(")\n");
            sb.append("🧾 Format: Subject | Score/Max (Percentage%)\n\n");

            for (String line : subjectLines) {
                sb.append(line).append("\n");
            }

            sb.append("\n📈 Performance Summary:\n");
            sb.append(String.format("Average: %.2f%%\n", avg));
            sb.append(String.format("Highest: %.2f%%\n", max));
            sb.append(String.format("Lowest : %.2f%%\n", min));

            sb.append("\n❗ Subjects Below 40%:\n");
            int lowCount = 0;
            for (Map.Entry<String, Double> entry : percentMap.entrySet()) {
                if (entry.getValue() < 40) {
                    String subject = entry.getKey().split("_")[0];
                    sb.append("- ").append(subject).append(" (").append(String.format("%.2f", entry.getValue())).append("%)\n");
                    lowCount++;
                }
            }
            if (lowCount == 0) sb.append("None\n");

            resultArea.setText(sb.toString());
            conn.close();
        } catch (Exception ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new ViewPerformance();
    }
}
