/*
 * buylll.com
 * created by Jing
 * 2015-10-01
 */

;var Buylll = Buylll || {};

Buylll.upTop = function(){
    $(".to-top").click(function(){
       $('html, body').animate({scrollTop:0}, 'slow');
    });
};
Buylll.getUrlArgs = function(){
    var args = {},
        query = location.search.substring(1),
        pairs = query.split("&"),
        pos = idx = argname = val = null;
        
    for(var i=0; i<pairs.length; i++){
        pos = pairs[i];
        idx = pos.indexOf("=");
        if(idx == -1) continue;
        argname = pos.substring(0, idx);
        val = decodeURIComponent(pos.substring(idx + 1));
        args[argname] = val;
    }
    
    return args;
};
Buylll.addShoppingcart = function(id,typeId,number){
    $.ajax({
        type: "post",
        url: DI.addShoppingcart,
        dataType: "json",
        data: {
            productId: Number(id),
            productTypeId: Number(typeId),
            number: number
        },
        success: function(data){
            console.log(data);
        },
        error: function(err){
            console.log(err);
        }
    });
};
Buylll.addFavorite = function(id){
    $.ajax({
        type: "post",
        url: DI.addFavorite,
        dataType: "json",
        data: {
            productId: id
        },
        success: function(data){
            if(data.status == "success"){
                alert("收藏成功");
            }
        },
        error: function(err){
            console.log(err);
        }
    });
};
Buylll.modifySelected = function(type, shoppingcartIds){
    $.ajax({
        type: "post",
        url: DI.modifySelected,
        data: {
            shoppingcartIds: shoppingcartIds,
            operate: type
        },
        dataType: "json",
        success: function(data){
            location.href = "balance_writeOrder.html";
        },
        error: function(err){
            console.log(err);
        }
    });
};
Buylll.uploadPreview = function(){
    var _self = this;
    
    _self.getObjectURL = function(file){
        var url = null;
        if(window.createObjectURL != undefined) {
            url = window.createObjectURL(file);
        }else if (window.URL != undefined){
            url = window.URL.createObjectURL(file);
        }else if (window.webkitURL != undefined){
            url = window.webkitURL.createObjectURL(file);
        }
        return url;
    };
    $("input[type='file']").change(function(){
        var showimg = $(this).siblings("img"),
            _this = this;
            
        showimg.attr("src", _self.getObjectURL(_this.files[0]));
    });
};

avalon.filters.area = function(country){
    var x = "";
    switch(country){
		case "hk":
		  x = "香港";
		  break;
		case "us":
		  x = "美国";
		  break;
		case "jp":
		  x = "日本";
		  break;
		case "it":
		  x = "意大利";
		  break;
		case "kp":
		  x = "韩国";
		  break;
		case "ch":
		  x = "瑞士";
		  break;
		case "th":
		  x = "泰国";
		  break;
		case "fr":
		  x = "法国";
		  break;
		case "uk":
		  x = "英国";
		  break;
		case "tw":
		  x = "台湾";
		  break;
		case "de":
		  x = "德国";
		  break;
		case "es":
		  x = "西班牙";
		  break;
		case "nl":
		  x = "荷兰";
		  break;
		case "ca":
		  x = "加拿大";
		  break;
		case "ar":
		  x = "阿根廷";
		  break;
		case "ru":
		  x = "俄罗斯";
		  break;
		case "br":
		  x = "巴西";
		  break;
	}
    return x;
};
avalon.filters.bind = function(level){
    if(level == 0){
        return "未绑定";
    }else if(level == 1){
        return "已绑定";
    }
};
avalon.filters.payway = function(way){
    if(way == "upacp_pc"){
        return "银联";
    }else if(way == "alipay_pc_direct"){
        return "支付宝";
    }else if(way == "wx_pub_qr"){
        return "微信";
    }
};
avalon.filters.popupSuccessStatus = function(status){
    return status ? "修改成功" : "修改失败";
};
avalon.filters.orderStatus = function(status){
    var val = "";
    if(status == 101){
        val = "待付款";
    }else if(status == 102){
        val = "待发货";
    }else if(status == 103){
        val = "待收货";
    }else if(status == 104){
        val = "待评价";
    }else if(status == 105){
        val = "退/换货";
    }
    return val;
};

$(function(){
    Buylll.upTop();
    
    $("body").on("keyup", ".ipt_num", function(e){
        var reg = /^(48|49|50|51|52|53|54|55|56|57|96|97|98|99|100|101|102|103|104|105)$/;
        if(!reg.test(e.keyCode)) console.log("error");
    });
    
    var commonCtrl = avalon.define({
        $id: "common",
        header: {
            logged: false,
            uname: "",
            productNum: 0,
            keyword: "",
            keywords: [],
            search: function(){
                location.href = "list.html?brand=false&name=" + commonCtrl.header.keyword;
            },
            quit: function(){
                location.href = DI.logout;
            }
        },
        navigation: [],
        initHeader: function(){
            $.ajax({
                type: "post",
                url: DI.getHotdictByStatus,
                data: {
                    status: true
                },
                dataType: "json",
                success: function(data){
                   commonCtrl.header.keywords = data;
                },
                error: function(err){
                    console.log(err);
                }
            });
            $.ajax({
          	    type:"post",
        	    dataType:"json",
        	    url:DI.getLogedUser,
        	    success:function(data){
        		    if(data.status=="success"){
                      	commonCtrl.header.logged = true;
                        commonCtrl.header.uname = data.name; 
        		    }
        	    }
            });
        },
        initNav: function(){
            $.ajax({
                type: "post",
                url: DI.getItems,
                dataType: "json",
                success: function(data){
            	    commonCtrl.navigation = data.items;
                },
                error: function(err){
                    console.log(err);
                }
            });
        }
    });
    avalon.scan();
});
