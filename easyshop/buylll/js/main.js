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

Buylll.baseUrl = "http://10.24.47.96:8088/easyshop2";
Buylll.DI = {
    "getCurrentUserInfo": Buylll.baseUrl + "/home/getCurrentUserInfo",
    "getHomeInfo": Buylll.baseUrl + "/home/getHomeInfo"
}

$(function(){
    Buylll.upTop();
    
    $("body").on("keyup", ".ipt_num", function(e){
        var reg = /^(48|49|50|51|52|53|54|55|56|57|96|97|98|99|100|101|102|103|104|105)$/;
        if(!reg.test(e.keyCode)) console.log("error");
    });
    
    var commonCtrl = avalon.define({
        $id: "common",
        logged: false,
        uid: "",
        uname: "",
        productNum: 0,
        keywords: [],
        initHeader: function(){
            if(localStorage.userid){
                commonCtrl.logged = true;
                commonCtrl.uid = localStorage.userid;
                commonCtrl.uname = localStorage.username;
            }else{
                commonCtrl.logged = false;
            }
//          $.ajax({
//              type: "post",
//              url: "获取用户信息",
//              dataType: "json",
//              success: function(data){
//                  commonCtrl.productNum = 0;
//                  commonCtrl.keywords = 0;
//              },
//              error: function(err){
//                  console.log(err);
//              }
//          });
        },
        initNav: function(){
//          $.ajax({
//              type: "post",
//              url: "fakeData/home.json", // 根据需要换成实际url
//              dataType: "json",
//              success: function(data){
//                  
//              },
//              error: function(err){
//                  console.log(err);
//              }
//          });
            
        }
    });
    avalon.scan();
});
