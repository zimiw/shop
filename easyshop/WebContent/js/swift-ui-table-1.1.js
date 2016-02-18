/*
 * swiftself.table组件;
 * v0.1 developed by Liusc @ 2015.9
 *  该组件主要依赖于avalon框架，来实现对多样式表格的展示。参数为：opts
 *  opts.targetId: 要用来进行表格展示的容器ms-controller ID，为必填参数 ;
 *  opts.templateId: 表格模板ID，如果不存在，则使用默认模板;
 *  opts.template: 如果表格模板ID不存在时，需要用户传入该参数：{heads:["columnName1"..,"columnNameN"],valueTemplate:["ColumnValue1"..,"ColumnValueN"]},
 *                 当colimnName为"operation"时，认为该列为操作列，将不会进行数据绑定。该方法暂时未做！
 *  opts.settings: 表格参数;
 *  opts.settings.url: 数据接口，如果没有，则默认显示无数据;
 *  opts.settings.searchParam: 数据查询参数;
 *  opts.settings.peerpagerow: 分页参数-每页显示多少行;如果不设置，表格将不会显示分页操作栏;
 *  opts.settings.isshowRowsList:是否显示每页行数配置下拉框;
 *  opts.settings.rowsConfigList:提供了具体的每页显示行数下拉框的内容;
 *  opts.settings.indexName: 表格索引名;
 *  opts.settings.paginationIndexName: 可以自定义分页参数接口;{'currentPage':'xxx','peerpageRows':'yyy'} 注意！如果指定了该参数，两个目标变量都需要指定
 *  opts.settings.sourceInterface: 获取数据的接口,有两个值{INum:'total',IData:"rows"};
 *  opts.settings.ajaxType: ajax传输类型："json"/"jsonp"
 *  opts.settings.ajaxDataHandler:ajaxDataHandler被设置后，将采用ajaxDataHandler来进行数据适配
 *  opts.settings.searchRootPath:该参数用来指定是否在ajax传输数据时搜索数据是否会带根元素，例如：searchRootPath="query",则发送ajax请求时，数据结构会如下query.searchParam1;query.searchParam2
 *  opts.customFuncs:表格的附加操作，用户可以自定义，为一个object。
 *  opts.dataFilter:表格数据的过滤操作函数，用户可以自定义，为一个object。
 * */
