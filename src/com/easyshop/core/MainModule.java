package com.easyshop.core;

import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

import com.easy.core.filters.MyFilter;

@SetupBy(value=MainSetup.class)
@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/",
        "*anno", "com.easyshop.core",
        "*tx"})
@Modules(scanPackage=true)
@Filters({@By(type=MyFilter.class)})
public class MainModule {
	
}
