 import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewPerformance extends JFrame {
    JTextField studentIdField;
    JTextArea resultArea;
    JButton fetch;

    public ViewPerformance() {
        setTitle("Student Marks Viewer");
        setSize(500, 400);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Enter Student ID:"));
        studentIdField = new JTextField(15);
        topPanel.add(studentIdField);
        fetch = new JButton("Fetch Marks");
        topPanel.add(fetch);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        fetch.addActionListener(e -> fetchMarks());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    void fetchMarks() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/student_perf", "root", "tanishqsaini");

            String studentId = studentIdField.getText().trim();
            if (studentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Student ID.");
                return;
            }

            String query = "SELECT student_name, subject, score, max_marks FROM results WHERE student_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            boolean found = false;
            String studentName = "";

            while (rs.next()) {
                if (!found) {
                    studentName = rs.getString("student_name");
                    sb.append("Results for: ").append(studentName).append(" (ID: ").append(studentId).append(")\n\n");
                    sb.append("Subject\t\tScore\tMax Marks\n");
                    sb.append("--------\t-----\t----------\n");
                    found = true;
                }

                sb.append(rs.getString("subject")).append("\t\t")
                  .append(rs.getInt("score")).append("\t")
                  .append(rs.getInt("max_marks")).append("\n");
            }

            if (!found) {
                sb.append("No results found for Student ID: ").append(studentId);
            }

            resultArea.setText(sb.toString());
            conn.close();

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ViewPerformance();
    }
}
