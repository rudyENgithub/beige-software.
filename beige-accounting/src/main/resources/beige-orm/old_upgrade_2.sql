delete from ACCOUNT where ITSID='COILSB';
insert into ACCOUNT (NORMALBALANCETYPE, ITSID, ITSNUMBER, ITSNAME, ISUSED, ITSTYPE, SUBACCTYPE, ISCREATEDBYUSER, DESCRIPTION) values (0, 'COGL', '5110', 'Cost Of Goods Loss/Stole/Broken', 1, 4, 2001, 0, null);
insert into ACCENTRIESSOURCESLINE (ITSID, ITSOWNER, FILENAME, ITSVERSION, SOURCETYPE, SETCODE, ISUSED, ENTRIESSOURCETYPE, ENTRIESACCOUNTINGTYPE, SOURCEIDNAME, DESCRIPTION) values (16, 1, 'GdLossCoglFifoLifoInvCat', 1462867931627, 11, 'InvItemCategory', 0, 0, 1, 'GOODSLOSS.ITSID', 'GoodsLoss, COGL FIFO/LIFO, Debit COGL.InvItemCategory Credit Inventory.InvItemCategory');
update DATABASEINFO set DATABASEVERSION=2, DESCRIPTION='BeigeAccounting version 2' where DATABASEID=1;