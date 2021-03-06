package org.beigesoft.replicator.service;

/*
 * Beigesoft ™
 *
 * Licensed under the Apache License, Version 2.0
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

import java.util.Map;

import org.beigesoft.persistable.UserRoleTomcat;
import org.beigesoft.persistable.UserTomcat;
import org.beigesoft.exception.ExceptionWithCode;

/**
 * <p>Service to fill UserTomcat in UserRoleTomcat.</p>
 *
 * @author Yury Demidenko
 */
public class SrvEntityFieldUserTomcatRepl implements ISrvEntityFieldFiller {

  /**
   * <p>
   * Fill given field of given entity according value represented as
   * string.
   * </p>
   * @param pEntity Entity.
   * @param pFieldName Field Name
   * @param pFieldStrValue Field value
   * @param pAddParam additional params
   * @throws Exception - an exception
   **/
  @Override
  public final void fill(final Object pEntity, final String pFieldName,
    final String pFieldStrValue,
      final Map<String, Object> pAddParam) throws Exception {
    if (!UserRoleTomcat.class.isAssignableFrom(pEntity.getClass())) {
      throw new ExceptionWithCode(ExceptionWithCode
        .CONFIGURATION_MISTAKE, "It's wrong service to fill that field: "
          + pEntity + "/" + pFieldName + "/" + pFieldStrValue);
    }
    UserRoleTomcat userRoleTomcat = (UserRoleTomcat) pEntity;
    if ("NULL".equals(pFieldStrValue)) {
      userRoleTomcat.setItsUser(null);
      return;
    }
    try {
      UserTomcat ownedEntity = new UserTomcat();
      ownedEntity.setItsUser(pFieldStrValue);
      userRoleTomcat.setItsUser(ownedEntity);
    } catch (Exception ex) {
      throw new ExceptionWithCode(ExceptionWithCode
        .WRONG_PARAMETER, "Can not fill field: " + pEntity + "/" + pFieldName
          + "/" + pFieldStrValue + ", " + ex.getMessage(), ex);
    }
  }
}
