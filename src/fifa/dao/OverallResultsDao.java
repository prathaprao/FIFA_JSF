package fifa.dao;

import fifa.utilities.JDBCConnect;
import fifa.utilities.PropertiesUtilities;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class OverallResultsDao implements fifa.utilities.FIFAConstants
{
  public OverallResultsDao() {}
  
  private final String SELECT_OVERALL = "sql.overallSelect";
  private final String SELECT_OVERALL2 = "sql.overallSelect2";
  private final String SELECT_OVERALL3 = "sql.overallSelect3";
  private final String SELECT_OVERALL4 = "sql.overallSelect4";
  
  public List<Integer> getOverallResultsList(String versionId, String gameType) {
    List<Integer> results = new ArrayList();
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    JDBCConnect jdbcConnect = null;
    int wins = 0;int draws = 0;int losses = 0;
    try
    {
      jdbcConnect = new JDBCConnect();
      conn = jdbcConnect.getConnection();
      
      PropertiesUtilities propertiesUtilities = PropertiesUtilities.getInstance();
      
      if (conn != null)
      {
        String sql = null;
        

        if (((StringUtils.isEmpty(versionId)) || (versionId.equalsIgnoreCase("ALL"))) && ((StringUtils.isEmpty(gameType)) || 
          (StringUtils.equalsIgnoreCase(gameType, "A")))) {
          preparedStatement = conn.prepareStatement(propertiesUtilities.getProperty(propertiesUtilities.getMessageResource(), 
            "sql.overallSelect"));
        }
        else if ((StringUtils.isNotEmpty(versionId)) && (!versionId.equalsIgnoreCase("ALL")) && 
          (StringUtils.isNotEmpty(gameType)) && (StringUtils.equalsIgnoreCase(gameType, "A")))
        {
          sql = propertiesUtilities.getProperty(propertiesUtilities.getMessageResource(), "sql.overallSelect2");
          emptySQLCheck(propertiesUtilities, sql, "sql.overallSelect2");
          preparedStatement = conn.prepareStatement(sql);
          preparedStatement.setString(1, versionId);
        }
        else if (((StringUtils.isEmpty(versionId)) || (versionId.equalsIgnoreCase("ALL"))) && 
          (StringUtils.isNotEmpty(gameType)) && 
          (!StringUtils.equalsIgnoreCase(gameType, "A")))
        {
          sql = propertiesUtilities.getProperty(propertiesUtilities.getMessageResource(), "sql.overallSelect4");
          emptySQLCheck(propertiesUtilities, sql, "sql.overallSelect4");
          preparedStatement = conn.prepareStatement(sql);
          preparedStatement.setString(1, gameType);
        } else {
          sql = propertiesUtilities.getProperty(propertiesUtilities.getMessageResource(), "sql.overallSelect3");
          emptySQLCheck(propertiesUtilities, sql, "sql.overallSelect3");
          preparedStatement = conn.prepareStatement(sql);
          preparedStatement.setString(1, versionId);
          preparedStatement.setString(2, gameType);
        }
        


        ResultSet rs = preparedStatement.executeQuery();
        
        while (rs.next()) {
          int goalsFor = rs.getInt("goalsFor");
          int goalsAgainst = rs.getInt("goalsAgainst");
          int penaltiesFor = rs.getInt("penaltiesFor");
          int penaltiesAgainst = rs.getInt("penaltiesAgainst");
          
          if (goalsFor > goalsAgainst) {
            wins++;
          }
          else if (goalsFor < goalsAgainst) {
            losses++;

          }
          else if (penaltiesFor > penaltiesAgainst) {
            wins++;
          }
          else if (penaltiesFor < penaltiesAgainst) {
            losses++;
          } else {
            draws++;
          }
        }
        


        rs.close();
      }
    }
    catch (SQLException se) {
      System.err.println(se.getLocalizedMessage());
    }
    catch (Exception e) {
      System.err.println(e.getLocalizedMessage());
    } finally {
      if (conn != null)
        jdbcConnect.closeConnection(conn);
    }
    results.add(Integer.valueOf(wins));
    results.add(Integer.valueOf(draws));
    results.add(Integer.valueOf(losses));
    return results;
  }
  
  private void emptySQLCheck(PropertiesUtilities propertiesUtilities, String sql, String message) {
    if (StringUtils.isEmpty(sql))
    {
      System.err.println("SELECT statement not found, in the resource " + propertiesUtilities.getMessageResource() + 
        " for the property " + message);
    }
  }
}
