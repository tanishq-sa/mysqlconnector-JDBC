import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddResult extends JFrame {
    JTextField idField, nameField, subjectField, scoreField, dateField;
    JButton submit;

    public AddResult() {
        setTitle("Add Student Result");
        setSize(400, 300);
        setResizable(false);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        JLabel titleLabel = new JLabel("📝 Add Student Result", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        Font inputFont = new Font("SansSerif", Font.PLAIN, 14);

        formPanel.add(new JLabel("Student ID:"));
        idField = new JTextField();
        idField.setFont(inputFont);
        formPanel.add(idField);

        formPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        nameField.setFont(inputFont);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Subject:"));
        subjectField = new JTextField();
        subjectField.setFont(inputFont);
        formPanel.add(subjectField);

        formPanel.add(new JLabel("Score:"));
        scoreField = new JTextField();
        scoreField.setFont(inputFont);
        formPanel.add(scoreField);

        formPanel.add(new JLabel("Test Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        dateField.setFont(inputFont);
        formPanel.add(dateField);

        submit = new JButton("Submit");
        formPanel.add(new JLabel(""));  // Empty label for alignment
        formPanel.add(submit);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        submit.addActionListener(e -> insertResult());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    void insertResult() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_perf", "root", "tanishqsaini");
            String query = "INSERT INTO results(student_id, student_name, subject, score, test_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, idField.getText());
            ps.setString(2, nameField.getText());
            ps.setString(3, subjectField.getText());
            ps.setInt(4, Integer.parseInt(scoreField.getText()));
            ps.setDate(5, Date.valueOf(dateField.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Result Added Successfully!");
            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new AddResult();
    }
}