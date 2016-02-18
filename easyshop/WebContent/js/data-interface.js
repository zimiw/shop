var DI = {};

//DI.baseURL = "http://192.168.0.105:8088/easyshop2"
DI.baseURL = ".."
// 注册相关
DI.login = DI.baseURL + "/personal/login";
DI.sendPhoneMessage = DI.baseURL + "/personal/sendPhoneMessage";
DI.addPersonal = DI.baseURL + "/personal/addPersonal";
DI.checkIsUnusedPhone = DI.baseURL + "/personal/checkIsUnusedPhone";
// 首页相关
DI.getHomeInfo = DI.baseURL + "/home/getHomeInfo";
DI.getItems = DI.baseURL+"/home/getItems";
DI.getHotKey = DI.baseURL+"/home/getHot";
DI.getBanner = DI.baseURL+"/home/getbanner";
DI.getLimit = DI.baseURL+"/home/getLimit";
DI.getSpecial = DI.baseURL+"/home/getSpecial";
DI.getAdviser = DI.baseURL+"/home/getAdviser";
DI.getHotSell = DI.baseURL+"/home/getHotSell";
DI.getWin = DI.baseURL+"/home/getWin";
DI.getLogedUser = DI.baseURL+"/home/getCurrentUser";
DI.logout = DI.baseURL+"/personal/logout";
// 商品详情
DI.getDetailInfo = DI.baseURL+"/front/detail/getDetailInfo";
DI.getcatalogItems = DI.baseURL+"/front/detail/getcatalogItems";
DI.getPrice = DI.baseURL+"/front/detail/getPrice";
DI.queryCommentsbyproductID = DI.baseURL+"/comment/queryCommentsbyproductID";
DI.getCommentTypes=DI.baseURL+"/comment/getCommentType";
DI.changeOrder=DI.baseURL+"/../product/changeOrder";
// 个人中心
DI.getUserInfo = DI.baseURL+"/personal/getUserInfo";
DI.getRecent = DI.baseURL+"/personalorder/getRecent";
DI.getFavorite = DI.baseURL+"/personal/getFavorite";
DI.getFavoriteAll = DI.baseURL+"/personal/getFavoriteAll";
DI.getCart = DI.baseURL+"/personal/getCart";
DI.deleteFavorite = DI.baseURL+"/personal/deleteFavorite";
DI.addFavorite = DI.baseURL+"/personal/addFavorite";
DI.getMyOrder = DI.baseURL+"/personal/getMyOrder";
DI.getMyOrderNum = DI.baseURL+"/personal/getMyOrderNum";
DI.getFullPersonlInfoById = DI.baseURL+"/personal/getFullPersonlInfoById";
DI.validateSendPhoneMessage = DI.baseURL+"/personal/validateSendPhoneMessage";
DI.updatePhone = DI.baseURL+"/personal/updatePhone";
DI.sendEmailMessage = DI.baseURL+"/personal/sendEmailMessage";
DI.sendPhoneMessage=DI.baseURL+"/personal/sendPhoneMessage";
DI.getFullPersonlInfoById=DI.baseURL+"/personal/getFullPersonlInfoById";
DI.validateSendEmailMessage = DI.baseURL+"/personal/validateSendEmailMessage";
DI.updateEmail = DI.baseURL+"/personal/updateEmail";
DI.checkIsCorrectPassword = DI.baseURL+"/personal/checkIsCorrectPassword";
DI.updatePassword = DI.baseURL+"/personal/updatePassword";
DI.unbindEmail = DI.baseURL+"/personal/unbindEmail";
DI.saveFullPersonlInfoById = DI.baseURL+"/personal/saveFullPersonlInfoById";
// 订单相关
DI.getOrders = DI.baseURL+"/order/getOrders";
DI.getOrdersByStatus = DI.baseURL+"/order/getOrdersByStatus";
DI.getCountByStatus = DI.baseURL+"/order/getCountByStatus";
DI.getAllCountByStatus = DI.baseURL+"/order/getAllCountByStatus";
DI.getOrderDetail = DI.baseURL+"/order/getOrderDetail";
DI.getOrderByOrderId = DI.baseURL+"/order/getOrderByOrderId";
DI.getOrderAndProduct = DI.baseURL+"/personalorder/getOrderAndProduct";
DI.addToOrder = DI.baseURL+"/shopcar/addToOrder";
DI.orderChange = DI.baseURL+"/personalchange/orderChange";
DI.orderReturn = DI.baseURL+"/personalchange/orderReturn";
// 地址相关
DI.getAddress = DI.baseURL+"/shopcar/getAddress";
DI.addAddress = DI.baseURL+"/shopcar/addAddress";
DI.modifyAddress = DI.baseURL+"/shopcar/modifyAddress";
DI.delAddress = DI.baseURL+"/shopcar/delAddress";
DI.setDefaultAddress = DI.baseURL+"/shopcar/setDefaultAddress";
// 购物车相关
DI.checkShoppingcart = DI.baseURL+"/shopcar/checkShoppingcart";
DI.delShoppingcart = DI.baseURL+"/shopcar/delShoppingcart";
DI.modifyNumber = DI.baseURL+"/shopcar/modifyNumber";
DI.modifySelected = DI.baseURL+"/shopcar/modifySelected";
DI.addShoppingcart = DI.baseURL+"/shopcar/addShoppingcart";
DI.checkSelected = DI.baseURL+"/shopcar/checkSelected";
DI.directAddToSelected = DI.baseURL+"/shopcar/directAddToSelected";
// 列表相关
DI.queryBrandByCatalogId = DI.baseURL+"/catalog/queryBrandByCatalogId";
DI.getProducts = DI.baseURL+"/catalog/getProducts";
DI.manualCheckIsHasPaid = DI.baseURL+"/charge/manualCheckIsHasPaid";
DI.getReturnDetail = DI.baseURL+"/personalorder/getReturnDetail";

// 获取热词
DI.getHotdictByStatus = DI.baseURL+"/hotdict/getHotdictByStatus";






















