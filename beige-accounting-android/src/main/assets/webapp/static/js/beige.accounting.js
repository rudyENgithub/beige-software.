/*
 * Beigesoft ™
 *
 * Licensed under the Apache License, Version 2.0
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

function setCostUom(cost, uomId, uomName, idDomBasePicker) {
  var whoPicking = cnvState["Who Picking"][idDomBasePicker];
  if (cost != null) {
    var itsPriceVisible = document.getElementById(whoPicking["pickingEntity"] + "itsPriceVisible");
    if(itsPriceVisible == null) {
      itsPriceVisible = document.getElementById(whoPicking["pickingEntity"] + "itsCostVisible");
    }
    if(itsPriceVisible != null) {
      itsPriceVisible.value = cost;
    }
    var itsPrice = document.getElementById(whoPicking["pickingEntity"] + "itsPrice");
    if(itsPrice == null) {
      itsPrice = document.getElementById(whoPicking["pickingEntity"] + "itsCost");
    }
    if(itsPrice != null) {
      itsPrice.value = cost;
    }
  }
  document.getElementById(whoPicking["pickingEntity"] + "unitOfMeasureId").value = uomId;
  var unitOfMeasureAppearanceVisible = document.getElementById(whoPicking["pickingEntity"] + "unitOfMeasureAppearanceVisible");
  unitOfMeasureAppearanceVisible.value = uomName;
  unitOfMeasureAppearanceVisible.onchange();
  calculateTotal(whoPicking["pickingEntity"]);
};

function openPickerSubacc(entitySimpleName, accName, subaccName, paramMobile) {
  var inpAccId = document.getElementById(entitySimpleName + accName + "Id");
  if (inpAccId.value == "") {
    showError(MSGS['choose_account_first']);
  } else {
    openEntityPicker('SubaccountLine', entitySimpleName, subaccName, "&fltordPitsOwnerValId='"
      + inpAccId.value + "'&fltordPitsOwnerOpr=eq&fltordPforcedFor=itsOwner&mobile=" + paramMobile);
  }
};

function selectSubacc(subaccId, subaccType, subaccAppearance, idDomBasePicker) {
  var whoPicking = cnvState["Who Picking"][idDomBasePicker];
  document.getElementById(whoPicking["pickingEntity"] + whoPicking["pickingField"] + "Appearance").value = subaccAppearance;
  document.getElementById(whoPicking["pickingEntity"] + whoPicking["pickingField"] + "Type").value = subaccType;
  document.getElementById(whoPicking["pickingEntity"] + whoPicking["pickingField"] + "Id").value = subaccId;
  var inpVisible = document.getElementById(whoPicking["pickingEntity"] + whoPicking["pickingField"] + "AppearanceVisible");
  inpVisible.value = subaccAppearance;
  inpVisible.onchange();
  document.getElementById(idDomBasePicker+"Dlg").close();
};

/**
 * <p>Select sub-account simple implementation.
 * Sub-account picker is enable if account is chosen despite of
 * it no has sub-account.
 * </p>
 **/
function clearSubacc(entitySimpleName, accName, subaccName) {
  var isDisabled = (document.getElementById(entitySimpleName + accName + "Id").value == '');
  document.getElementById(entitySimpleName + subaccName + "Choose").disabled = isDisabled;
  document.getElementById(entitySimpleName + subaccName + "Clear").disabled = isDisabled;
  document.getElementById(entitySimpleName + subaccName + "Appearance").value = "";
  document.getElementById(entitySimpleName + subaccName + "Type").value = "";
  document.getElementById(entitySimpleName + subaccName + "Id").value = "";
  var inpVisible = document.getElementById(entitySimpleName + subaccName + "AppearanceVisible");
  inpVisible.value = "";
  inpVisible.onchange();
};

function clearSubaccLine(entitySimpleName) {
  document.getElementById(entitySimpleName + "subaccId").setAttribute("value", "");
  document.getElementById(entitySimpleName + "subaccNameAppearance").setAttribute("value", "");
  document.getElementById(entitySimpleName + "subaccNameAppearanceVisible").setAttribute("value", "");
};

function selectAccSubacc(entityId, entityAppearance, idDomBasePicker) {
  var whoPicking = cnvState["Who Picking"][idDomBasePicker];
  document.getElementById(whoPicking["pickingEntity"] +"subaccId").setAttribute("value", entityId);
  document.getElementById(whoPicking["pickingEntity"] +"subaccNameAppearance").setAttribute("value", entityAppearance);
  var inpAppearanceVisible = document.getElementById(whoPicking["pickingEntity"] + "subaccNameAppearanceVisible");
  inpAppearanceVisible.value = entityAppearance;
  inpAppearanceVisible.onchange();
  document.getElementById(idDomBasePicker+"Dlg").close();
};

function calculateTotalTax(nameEntity, totalGross) {
  var inpAllowance = document.getElementById(nameEntity + "allowance");
  var inpPlusAmount = document.getElementById(nameEntity + "plusAmount");
  var inpPercentage = document.getElementById(nameEntity + "itsPercentage");
  var allowance = parseFloat(inpAllowance.value);
  var plusAmount = parseFloat(inpPlusAmount.value);
  var itsPercentage = parseFloat(inpPercentage.value);
  var inpTotal = document.getElementById(nameEntity + "itsTotal");
  var total = plusAmount + (totalGross - allowance) * itsPercentage / 100;
  inpTotal.value = total.toFixed(2);
  inputHasBeenChanged(inpTotal);
};

function clearWageTaxes(nameEntity) {
  var inpTotalWageTaxes = document.getElementById(nameEntity + "totalWageTaxes");
  var inpTotalWageTaxesVisible = document.getElementById(nameEntity + "totalWageTaxesVisible");
  inpTotalWageTaxes.value = 0;
  inpTotalWageTaxesVisible.value = 0;
  inputHasBeenChanged(inpTotalWageTaxesVisible);
};

function tryToSetPercentagePlusAmount(itsPercentage, plusAmount, idDomBasePicker) {
  var whoPicking = cnvState["Who Picking"][idDomBasePicker];
  var inpPercentage = document.getElementById(whoPicking["pickingEntity"] + "itsPercentage");
  if(inpPercentage != null) {
    var inpPlusAmount = document.getElementById(whoPicking["pickingEntity"] + "plusAmount");
    if(inpPlusAmount != null) {
      inpPlusAmount.value = plusAmount;
      inputHasBeenChanged(inpPlusAmount);
    }
    inpPercentage.value = itsPercentage;
    inpPercentage.onchange();
  }
};
