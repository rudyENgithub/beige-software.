package org.beigesoft.accounting.persistable;

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

import org.beigesoft.model.IOwned;
import org.beigesoft.accounting.persistable.base.AInvItemMovementCost;

/**
 * <pre>
 * Model of ManufacturingProcess Line of used material.
 * It is immutable.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class UsedMaterialLine extends AInvItemMovementCost
  implements IMakingWarehouseEntry, IOwned<ManufacturingProcess> {

  /**
   * <p>Product In Progress.</p>
   **/
  private ManufacturingProcess itsOwner;

  /**
   * <p>Warehouse site from (optional).
   * If it's empty (null) then withdrawal will be from the first
   * site/s that has the goods, otherwise withdrawal will be exactly
   * from this site.</p>
   **/
  private WarehouseSite warehouseSiteFo;

  /**
   * <p>Reversed line ID (if this reverse it).</p>
   **/
  private Long reversedId;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Geter for reversedId.</p>
   * @return Long
   **/
  @Override
  public final Long getReversedId() {
    return this.reversedId;
  }

  /**
   * <p>Setter for reversedId.</p>
   * @param pReversedId reference
   **/
  @Override
  public final void setReversedId(final Long pReversedId) {
    this.reversedId = pReversedId;
  }

  /**
   * <p>Constant of code type.</p>
   * @return 1003
   **/
  @Override
  public final Integer constTypeCode() {
    return 1003;
  }

  /**
   * <p>Get for document Date.</p>
   * @return Date
   **/
  @Override
  public final Date getDocumentDate() {
    return getItsOwner().getItsDate();
  }

  /**
   * <p>Get Owner Type if exist  e.g. PurchaseInvoice 1.</p>
   * @return Integer
   **/
  @Override
  public final Integer getOwnerType() {
    return new ManufacturingProcess().constTypeCode();
  }

  /**
   * <p>Get for owner's ID.</p>
   * @return Long
   **/
  @Override
  public final Long getOwnerId() {
    return this.getItsOwner().getItsId();
  }

  /**
   * <p>Getter for description.</p>
   * @return String
   **/
  @Override
  public final String getDescription() {
    return this.description;
  }

  /**
   * <p>Setter for description.</p>
   * @param pDescription reference
   **/
  @Override
  public final void setDescription(final String pDescription) {
    this.description = pDescription;
  }

  /**
   * <p>Geter for itsOwner.</p>
   * @return ManufacturingProcess
   **/
  @Override
  public final ManufacturingProcess getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final ManufacturingProcess pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for warehouseSiteFo.</p>
   * @return WarehouseSite
   **/
  public final WarehouseSite getWarehouseSiteFo() {
    return this.warehouseSiteFo;
  }

  /**
   * <p>Setter for warehouseSiteFo.</p>
   * @param pWarehouseSiteFo reference
   **/
  public final void setWarehouseSiteFo(final WarehouseSite pWarehouseSiteFo) {
    this.warehouseSiteFo = pWarehouseSiteFo;
  }
}
