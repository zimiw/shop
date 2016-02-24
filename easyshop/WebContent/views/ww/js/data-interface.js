/*
 * 用来存放所有的数据接口 
 * */
;var DI = DI || {};
DI.baseURL = "/easyshop";
//DI.baseURL = "http://dev.cnsuning.com:8080";

//test
DI.infoMaintainURL = DI.baseURL + "/nsf-istore-admin/store/queryStoreList.do";
	
DI.salestatistic = DI.baseURL +"/admin/report/salestatistic";
DI.loadSupplierList = DI.baseURL +"/admin/report/loadSupplierList";
DI.loadCatalogIdList = DI.baseURL +"/admin/report/loadCatalogIdList";
DI.loadBrandList  = DI.baseURL +"/admin/report/loadBrandList";

DI.salesreport = DI.baseURL +"/admin/report/salesreport";
DI.luckstatistic = DI.baseURL +"/admin/report/luckstatistic";
DI.transportstatistic = DI.baseURL +"/admin/report/transportstatistic";

DI.getRoleLists = DI.baseURL +"/role/getRoleLists"; 
DI.getAllFunctions = DI.baseURL +"/functions/getAllFunctions";  
