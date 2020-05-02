package edu.boisestate.cs410.gradebook;

import com.budhash.cliche.Command;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

public class GradeBookShell {
    private final Connection db;

    public GradeBookShell(Connection cxn) {
        db = cxn;
    }

    /*******************************
     * Below commands are general
     * CRUD operation commands.
     *******************************/

    @Command
    public void newClass(String courseNum, String term, int sectionNum, String description) throws SQLException {
//        String query = "SELECT fund_id, fund_name FROM fund";
//        try (Statement stmt = db.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//            System.out.format("Funds:%n");
//            while (rs.next()) {
//                System.out.format("%d: %s%n",
//                        rs.getInt(1),
//                        rs.getString(2));
//            }
//        }
    }

    @Command
    public void listClasses() throws SQLException {
//        String query =
//                "SELECT donor_name, donor_address,\n" +
//                        " donor_city, donor_state, donor_zip,\n" +
//                        " SUM(amount) AS total_given\n" +
//                        "FROM donor\n" +
//                        "JOIN gift USING (donor_id)\n" +
//                        "JOIN gift_fund_allocation USING (gift_id)\n" +
//                        "WHERE donor_id = ?\n" +
//                        "GROUP BY donor_id;";
//        try (PreparedStatement stmt = db.prepareStatement(query)) {
//            stmt.setInt(1, id);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (!rs.next()) {
//                    System.err.format("%d: donor does not exist%n", id);
//                    return;
//                }
//                System.out.format("%s%n", rs.getString("donor_name"));
//                System.out.format("%s%n", rs.getString("donor_address"));
//                System.out.format("%s, %s %s%n",
//                        rs.getString("donor_city"),
//                        rs.getString("donor_state"),
//                        rs.getString("donor_zip"));
//                System.out.format("Total given: %s%n",
//                        rs.getBigDecimal("total_given"));
//            }
//        }
    }

    @Command
    public void selectClass(String courseNum) throws SQLException {
//        String query = "UPDATE donor SET donor_name = ? WHERE donor_id = ?";
//        try (PreparedStatement stmt = db.prepareStatement(query)) {
//            stmt.setString(1, name);
//            stmt.setInt(2, id);
//            System.out.format("Renaming donor %d to %s%n", id, name);
//            int nrows = stmt.executeUpdate();
//            System.out.format("updated %d donors%n", nrows);
//        }
    }

    @Command
    public void selectClass(String courseNum, String term) throws SQLException {
//        String query = "UPDATE donor SET donor_name = ? WHERE donor_id = ?";
//        try (PreparedStatement stmt = db.prepareStatement(query)) {
//            stmt.setString(1, name);
//            stmt.setInt(2, id);
//            System.out.format("Renaming donor %d to %s%n", id, name);
//            int nrows = stmt.executeUpdate();
//            System.out.format("updated %d donors%n", nrows);
//        }
    }

    @Command
    public void selectClass(String courseNum, String term, int sectionNum) throws SQLException {
//        String query = "UPDATE donor SET donor_name = ? WHERE donor_id = ?";
//        try (PreparedStatement stmt = db.prepareStatement(query)) {
//            stmt.setString(1, name);
//            stmt.setInt(2, id);
//            System.out.format("Renaming donor %d to %s%n", id, name);
//            int nrows = stmt.executeUpdate();
//            System.out.format("updated %d donors%n", nrows);
//        }
    }

    @Command
    public void showClass() throws SQLException {
//        String insertGift = "INSERT INTO gift (donor_id, gift_date) VALUES (?, ?)";
//        String allocate = "INSERT INTO gift_fund_allocation (gift_id, fund_id, amount) VALUES (?, ?, ?)";
//        int giftId;
//        db.setAutoCommit(false);
//        try {
//            try (PreparedStatement stmt = db.prepareStatement(insertGift, Statement.RETURN_GENERATED_KEYS)) {
//                stmt.setInt(1, donor);
//                stmt.setString(2, date);
//                stmt.executeUpdate();
//                // fetch the generated gift_id!
//                try (ResultSet rs = stmt.getGeneratedKeys()) {
//                    if (!rs.next()) {
//                        throw new RuntimeException("no generated keys???");
//                    }
//                    giftId = rs.getInt(1);
//                    System.out.format("Creating gift %d%n", giftId);
//                }
//            }
//            try (PreparedStatement stmt = db.prepareStatement(allocate)) {
//                for (int i = 0; i < allocs.length; i += 2) {
//                    stmt.setInt(1, giftId);
//                    stmt.setInt(2, Integer.parseInt(allocs[i]));
//                    stmt.setBigDecimal(3, new BigDecimal(allocs[i + 1]));
//                    stmt.executeUpdate();
//                }
//            }
//            db.commit();
//        } catch (SQLException | RuntimeException e) {
//            db.rollback();
//            throw e;
//        } finally {
//            db.setAutoCommit(true);
//        }
    }


    /*******************************
     * Below commands are performed
     * on currently active class.
     *******************************/

    /***** Category and Item Management *****/
    @Command
    public void showCategories() throws SQLException {
//        generate(50);
    }

    @Command
    public void addCategory(String catName, float catWeight) throws SQLException {
//        generate(donors, 10);
    }

    @Command
    public void showItems() throws SQLException {
//        generate(donors, 10);
    }

    @Command
    public void addItem(String catName, String description, int points) throws SQLException {
//        generate(donors, 10);
    }

    /***** Student Management *****/

    @Command
    public void addStudent(String username, int studentID, String name) throws SQLException {
//        generate(donors, 10);
    }

    @Command
    public void addStudent(String username) throws SQLException {
//        generate(donors, 10);
    }

    @Command
    public void showStudents() throws SQLException {
//        generate(donors, 10);
    }

    @Command
    public void showStudents(String query) throws SQLException {
//        generate(donors, 10);
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