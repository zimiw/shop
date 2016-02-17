package com.easyshop.core.modules;

import com.easyshop.bean.Comment;
import com.easyshop.bean.Personal;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

@IocBean
@At("/comment")
@Ok("json")
@Fail("http:500")
public class CommentModule {

	@Inject
    protected Dao dao;
	private Map<String,Object> result;
	
	@At
    @Filters(@By(type=CheckSession.class, args={"backUserId", "/views/login.html"}))
	public Object getAllComments(@Param("currentPage")int currentPage,@Param("peerpageRows")int peerpageRows){
		this.result=new HashMap<String,Object>();
		List<Comment> temp = dao.query(Comment.class, null);
		this.result.put("total", temp.size());
		Pager pager = dao.createPager(currentPage, peerpageRows);
		List<Comment> users = dao.query(Comment.class, null,pager);
		for(Comment c : users){
			Personal p = dao.fetch(Personal.class,Cnd.where("id","=",c.getPersonId()));
			c.setPerson(p);
		}
		this.result.put("rows", users);
		return this.result;
	}
	@At
    @Filters(@By(type=CheckSession.class, args={"backUserId", "/views/login.html"}))
	public Object deleteOne(@Param("commentId")int ID){
		this.result=new HashMap<String,Object>();
		if(dao.delete(Comment.class,ID)>0){
			this.result.put("status", "success");
		}else{
			this.result.put("status", "fail");
			this.result.put("msg", "删除评论失败");
		}
		return this.result;
	}
	@At
    //@Filters(@By(type=CheckSession.class, args={"backUserId", "/views/login.html"}))
	public Object queryCommentsbyproductID(@Param("productId")int ID,@Param("currentPage")int currentPage,@Param("peerpageRows")int peerpageRows){
		this.result=new HashMap<String,Object>();
		Pager pager = dao.createPager(currentPage, peerpageRows);
		List<Comment> temp = dao.query(Comment.class, Cnd.where("productId","=",ID),pager);
		for(Comment c: temp){
			Personal p = dao.fetch(Personal.class,Cnd.where("id","=",c.getPersonId()));
			c.setPerson(p);
		}
		this.result.put("status", "success");
		this.result.put("rows",temp);
		return this.result;
	}
	@At
    @Filters(@By(type=CheckSession.class, args={"backUserId", "/views/login.html"}))
	public Object queryCommentByCommentId(@Param("commentId")long commentId){
		this.result=new HashMap<String,Object>();
		Comment comment=dao.fetch(Comment.class,commentId);
        if (comment != null) {
            this.result.put("status", "success");
            this.result.put("commentId",commentId);
            this.result.put("content",comment.getContent());
            this.result.put("type",comment.getType());
            this.result.put("productId",comment.getProductId());
            Personal p = dao.fetch(Personal.class,Cnd.where("personalId","=",comment.getPersonId()));
            this.result.put("person", p);
        }else{
            this.result.put("status", "fail");
            this.result.put("msg", "没有查询到该评论");
        }

		return this.result;
	}

    @At
    @Filters(@By(type=CheckSession.class, args={"frontUserId", "/front/login.html"}))
    public Object addComment(@Param("productId") String productId, @Param("type") String type, @Param("content") String content,HttpSession session) {

        this.result = new HashMap<String, Object>();
        
        //获取用户ID
        long personid = (long)session.getAttribute("frontUserId");
        if ("".equals(productId) || "".equals(productId)||"".equals(type) || "".equals(content)) {
            this.result.put("status", "error");
            this.result.put("msg", "参数错误");
            return this.result;
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setType(type);
        comment.setProductId(Integer.valueOf(productId));
        comment.setPersonId(personid);
        dao.insert(comment);
        this.result.put("status", "success");
        this.result.put("commentId", comment.getCommentId());
        return this.result;
    }
    @At
    public Object getCommentType(@Param("productId")int id){
    	this.result=new HashMap<String,Object>();
    	ArrayList<HashMap<String,Object>> commentTypes=new ArrayList<HashMap<String,Object>>();
		List<Comment> goods = dao.query(Comment.class, Cnd.where("type","=",1).and("productId","=",id));
		List<Comment> normals = dao.query(Comment.class, Cnd.where("type","=",2).and("productId","=",id));
		List<Comment> bads = dao.query(Comment.class, Cnd.where("type","=",3).and("productId","=",id));
		HashMap<String,Object> commentlist=new HashMap<String,Object>();
		commentlist.put("goods", goods);
		HashMap<String,Object> normallist=new HashMap<String,Object>();
		commentlist.put("normals", normals);
		HashMap<String,Object> badlist=new HashMap<String,Object>();
		commentlist.put("bads", bads);
		this.result.put("commentTypes", commentlist);
		return this.result;
    }
    @At
    public Object queryCommentByType(@Param("type")String type,@Param("productId")int id){
    	this.result=new HashMap<String,Object>();
    	List<Comment> comments = dao.query(Comment.class, Cnd.where("type","=",type).and("productId","=",id));
    	this.result.put("comments", comments);
    	return this.result;
    	
    }
}
