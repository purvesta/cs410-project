package edu.boisestate.cs410.gradebook;

import com.budhash.cliche.Command;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

public class GradeBookShell {
    private final Connection db;
    private int currID;
    private String currClass;
    private String currTerm;
    private int currSection;

    public GradeBookShell(Connection cxn) {
        db = cxn;
        currID = 0;
        currClass = "";
        currTerm = "";
        currSection = 0;
    }

    /*******************************
     * Below commands are general
     * CRUD operation commands.
     *******************************/

    @Command
    public void newClass(String courseNum, String term, int sectionNum, String description) throws SQLException {
        String query = "INSERT INTO class (course_num, term, section_num, class_desc)" +
                       "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, courseNum);
            stmt.setString(2, term);
            stmt.setInt(3, sectionNum);
            stmt.setString(4, description);
            System.out.format("Creating class:\n\t%s %s %d\n\t%s", courseNum, term, sectionNum, description);
            stmt.execute();
        }
    }

    @Command
    public void listClasses() throws SQLException {

        String query = "SELECT c.course_num, count(sc.student_id)" +
                       "  FROM class AS c" +
                       "      JOIN student_class AS sc" +
                       "          ON (c.class_id=sc.class_id)" +
                       " GROUP BY c.course_num";
        try (Statement stmt = db.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Course Number : Num Students");
            while (rs.next()) {
                System.out.format("%s : %d", rs.getString(1), rs.getInt(2));
            }
        }
    }

    @Command
    public void selectClass(String courseNum) throws SQLException {
        String query = "SELECT class_id, course_num, term, section_num" +
                       "  FROM class" +
                       " WHERE term=(" +
                       "             SELECT term " +
                       "               FROM class" +
                       "              WHERE course_num=?" +
                       "              ORDER BY SUBSTRING(term, 3) DESC" +
                       "              LIMIT 1" +
                       "       );";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, courseNum);
            ResultSet rs = stmt.executeQuery();
            rs.last();
            if(rs.getRow() > 1)
                System.out.println("ERROR: More than one section for the most recent term.");
            else {
                this.currID = rs.getInt(1);
                this.currClass = rs.getString(2);
                this.currTerm = rs.getString(3);
                this.currSection = rs.getInt(4);
            }
        }
    }

    @Command
    public void selectClass(String courseNum, String term) throws SQLException {
        String query = "SELECT class_id, course_num, term, section_num" +
                       "  FROM class" +
                       " WHERE courseNum=? AND term=?;";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, courseNum);
            stmt.setString(2, term);
            ResultSet rs = stmt.executeQuery();
            rs.last();
            if(rs.getRow() > 1)
                System.out.format("ERROR: More than one section for the %s term.", term);
            else {
                this.currID = rs.getInt(1);
                this.currClass = rs.getString(2);
                this.currTerm = rs.getString(3);
                this.currSection = rs.getInt(4);
            }
        }
    }

    @Command
    public void selectClass(String courseNum, String term, int sectionNum) throws SQLException {
        String query = "SELECT class_id, course_num, term, section_num" +
                       "  FROM class" +
                       " WHERE courseNum=? AND term=? AND section_num=?;";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, courseNum);
            stmt.setString(2, term);
            stmt.setInt(3, sectionNum);
            ResultSet rs = stmt.executeQuery();
            this.currID = rs.getInt(1);
            this.currClass = rs.getString(2);
            this.currTerm = rs.getString(3);
            this.currSection = rs.getInt(4);
        }
    }

    @Command
    public void showClass() throws SQLException {
        System.out.format("Currently active class:\n\t%s %s %d", this.currClass, this.currTerm, this.currSection);
    }


    /*******************************
     * Below commands are performed
     * on currently active class.
     *******************************/

    /***** Category and Item Management *****/
    @Command
    public void showCategories() throws SQLException {
        String query = "SELECT cat_name, cat_weight" +
                       "  FROM category" +
                       " WHERE class_id=?;";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Category : Weight");
            while (rs.next()) {
                System.out.format("%s : %d", rs.getString(1), rs.getInt(2));
            }
        }
    }

    @Command
    public void addCategory(String catName, int catWeight) throws SQLException {
        String query = "INSERT INTO category (cat_name, cat_weight, class_id)" +
                       "VALUES (?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, catName);
            stmt.setInt(2, catWeight);
            stmt.setInt(3, this.currID);
            System.out.format("Creating category:\n\t%s %d", catName, catWeight);
            stmt.execute();
        }
    }

    @Command
    public void showItems() throws SQLException {
        String query = "SELECT i.item_name, i.item_points_worth" +
                       "  FROM item AS i" +
                       "      JOIN category AS c" +
                       "          ON (i.cat_id=c.cat_id)" +
                       " WHERE c.class_id=?";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Item : Points");
            while (rs.next()) {
                System.out.format("%s : %d", rs.getString(1), rs.getInt(2));
            }
        }
    }

    @Command
    public void addItem(String itemName, String catName, String description, int points) throws SQLException {
        int catID = 0;
        String query = "SELECT cat_id" +
                       "  FROM category AS c" +
                       " WHERE class_id=? AND cat_name=?";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            stmt.setString(2, catName);
            ResultSet rs = stmt.executeQuery();
            catID = rs.getInt(1);
            System.out.println("Item : Points");
            while (rs.next()) {
                System.out.format("%s : %d", rs.getString(1), rs.getInt(2));
            }
        }
        query = "INSERT INTO item (item_name, item_points_worth, item_desc, cat_id)" +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, itemName);
            stmt.setInt(2, points);
            stmt.setString(3, description);
            stmt.setInt(4, catID);
            System.out.format("Creating item:\n\t%s %s %d\n\t%s", itemName, catName, points, description);
            stmt.execute();
        }
    }

    /***** Student Management *****/

    @Command
    public void addStudent(String username, int studentID, String name) throws SQLException {
        String currName = "";
        boolean exists = false;
        String query = "SELECT student_name, count(student_id)" +
                       "  FROM student" +
                       " WHERE student_id=?" +
                       " GROUP BY student_name";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, studentID);
            ResultSet rs = stmt.executeQuery();
            currName = rs.getString(1);
            exists = rs.getInt(2) > 0;
        }

        if(!exists){
            // Add the student to student table
            query = "INSERT INTO student (student_id, username, student_name)" +
                    "VALUES (?, ?, ?)";
            try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                stmt.setInt(1, studentID);
                stmt.setString(2, username);
                stmt.setString(3, name);
                System.out.format("Creating student:\n\t%d %s %d", studentID, username, name);
                stmt.execute();
            }
        }
        else if(!currName.equals("") && !currName.equals(name)){
            // They got a new name
            query = "UPDATE student SET student_name=? WHERE student_id=?";
            try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                stmt.setString(1, name);
                stmt.setInt(2, studentID);
                System.out.format("WARNING: Name specified does not match previous record, updating to new name.");
                stmt.execute();
            }
        }
        // Enroll student in class
        query = "INSERT INTO student_class (class_id, student_id)" +
                "VALUES (?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            stmt.setInt(2, studentID);
            System.out.format("Adding student to class:\n\t%s %s", username, this.currClass);
            stmt.execute();
        }
    }

    @Command
    public void addStudent(String username) throws SQLException {
        boolean exists = false;
        String query = "SELECT student_name, count(student_id)" +
                "  FROM student" +
                " WHERE username=?" +
                " GROUP BY student_name";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            exists = rs.getInt(2) > 0;
        }

        if(!exists)
            System.out.println("ERROR: Specified student does not exist.");

        query = "INSERT INTO student_class (student_id, class_id)" +
                "SELECT student_id, ?" +
                "  FROM student" +
                " WHERE username=?";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            stmt.setString(2, username);
            System.out.format("Adding student to class:\n\t%s %s", username, this.currClass);
            stmt.execute();
        }
    }

    @Command
    public void showStudents() throws SQLException {
        String query = "SELECT s.username, s.student_name" +
                       "  FROM student AS s" +
                       "      JOIN student_class AS sc" +
                       "          ON (s.student_id=sc.student_id)" +
                       " WHERE sc.class_id=?";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Username : Student Name");
            while (rs.next()) {
                System.out.format("%s : %s", rs.getString(1), rs.getString(2));
            }
        }
    }

    @Command
    public void showStudents(String query) throws SQLException {
        // Use UPPER in order to ignore case
        String sql = "SELECT s.username, s.student_name" +
                     "  FROM student AS s" +
                     "      JOIN student_class AS sc" +
                     "          ON (s.student_id=sc.student_id)" +
                     " WHERE sc.class_id=? AND (UPPER(s.username) LIKE UPPER('%?%') OR UPPER(s.student_name) LIKE UPPER('%?%'))";
        try (PreparedStatement stmt = db.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            stmt.setString(2, query);
            stmt.setString(3, query);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Username : Student Name");
            while (rs.next()) {
                System.out.format("%s : %s", rs.getString(1), rs.getString(2));
            }
        }
    }

    @Command
    public void grade(String itemName, String username, int grade) throws SQLException {
//        generate(donors, 10);
    }

    /***** Grade Reporting *****/

    @Command
    public void studentGrades(String username) throws SQLException {
//        generate(donors, 10);
    }

    @Command
    public void gradebook() throws SQLException {
//        generate(donors, 10);
    }

    public static void main(String[] args) throws IOException, SQLException {
        // First (and only) command line argument: database URL
        String dbUrl = args[0];
        try (Connection cxn = DriverManager.getConnection("jdbc:" + dbUrl)) {
            GradeBookShell shell = new GradeBookShell(cxn);
            ShellFactory.createConsoleShell("gradebook", "", shell)
                    .commandLoop();
        }
    }
}