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

import java.math.BigDecimal;

import org.beigesoft.persistable.APersistableBaseNameVersion;

/**
 * <pre>
 * Model of invItem.
 * Version, changed time algorithm cause check dirty of
 * calculated from it (derived) records.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class InvItem extends APersistableBaseNameVersion {

  /**
   * <p>Merchandise or stock in trade type ID.</p>
   **/
  public static final Long MERCHANDISE_ID = 1L;

  /**
   * <p>Raw materials type ID.</p>
   **/
  public static final Long MATERIAL_ID = 2L;

  /**
   * <p>Work in process type ID.</p>
   **/
  public static final Long WORK_IN_PROGRESS_ID = 3L;

  /**
   * <p>Finished products type ID.</p>
   **/
  public static final Long FINISHED_PRODUCT_ID = 4L;

  /**
   * <p>Supplies that physically become a part of
   * the item intended for sale type ID.</p>
   **/
  public static final Long SUPPLIES_PART_OF_PRODUCT_ID = 5L;

  /**
   * <p>Its category.
   * It used for filter list of invItems.</p>
   **/
  private InvItemCategory itsCategory;

  /**
   * <p>Default unit of measure.</p>
   **/
  private UnitOfMeasure defUnitOfMeasure;

  /**
   * <p>Inventory Item Type according the law.
   * For US these are Merchandise or stock in trade,
   * Raw materials, Work in process, Finished products, Supplies that physically
   * become a part of the item intended for sale.</p>
   **/
  private InvItemType itsType;

  /**
   * <p>Known cost used for COGS.
   * It may includes of all direct and indirect
   * costs that allowed in inventory.</p>
   **/
  private BigDecimal knownCost;

  /**
   * <p>Tax category e.g. "NY: tax1 10%, tax2 5%".</p>
   **/
  private InvItemTaxCategory taxCategory;

  //Simple getters and setters:
  /**
   * <p>Geter for itsType.</p>
   * @return InvItemType
   **/
  public final InvItemType getItsType() {
    return this.itsType;
  }

  /**
   * <p>Setter for itsType.</p>
   * @param pItsType reference
   **/
  public final void setItsType(final InvItemType pItsType) {
    this.itsType = pItsType;
  }

  /**
   * <p>Geter for defUnitOfMeasure.</p>
   * @return UnitOfMeasure
   **/
  public final UnitOfMeasure getDefUnitOfMeasure() {
    return this.defUnitOfMeasure;
  }

  /**
   * <p>Setter for defUnitOfMeasure.</p>
   * @param pDefUnitOfMeasure reference
   **/
  public final void setDefUnitOfMeasure(final UnitOfMeasure pDefUnitOfMeasure) {
    this.defUnitOfMeasure = pDefUnitOfMeasure;
  }

  /**
   * <p>Geter for itsCategory.</p>
   * @return InvItemCategory
   **/
  public final InvItemCategory getItsCategory() {
    return this.itsCategory;
  }

  /**
   * <p>Setter for itsCategory.</p>
   * @param pItsCategory reference
   **/
  public final void setItsCategory(final InvItemCategory pItsCategory) {
    this.itsCategory = pItsCategory;
  }

  /**
   * <p>Geter for knownCost.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getKnownCost() {
    return this.knownCost;
  }

  /**
   * <p>Setter for knownCost.</p>
   * @param pKnownCost reference
   **/
  public final void setKnownCost(final BigDecimal pKnownCost) {
    this.knownCost = pKnownCost;
  }

  /**
   * <p>Geter for taxCategory.</p>
   * @return InvItemTaxCategory
   **/
  public final InvItemTaxCategory getTaxCategory() {
    return this.taxCategory;
  }

  /**
   * <p>Setter for taxCategory.</p>
   * @param pTaxCategory reference
   **/
  public final void setTaxCategory(final InvItemTaxCategory pTaxCategory) {
    this.taxCategory = pTaxCategory;
  }
}
