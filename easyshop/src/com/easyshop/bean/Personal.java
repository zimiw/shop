package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 会员信息
 * 
 * @author luocz
 */
@Table("personal")
public class Personal {

    /**
     * 用户id
     */
    @Id
    private long id;

    /**
     * 头像
     */
    @Column
    private String password;

    /**
     * 头像
     */
    @Column
    private String img;

    /**
     * 用户�?登录用的
     */
    @Column
    private String name;

    /**
     * 昵称
     */
    @Column
    private String nickname;

    /**
     * 签名
     */
    @Column
    private String sign;

    /**
     * 性别 0未设�?1�?2�?
     */
    @Column
    private int sex;

    /**
     * 电话
     */
    @Column
    private String phone;

    /**
     * email
     */
    @Column
    private String email;

    /**
     * 用户�?
     */
    @Column
    private String code;

    /**
     * 生日
     */
    @Column
    private String birth;

    /**
     * 常住�?�?
     */
    @Column
    private String province;

    /**
     * 常住�?城市
     */
    @Column
    private String city;

    /**
     * 常住�?地区
     */
    @Column
    private String district;

    /**
     * 常住�?街道
     */
    @Column
    private String street;

    /**
     * 会员级别
     */
    @Column
    private String level;

    /**
     * 密码级别 密码长度小于6就是低，6�?0中，10以上�?
     */
    private String passwordLevel;

    /**
     * 安全等级
     *
     * 密码 手机 邮箱
     *
     * 2个出问题就是�?1个出问题就是�?0个出问题就是�?
     */
    @Column
    private String safe;

    /**
     * 是否绑定手机  1为绑�?0为未绑定
     */
    @Column
    private int bindPhone;

    /**
     * 是否绑定邮箱 1为绑�?0为未绑定
     */
    @Column
    private int bindEmail;

    /**
     * 待付款商品数�?
     */
    @Column
    private int pnopay;

    /**
     * 待发�?
     */
    @Column
    private int psend;

    /**
     * 待收货商品数�?
     */
    @Column
    private int pwait;

    /**
     * 待评价商品数�?
     */
    @Column
    private int pevaluate;
    
    @Column
    private boolean isPlayGame;
    

    public boolean isPlayGame() {
		return isPlayGame;
	}

	public void setPlayGame(boolean isPlayGame) {
		this.isPlayGame = isPlayGame;
	}

	/**
     * 用户登录后根据时间展示欢迎语
     */
    private String signature;
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordLevel() {
        return passwordLevel;
    }

    public void setPasswordLevel(String passwordLevel) {
        this.passwordLevel = passwordLevel;
    }

    @Override
    public String toString() {
        return "Personal{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", img='" + img + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sign='" + sign + '\'' +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", code='" + code + '\'' +
                ", birth='" + birth + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", street='" + street + '\'' +
                ", level='" + level + '\'' +
                ", passwordLevel='" + passwordLevel + '\'' +
                ", safe='" + safe + '\'' +
                ", bindPhone=" + bindPhone +
                ", bindEmail=" + bindEmail +
                ", pnopay=" + pnopay +
                ", psend=" + psend +
                ", pwait=" + pwait +
                ", pevaluate=" + pevaluate +
                ", signature='" + signature + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSafe() {
        return safe;
    }

    public void setSafe(String safe) {
        this.safe = safe;
    }

    public int getBindPhone() {
        return bindPhone;
    }

    public void setBindPhone(int bindPhone) {
        this.bindPhone = bindPhone;
    }

    public int getBindEmail() {
        return bindEmail;
    }

    public void setBindEmail(int bindEmail) {
        this.bindEmail = bindEmail;
    }

    public int getPwait() {
        return pwait;
    }

    public void setPwait(int pwait) {
        this.pwait = pwait;
    }

    public int getPnopay() {
        return pnopay;
    }

    public void setPnopay(int pnopay) {
        this.pnopay = pnopay;
    }

    public int getPevaluate() {
        return pevaluate;
    }

    public void setPevaluate(int pevaluate) {
        this.pevaluate = pevaluate;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getPsend() {
        return psend;
	}

    public void setPsend(int psend) {
        this.psend = psend;
	}

}
