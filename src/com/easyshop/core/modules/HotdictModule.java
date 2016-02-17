package com.easyshop.core.modules;

import com.easyshop.bean.Hotdict;
import com.easyshop.vo.ResultVo;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import java.util.List;
import java.util.Map;

@IocBean
@At("/hotdict")
@Ok("json")
@Fail("http:500")
public class HotdictModule {

    @Inject
    protected Dao dao;
    private Map<String, Object> result;

    /**
     * http://localhost:8181/easyshop/hotdict/addHotdict?name=AAaa111
     *
     * {"status":"fail","msg":"您添加的热词和已有的重复了"}
     * {"status":"success"}
     *
     * @param name 不区分大小写
     *
     * @return
     */
    @At
    public ResultVo addHotdict(@Param("name") String name) {
        try {
            dao.insert(new Hotdict(name));
        } catch (Exception e) {
            //e.printStackTrace();
            return new ResultVo("fail", "您添加的热词和已有的重复了");
        }
        return new ResultVo();
    }

    /**
     * http://localhost:8181/easyshop/hotdict/delHotdict?name=AAaa111
     *
     * {"status":"success"}
     * @param name
     * @return
     */
    @At
    public ResultVo delHotdict(@Param("name") String name) {
        dao.delete(new Hotdict(name));
        return new ResultVo();
    }

    /**
     * http://localhost:8181/easyshop/hotdict/setHotdictStatus?name=2&status=false {"status":"success"}
     *
     * @param name   热词名称
     * @param status true表示启用 false表示不用
     *
     * @return
     */
    @At
    public ResultVo setHotdictStatus(@Param("name") String name, @Param("status") boolean status) {
        Hotdict hotdict = new Hotdict(name, status);
        dao.update(hotdict);
        return new ResultVo();
    }

    /**
     * http://10.24.47.137:8181/easyshop/hotdict/getHotdictByStatus?status=true [{"name":"2","status":true},
     * {"name":"AAA","status":false}, {"name":"AAaa","status":false}, {"name":"AAaa1","status":false},
     * {"name":"AAaa111","status":false}] 如果没有返回:[]
     *
     * @param status 如果为空,表示查询所有的 true表示启用 false表示不用
     *
     * @return
     */
    @At
    @Filters
    public List<Hotdict> getHotdictByStatus(@Param("status") Boolean status) {
        Cnd condition = Cnd.where("1", "=", 1);
        if (status != null) {
            //不能用boolean 否则,status默认为false
            condition.and("status", "=", status);
        }
        List<Hotdict> list = dao.query(Hotdict.class, condition);
        return list;
    }

}
