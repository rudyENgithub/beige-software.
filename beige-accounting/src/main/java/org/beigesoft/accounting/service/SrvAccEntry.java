package org.beigesoft.accounting.service;

/*
 * Beigesoft ™
 *
 * Licensed under the Apache License, Version 2.0
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.accounting.model.CmprAccSourcesByType;
import org.beigesoft.accounting.persistable.IDoc;
import org.beigesoft.accounting.persistable.Account;
import org.beigesoft.accounting.persistable.AccountingEntry;
import org.beigesoft.accounting.persistable.AccEntriesSourcesLine;
import org.beigesoft.orm.service.ISrvOrm;
import org.beigesoft.orm.service.ISrvDatabase;
import org.beigesoft.orm.model.IRecordSet;

/**
 * <p>Business service for accounting entries.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class SrvAccEntry<RS> implements ISrvAccEntry {

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>Date Formatter.</p>
   **/
  private DateFormat dateFormatter;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>Balance service.</p>
   **/
  private ISrvBalance srvBalance;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Business service for code - java type map.</p>
   **/
  private ISrvTypeCode srvTypeCode;

  /**
   * <p>Lazy initialized SQL queries map.</p>
   **/
  private final Map<String, String> queries = new HashMap<String, String>();

  /**
   * <p>Comparator for accounting entries source.</p>
   **/
  private CmprAccSourcesByType cmprAccSourcesByType =
    new CmprAccSourcesByType();

  /**
   * <p>minimum constructor.</p>
   **/
  public SrvAccEntry() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSrvOrm ORM service
   * @param pSrvDatabase Database service
   * @param pSrvTypeCode service for code - java type map of material holders
   * @param pSrvAccSettings AccSettings service
   * @param pSrvBalance Balance service
   * @param pSrvI18n I18N service
   * @param pDateFormatter for description
   **/
  public SrvAccEntry(final ISrvOrm<RS> pSrvOrm,
      final ISrvDatabase<RS> pSrvDatabase, final ISrvTypeCode pSrvTypeCode,
        final ISrvAccSettings pSrvAccSettings, final ISrvBalance pSrvBalance,
          final ISrvI18n pSrvI18n, final DateFormat pDateFormatter) {
    this.srvDatabase = pSrvDatabase;
    this.srvBalance = pSrvBalance;
    this.srvOrm = pSrvOrm;
    this.srvTypeCode = pSrvTypeCode;
    this.srvAccSettings = pSrvAccSettings;
    this.srvI18n = pSrvI18n;
    this.dateFormatter = pDateFormatter;
  }

  /**
   * <p>Make accounting entries for document.</p>
   * @param pAddParam additional param
   * @param pEntity a document
   * @throws Exception - an exception
   **/
  @Override
  public final void makeEntries(final Map<String, Object> pAddParam,
    final IDoc pEntity) throws Exception {
    Calendar calCurrYear = Calendar.getInstance();
    calCurrYear.setTime(getSrvAccSettings().lazyGetAccSettings()
      .getCurrentAccYear());
    calCurrYear.set(Calendar.MONTH, 0);
    calCurrYear.set(Calendar.DAY_OF_MONTH, 1);
    calCurrYear.set(Calendar.HOUR_OF_DAY, 0);
    calCurrYear.set(Calendar.MINUTE, 0);
    calCurrYear.set(Calendar.SECOND, 0);
    calCurrYear.set(Calendar.MILLISECOND, 0);
    Calendar calDoc = Calendar.getInstance();
    calDoc.setTime(pEntity.getItsDate());
    calDoc.set(Calendar.MONTH, 0);
    calDoc.set(Calendar.DAY_OF_MONTH, 1);
    calDoc.set(Calendar.HOUR_OF_DAY, 0);
    calDoc.set(Calendar.MINUTE, 0);
    calDoc.set(Calendar.SECOND, 0);
    calDoc.set(Calendar.MILLISECOND, 0);
    if (calCurrYear.getTime().getTime() != calDoc.getTime().getTime()) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "wrong_year");
    }
    StringBuffer sb = new StringBuffer();
    List<AccEntriesSourcesLine> sourcesLines = srvAccSettings
      .lazyGetAccSettings().getAccEntriesSources();
    java.util.Collections.sort(sourcesLines, this.cmprAccSourcesByType);
    int i = 0;
    for (AccEntriesSourcesLine sourcesLine : sourcesLines) {
      if (sourcesLine.getIsUsed()
        && sourcesLine.getSourceType().equals(pEntity.constTypeCode())) {
        String query = lazyGetQuery(sourcesLine.getFileName());
        String idName = "ITSID";
        if (pEntity.getIdBirth() != null) {
          idName = "IDBIRTH";
        }
        query = query.replace(":IDNAME", idName);
        //for foreign document this algorithm is also right:
        String strWhereDocId = sourcesLine.getSourceIdName() + " = "
          + pEntity.getItsId().toString();
        if (query.contains(":WHEREADD")) {
          query = query.replace(":WHEREADD", " and " + strWhereDocId);
        } else if (query.contains(":WHERE")) {
          query = query.replace(":WHERE", " where " + strWhereDocId);
        }
        if (i++ > 0) {
          sb.append("\nunion all\n\n");
        }
        sb.append(query);
      }
    }
    String query = sb.toString();
    if (query.trim().length() == 0) {
      throw new ExceptionWithCode(ExceptionWithCode.CONFIGURATION_MISTAKE,
        "there_is_no_accounting_sources");
    }
    IRecordSet<RS> recordSet = null;
    try {
      recordSet = getSrvDatabase().retrieveRecords(query);
      if (recordSet.moveToFirst()) {
        Long itsDateLong = null;
        do {
          AccountingEntry accEntry = new AccountingEntry();
          accEntry.setIdDatabaseBirth(getSrvDatabase().getIdDatabase());
          if (itsDateLong == null) {
            itsDateLong = getSrvDatabase().getSrvRecordRetriever()
              .getDate(recordSet.getRecordSet(), "ITSDATE").getTime();
            getSrvBalance().handleNewAccountEntry(null, null,
              new Date(itsDateLong)); //This is for SrvBalanceStd only!!!
          }
          Date itsDate = new Date(itsDateLong++);
          accEntry.setItsDate(itsDate);
          accEntry.setSourceType(pEntity.constTypeCode());
          if (pEntity.getIdBirth() != null) { //accounting foreign document
            accEntry.setSourceId(pEntity.getIdBirth());
          } else {
            accEntry.setSourceId(pEntity.getItsId());
          }
          accEntry.setSourceDatabaseBirth(pEntity.getIdDatabaseBirth());
          String accDebitId = getSrvDatabase().getSrvRecordRetriever()
            .getString(recordSet.getRecordSet(), "ACCDEBIT");
          Account accDebit = new Account();
          accDebit.setItsId(accDebitId);
          accEntry.setAccDebit(accDebit);
          accEntry.setSubaccDebitType(getSrvDatabase().getSrvRecordRetriever()
            .getInteger(recordSet.getRecordSet(), "SUBACCDEBITTYPE"));
          accEntry.setSubaccDebitId(getSrvDatabase().getSrvRecordRetriever()
            .getLong(recordSet.getRecordSet(), "SUBACCDEBITID"));
          accEntry.setSubaccDebit(getSrvDatabase().getSrvRecordRetriever()
            .getString(recordSet.getRecordSet(), "SUBACCDEBIT"));
          accEntry.setDebit(getSrvDatabase().getSrvRecordRetriever()
            .getBigDecimal(recordSet.getRecordSet(), "DEBIT").setScale(
              getSrvAccSettings().lazyGetAccSettings().getCostPrecision(),
                getSrvAccSettings().lazyGetAccSettings().getRoundingMode()));
          String accCreditId = getSrvDatabase().getSrvRecordRetriever()
            .getString(recordSet.getRecordSet(), "ACCCREDIT");
          Account accCredit = new Account();
          accCredit.setItsId(accCreditId);
          accEntry.setAccCredit(accCredit);
          accEntry.setSubaccCreditType(getSrvDatabase().getSrvRecordRetriever()
            .getInteger(recordSet.getRecordSet(), "SUBACCCREDITTYPE"));
          accEntry.setSubaccCreditId(getSrvDatabase().getSrvRecordRetriever()
            .getLong(recordSet.getRecordSet(), "SUBACCCREDITID"));
          accEntry.setSubaccCredit(getSrvDatabase().getSrvRecordRetriever()
            .getString(recordSet.getRecordSet(), "SUBACCCREDIT"));
          accEntry.setCredit(getSrvDatabase().getSrvRecordRetriever()
            .getBigDecimal(recordSet.getRecordSet(), "CREDIT").setScale(
              getSrvAccSettings().lazyGetAccSettings().getCostPrecision(),
                getSrvAccSettings().lazyGetAccSettings().getRoundingMode()));
          String docId = pEntity.getItsId().toString();
          if (pEntity.getIdBirth() != null) {
            docId = pEntity.getIdBirth().toString();
          }
          accEntry.setDescription(getSrvI18n().getMsg(pEntity.getClass()
              .getSimpleName() + "short") + " #" + pEntity.getIdDatabaseBirth()
                + "-" + docId + ", " + getDateFormatter()
              .format(pEntity.getItsDate()) + " " + pEntity.getDescription());
          this.srvOrm.insertEntity(accEntry);
        } while (recordSet.moveToNext());
      }
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
    }
    pEntity.setHasMadeAccEntries(true);
    srvOrm.updateEntity(pEntity);
  }

  /**
   * <p>Make accounting entries for reversing document.</p>
   * @param pAddParam additional param
   * @param pReversing a reversing document
   * @param pReversed a reversed document
   * @throws Exception - an exception
   **/
  @Override
  public final void reverseEntries(final Map<String, Object> pAddParam,
    final IDoc pReversing,
      final IDoc pReversed) throws Exception {
    List<AccountingEntry> sources = getSrvOrm().retrieveListWithConditions(
      AccountingEntry.class, " where SOURCETYPE=" + pReversing.constTypeCode()
        + " and SOURCEID=" + pReversing.getReversedId()
      + " and SOURCEDATABASEBIRTH=" + pReversing.getReversedIdDatabaseBirth());
    Long itsDateLong = null;
    for (AccountingEntry source : sources) {
      if (!source.getIdDatabaseBirth()
        .equals(getSrvDatabase().getIdDatabase())) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "can_not_reverse_foreign_acconting_entries");
      }
      if (itsDateLong == null) {
        itsDateLong = pReversing.getItsDate().getTime();
        getSrvBalance().handleNewAccountEntry(null, null,
          new Date(itsDateLong)); //This is for SrvBalanceStd only!!!
      }
      AccountingEntry accEntry = new AccountingEntry();
      accEntry.setItsDate(new Date(itsDateLong++));
      accEntry.setIdDatabaseBirth(getSrvDatabase().getIdDatabase());
      accEntry.setSourceType(pReversing.constTypeCode());
      Long reversingDocId = pReversing.getItsId();
      if (pReversing.getIdBirth() != null) {
        reversingDocId = pReversing.getIdBirth();
      }
      accEntry.setSourceId(reversingDocId);
      accEntry.setSourceDatabaseBirth(pReversing.getIdDatabaseBirth());
      accEntry.setAccDebit(source.getAccDebit());
      accEntry.setSubaccDebitType(source.getSubaccDebitType());
      accEntry.setSubaccDebitId(source.getSubaccDebitId());
      accEntry.setSubaccDebit(source.getSubaccDebit());
      if (source.getDebit() != null) {
        accEntry.setDebit(source.getDebit().negate());
      }
      accEntry.setAccCredit(source.getAccCredit());
      accEntry.setSubaccCreditType(source.getSubaccCreditType());
      accEntry.setSubaccCreditId(source.getSubaccCreditId());
      accEntry.setSubaccCredit(source.getSubaccCredit());
      if (source.getCredit() != null) {
        accEntry.setCredit(source.getCredit().negate());
      }
      accEntry.setDescription(getSrvI18n().getMsg("made_at") + " "
        + getDateFormatter().format(new Date()) + " " + getSrvI18n()
          .getMsg("by") + " " + getSrvI18n().getMsg(pReversing.getClass()
            .getSimpleName() + "short") + " #" + pReversing.getIdDatabaseBirth()
              + "-" + reversingDocId + ", " + getDateFormatter()
                .format(pReversing.getItsDate())
              // reversed from  current DB:
              + ", " + getSrvI18n().getMsg("reversed_entry_n") + source
            .getIdDatabaseBirth() + "-" + source.getItsId()
          + " " + pReversing.getDescription());
      accEntry.setReversedId(source.getItsId());
      accEntry.setReversedIdDatabaseBirth(source.getIdDatabaseBirth());
      srvOrm.insertEntity(accEntry);
      source.setDescription(source.getDescription().trim()
        + " " + getSrvI18n().getMsg("reversing_entry_n") + accEntry
          .getIdDatabaseBirth() + "-"
            + accEntry.getItsId()); // new in current DB
      source.setReversedId(accEntry.getItsId());
      source.setReversedIdDatabaseBirth(accEntry.getIdDatabaseBirth());
      srvOrm.updateEntity(source);
    }
    pReversing.setHasMadeAccEntries(true);
    srvOrm.updateEntity(pReversing);
  }

  /**
   * <p>Retrieve accounting entries for document.</p>
   * @param pAddParam additional param
   * @param pEntity a document
   * @throws Exception - an exception
   **/
  @Override
  public final List<AccountingEntry> retrieveAccEntriesFor(
    final Map<String, Object> pAddParam,
      final IDoc pEntity) throws Exception {
    Long docId = pEntity.getItsId();
    if (pEntity.getIdBirth() != null) {
      docId = pEntity.getIdBirth();
    }
    List<AccountingEntry> result = getSrvOrm().retrieveListWithConditions(
      AccountingEntry.class, " where SOURCETYPE=" + pEntity.constTypeCode()
        + " and SOURCEID=" + docId + " and SOURCEDATABASEBIRTH="
          + pEntity.getIdDatabaseBirth());
    return result;
  }

  /**
   * <p>Make accounting entries for all documents.
   * It find out date of first document that has no entries, then
   * make request for all documents since that date.
   * </p>
   * @param pAddParam additional param
   * @throws Exception - an exception
   **/
  @Override
  public final void makeEntriesAll(
    final Map<String, Object> pAddParam) throws Exception {
    //TODO
    throw new Exception("Doesn't implemented yet");
  }

  //Utils:
  /**
   * <p>Query loader.</p>
   * @param pFileName File Name
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuery(final String pFileName) throws Exception {
    if (this.queries.get(pFileName) == null) {
      String flName = "/" + "accounting" + "/"
        + "journalEntries" + "/" + pFileName + ".sql";
      this.queries.put(pFileName, loadString(flName));
    }
    return this.queries.get(pFileName);
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = SrvAccEntry.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = SrvAccEntry.class.getResourceAsStream(pFileName);
        byte[] bArray = new byte[inputStream.available()];
        inputStream.read(bArray, 0, inputStream.available());
        return new String(bArray, "UTF-8");
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }
    }
    return null;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }

  /**
   * <p>Geter for srvDatabase.</p>
   * @return ISrvDatabase
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

  /**
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final void setSrvAccSettings(final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }

  /**
   * <p>Getter for srvBalance.</p>
   * @return ISrvBalance
   **/
  public final ISrvBalance getSrvBalance() {
    return this.srvBalance;
  }

  /**
   * <p>Setter for srvBalance.</p>
   * @param pSrvBalance reference
   **/
  public final void setSrvBalance(final ISrvBalance pSrvBalance) {
    this.srvBalance = pSrvBalance;
  }

  /**
   * <p>Getter for srvTypeCode.</p>
   * @return ISrvTypeCode
   **/
  public final ISrvTypeCode getSrvTypeCode() {
    return this.srvTypeCode;
  }

  /**
   * <p>Setter for srvTypeCode.</p>
   * @param pSrvTypeCode reference
   **/
  public final void setSrvTypeCode(final ISrvTypeCode pSrvTypeCode) {
    this.srvTypeCode = pSrvTypeCode;
  }

  /**
   * <p>Getter for queries.</p>
   * @return Map<String, String>
   **/
  public final Map<String, String> getQueries() {
    return this.queries;
  }

  /**
   * <p>Getter for cmprAccSourcesByType.</p>
   * @return CmprAccSourcesByType
   **/
  public final CmprAccSourcesByType getCmprAccSourcesByType() {
    return this.cmprAccSourcesByType;
  }

  /**
   * <p>Setter for cmprAccSourcesByType.</p>
   * @param pCmprAccSourcesByType reference
   **/
  public final void setCmprAccSourcesByType(
    final CmprAccSourcesByType pCmprAccSourcesByType) {
    this.cmprAccSourcesByType = pCmprAccSourcesByType;
  }

  /**
   * <p>Getter for srvI18n.</p>
   * @return ISrvI18n
   **/
  public final ISrvI18n getSrvI18n() {
    return this.srvI18n;
  }

  /**
   * <p>Setter for srvI18n.</p>
   * @param pSrvI18n reference
   **/
  public final void setSrvI18n(final ISrvI18n pSrvI18n) {
    this.srvI18n = pSrvI18n;
  }

  /**
   * <p>Getter for dateFormatter.</p>
   * @return DateFormat
   **/
  public final DateFormat getDateFormatter() {
    return this.dateFormatter;
  }

  /**
   * <p>Setter for dateFormatter.</p>
   * @param pDateFormatter reference
   **/
  public final void setDateFormatter(final DateFormat pDateFormatter) {
    this.dateFormatter = pDateFormatter;
  }
}
