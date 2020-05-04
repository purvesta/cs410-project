package edu.boisestate.cs410.gradebook;

import com.budhash.cliche.Command;
import com.budhash.cliche.Param;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;
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

    /**
     * @param courseNum
     * @param term
     * @param sectionNum
     * @param description
     * @throws SQLException
     */
    @Command
    public void newClass(@Param(name="courseNum") String courseNum, @Param(name="term") String term,
                         @Param(name="sectionNum") int sectionNum, @Param(name="description") String description) throws SQLException {
        String query = "INSERT INTO class (course_num, term, section_num, class_desc)" +
                       "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, courseNum);
            stmt.setString(2, term);
            stmt.setInt(3, sectionNum);
            stmt.setString(4, description);
            System.out.format("Creating class:\n\t%s %s %d\n\t%s\n", courseNum, term, sectionNum, description);
            stmt.execute();
        }
    }

    /**
     * @throws SQLException
     */
    @Command
    public void listClasses() throws SQLException {

        String query = "SELECT c.course_num, count(sc.student_id)" +
                       "  FROM class AS c" +
                       "      JOIN student_class AS sc" +
                       "          ON (c.class_id=sc.class_id)" +
                       " GROUP BY c.course_num";
        try (Statement stmt = db.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.format("%-20.20s %-20.20s\n", "Course Number", "Num Students");
            while (rs.next()) {
                System.out.format("%-20.20s %-20.20s\n", rs.getString(1), rs.getInt(2));
            }
        }
    }

    /**
     * @param courseNum
     * @throws SQLException
     */
    @Command
    public void selectClass(@Param(name="courseNum") String courseNum) throws SQLException {
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
                System.out.println("ERROR: More than one section for the most recent term.\n");
            else {
                this.currID = rs.getInt(1);
                this.currClass = rs.getString(2);
                this.currTerm = rs.getString(3);
                this.currSection = rs.getInt(4);
            }
        }
    }

    /**
     * @param courseNum
     * @param term
     * @throws SQLException
     */
    @Command
    public void selectClass(@Param(name="courseNum") String courseNum, @Param(name="term") String term) throws SQLException {
        String query = "SELECT class_id, course_num, term, section_num" +
                       "  FROM class" +
                       " WHERE course_num=? AND term=?;";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, courseNum);
            stmt.setString(2, term);
            ResultSet rs = stmt.executeQuery();
            rs.last();
            if(rs.getRow() > 1)
                System.out.format("ERROR: More than one section for the %s term.\n", term);
            else {
                this.currID = rs.getInt(1);
                this.currClass = rs.getString(2);
                this.currTerm = rs.getString(3);
                this.currSection = rs.getInt(4);
            }
        }
    }

    /**
     * @param courseNum
     * @param term
     * @param sectionNum
     * @throws SQLException
     */
    @Command
    public void selectClass(@Param(name="courseNum") String courseNum, @Param(name="term") String term,
                            @Param(name="sectionNum") int sectionNum) throws SQLException {
        String query = "SELECT class_id, course_num, term, section_num" +
                       "  FROM class" +
                       " WHERE course_num=? AND term=? AND section_num=?;";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, courseNum);
            stmt.setString(2, term);
            stmt.setInt(3, sectionNum);
            ResultSet rs = stmt.executeQuery();
            rs.last();
            this.currID = rs.getInt(1);
            this.currClass = rs.getString(2);
            this.currTerm = rs.getString(3);
            this.currSection = rs.getInt(4);
        }
    }

    /**
     * @throws SQLException
     */
    @Command
    public void showClass() throws SQLException {
        System.out.format("Currently active class:\n\t%s %s %d\n", this.currClass, this.currTerm, this.currSection);
    }


    /*******************************
     * Below commands are performed
     * on currently active class.
     *******************************/

    /***** Category and Item Management *****/

    /**
     * @throws SQLException
     */
    @Command
    public void showCategories() throws SQLException {
        String query = "SELECT cat_name, cat_weight" +
                       "  FROM category" +
                       " WHERE class_id=?;";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            ResultSet rs = stmt.executeQuery();
            System.out.format("%-20.20s %-20.20s\n", "Category", "Weight");
            while (rs.next()) {
                System.out.format("%-20.20s %-20.20s\n", rs.getString(1), rs.getInt(2));
            }
        }
    }

    /**
     * @param catName
     * @param catWeight
     * @throws SQLException
     */
    @Command
    public void addCategory(@Param(name="catName") String catName, @Param(name="catWeight") int catWeight) throws SQLException {
        String query = "INSERT INTO category (cat_name, cat_weight, class_id)" +
                       "VALUES (?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, catName);
            stmt.setInt(2, catWeight);
            stmt.setInt(3, this.currID);
            System.out.format("Creating category:\n\t%s %d\n", catName, catWeight);
            stmt.execute();
        }
    }

    /**
     * @throws SQLException
     */
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
            System.out.format("%-20.20s %-20.20s\n", "Item", "Points");
            while (rs.next()) {
                System.out.format("%-20.20s %-20.20s\n\n", rs.getString(1), rs.getInt(2));
            }
        }
    }

    /**
     * @param itemName
     * @param catName
     * @param description
     * @param points
     * @throws SQLException
     */
    @Command
    public void addItem(@Param(name="itemName") String itemName, @Param(name="catName") String catName,
                        @Param(name="description") String description, @Param(name="points") int points) throws SQLException {
        int catID = 0;
        String query = "SELECT cat_id" +
                       "  FROM category AS c" +
                       " WHERE class_id=? AND cat_name=?";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            stmt.setString(2, catName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                catID = rs.getInt(1);
        }
        query = "INSERT INTO item (item_name, item_points_worth, item_desc, cat_id)" +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, itemName);
            stmt.setInt(2, points);
            stmt.setString(3, description);
            stmt.setInt(4, catID);
            System.out.format("Creating item:\n\t%s %s %d\n\t%s\n", itemName, catName, points, description);
            stmt.execute();
        }
    }

    /***** Student Management *****/

    /**
     * @param username
     * @param studentID
     * @param name
     * @throws SQLException
     */
    @Command
    public void addStudent(@Param(name="username") String username, @Param(name="studentID") int studentID,
                           @Param(name="name") String name) throws SQLException {
        String currName = "";
        boolean exists = false;
        String query = "SELECT student_name, count(student_id)" +
                       "  FROM student" +
                       " WHERE student_id=?" +
                       " GROUP BY student_name";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, studentID);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                currName = rs.getString(1);
                exists = rs.getInt(2) > 0;
            }
        }

        if(!exists){
            // Add the student to student table
            query = "INSERT INTO student (student_id, username, student_name)" +
                    "VALUES (?, ?, ?)";
            try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                stmt.setInt(1, studentID);
                stmt.setString(2, username);
                stmt.setString(3, name);
                System.out.format("Creating student:\n\t%d %s %s\n", studentID, username, name);
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
            System.out.format("Adding student to class:\n\t%s %s\n", username, this.currClass);
            stmt.execute();
        }
    }

    /**
     * @param username
     * @throws SQLException
     */
    @Command
    public void addStudent(@Param(name="username") String username) throws SQLException {
        boolean exists = false;
        String query = "SELECT student_name, count(student_id)" +
                "  FROM student" +
                " WHERE username=?" +
                " GROUP BY student_name";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
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
            System.out.format("Adding student to class:\n\t%s %s\n", username, this.currClass);
            stmt.execute();
        }
    }

    /**
     * @throws SQLException
     */
    @Command
    public void showStudents() throws SQLException {
        String query = "SELECT s.username, s.student_name" +
                       "  FROM student AS s" +
                       "      JOIN student_class AS sc" +
                       "          ON (s.student_id=sc.student_id)" +
                       " WHERE sc.class_id=?;";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            ResultSet rs = stmt.executeQuery();
            System.out.format("%-20.20s %-20.20s\n", "Username", "Student Name");
            while (rs.next())
                System.out.format("%-20.20s %-20.20s\n", rs.getString(1), rs.getString(2));
        }
    }

    /**
     * @param query
     * @throws SQLException
     */
    @Command
    public void showStudents(@Param(name="query") String query) throws SQLException {
        // Use UPPER in order to ignore case
        query = "%"+query+"%";
        String sql = "SELECT s.username, s.student_name" +
                     "  FROM student AS s" +
                     "      JOIN student_class AS sc" +
                     "          ON (s.student_id=sc.student_id)" +
                     " WHERE sc.class_id=? AND (UPPER(s.student_name) LIKE UPPER(?) OR UPPER(s.username) LIKE UPPER(?));";
        try (PreparedStatement stmt = db.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            stmt.setString(2, query);
            stmt.setString(3, query);
            ResultSet rs = stmt.executeQuery();
            System.out.format("%-20.20s %-20.20s\n", "Username", "Student Name");
            while (rs.next())
                System.out.format("%-20.20s %-20.20s\n", rs.getString(1), rs.getString(2));
        }
    }

    /**
     * @param itemName
     * @param username
     * @param grade
     * @throws SQLException
     */
    @Command
    public void grade(@Param(name="itemName") String itemName, @Param(name="username") String username,
                      @Param(name="grade") int grade) throws SQLException {
        // Get studentID associated with username
        int studID = 0;
        String query = "SELECT student_id" +
                       "  FROM student" +
                       " WHERE username=?;";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                studID = rs.getInt(1);
        }
        // Get itemID associated with category associated with class
        // Limit 1 for safety, although there shouldn't be more than 1 record returned
        int itemID = 0;
        query = "SELECT i.item_id" +
                "  FROM item AS i" +
                "      JOIN category AS c" +
                "          ON (i.cat_id=c.cat_id)" +
                " WHERE c.class_id=? AND i.item_name=?" +
                " LIMIT 1;";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            stmt.setString(2, itemName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                itemID = rs.getInt(1);
        }
        // Check if grade exists for the item
        boolean exists = false;
        query = "SELECT count(grade_id)" +
                "  FROM grade" +
                " WHERE student_id=? AND item_id=?;";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, studID);
            stmt.setInt(2, itemID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                if(rs.getInt(1) > 0)
                    exists = true;
        }
        // Either update or insert depending on if grade exists
        if(exists){
            // Update
            query = "UPDATE grade SET score=? WHERE student_id=? AND item_id=?;";
            try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                stmt.setInt(1, grade);
                stmt.setInt(2, studID);
                stmt.setInt(3, itemID);
                System.out.format("Updating grade:\n\t%s %s %d\n", username, itemName, grade);
                stmt.execute();
            }
        }
        else {
            // Insert
            query = "INSERT INTO grade (score, student_id, item_id)" +
                    "VALUES (?, ?, ?);";
            try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                stmt.setInt(1, grade);
                stmt.setInt(2, studID);
                stmt.setInt(3, itemID);
                System.out.format("Creating grade:\n\t%s %s %d\n", username, itemName, grade);
                stmt.execute();
            }
        }
    }

    /***** Grade Reporting *****/

    /**
     * @param username
     * @throws SQLException
     */
    @Command
    public void studentGrades(@Param(name="username") String username) throws SQLException {

        // Item grades by category
        String query1 = "SELECT c.cat_name, i.item_name, CAST(g.score AS FLOAT)/i.item_points_worth*100 AS item_score" +
                        "  FROM grade AS g" +
                        "      RIGHT JOIN item AS i" +
                        "          ON (g.item_id=i.item_id)" +
                        "      JOIN category AS c" +
                        "          ON (i.cat_id=c.cat_id)" +
                        "      JOIN student_class AS sc" +
                        "          ON (c.class_id=sc.class_id)" +
                        "      JOIN student AS s" +
                        "          ON (sc.student_id=s.student_id)" +
                        " WHERE c.class_id=? AND s.username=?" +
                        " GROUP BY c.cat_name, i.item_name, item_score";

        try (PreparedStatement stmt = db.prepareStatement(query1, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();
            System.out.println(username);
            System.out.format("%-20.20s %-20.20s %-20.20s\n","Category Name", "Item", "Item Score");
            while (rs.next())
                System.out.format("%-20.20s %-20.20s %-20.20s\n",rs.getString(1), rs.getString(2), rs.getFloat(3));
        }

        calcAttemptedAndTotal(username, 1);

    }

    private void calcAttemptedAndTotal(String username, int func) throws SQLException {
        // Func 1 == studentGrades
        // Func 2 == gradebook

        double attempted = 0.0;
        // Calculate Attempted
        String query1 = "SELECT SUM(a.attempted)" +
                        "  FROM (" +
                        "        SELECT (1.0*SUM(gs.score))/SUM(i.item_points_worth)*100*(c.cat_weight*0.01) AS attempted" +
                        "          FROM (SELECT s.username, g.score, g.student_id, g.item_id" +
                        "                  FROM grade AS g " +
                        "                      RIGHT JOIN student AS s" +
                        "                          ON g.student_id=s.student_id" +
                        "               ) AS gs" +
                        "              JOIN item AS i" +
                        "                  ON (gs.item_id=i.item_id)" +
                        "              JOIN category AS c" +
                        "                  ON (i.cat_id=c.cat_id)" +
                        "              JOIN student_class AS sc" +
                        "                  ON (c.class_id=sc.class_id)" +
                        "         WHERE c.class_id=? AND gs.username=?" +
                        "         GROUP BY c.cat_weight" +
                        "       ) AS a";

        try (PreparedStatement stmt = db.prepareStatement(query1, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();
            System.out.println();
            while (rs.next()) {
                if (func == 1)
                    System.out.format("%-20.20s %-20.20s\n", "Attempted Grade", rs.getFloat(1));
                attempted = rs.getDouble(1);
            }
        }

        // Gather the student ID... my brain is fried
        int studID = 0;
        String studIDQuery = "SELECT student_id FROM student WHERE username=?";
        try (PreparedStatement stmt = db.prepareStatement(studIDQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            rs.last();
            studID = rs.getInt(1);
        }

        // Calculate Total
        double total = 0.0;
        String query2 = "SELECT SUM(CAST(f.attempted AS FLOAT)/f.total*f.cat_weight*0.01)*100" +
                        "  FROM (" +
                        "SELECT a.cat_name, a.cat_weight, SUM(a.attempted) AS attempted, SUM(t.total) AS total" +
                        "  FROM (SELECT c.cat_name, c.cat_weight, SUM(g.score) AS attempted" +
                        "          FROM category AS c" +
                        "              JOIN item AS i" +
                        "                  ON (c.cat_id=i.cat_id)" +
                        "              JOIN grade AS g" +
                        "                  ON (i.item_id=g.item_id)" +
                        "         WHERE g.student_id=? AND c.class_id=?" +
                        "         GROUP BY c.cat_name, c.cat_weight) as a" +
                        "      JOIN (SELECT c.cat_name, SUM(i.item_points_worth) AS total" +
                        "              FROM category AS c" +
                        "                  JOIN item AS i" +
                        "                      ON (c.cat_id=i.cat_id)" +
                        "             WHERE c.class_id=?" +
                        "             GROUP BY c.cat_name) AS t" +
                        "          ON a.cat_name=t.cat_name" +
                        " GROUP BY a.cat_name, a.cat_weight) AS f";
        try (PreparedStatement stmt = db.prepareStatement(query2, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, studID);
            stmt.setInt(2, this.currID);
            stmt.setInt(3, this.currID);
            ResultSet rs = stmt.executeQuery();
            rs.last();
            if(rs.getRow() == 0)
                total = 0.0;
            else
                total = rs.getFloat(1);

            if (func == 1) {
                System.out.format("%-20.20s %-20.20s\n", "Total Grade", rs.getFloat(1));
                return;
            }
        }

        String query3 = "SELECT student_name FROM student WHERE username=?";
        try (PreparedStatement stmt = db.prepareStatement(query3, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                System.out.format("%-20.20s %-20.20s %-20.20s", rs.getString(1), attempted, total);
        }
    }

    /**
     * @throws SQLException
     */
    @Command
    public void gradebook() throws SQLException {
        // Get all students in class
        String query = "SELECT s.student_id, s.username, s.student_name" +
                       "  FROM student AS s" +
                       "      JOIN student_class AS sc" +
                       "          ON (s.student_id=sc.student_id)" +
                       "WHERE sc.class_id=?";
        try (PreparedStatement stmt = db.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, this.currID);
            ResultSet rs = stmt.executeQuery();
            System.out.format("%-20.20s %-20.20s %-20.20s", "Student", "Attempted", "Total");
            while (rs.next()){
                calcAttemptedAndTotal(rs.getString(2), 2);
            }
            System.out.println();
        }
    }

    /**
     * Entry point.
     *
     * @param args
     * @throws IOException
     * @throws SQLException
     */
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