SwiftUI = {};
SwiftUI.swiftTable = {
	getInstance: function(opts) {
		//初始化表格,对初始化参数进行有效性检验
		if (arguments.length === 0) {
			throw "初始化参数错误！";
		} else if (!opts.targetId) {
			throw "请输入容器ID";
		}
		var instance = {};
		/********************************************私有工具函数********************************************/
		//合并搜索参数
		function mix(source, target) {
				for (var item in source) {
					if (source.hasOwnProperty(item) && (!target.hasOwnProperty(item))) {
						target[item] = source[item];
					}
				}
				return target;
			}
			//数据适配器接口 ,如果opts.settings.ajaxDataHandler被设置后，将采用ajaxDataHandler来进行数据适配
		function dataAdapter(Origindata) {
				if (opts.settings.ajaxDataHandler) {
					return opts.settings.ajaxDataHandler(Origindata);
				} else {
                    var temp={total:0,rows:[]};
					for (var index in Origindata) {
						if (typeof Origindata[index] === "object" && Object.prototype.toString.call(Origindata[index]) !== "[object Array]") {
							temp = subDataAdapter(temp, Origindata[index]);
						} else {
							if (index === opts.settings.sourceInterface.INum) {
								temp.total = Origindata[index];
							} else if (index === opts.settings.sourceInterface.IData) {
								temp.rows = Origindata[index];
							}
						}
					}
					return temp;
				}
			}
			//数据适配器子接口
		function subDataAdapter(innerData, Origindata) {
				for (var index in Origindata) {
					if (typeof Origindata[index] === "object" && Object.prototype.toString.call(Origindata[index]) !== "[object Array]") {
						innerData = subDataAdapter(innerData, Origindata[index]);
					} else {
						if (index === opts.settings.sourceInterface.INum) {
							innerData.total = Origindata[index];
						} else if (index === opts.settings.sourceInterface.IData) {
							innerData.rows = Origindata[index];
						}
					}
				}
				return innerData;
			}
			//搜索参数名适配器
		function searchParamAdapter(opts) {
			var tempParam = {};
			if (opts.settings.paginationIndexName !== undefined) {
				if (!opts.settings.paginationIndexName["currentPage"] && !opts.settings.paginationIndexName["peerpageRows"]) {
					throw "parameter of the 'paginationIndexName' is not addigned completely, both 'currentPage' and 'peerpageRows' should be assinged.";
				}
				for (var item in opts.settings.paginationIndexName) {
					if (item === "currentPage") {
						tempParam[opts.settings.paginationIndexName[item]] = 1;
					}
					if (item === "peerpageRows") {
						tempParam[opts.settings.paginationIndexName[item]] = ((opts.settings.peerpagerow) ? opts.settings.peerpagerow : 10);
					}
				}
			} else {
				tempParam = {
					currentPage: 1,
					peerpageRows: ((opts.settings.peerpagerow) ? opts.settings.peerpagerow : 10)
				};
			}
			return tempParam;
		}
		function setData(data) {
				if (data) {
				    if(data.rows){
    					if (data.rows.length === 0) {
    						instance.table.startrow = 0;
    						instance.table.endrow = 0;
    						instance.table.totalpage = 0;
    						instance.table.currentpage = 0;
    						instance.table.firstpagestyle = "disabled";
    						instance.table.prepagestyle = "disabled";
    						instance.table.nextpagestyle = "disabled";
    						instance.table.lastpagestyle = "disabled";
    						instance.table.total = data.total;
    						instance.table.successFuc(data.rows);
    					} else {
    						//                      if(instance.table.currentpage==0){
    						instance.table.currentpage = 1;
    						instance.table.$currentInd = 1;
    						//                      }
    						instance.table.startrow = (instance.table.currentpage - 1) * instance.table.peerpagerows + 1;
    						instance.table.endrow = (instance.table.currentpage - 1) * instance.table.peerpagerows + data.rows.slice(0, instance.table.peerpagerows).length;
    						instance.table.totalpage = Math.ceil(parseInt(data.total) / parseInt(instance.table.peerpagerows));
    						//                      instance.table.totalpage = Math.ceil(parseInt(data.rows.slice(0,instance.table.peerpagerows).length) / parseInt(instance.table.peerpagerows));
    						if (instance.table.totalpage == 1) {
    							instance.table.firstpagestyle = "disabled";
    							instance.table.prepagestyle = "disabled";
    							instance.table.nextpagestyle = "disabled";
    							instance.table.lastpagestyle = "disabled";
    						} else if (instance.table.totalpage > 1) {
    							instance.table.firstpagestyle = "disabled";
    							instance.table.prepagestyle = "disabled";
    							instance.table.nextpagestyle = " ";
    							instance.table.lastpagestyle = " ";
    						}
    						instance.table.total = data.total;
    						instance.table.successFuc(data.rows);
    					}
					}else{
					    throw "数据结构错误，缺少rows参数，请检查返回值";
					}
				}
			}
			/********************************************私有工具函数********************************************/
		window.swifttables = window.swifttables || [];
		instance.searchParam = searchParamAdapter(opts);
		if (opts.settings.searchParam) {
			instance.searchParam = mix(instance.searchParam, opts.settings.searchParam);
		}
		//初始化表格对象池
		(opts.settings.indexName) ? (window.swifttables[opts.settings.indexName] = instance) : (window.swifttables.push(instance));
		//进行参数初始化
		instance.url = (opts.settings.url) ? opts.settings.url : '';
		//获取表格容器对象
		document.getElementById(opts.targetId).innerHTML = '<div ms-include="' + opts.templateId + '" data-include-replace="false"></div>'; //向容器中插入模板引用容器
		//生成表格对象 开始
		instance.table = avalon.define({
			$id: opts.targetId,
			templateID: opts.templateId,
			sources: [],
			dataFilter: opts.dataFilter,
			rowsConfig: true,
			index: 1,
			isshowRowsList:(opts.settings.isshowRowsList)?opts.settings.isshowRowsList:true,
			rowsConfigList:(opts.settings.rowsConfigList)?opts.settings.rowsConfigList:[5,10,15,20],
			successFuc: function(data) {
				if (typeof instance.table.dataFilter === "function") {
					instance.table.sources = [];
					instance.table.sources = instance.table.dataFilter(data.slice(0, instance.table.peerpagerows));
				} else {
					instance.table.sources = [];
					instance.table.sources = data.slice(0, instance.table.peerpagerows);
				}
				avalon.scan();
			},
			refresh:function(){
			    if (opts.settings.paginationIndexName !== undefined) {
                    if (!opts.settings.paginationIndexName["currentPage"] && !opts.settings.paginationIndexName["peerpageRows"]) {
                        throw "parameter of the 'paginationIndexName' is not addigned completely, both 'currentPage' and 'peerpageRows' should be assinged.";
                    }
                    for (var item in opts.settings.paginationIndexName) {
                        if (item === "peerpageRows") {
                            instance.searchParam[opts.settings.paginationIndexName[item]] = instance.table.peerpagerows;
                        }
                    }
                } else {
                    instance.searchParam.peerpageRows=instance.table.peerpagerows;
                }
			    $.ajax({
                    type: 'post',
                    dataType: (opts.settings.ajaxType) ? opts.settings.ajaxType : "json",
                    data: instance.searchParam,
                    url: instance.url,
                    success: function(data) {
                        var tempData = dataAdapter(data);
                        if(tempData.rows.length>0){
                            setData(tempData);
                        }else{
                        	instance.searchParam.currentPage-=1;
                        	instance.table.refresh();
                        }
                    },
                    error: function(a, b, c) {
                        console.log(b);
                    }
                });
			},
			/*以下开始是分页操作*/
			currentpage: 0,
			totalpage: 0,
			startrow: 0,
			endrow: 0,
			total: 0, //总条数
			$currentInd: 0,
			peerpagerows: (opts.settings.peerpagerow === undefined) ? 10 : opts.settings.peerpagerow,
			$currentpage: 0,
			firstpagestyle: "disabled",
			prepagestyle: "disabled",
			nextpagestyle: " ",
			lastpagestyle: " ",
			jumppagestyle: " ",
			//跳转至首页
			firstpage: function() {
				if (instance.table.$currentInd > 1) {
					instance.table.firstpagestyle = "disabled";
					instance.table.prepagestyle = "disabled";
					instance.table.nextpagestyle = " ";
					instance.table.lastpagestyle = " ";
					instance.table.index = 1;
					instance.table.$currentInd = 1;
					instance.table.currentpage = 1;
					instance.table.oldPage = 1;
					if (opts.settings.paginationIndexName) {
						instance.searchParam[opts.settings.paginationIndexName['currentPage']] = 1;
					} else {
						instance.searchParam['currentPage'] = 1;
					}
					//配置异步数据传输接口
					var ajaxSetting = {
						type: "post",
						data: instance.searchParam,
						url: instance.url,
						dataType: (opts.settings.ajaxType) ? opts.settings.ajaxType : "json",
						success: function(data) {
							var tempData = dataAdapter(data);
							if (tempData) {
								if (tempData.rows.length === 0) {
									return;
								}
							} else {
								return;
							}
							instance.table.successFuc(tempData.rows);
							instance.table.startrow = (instance.table.currentpage - 1) * instance.table.peerpagerows + 1;
                            instance.table.endrow = (instance.table.currentpage - 1) * instance.table.peerpagerows + instance.table.sources.length;
						}
					};
					$.ajax(ajaxSetting);
					//配置异步数据传输接口
				}
			},
			//上一页
			prepage: function() {
				if (instance.table.$currentInd > 1) {
					instance.table.nextpagestyle = " ";
					instance.table.lastpagestyle = " ",
						instance.table.currentpage = instance.table.index = instance.table.$currentInd -= 1;
					instance.table.oldPage = instance.table.currentpage;
					instance.table.startrow = (instance.table.currentpage - 1) * instance.table.peerpagerows + 1;
					instance.table.endrow = (instance.table.currentpage - 1) * instance.table.peerpagerows + instance.table.sources.length;
					if (opts.settings.paginationIndexName) {
						instance.searchParam[opts.settings.paginationIndexName['currentPage']] = instance.table.currentpage;
					} else {
						instance.searchParam['currentPage'] = instance.table.currentpage;
					}
					//配置异步数据传输接口 开始
					var ajaxSetting = {
						type: "post",
						data: instance.searchParam,
						url: instance.url,
						dataType: (opts.settings.ajaxType) ? opts.settings.ajaxType : "json",
						success: function(data) {
							var tempData = dataAdapter(data);
							if (tempData) {
								if (tempData.rows.length === 0) {
									return;
								}
							} else {
								return;
							}
							instance.table.successFuc(tempData.rows);
						}
					};
					$.ajax(ajaxSetting);
					//配置异步数据传输接口 结束
					if (instance.table.$currentInd === 1) {
						instance.table.prepagestyle = "disabled";
						instance.table.firstpagestyle = "disabled";
					}
				}
			},
			//下一页
			nextpage: function() {
				if (instance.table.$currentInd < instance.table.totalpage) {
					instance.table.firstpagestyle = " ";
					instance.table.prepagestyle = " ";
					instance.table.currentpage = instance.table.index = instance.table.$currentInd += 1;
					instance.table.oldPage = instance.table.currentpage;
					instance.table.startrow = (instance.table.currentpage - 1) * instance.table.peerpagerows + 1;
					instance.table.endrow = (instance.table.currentpage - 1) * instance.table.peerpagerows + instance.table.sources.length;
					if (opts.settings.paginationIndexName) {
						instance.searchParam[opts.settings.paginationIndexName['currentPage']] = instance.table.currentpage;
					} else {
						instance.searchParam['currentPage'] = instance.table.currentpage;
					}
					//配置异步数据传输接口 开始
					var ajaxSetting = {
						type: "post",
						data: instance.searchParam,
						url: instance.url,
						dataType: (opts.settings.ajaxType) ? opts.settings.ajaxType : "json",
						success: function(data) {
							var tempData = dataAdapter(data);
							if (tempData) {
								if (tempData.rows.length === 0) {
									return;
								}
							} else {
								return;
							}
							instance.table.successFuc(tempData.rows);
						}
					};
					$.ajax(ajaxSetting);
					//配置异步数据传输接口 结束
					if (instance.table.$currentInd === instance.table.totalpage) {
						instance.table.nextpagestyle = "disabled";
						instance.table.lastpagestyle = "disabled";
					}
				}
			},
			//跳转至尾页
			lastpage: function() {
				if (instance.table.$currentInd < instance.table.totalpage) {
					instance.table.firstpagestyle = " ";
					instance.table.prepagestyle = " ";
					instance.table.nextpagestyle = "disabled";
					instance.table.lastpagestyle = "disabled";
					instance.table.$currentInd = instance.table.totalpage;
					instance.table.currentpage = instance.table.totalpage;
					instance.table.index = instance.table.totalpage;
					instance.table.oldPage = instance.table.currentpage;
					if (opts.settings.paginationIndexName) {
						instance.searchParam[opts.settings.paginationIndexName['currentPage']] = instance.table.currentpage;
					} else {
						instance.searchParam['currentPage'] = instance.table.currentpage;
					}
					//配置异步数据传输接口 开始
					var ajaxSetting = {
						type: "post",
						data: instance.searchParam,
						url: instance.url,
						dataType: (opts.settings.ajaxType) ? opts.settings.ajaxType : "json",
						success: function(data) {
							var tempData = dataAdapter(data);
							if (tempData) {
								if (tempData.rows.length === 0) {
									return;
								}
							} else {
								return;
							}
							instance.table.successFuc(tempData.rows);
							instance.table.startrow = (instance.table.currentpage - 1) * instance.table.peerpagerows + 1;
                            instance.table.endrow = (instance.table.currentpage - 1) * instance.table.peerpagerows + instance.table.sources.length;
						}
					};
					$.ajax(ajaxSetting);
					//配置异步数据传输接口 结束
				}
			},
			oldPage: 1,
                //跳转值X页
			jumppage: function() {
			    setTimeout(function(){
    				if (instance.table.currentpage != "") {
    					var reg = /^\d+$/;
    					//console.log(instance.table.currentpage.match(reg))
    					if (!reg.test(instance.table.currentpage)) {
    						console.log(instance.table.currentpage)
    						instance.table.currentpage = instance.table.oldPage;
    						return;
    					}
    					if (instance.table.currentpage > instance.table.totalpage || instance.table.currentpage == 0) {
    						instance.table.currentpage = instance.table.oldPage;
    						return;
    					}
    					instance.table.$currentInd = instance.table.index = parseInt(instance.table.currentpage);
    					if (instance.table.$currentInd === 1) {
    						//跳转到第一页
    						//判断当前共有几页，当只有一页的时候，全部操作都置灰
    						if (instance.table.totalpage == 1) {
    							instance.table.firstpagestyle = "disabled";
    							instance.table.prepagestyle = "disabled";
    							instance.table.nextpagestyle = "disabled";
    							instance.table.lastpagestyle = "disabled";
    						} else {
    							instance.table.firstpagestyle = "disabled";
    							instance.table.prepagestyle = "disabled";
    							instance.table.nextpagestyle = " ";
    							instance.table.lastpagestyle = " ";
    						}
    						instance.table.oldPage = 1;
    					} else if (instance.table.$currentInd === instance.table.totalpage) {
    						//跳转到最后一页
    						instance.table.firstpagestyle = " ";
    						instance.table.prepagestyle = " ";
    						instance.table.nextpagestyle = "disabled";
    						instance.table.lastpagestyle = "disabled";
    						instance.table.oldPage = instance.table.totalpage;
    					} else if (instance.table.$currentInd > 0 && instance.table.$currentInd < instance.table.totalpage) {
    						instance.table.firstpagestyle = " ";
    						instance.table.prepagestyle = " ";
    						instance.table.nextpagestyle = " ";
    						instance.table.lastpagestyle = " ";
    						instance.table.oldPage = instance.table.$currentInd;
    					}
    					instance.table.startrow = (instance.table.oldPage - 1) * instance.table.peerpagerows + 1;
    					instance.table.endrow = (instance.table.oldPage - 1) * instance.table.peerpagerows + instance.table.sources.length;
    					if (opts.settings.paginationIndexName) {
    						instance.searchParam[opts.settings.paginationIndexName['currentPage']] = instance.table.$currentInd;
    					} else {
    						instance.searchParam['currentPage'] = instance.table.$currentInd;
    					}
    					//配置异步数据传输接口 开始
    					var ajaxSetting = {
    						type: "post",
    						data: instance.searchParam,
    						url: instance.url,
    						dataType: (opts.settings.ajaxType) ? opts.settings.ajaxType : "json",
    						success: function(data) {
    							var tempData = dataAdapter(data);
    							if (tempData) {
    								if (tempData.rows.length === 0) {
    									return;
    								}
    							} else {
    								return;
    							}
    							instance.table.successFuc(tempData.rows);
    						}
    					};
    					$.ajax(ajaxSetting);
    					//配置异步数据传输接口 结束
    				}
				},0);
			},
			customFuncs: (opts.customFuncs === undefined) ? {} : opts.customFuncs,
		});
		avalon.scan();
		//生成表格对象 结束

		//初始化表格 开始
		$.ajax({
			type: 'post',
			dataType: (opts.settings.ajaxType) ? opts.settings.ajaxType : "json",
			data: instance.searchParam,
			url: instance.url,
			success: function(data) {
				var tempData = dataAdapter(data);
				setData(tempData);
			},
			error: function(a, b, c) {
				console.log(b);
			}
		});
		//初始化表格 结束
		/********************************************实例对象方法********************************************/
		instance.search = function(searchParams) {
				//为了避免搜索条件被污染，每次搜索前都会重置搜索条件
				instance.searchParam = searchParamAdapter(opts);
				instance.searchParam = mix(instance.searchParam, searchParams);
				$.ajax({
					type: 'post',
					dataType: (opts.settings.ajaxType) ? opts.settings.ajaxType : "json",
					data: instance.searchParam,
					url: instance.url,
					success: function(data) {
						var tempData = dataAdapter(data);
						setData(tempData);
					},
					error: function(a, b, c) {
						console.log(b);
					}
				});
			}
			//在传输具有对象嵌套结构的json对象时调用该函数
		instance.complexSearch = function(searchParams) {
			//为了避免搜索条件被污染，每次搜索前都会重置搜索条件
			instance.searchParam = searchParamAdapter(opts);
			instance.searchParam = mix(instance.searchParam, searchParams);
			$.ajax({
				type: 'post',
				dataType: (opts.settings.ajaxType) ? opts.settings.ajaxType : "json",
                    data: {"searchParam":JSON.stringify(instance.searchParam)},
				url: instance.url,
				success: function(data) {
					var tempData = dataAdapter(data);
					setData(tempData);
				},
				error: function(a, b, c) {
					console.log(b);
				}
			});
		}
		instance.setData = function(data) {
			var tempData = dataAdapter(data);
			instance.table.successFuc(tempData.rows);
			var totalpage = Math.ceil(parseInt(tempData.total) / parseInt(instance.table.peerpagerows));
			if (totalpage === 1) {
				instance.table.firstpagestyle = "disabled";
				instance.table.prepagestyle = "disabled";
				instance.table.nextpagestyle = "disabled";
				instance.table.lastpagestyle = "disabled";
				instance.table.jumppagestyle = "disabled";
			} else {
				instance.table.firstpagestyle = " ";
				instance.table.prepagestyle = " ";
				instance.table.nextpagestyle = " ";
				instance.table.lastpagestyle = " ";
				instance.table.jumppagestyle = "false";
			}
			instance.table.currentpage = 1;
			instance.table.startrow = 1;
			instance.table.endrow = tempData.total;
			instance.table.totalpage = Math.ceil(parseInt(tempData.total) / parseInt(instance.table.peerpagerows));
			instance.table.total = tempData.total;
			instance.table.$currentInd = 1;
		}
		instance.refresh = function() {
			$.ajax({
				type: 'post',
				dataType: (opts.settings.ajaxType) ? opts.settings.ajaxType : "json",
				data: instance.searchParam,
				url: instance.url,
				success: function(data) {
					var tempData = dataAdapter(data);
					if(tempData.rows.length>0){
                        setData(tempData);
                    }else{
                    	if(instance.searchParam.currentPage==1){
                    		instance.table.$currentInd = 1;
        					instance.table.currentpage = 1;
        					instance.table.startrow = (instance.table.currentpage - 1) * instance.table.peerpagerows + 1;
        					instance.table.endrow = (instance.table.currentpage - 1) * instance.table.peerpagerows + tempData.rows.length;
        					instance.table.totalpage = Math.ceil(parseInt(tempData.total) / parseInt(instance.table.peerpagerows));
        					instance.table.total = tempData.total;
        					instance.table.successFuc(tempData.rows);
        					if(instance.table.totalpage>1){
                                instance.table.firstpagestyle = "disabled";
                                instance.table.prepagestyle = "disabled";
                                instance.table.nextpagestyle = " ";
                                instance.table.lastpagestyle = " ";
                            }else if(instance.table.totalpage==1){
                                instance.table.firstpagestyle = "disabled";
                                instance.table.prepagestyle = "disabled";
                                instance.table.nextpagestyle = "disabled";
                                instance.table.lastpagestyle = "disabled";
                            }
                    	}else{
                    		instance.searchParam.currentPage-=1;
                        	instance.refresh();
                    	}
                    }
					avalon.scan();
				}
			});
		}
		instance.setParam = function(params) {
				for (var item in params) {
					if (source.hasOwnProperty(item) && (!target.hasOwnProperty(item))) {
						target[item] = source[item];
					}
				}
			}
			/********************************************实例对象方法********************************************/
		return instance;
	},
	/********************************************公共静态函数********************************************/
	//index可以为数字索引也可以为字符串索引
	getswiftTable: function(index) {
			return window.swifttables[index];
		}
		/********************************************公共静态函数********************************************/
}