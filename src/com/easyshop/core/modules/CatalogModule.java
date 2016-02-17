package com.easyshop.core.modules;

import com.easyshop.bean.*;
import com.easyshop.utils.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean
@At("/catalog")
@Ok("json")
@Fail("http:500")
public class CatalogModule {

    @Inject
    protected Dao dao;
    private Map<String,Object> result;

    /**
     * ===============================================================
     *
     *      新版本开始
     *
     * ===============================================================
     */

    /**
     * 根据id获取子分类的对象list 第一层类目的父id是0
     * http://localhost:8181/easyshop/catalog/getSubCatalogById?id=1
     *
     * 如果有数据返回:[{"catalogId":35,"catalogName":"时尚美妆","pid":1}, {"catalogId":39,"catalogName":"进口美食","pid":1}]
     * 没有数据的话,返回: []
     *
     * @param id
     * @return
     */
    @At
    public List<Catalogs> getSubCatalogById(@Param("id")int id){
        List<Catalogs> temp = dao.query(Catalogs.class, Cnd.where("pid","=",id));
        return temp;
    }

    /**
     * 根据第三级类目id,获取关联的品牌
     * http://localhost:8181/easyshop/catalog/getBrandByCatalogId?catalogId=35
     * [{"brandId":5,"brandName":"迪奥","catalogId":0}, {"brandId":6,"brandName":"channel","catalogId":0}]
     *	没有数据的话,返回: []
     *
     * @param catalogId
     * @return
     */
    @At
    @Filters
    public List<Brand> getBrandByCatalogId(@Param("catalogId")int catalogId){
        Sql sql = Sqls.create("SELECT  a.brandId,a.brandName  FROM brand a, brand_catalog_rel b "
                + "WHERE a.brandId=b.brandId and b.catalogId=@catalogId");
        sql.params().set("catalogId", catalogId);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Brand.class));
        dao.execute(sql);
        List<Brand> list = sql.getList(Brand.class);
        return list;
    }

    /**
     * 根据品牌id,获取关联的供应商
     * http://localhost:8181/easyshop/catalog/getSupplierByBrandId?brandId=6
     * [{"id":1,"name":"channel 供应商1"}, {"id":2,"name":"channel 供应商2"}]
     * 没有数据的话,返回: []
     *
     * @param brandId
     * @return
     */
    @At
    public List<Supplier> getSupplierByBrandId(@Param("brandId")int brandId){
        Sql sql = Sqls.create("SELECT a.id,a.name FROM supplier a, brand_supplier_rel b"
                + " WHERE a.id=b.supplierId and b.brandId=@brandId");
        sql.params().set("brandId", brandId);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Supplier.class));
        dao.execute(sql);
        List<Supplier> list = sql.getList(Supplier.class);
        return list;
    }




    /**
     * ===============================================================
     *
     *      新版本结束
     *
     * ===============================================================
     */


    /**
     * ===============================================================
     *
     *      老版本修改开始
     *
     * ===============================================================
     */
    /**
     * 前台查询商品
     * http://localhost:8181/easyshop/catalog/getProducts?currentPage=1&peerpageRows=2&name=2&catalogId=33&brandId=6&activityType=1&orderBy=3
     * {"total":16,"rows":[{"originPrice":0.0,"currentPrice":0.0,"productId":28,"name":"2222","minPrice":8.0,"activityType":0,"limitActivityStatus":0,"limitActivityTotalCount":0,"limitActivityTotalLeftCount":0,"weight":0.0,"inputDate":"2016-01-28 17:25:35","viewCount":0,"sellCount":0,"totalCount":0,"totalLeftCount":0,"status":false,"supplierId":2,"catalogId1":11,"catalogId2":22,"catalogId3":33,"catalogId":0,"isHotKey":false,"isNew":false,"brandId":6,"specialPrice":0.0,"activityId":0,"country":"america","imgs":[{"imgID":70,"imgsource":"http://buylll.oss-cn-qingdao.aliyuncs.com/xxx_1453706125797","isTopimg":true,"productId":28,"isSmallView":false,"isSizePic":false,"isDetailPic":false,"orderId":0}, {"imgID":71,"imgsource":"23","isTopimg":true,"productId":28,"isSmallView":false,"isSizePic":false,"isDetailPic":false,"orderId":0}],"number":0,"price":0.0}, {"originPrice":0.0,"currentPrice":0.01,"productId":3,"name":"雅诗兰黛鲜亮焕采精粹水（特润型）","minPrice":0.0,"activityType":0,"limitActivityStatus":0,"limitActivityTotalCount":0,"limitActivityTotalLeftCount":0,"weight":0.0,"inputDate":"2016-01-22 04:34:06","viewCount":0,"sellCount":0,"totalCount":0,"totalLeftCount":0,"status":true,"detailTitle":"","descp":"","supplierId":0,"catalogId1":0,"catalogId2":0,"catalogId3":0,"catalogId":0,"isHotKey":false,"provider":"北京美瑶","providerAddress":"北京","isNew":false,"brandId":14,"wlStatus":"","payStatus":"","orderStatus":"","unit":"瓶","specialPrice":595.0,"activityId":9,"country":"美国","external":"","imgs":[],"number":0,"price":0.0}]}
     *  {"total":0,"rows":[]}
     * @param currentPage 第几页
     * @param peerpageRows 每页几条
     * @param name 搜索的 关键字
     * @param catalogId 从第三级分类id
     * @param brandId 品牌id
     * @param orderBy 0综合 1价格从低到高 2价格从高到低 3销量从低到高 4销量从高到低
     * @param activityType 1表示限时活动
     * @return
     */
    @At
    public Object getProducts(@Param("currentPage")int currentPage,@Param("peerpageRows")int peerpageRows,@Param("name")String name,
            @Param("catalogId")int catalogId,@Param("brandId")int brandId,@Param("orderBy")int orderBy,@Param("activityType")int activityType){
        this.result=new HashMap<String,Object>();
        //设置分页参数
        Pager pager = dao.createPager(currentPage, peerpageRows);
        Cnd condition=Cnd.where("status","=",1);
        if (!StringUtils.isEmpty(name)) {
            condition.and("name", "like", "%" + name + "%");
        }
        if(catalogId!=0){
            //表示只通过从第三级分类id查询
            condition.and("catalogId3", "=", catalogId);
        }
        if(brandId!=0){
            //表示点击了某一个品牌
            condition.and("brandId","=",brandId);
        }
        if(activityType==1){
            //表示限时活动
            condition.and("activityType","=",activityType);
        }
        this.result.put("total", dao.count(Product.class,condition));
        if (orderBy==0) {
            condition.orderBy("inputDate", "desc");
        }else if (orderBy==1) {
            condition.orderBy("minPrice", "asc");
        }else if (orderBy==2) {
            condition.orderBy("minPrice", "desc");
        }else if (orderBy==3) {
            condition.orderBy("sellCount", "asc");
        }else if (orderBy==4) {
            condition.orderBy("sellCount", "desc");
        }
        List<Product> ps = dao.query(Product.class, condition,pager);
        for(Product p : ps){
            List<Images> imgs = dao.query(Images.class,
                    Cnd.where("productId", "=", p.getProductId()).and("isTopimg", "=", true));
            p.setImgs(imgs);
        }
        this.result.put("rows", ps);
        return this.result;
/*        this.result=new HashMap<String,Object>();
        if(0==ID){
            List<Brand> brands=dao.query(Brand.class, Cnd.where("catalogId","=",CID));
            ArrayList<Integer> bid = new ArrayList<Integer>();
            for(Brand b : brands){
                bid.add(b.getBrandId());
            }
            List<Product> ps = dao.query(Product.class, Cnd.where("brandId","in",bid.toArray()));
            for(Product p : ps){
                List<Images> imgs = dao.query(Images.class, Cnd.where("productId","=",p.getProductId()));
                p.setImgs(imgs);
            }
            this.result.put("products", ps);
            return this.result;
        }else{
            List<Product> products = dao.query(Product.class, Cnd.where("brandId","=",ID));
            for(Product p : products){
                List<Images> imgs = dao.query(Images.class, Cnd.where("productId","=",p.getProductId()));
                p.setImgs(imgs);
            }
            this.result.put("products", products);
            return this.result;
        }*/
    }

    /**
     * ===============================================================
     *
     *      老版本修改结束
     *
     * ===============================================================
     */

    /**
     *
     * @param name
     * @param pid
     * @return
     */
    @At
    public Object addCatalog(@Param("catalogName")String name,@Param("pid")int pid){
        this.result=new HashMap<String,Object>();
        //pid为0代表这个对象是一个顶级对象
        Catalogs catalog=new Catalogs();
        catalog.setCatalogName(name);
        catalog.setPid(pid);
        if(dao.insert(catalog)!=null){
            this.result.put("status", "success");
        }else{
            this.result.put("status", "fail");
            this.result.put("msg", "添加分类失败");
        }
        return this.result;
    }
    @At
    public Object deleteOne(@Param("catalogId")int ID){
        this.result=new HashMap<String,Object>();
        if(dao.delete(Catalogs.class,ID)>0){
            this.result.put("status", "success");
        }else{
            this.result.put("status", "fali");
            this.result.put("msg", "无法删除商品分类");
        }
        return this.result;
    }
    @At
    public Object getAllCatalogs(@Param("currentPage")int currentPage,@Param("peerpageRows")int peerpageRows){
        this.result=new HashMap<String,Object>();
        List<Catalogs> temp = dao.query(Catalogs.class, null);
        this.result.put("total", temp.size());
        Pager pager = dao.createPager(currentPage, peerpageRows);
        List<Catalogs> results = dao.query(Catalogs.class,null, pager);
        this.result.put("rows", results);
        return this.result;
    }
    /*
     * 这个方法主要用来获取ID所标识的catalog以及它所有的子级对象
     * */
    @At
    public Object queryCatalog(@Param("catalogId")int ID){
        this.result=new HashMap<String,Object>();
        List<Catalogs> catalogs=this.query(ID);
        this.result.put("total",catalogs.size());
        this.result.put("rows",catalogs);
        return this.result;

    }
    @At
    public Object queryCatalogWithBrand(@Param("catalogId")int ID){
        this.result=new HashMap<String,Object>();
        List<Catalogs> catalogs=this.query(ID);
        this.result.put("total",catalogs.size());
        this.result.put("rows",catalogs);
        return this.result;

    }
    private List<Catalogs> query(int ID){
        //第一次查询，获得所有的顶级对象
        List<Catalogs> results=dao.query(Catalogs.class, Cnd.where("catalogId","=",ID));
        //第二次查询。获取子级对象
        for(Catalogs catalog : results){
            List<Brand> brands = dao.query(Brand.class, Cnd.where("catalogId","=",catalog.getCatalogId()));
            catalog.setBrands(brands);
            this.subquery(catalog, catalog.getCatalogId());
        }
        return results;
    }
    private void subquery(Catalogs catalog,int ID){
        List<Catalogs> temp = dao.query(Catalogs.class, Cnd.where("pid","=",catalog.getCatalogId()));

        //		if(temp.size()>0){
        catalog.setSubCatalogs(temp);
        //		}
        for(Catalogs subtemp :temp){
            List<Brand> brands = dao.query(Brand.class, Cnd.where("catalogId","=",subtemp.getCatalogId()));
            subtemp.setBrands(brands);
            subquery(subtemp,subtemp.getCatalogId());
        }
        return;
    }

    @At
    public Object getTopItems(){
        this.result=new HashMap<String,Object>();
        List<Catalogs> items=dao.query(Catalogs.class, Cnd.where("pid","=",1));
        this.result.put("rows", items);
        return this.result;
    }

    @At
    public Object queryBrandByCatalogId(@Param("catalogId")int ID,@Param("brandId")int brandId){
        this.result=new HashMap<String,Object>();
        if(0==brandId){
            //表示点击首页的第三级分类
            List<Brand> brands=dao.query(Brand.class, Cnd.where("catalogId","=",ID));
            for(Brand b : brands){
                List<Product> ps=dao.query(Product.class, Cnd.where("brandId","=",b.getBrandId()));
                b.setProducts(ps);
            }
            this.result.put("results", brands);
            return this.result;
        }else{
            //表示按照品牌来查询的
            List<Product> ps=dao.query(Product.class, Cnd.where("brandId","=",brandId));
            this.result.put("results", ps);
        }
        return this.result;
    }

}
