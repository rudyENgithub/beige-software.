select ACCOUNT.ITSID, SUBACCOUNTLINE.SUBACCID  
from ACCOUNT
left join SUBACCOUNTLINE on SUBACCOUNTLINE.ITSOWNER=ACCOUNT.ITSID 
where ISUSED=1