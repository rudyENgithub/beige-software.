package org.beigesoft.web.servlet;

/*
 * Beigesoft ™
 *
 * Licensed under the Apache License, Version 2.0
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

import java.io.File;
import java.io.IOException;
import java.util.List;

import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;

import com.zaxxer.hikari.HikariDataSource;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.settings.MngSettings;
import org.beigesoft.test.persistable.GoodVersionTime;
import org.beigesoft.orm.service.SrvOrmSqlite;
import org.beigesoft.orm.service.ISrvOrm;
import org.beigesoft.orm.service.SrvSqlEscape;
import org.beigesoft.orm.service.HlpInsertUpdate;
import org.beigesoft.log.LoggerSimple;
import org.beigesoft.jdbc.service.SrvDatabase;
import org.beigesoft.jdbc.service.SrvRecordRetriever;

/**
 * <p>SQLite fn lock DB problem:
 * <pre>
 * java.sql.SQLException: database is locked
 * at org.sqlite.core.DB.throwex(DB.java:859)
 * at org.sqlite.core.DB.exec(DB.java:142)
 * at org.sqlite.jdbc3.JDBC3Connection.commit(JDBC3Connection.java:165)
 * at com.zaxxer.hikari.pool.ProxyConnection.commit(ProxyConnection.java:352)
 * at com.zaxxer.hikari.pool.HikariProxyConnection.commit(HikariProxyConnection.java)
 * at org.beigesoft.jdbc.service.SrvDatabase.commitTransaction(SrvDatabase.java:199)
 * at org.beigesoft.web.service.SrvWebEntity.save(SrvWebEntity.java:314)
 * at org.beigesoft.erp.servletaj.WEntity.doPost(WEntity.java:132).
 * </pre>
 *  PROBLEM IS initialize DATABASE (create tables if need) during servlet startup.
 *  Init ORM during GET/POST resolves this problem.
 * </p>
 *
 * @author Yury Demidenko
 */
@SuppressWarnings("serial")
public class WSqliteLock extends HttpServlet {

  /**
   * <p>Folder for redirected JSP, e.g. "JSP WEB-INF/jsp/".
   * Settled through init params.</p>
   **/
  private String dirJsp;

  SrvDatabase srvDatabase = null;
  
  SrvOrmSqlite<ResultSet> srvOrm = null;

  @Override
  public final void init() throws ServletException {
    this.dirJsp = getInitParameter("dirJsp");
    try {
      LoggerSimple logger = new LoggerSimple();
      logger.setIsShowDebugMessages(true);
      srvOrm = new SrvOrmSqlite<ResultSet>();
      srvDatabase = new SrvDatabase();
      srvDatabase.setLogger(logger);
      SrvRecordRetriever srvRecordRetriever = new SrvRecordRetriever();
      srvOrm.setSrvRecordRetriever(srvRecordRetriever);
      srvOrm.setSrvDatabase(srvDatabase);
      srvOrm.setLogger(logger);
      srvOrm.setHlpInsertUpdate(new HlpInsertUpdate());
      srvOrm.setSrvSqlEscape(new SrvSqlEscape());
      srvDatabase.setHlpInsertUpdate(srvOrm.getHlpInsertUpdate());
      MngSettings mngSettings = new MngSettings();
      mngSettings.setLogger(logger);
      srvOrm.setMngSettings(mngSettings);
      srvOrm.loadConfiguration("beige-orm", "persistence-sqlite.xml");
      String currDir = System.getProperty("user.dir");
      HikariDataSource ds = new HikariDataSource();
      String jdbcUrl = getInitParameter("databaseName");
      if (jdbcUrl != null && jdbcUrl.contains(ISrvOrm.WORD_CURRENT_DIR)) {
        jdbcUrl = jdbcUrl.replace(ISrvOrm.WORD_CURRENT_DIR,
          currDir + File.separator);
      } else if (jdbcUrl != null
        && jdbcUrl.contains(ISrvOrm.WORD_CURRENT_PARENT_DIR)) {
        File fcd = new File(currDir);
        jdbcUrl = jdbcUrl.replace(ISrvOrm.WORD_CURRENT_PARENT_DIR,
          fcd.getParent() + File.separator);
      }
      ds.setJdbcUrl(jdbcUrl);
      ds.setDriverClassName("org.sqlite.JDBC");
      srvDatabase.setDataSource(ds);
      //srvOrm.createTablesIfNeed(); !!!!!!!!problem
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  public final void doGet(final HttpServletRequest pReq,
    final HttpServletResponse pResp) throws ServletException, IOException {
    try {
      srvDatabase.setIsAutocommit(false);
      srvDatabase.setTransactionIsolation(SrvDatabase.TRANSACTION_READ_UNCOMMITTED);
      srvDatabase.beginTransaction();
      String nameAction = pReq.getParameter("nameAction");
      if ("create".equals(nameAction)) {
        GoodVersionTime cake = new GoodVersionTime();
        cake.setItsName("bestproduct");
        cake.setIdDatabaseBirth(999);
        srvOrm.insertEntity(cake);
      } else if ("delete".equals(nameAction)) {
        GoodVersionTime cake = srvOrm.retrieveEntityWithConditions(GoodVersionTime.class, "where ITSNAME='bestproduct'");
        if (cake != null) {
          srvOrm.deleteEntity(cake);
        }
      } else {
        throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
          "Method GET action " + nameAction + " not supported!");
      }
      List<GoodVersionTime> goods = srvOrm.retrieveList(GoodVersionTime.class);
      pReq.setAttribute("goods", goods);
      srvDatabase.commitTransaction();
      String nameRenderer = pReq.getParameter("nameRenderer");
      String path = dirJsp + "test/" + nameRenderer + ".jsp";
      RequestDispatcher rd = getServletContext().getRequestDispatcher(path);
      rd.include(pReq, pResp);
    } catch (Exception e) {
      e.printStackTrace();
      if (e instanceof ExceptionWithCode) {
        pReq.setAttribute("error_code",
          ((ExceptionWithCode) e).getCode());
        pReq.setAttribute("short_message",
          ((ExceptionWithCode) e).getShortMessage());
      } else {
        pReq.setAttribute("error_code",
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
      pReq.setAttribute("javax.servlet.error.status_code",
        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      pReq.setAttribute("javax.servlet.error.exception", e);
      pReq.setAttribute("javax.servlet.error.request_uri",
        pReq.getRequestURI());
      pReq.setAttribute("javax.servlet.error.servlet_name", this.getClass()
        .getCanonicalName());
      pResp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } finally {
      if (srvDatabase != null) {
        try {
          srvDatabase.releaseResources();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  //Simple getters and setters:
  /**
   * <p>Geter for dirJsp.</p>
   * @return String
   **/
  public final String getDirJsp() {
    return this.dirJsp;
  }

  /**
   * <p>Setter for dirJsp.</p>
   * @param pDirJsp reference
   **/
  public final void setDirJsp(final String pDirJsp) {
    this.dirJsp = pDirJsp;
  }
}
