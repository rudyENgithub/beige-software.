package org.beigesoft.orm.service;

/*
 * Beigesoft ™
 *
 * Licensed under the Apache License, Version 2.0
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

import org.beigesoft.orm.model.IRecordSet;
import org.beigesoft.orm.model.ColumnsValues;

/**
 * <p>Abstraction of database service.
 * </p>
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public interface ISrvDatabase<RS> {

  /**
   * <p>Word that point to current parent dir
   * System.getProperty("user.dir").parent.</p>
   **/
  String KEY_ID_DATABASE = "idDatabase";

  /**
   * <p>TRANSACTION ISOLATION READ UNCOMMITTED.</p>
   **/
  Integer TRANSACTION_READ_UNCOMMITTED = 1;

  /**
   * <p>TRANSACTION ISOLATION READ COMMITTED.</p>
   **/
  Integer TRANSACTION_READ_COMMITTED = 2;

  /**
   * <p>TRANSACTION ISOLATION REPEATABLE READ.</p>
   **/
  Integer TRANSACTION_REPEATABLE_READ = 3;

  /**
   * <p>TRANSACTION ISOLATION SERIALIZABLE.</p>
   **/
  Integer TRANSACTION_SERIALIZABLE = 4;

  /**
   * <p>TRANSACTION NOT SUPPORTED.</p>
   **/
  Integer TRANSACTION_NONE = 5;

  /**
   * <p>SQL exception.</p>
   **/
  int SQL_EXEC_ERROR = 1154;

  /**
   * <p>Dirty read.</p>
   **/
  int DIRTY_READ = 1151;

  /**
   * <p>Exception update/insert It should be 1 row updated/inserted
   * but it was...</p>
   **/
  int ERROR_INSERT_UPDATE = 1152;

  /**
   * <p>Bad parameters e.g. isolation level.</p>
   **/
  int BAD_PARAMS = 1153;

  /**
   * <p>Getter for database ID, it is settled by SQL script on DB create.
   * Any database must has ID, int is suitable type for that cause
   * its range is enough and it's faster than String.</p>
   * @return ID database
   **/
  int getIdDatabase();

  /**
   * <p>Getter for database Version.
   * Any database must has Version for upgrade purpose.</p>
   * @return database version
   **/
  int getVersionDatabase();

  /**
   * <p>Get if RDBMS in autocommit mode.</p>
   * @return if autocommit
   * @throws Exception - an exception
   **/
  boolean getIsAutocommit() throws Exception;

  /**
   * <p>Set RDBMS autocommit mode.</p>
   * @param pIsAutocommit if autocommit
   * @throws Exception - an exception
   **/
  void setIsAutocommit(boolean pIsAutocommit) throws Exception;

  /**
   * <p>Set transaction isolation level.</p>
   * @param pLevel according ISrvOrm
   * @throws Exception - an exception
   **/
  void setTransactionIsolation(int pLevel) throws Exception;

  /**
   * <p>Get transaction isolation level.</p>
   * @return pLevel according ISrvOrm
   * @throws Exception - an exception
   **/
  int getTransactionIsolation() throws Exception;

  /**
   * <p>Create savepoint.</p>
   * @param pSavepointName savepoint name
   * @throws Exception - an exception
   **/
  void createSavepoint(String pSavepointName) throws Exception;

  /**
   * <p>Release savepoint.</p>
   * @param pSavepointName savepoint name
   * @throws Exception - an exception
   **/
  void releaseSavepoint(String pSavepointName) throws Exception;

  /**
   * <p>Rollback transaction to savepoint.</p>
   * @param pSavepointName savepoint name
   * @throws Exception - an exception
   **/
  void rollBackTransaction(String pSavepointName) throws Exception;

  /**
   * <p>Start new transaction.</p>
   * @throws Exception - an exception
   **/
  void beginTransaction() throws Exception;

  /**
   * <p>Commit transaction.</p>
   * @throws Exception - an exception
   **/
  void commitTransaction() throws Exception;

  /**
   * <p>Rollback transaction.</p>
   * @throws Exception - an exception
   **/
  void rollBackTransaction() throws Exception;

  /**
   * <p>Retrieve records from DB.</p>
   * @param pSelect query SELECT
   * @return IRecordSet record set
   * @throws Exception - an exception
   **/
  IRecordSet<RS> retrieveRecords(String pSelect) throws Exception;

  /**
   * <p>Execute any SQL query that returns no data.
   * E.g. PRAGMA, etc.</p>
   * @param pQuery query
   * @throws Exception - an exception
   **/
  void executeQuery(String pQuery) throws Exception;

  /**
   * <p>Execute SQL UPDATE that returns affected rows.
   * It is to adapt Android insert/update/delete interface.
   * </p>
   * @param pTable table name
   * @param pColumnsVals type-safe map column name - column value
   * @param pWhere where conditions e.g. "itsId=2"
   * @return row count affected
   * @throws Exception - an exception
   **/
  int executeUpdate(String pTable, ColumnsValues pColumnsVals,
    String pWhere) throws Exception;

  /**
   * <p>Execute SQL INSERT that returns affected rows.
   * It is to adapt Android insert/update/delete interface.
   * </p>
   * @param pTable table name
   * @param pColumnsVals type-safe map column name - column value
   * @return row count affected for JDBC
   * for Android -1 - error or autogenerated key
   * or ? maybe 1 for String key ?
   * @throws Exception - an exception
   **/
  long executeInsert(String pTable,
    ColumnsValues pColumnsVals) throws Exception;

  /**
   * <p>Execute SQL DELETE that returns affected rows.
   * It is to adapt Android insert/update/delete interface.
   * </p>
   * @param pTable table name
   * @param pWhere where conditions e.g. "itsId=2" or NULL -delete all
   * @return row count affected
   * @throws Exception - an exception
   **/
  int executeDelete(String pTable, String pWhere) throws Exception;

  /**
   * <p>Release resources if they is not null.</p>
   * @throws Exception - an exception
   **/
  void releaseResources() throws Exception;

  //some useful utilities that must be:
  /**
   * <p>Get recordset retriever.</p>
   * @return recordset retriever
   **/
  ISrvRecordRetriever<RS> getSrvRecordRetriever();

  /**
   * <p>Set recordset retriever.</p>
   * @param pSrvRecordRetriever recordset retriever
   **/
  void setSrvRecordRetriever(ISrvRecordRetriever<RS> pSrvRecordRetriever);

  /**
   * <p>Evaluate single Integer result.</p>
   * @param pQuery Query
   * @param pColumnName Column Name
   * @return Integer result e.g 11231 or NULL
   * @throws Exception - an exception
   */
  Integer evalIntegerResult(
    String pQuery, String pColumnName) throws Exception;

  /**
   * <p>Evaluate single Long result.</p>
   * @param pQuery Query
   * @param pColumnName Column Name
   * @return Long result e.g 11231 or NULL
   * @throws Exception - an exception
   */
  Long evalLongResult(
    String pQuery, String pColumnName) throws Exception;

  /**
   * <p>Evaluate single Float result.</p>
   * @param pQuery Query
   * @param pColumnName Column Name
   * @return Float result e.g 1.1231 or NULL
   * @throws Exception - an exception
   */
  Float evalFloatResult(
    String pQuery, String pColumnName) throws Exception;

  /**
   * <p>Evaluate single Double result.</p>
   * @param pQuery Query
   * @param pColumnName Column Name
   * @return Double result e.g 1.1231 or NULL
   * @throws Exception - an exception
   */
  Double evalDoubleResult(
    String pQuery, String pColumnName) throws Exception;

  /**
   * <p>Evaluate Double results.</p>
   * @param pQuery Query
   * @param pColumnNames Column Names
   * @return Double[] result e.g. [2.14, NULL, 111.456]
   * @throws Exception - an exception
   */
  Double[] evalDoubleResults(
    String pQuery, String[] pColumnNames) throws Exception;
}