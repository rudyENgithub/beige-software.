select 10 as SOURCETYPE, PAYMENTFROM.:IDNAME as SOURCEID, PAYMENTFROM.ITSDATE, PAYMENTFROM.ACCCASH as ACCDEBIT,
SUBACCCASHTYPE as SUBACCDEBITTYPE, SUBACCCASHID as SUBACCDEBITID, SUBACCCASH as SUBACCDEBIT, PAYMENTFROM.ITSTOTAL as DEBIT,
'AccReceivable' as ACCCREDIT, 2004  as SUBACCCREDITTYPE, DEBTORCREDITOR.ITSID as SUBACCCREDITID, DEBTORCREDITOR.ITSNAME as SUBACCCREDIT, PAYMENTFROM.ITSTOTAL as CREDIT
from PAYMENTFROM
join SALESINVOICE on SALESINVOICE.ITSID = PAYMENTFROM.SALESINVOICE
join DEBTORCREDITOR on DEBTORCREDITOR.ITSID = SALESINVOICE.CUSTOMER
where PAYMENTFROM.REVERSEDID is null and PAYMENTFROM.HASMADEACCENTRIES = 0 and SALESINVOICE.HASMADEACCENTRIES = 1 :WHEREADD
