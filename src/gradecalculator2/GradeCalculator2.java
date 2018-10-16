package gradecalculator2;

//Imports are listed in full to show what's being used
//could just import javax.swing.* and java.awt.* etc..
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.ItemSelectable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


/*
=======================
--- To do next ---

* Input validation
* What-if auditing
* Class Deletion?
* Show the classes that are available

=======================
 */
public class GradeCalculator2 {
//Note: Typically the main method will be in a
//separate class. As this is a simple one class
//example it's all in the one class.

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        new GuiApp1();
    }

// Make some global/public variables for accessibility.
    public static JTextField weightField = new JTextField();
    public static JTextField gradeField = new JTextField();
    public static JTextField descriptionField = new JTextField();
    public static JTextField classField = new JTextField();
    public static JTextArea gradesArea = new JTextArea();
    public static JTextField necessaryField = new JTextField();
    public static JTextField projectedField = new JTextField();
    public static JTextArea assignmentsArea = new JTextArea();
    public static String currentClassInTextArea = "";
    public static JComboBox<String> classesCB;
    public static List<String> classes = new ArrayList<>();

    static class GuiApp1 {

        public GuiApp1() throws SQLException, IOException, ClassNotFoundException {
            JFrame guiFrame = new JFrame();

//make sure the program exits when the frame closes
            guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            guiFrame.setTitle("Grade Calculator II");
            guiFrame.setSize(1000, 1000);

//This will center the JFrame in the middle of the screen
            guiFrame.setLocationRelativeTo(null);
            guiFrame.setLayout(null);

//make sure the JFrame is visible
            guiFrame.setVisible(true);

// Get the MySQL up and running.
            Runtime.getRuntime().exec("C:\\xampp\\mysql\\bin\\mysqld.exe", null, new File("C:\\xampp\\mysql\\bin"));

            Scanner keyboard = new Scanner(System.in);
            // load JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded");
            System.out.println();

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/COLLEGE", "root", "");

//// SQL statement that retrieves all available classes.            
            String getClasses = "SELECT class\n"
                    + "FROM grades\n"
                    + "WHERE semester = '" + getCurrentSemester() + "'\n"
                    + "GROUP BY class;";

            PreparedStatement statement2 = connection.prepareStatement(getClasses);
            ResultSet results2 = statement2.executeQuery();

            // CREATE THE JCOMBOBOX FOR THE CLASSES
            while (results2.next()) {
                classes.add(results2.getString(1).toUpperCase());
            }

            // Turn semesters ArrayList into a String array.
            String classesStrArray[] = new String[classes.size()];
            for (int i = 0; i < classes.size(); i++) {
                classesStrArray[i] = classes.get(i).toString().toUpperCase();
            }

// Area where a new grade can be added.
            JLabel addGrade = new JLabel("Add a Grade", SwingConstants.CENTER);
            addGrade.setVisible(true);
            addGrade.setBounds(60, 20, 100, 20);
            addGrade.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            guiFrame.add(addGrade);

// Weight label.
            JLabel weight = new JLabel("Weight (%): ");
            weight.setVisible(true);
            weight.setBounds(30, 50, 100, 20);
            guiFrame.add(weight);

// Grade label.
            JLabel grade = new JLabel("Grade (%): ");
            grade.setVisible(true);
            grade.setBounds(30, 80, 100, 20);
            guiFrame.add(grade);

// Class name label.
            JLabel assignmentDesc = new JLabel("Description: ");
            assignmentDesc.setVisible(true);
            assignmentDesc.setBounds(30, 110, 100, 20);
            guiFrame.add(assignmentDesc);

// Assignment description.
            JLabel className = new JLabel("Class: ");
            className.setBounds(30, 140, 100, 20);
            guiFrame.add(className);

//////////////////////////////////////            
// Text fields for the above JLabels.
            weightField.setVisible(true);
            weightField.setBounds(140, 50, 100, 20);
            guiFrame.add(weightField);
            weightField.requestFocusInWindow();  // Place the cursor in this text field on startup.

            gradeField.setVisible(true);
            gradeField.setBounds(140, 80, 100, 20);
            guiFrame.add(gradeField);

            descriptionField.setVisible(true);
            descriptionField.setBounds(140, 110, 100, 20);
            guiFrame.add(descriptionField);

            classField.setVisible(true);
            classField.setBounds(140, 140, 100, 20);
            guiFrame.add(classField);

//////////////////////////////////////
// Button for putting the grade, its weight, a description, and the class in the database.
            JButton addToDB = new JButton("ADD");
            addToDB.setBounds(70, 180, 100, 20);
            addToDB.setVisible(true);
            guiFrame.add(addToDB);
            addToDB.addActionListener(
                    new ActionListener() { //anonymous inner class
                //event handler called when addToDB is clicked
                public void actionPerformed(ActionEvent event) {
                    try {
                        addGradeActionPerformed(event);
                    } catch (IOException ex) {
                        Logger.getLogger(GradeCalculator2.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(GradeCalculator2.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(GradeCalculator2.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GradeCalculator2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                private void addGradeActionPerformed(ActionEvent event) throws IOException, ClassNotFoundException, SQLException, InterruptedException {
                    // Get the semester in form "FALL 2016"
                    String currentSemester;
                    if (Calendar.getInstance().get(Calendar.MONTH) <= 6) {
                        currentSemester = "SPRING " + Calendar.getInstance().get(Calendar.YEAR);
                    } else {
                        currentSemester = "FALL " + Calendar.getInstance().get(Calendar.YEAR);;
                    }

                    // Add the class to the combo box, if it doesn't already exist there.
                    if (!classes.contains(classField.getText().toUpperCase())) {
                        classes.add(classField.getText().toUpperCase());
                        classesCB.addItem(classField.getText().toUpperCase());
                    }

                    addGrade(Double.parseDouble(gradeField.getText()), Double.parseDouble(weightField.getText()), descriptionField.getText(), classField.getText(), currentSemester);
                    fillTextAreaWithGrades();  // Recompute grades and display.                    
                }
            }
            );//ActionListerner listens for button to be clicked

// Get the available semesters from the database.            
////////////////////////////////////////////////////////////////////////////////
// CREATE THE JCOMBOBOX
//// SQL statement that retrieves all available semesters.            
            String getSemesters = "SELECT semester\n"
                    + "FROM grades\n"
                    + "GROUP BY semester;";

            PreparedStatement statement = connection.prepareStatement(getSemesters);
            ResultSet results = statement.executeQuery();

            List<String> semesters = new ArrayList<>();

            while (results.next()) {
                semesters.add(results.getString(1));
            }

            // Turn semesters ArrayList into a String array.
            String semestersStrArray[] = new String[semesters.size()];
            for (int i = 0; i < semesters.size(); i++) {
                semestersStrArray[i] = semesters.get(i).toString();
            }
            
// Get the percentage points and performance for each class in that semester.
            gradesArea.setBounds(40, 500, 400, 350);
            gradesArea.setEditable(false);
            guiFrame.add(gradesArea);
            // Populate the gradesArea with all the available grades.
            fillTextAreaWithGrades();

// Display all current classes, their percentage points, and current performance.
            JLabel currentClassesLabel = new JLabel("Current Classes", SwingConstants.CENTER);
            currentClassesLabel.setBounds(180, 440, 100, 20);
            currentClassesLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            guiFrame.add(currentClassesLabel);

            JLabel classLabel = new JLabel("Class");
            classLabel.setBounds(50, 540, 170, 20);
            guiFrame.add(classLabel);

            JLabel percentagePoints = new JLabel("Earned Points (%)");
            percentagePoints.setBounds(160, 540, 170, 20);
            guiFrame.add(percentagePoints);

            JLabel performance = new JLabel("Performance (%)");
            performance.setBounds(330, 540, 170, 20);
            guiFrame.add(performance);

// Labels, buttons, and text fields for the what-if.
            JLabel whatIf = new JLabel("What-if", SwingConstants.CENTER);
            whatIf.setBounds(610, 210, 100, 20);
            whatIf.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            guiFrame.add(whatIf);

            JLabel projectedGrade1Label = new JLabel("What if I earn ");
            projectedGrade1Label.setBounds(500, 250, 100, 20);
            guiFrame.add(projectedGrade1Label);

            projectedField.setVisible(true);
            projectedField.setBounds(615, 250, 100, 20);
            guiFrame.add(projectedField);

            JLabel projectedGrade2Label = new JLabel(" on remaining assignments?");
            projectedGrade2Label.setBounds(750, 250, 200, 20);
            guiFrame.add(projectedGrade2Label);

            JLabel necessaryGrade1Label = new JLabel("What grade must I earn on remaining assignments in order to get ");
            necessaryGrade1Label.setBounds(210, 280, 400, 20);
            guiFrame.add(necessaryGrade1Label);

            necessaryField.setVisible(true);
            necessaryField.setBounds(615, 280, 100, 20);
            guiFrame.add(necessaryField);

            JLabel necessaryGrade2Label = new JLabel(" overall in the course?");
            necessaryGrade2Label.setBounds(750, 280, 200, 20);
            guiFrame.add(necessaryGrade2Label);

            classesCB = new JComboBox<String>(classesStrArray);
            classesCB.setVisible(true);
            classesCB.setBounds(470, 210, 100, 20);
            guiFrame.add(classesCB);

            JButton processWhatIfButton = new JButton("PROCESS WHAT-IF");
            processWhatIfButton.setBounds(560, 325, 200, 20);
            processWhatIfButton.setVisible(true);
            guiFrame.add(processWhatIfButton);
            processWhatIfButton.addActionListener(
                    new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        showWhatIfResults();
                    } catch (SQLException ex) {
                        Logger.getLogger(GradeCalculator2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                private void showWhatIfResults() throws SQLException {
                    // Get the what-if grade for remaining assignments.
                    double projectedGrade = Double.parseDouble(projectedField.getText()) / 100;
                    double desiredGrade = Double.parseDouble(necessaryField.getText());

                    // Set up SQL connection.
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/COLLEGE", "root", "");

                    // Query database for results.
                    // Query the database for the percentage points and grades received in the classes.            
                    String getGrades = "SELECT SUM(weight), ROUND(SUM(weight*grade), 2), ROUND(((SUM(weight*grade)/ SUM(weight))*100), 2)\n"
                            + "FROM grades\n"
                            + "WHERE semester = '" + getCurrentSemester() + "' AND class = '" + classesCB.getSelectedItem() + "';";

                    PreparedStatement statement2 = connection.prepareStatement(getGrades);
                    ResultSet results2 = statement2.executeQuery();

                    results2.next();

                    double totalWeights = results2.getDouble(1);
                    double totalPPoints = results2.getDouble(2);

                    double gradeReceived = projectedGrade * (100 - totalWeights) + totalPPoints;

                    // Round gradeReceived to 1` decimal place.
                    gradeReceived = gradeReceived * 10;
                    gradeReceived = Math.round(gradeReceived);
                    gradeReceived = gradeReceived / 10;

                    double requiredGradeToGetDesiredGrade = (desiredGrade - totalPPoints) / (100 - totalWeights);
                    requiredGradeToGetDesiredGrade = requiredGradeToGetDesiredGrade * 1000;
                    requiredGradeToGetDesiredGrade = Math.round(requiredGradeToGetDesiredGrade);
                    requiredGradeToGetDesiredGrade = requiredGradeToGetDesiredGrade / 1000;

                    // Display results to screen.
                    JOptionPane.showMessageDialog(null, "If you earn " + 100 * projectedGrade
                            + "% overall on remaining\ncoursework, your grade will be: " + gradeReceived + "%\n\n"
                            + "In order to get " + desiredGrade + "% overall, you will\nneed " + requiredGradeToGetDesiredGrade * 100 + "% on remaining coursework.");
                    connection.close();

                    // Put the cursor in the projectedField.
                    projectedField.requestFocusInWindow();
                }
            }
            );

////////////////////////////////////////////////////////////////////
// Combo box, text area, text field, and button for class grades.
            // Label to describe the contents of the text area.
            JLabel descGradeWeight = new JLabel(" Description                                   Grade                                              Weight");
            descGradeWeight.setBounds(500, 475, 700, 20);
            guiFrame.add(descGradeWeight);

            assignmentsArea.setBounds(500, 500, 400, 350);
            assignmentsArea.setEditable(false);
            guiFrame.add(assignmentsArea);

            // Display all grades for the current class, their percentage points, and current performance.
            JLabel doneGradesLbl = new JLabel("Grades", SwingConstants.CENTER);
            doneGradesLbl.setBounds(640, 440, 100, 20);
            doneGradesLbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            guiFrame.add(doneGradesLbl);

            // Will use the combo box for the what-if audit.
            String getGrades = "SELECT description, grade*100, weight\n"
                    + "FROM grades\n"
                    + "WHERE class = '" + classesCB.getSelectedItem() + "';";
            currentClassInTextArea = (String) classesCB.getSelectedItem(); // Set this variable to the currently selected class.

            PreparedStatement statement3 = connection.prepareStatement(getGrades);
            ResultSet results3 = statement3.executeQuery();

            while (results3.next()) {
                assignmentsArea.append(results3.getString(1) + "\t\t" + results3.getDouble(2) + "\t\t" + results3.getDouble(3) + "\n");
            }

// ItemListener for the combobox (will be called when the selected item in the combobox is changed).
            ItemListener itemListener = new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent ie) {
                    assignmentsArea.setText("");

                    // Query database to get grades for newly selected class.
                    // Will use the combo box for the what-if audit.
                    String getGrades = "SELECT description, grade*100, weight\n"
                            + "FROM grades\n"
                            + "WHERE class = '" + classesCB.getSelectedItem() + "';";
                    currentClassInTextArea = (String) classesCB.getSelectedItem(); // Set this variable to the currently selected class.

                    try {
                        // Get the MySQL up and running.
                        Runtime.getRuntime().exec("C:\\xampp\\mysql\\bin\\mysqld.exe", null, new File("C:\\xampp\\mysql\\bin"));
                    } catch (IOException ex) {
                        Logger.getLogger(GradeCalculator2.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    Scanner keyboard = new Scanner(System.in);
                    try {
                        // load JDBC driver
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(GradeCalculator2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("Driver loaded");
                    System.out.println();

                    Connection connection = null;
                    try {
                        connection = DriverManager.getConnection("jdbc:mysql://localhost/COLLEGE", "root", "");
                    } catch (SQLException ex) {
                        Logger.getLogger(GradeCalculator2.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    PreparedStatement statement3 = null;
                    try {
                        statement3 = connection.prepareStatement(getGrades);
                    } catch (SQLException ex) {
                        Logger.getLogger(GradeCalculator2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ResultSet results3 = null;
                    try {
                        results3 = statement3.executeQuery();
                    } catch (SQLException ex) {
                        Logger.getLogger(GradeCalculator2.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    List<String> grades = new ArrayList<>();

                    try {
                        while (results3.next()) {
                            assignmentsArea.append(results3.getString(1) + "\t\t" + results3.getDouble(2) + "\t\t" + results3.getDouble(3) + "\n");
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(GradeCalculator2.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    int state = ie.getStateChange();
                    System.out.println((state == ItemEvent.SELECTED) ? "Selected" : "Deselected");
                    System.out.println("Item: " + ie.getItem());
                    ItemSelectable is = ie.getItemSelectable();

                    try {
                        // close connection
                        connection.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(GradeCalculator2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            classesCB.addItemListener(itemListener);

/////////////////////////////////////////////////////////////////////
// Close the SQL connection.            
            connection.close();

// Reset the JFrame.            
            guiFrame.revalidate();
            guiFrame.repaint();
        }

        public String getCurrentSemester() {
            // Get the semester in form "FALL 2016"
            String currentSemester;
            if (Calendar.getInstance().get(Calendar.MONTH) <= 6) {
                currentSemester = "SPRING " + Calendar.getInstance().get(Calendar.YEAR);
            } else {
                currentSemester = "FALL " + Calendar.getInstance().get(Calendar.YEAR);;
            }

            return currentSemester;
        }

        public void fillTextAreaWithGrades() throws SQLException {
            gradesArea.setText("");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/COLLEGE", "root", "");

            // Place all grades in the text area for the chosen semester.
            // Get the semester in form "FALL 2016"
            String currentSemesterForTextArea = getCurrentSemester();
            // Query the database for the percentage points and grades received in the classes.            
            String getGrades = "SELECT class, ROUND(SUM(weight*grade), 2), ROUND(((SUM(weight*grade)/ SUM(weight))*100), 2)\n"
                    + "FROM grades\n"
                    + "WHERE semester = '" + currentSemesterForTextArea + "'\n"
                    + "GROUP BY class;";

            PreparedStatement statement2 = connection.prepareStatement(getGrades);
            ResultSet results2 = statement2.executeQuery();

            while (results2.next()) {
                gradesArea.append(results2.getString(1).toUpperCase() + "\t\t" + results2.getDouble(2) + "\t\t" + results2.getDouble(3) + "\n");
            }

            connection.close();
        }

        public void addGrade(double grade, double weight, String description, String className, String semester) throws IOException, ClassNotFoundException, SQLException, InterruptedException {
            // Get the MySQL up and running.
            Runtime.getRuntime().exec("C:\\xampp\\mysql\\bin\\mysqld.exe", null, new File("C:\\xampp\\mysql\\bin"));

            // Append the grade to the text area
            Scanner keyboard = new Scanner(System.in);
            // load JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded");
            System.out.println();

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/COLLEGE", "root", "");

            // The sql string will hold the insert statement.
            String sql = "";

            if (currentClassInTextArea.toUpperCase().equals(className.toUpperCase())) {

                assignmentsArea.append(description + "\t\t" + grade + "\t\t" + weight + "\n");
            }

            // Make sure grades and weights are rounded appropriately.
            weight *= 100;
            weight = Math.round(weight);
            weight /= 100;
            System.out.println("NEW WEIGHT: " + weight);

            grade *= 100;
            grade = Math.round(grade);
            grade /= 100;
            System.out.println("NEW GRADE: " + grade);

            sql = "INSERT INTO GRADES (grade, weight, description, class, semester) values ("
                    + grade / 100 + ", " + weight + ", '" + description + "', '" + className.toUpperCase() + "', '" + semester + "');";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();

            // Null out the fields, since the grade has been submitted.
            gradeField.setText("");
            weightField.setText("");
            descriptionField.setText("");
            //classField.setText("");  
            weightField.requestFocusInWindow();  // Set the cursor to blinking in this field.

            System.out.println("execute successful");
            connection.close();
            System.out.println("connection closed.");
        }
    }
}
