/*
 * 用来存放所有的数据接口 
 * */
;var DI = DI || {};
DI.baseURL = "http://localhost:8080/easyshop";
//DI.baseURL = "http://10.24.47.52:8080/easyshop";

// 查询类目
DI.getItems = DI.baseURL+"/home/getItems";
// 获取所有商品
DI.queryAllProducts = DI.baseURL+"/product/queryAllProducts";
//DI.getProducts = DI.baseURL+"/catalog/getProducts";
// 根据3级目录获取所有品牌
DI.getBrandByCatalogId = DI.baseURL+"/catalog/getBrandByCatalogId";
// 根据品牌id,获取关联的供应商
DI.getSupplierByBrandId = DI.baseURL+"/catalog/getSupplierByBrandId";
// 新增商品
DI.addProductByAll = DI.baseURL+"/product/addProductByAll";
// 编辑商品
DI.updateProductByAll = DI.baseURL+"/product/updateProductByAll";
// 查询所有订单
DI.queryOrderList = DI.baseURL+"/admin/order/queryOrderList";
// 设置上架下架
DI.setProductStatus = DI.baseURL+"/product/setProductStatus";
// 获取订单详情
DI.getOrderDetail = DI.baseURL+"/admin/order/getOrderDetail";
// 获取省市区
DI.getCitys = DI.baseURL+"/address/getCitys";
DI.getAreas = DI.baseURL+"/address/getAreas";
// 发货
DI.endExpress = DI.baseURL+"/express/endExpress";
// 轮播图获取
DI.queryBranner = DI.baseURL+"/admin/activity/queryBranner";
DI.addBranner = DI.baseURL+"/admin/activity/addBranner";
// 限时活动获取列表
DI.queryActivityList = DI.baseURL+"/admin/activity/queryActivityList";
// 限时活动设置页面获取商品信息
DI.queryActivityProductList = DI.baseURL+"/admin/activity/queryActivityProductList";
// 限时活动保存
DI.saveActivity = DI.baseURL+"/admin/activity/saveActivity";
// 删除对应的限时活动
DI.delActivity = DI.baseURL+"/admin/activity/delActivity";
// 查询活动商品属性
DI.queryActivityProductType = DI.baseURL+"/admin/activity/queryActivityProductType";
// 专场活动获取列表
DI.queryActivtySpecialtyList = DI.baseURL+"/admin/activity/queryActivtySpecialtyList";
// 专场活动保存
DI.saveActivtySpecialty = DI.baseURL+"/admin/activity/saveActivtySpecialty";
// 专场活动删除
DI.delActivtySpecialty = DI.baseURL+"/admin/activity/delActivtySpecialty";
// 热卖商品获取
DI.queyActivtyHeatOneList = DI.baseURL+"/admin/activity/queyActivtyHeatOneList";
DI.queyActivtyHeatTwoList = DI.baseURL+"/admin/activity/queyActivtyHeatTwoList";
// 热卖商品保存
DI.saveActivtyHeat = DI.baseURL+"/admin/activity/saveActivtyHeat";
// 热卖商品删除
DI.delActivtyHeat = DI.baseURL+"/admin/activity/delActivtyHeat";
// 抽奖查询
DI.queryLotteryList = DI.baseURL+"/admin/activity/queryLotteryList";
// 抽奖设置是否参与
DI.addLottery = DI.baseURL+"/admin/activity/addLottery";
// 抽奖配置保存
DI.addLotteryConf = DI.baseURL+"/admin/activity/addLotteryConf";
// 登出
DI.logout = DI.baseURL+"/user/logout";




























