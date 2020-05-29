

/* global App, BMap, BMapLib, INFOBOX_AT_TOP */

App.factory('Client', function ($http, $q, $rootScope, Global, $localStorage) {

    var obj = {};
    obj.form = function (url, data) {
        var defer = $q.defer();
        $.ajax({
            url: url, data: data, method: 'POST', headers:{"Authorization" : $localStorage.bearer},
            success: function (data, status, header, config) {
                if (data && data.code === 401) {
                    $rootScope.onError("err", {code:401});
                    return;
                }
                defer.resolve(data, status, header, config);
            },
            error: function (data, status, header, config) {
                if (data && data.code === 401) {
                    $rootScope.onError("err", {code:401});
                    return;
                }
                defer.reject(data, status, header, config);
            }
        });
        return defer.promise;
    };

    obj.json = function (url, data) {
        var defer = $q.defer();
        $http.post(url, data, {headers: {"Authorization" : $localStorage.bearer}}).success(function (data, status, header, config) {
            if (data && data.code === 401) {
                $rootScope.onError("err", {code:401});
                return;
            }
            defer.resolve(data, status, header, config);
        }).error(function (data, status, header, config) {
            if (data && data.code === 401) {
                $rootScope.onError("err", {code:401});
                return;
            }
            defer.reject(data, status, header, config);
        });
        return defer.promise;
    };
    
    obj.login = function (url, data) {
        var defer = $q.defer();
        $http.post(url, data).success(function (data, status, header, config) {
/*            if (data && data.code == 401) {
                $rootScope.onError("err", {code:401});
                return;
            }*/
            defer.resolve(data, status, header, config);
        }).error(function (data, status, header, config) {
/*            if (data && data.code == 401) {
                $rootScope.onError("err", {code:401});
                return;
            }*/
            defer.reject(data, status, header, config);
        });
        return defer.promise;
    };

    obj.get = function (url, data, skip) {
        var defer = $q.defer();
        if (data) {
            for (var i in data) {
                if (data.hasOwnProperty(i)) {
                    if (url.indexOf("?") > 0) {
                        url += "&" + i + "=" + data[i];
                    } else {
                        url += "?" + i + "=" + data[i];
                    }
                }
            }
        }
        $http.get({url: url, headers: {"Authorization" : $localStorage.bearer}}).success(function (data, status, header, config) {
            if (data && data.code === 401 && !skip) {
                $rootScope.onError("err", {code:401});
                return;
            }
            defer.resolve(data, status, header, config);
        }).error(function (data, status, header, config) {
            if (data && data.code === 401 && !skip) {
                $rootScope.onError("err", {code:401});
                return;
            }
            defer.reject(data, status, header, config);
        });
        return defer.promise;
    };
    
    obj.jsonp = function (u, data) {
        var defer = $q.defer();
        
        var url = u;
        for (var i in data) {
            if (data.hasOwnProperty(i)) {
                if (url.indexOf('?') > 0) {
                    url += "&" + i + '=' + data[i];
                } else {
                    url += "?" + i + '=' + data[i];
                }
            }
        }
        $http.jsonp({url: url, headers: {"Authorization" : $localStorage.bearer}}).success(function (data) {
            if (data && data.code === 401) {
                $rootScope.onError("err", {code:401});
                return;
            }
            defer.resolve(data);
        }).error(function (data) {
            if (data && data.code === 401) {
                $rootScope.onError("err", {code:401});
                return;
            }
            defer.reject(data);
        });
        return defer.promise;
    };
    
    obj.upload = function (url, form) {
        var defer = $q.defer();
        $http.post(url, form, {
            withCredentials: true,
            headers: {'Content-Type': undefined ,"Authorization" : $localStorage.bearer},
            transformRequest: angular.identity
        }).success(function (data) {
            if (data && data.code === 401) {
                $rootScope.onError("err", {code:401});
                return;
            }
            defer.resolve(data);
        }).error(function (data) {
            if (data && data.code === 401) {
                $rootScope.onError("err", {code:401});
                return;
            }
            defer.reject(data);
        });
        return defer.promise;
    };
    
    return obj;
});

App.factory('PageFactory', function ($http, $q, Global, $rootScope, $localStorage) {
    
    /**
     * 创建分页查询器实例。
     * 
     * @param {string} url 分页查询的URL地址，可附加自定义的查询参数；
     * @param {object} config 配置项，包括filter（过滤参数）、pageParam（分页页码的参数key）、sizeParam（分页大小的参数key）、pageSize（分页大小）
     * @returns {Pager}
     */
    function Pager (url, config) {
        if ('undefined' === typeof config) {
            config = {};
        }
        if (config.filter) {
            this.filter = config.filter;
        }
        if (config.sort) {
            this.sort = config.sort;
        }
        if (config.api) {
            this.api = config.api;
        }
        this._baseUrl = url;
        
        this.pageParam = config.pageParam ? config.pageParam : 'no';
        this.sizeParam = config.sizeParam ? config.sizeParam : 'size';
        this._size = 'undefined' !== typeof config.pageSize ? config.pageSize : Global.PAGESIZE;
        
        this._index = 1;
        this._total = 0;
    }
    
    var obj = {};
    
    /**
     * 查询分页数据
     * 
     * @param {integer} index 页码，1起始
     * @param {integer} size 分页大小
     * @param {string} url 查询url，覆写config的url
     * @returns {defer.promise}
     */
    Pager.prototype.query = function (index, size, url) {
        if ('undefined' !== typeof index) {
            this._index = index;
        }
        if ('undefined' !== typeof size) {
            this._size = size;
        }
        if ('undefined' !== typeof url) {
            this._baseUrl = url;
        }
        var that = this;
        
        var defer = $q.defer();
        var page = {};
        page[this.pageParam] = this._index;
        page[this.sizeParam] = this._size;
        
        url = this._baseUrl + (this._baseUrl.indexOf('?')>=0?'&':'?') + "paramJson=" + encodeURIComponent(JSON.stringify({
            api:this.api,
            filter: this.filter,
            sort: this.sort?{field: this.sort.name, order: this.sort.type}:null,
            page: this._index,
            size: this._size,
            userID: $localStorage.user.id
        }));
        $http({url: url, method: "GET", headers:{"Authorization" : $localStorage.bearer}}).success(function (data) {
            if (data && data.code === 9999) {
                $rootScope.onError("err", {code:401});
                return;
            }
            if (data.code === 200) {
                that._total = data.data.totalElements;
                defer.resolve(data);
            } else {
                defer.reject(data);
                if (data.code === 401) {
                    $rootScope.alert({
                        msg: '登录超时，请重新登陆',
                        buttons: [{
                            label: "确定"
                        }]
                    }).result.then(function (data) {
                        $rootScope.showLogin();
                    });
                    $timeout(function () {
                        $rootScope.showLogin();
                    },5000);
                }
            }
        }).error(function (data) {
            if (data && data.code === 401) {
                $rootScope.onError("err", {code:401});
                return;
            }
            defer.reject(data);
        });
        return defer.promise;
    };
    
    Pager.prototype.next = function () {
        var pages = Math.floor((this._total-1)/this._size + 1);
        if (this._index === pages) {
            return false;
        }
        return this.query(this._index + 1);
    };
    
    Pager.prototype.previous = function () {
        if (this._index === 1) {
            return false;
        }
        return this.query(this._index - 1);
    };
    
    Pager.prototype.resort = function (obj) {
        this.sort = obj;
        return this;
    };
    
    Pager.prototype.refilter = function (obj) {
        if (null === obj)
            delete this.filter;
        else
            this.filter = obj;
        return this;
    };
    
    Pager.prototype.relocate = function (obj) {
        this._baseUrl = obj;
        return this;
    };
    
    Pager.prototype.current = function () {
        return this._index;
    };
    
    Pager.prototype.total = function () {
        return this._total;
    };
    
    Pager.prototype.last = function () {
        return Math.floor((this._total - 1) / this._size + 1);
    };
    
    obj.page = function (url, config) {
        return new Pager(url, config);
    };
    
    return obj;
});
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
App.factory("MapUtil", function ($templateCache, Global, $http, $uibModal) {
    
    var buildContent = function(key, $scope, opt) {

        if (angular.isUndefined(opt) || null === opt || false === opt) {
            var opt = {
                tpl: "map.infobox.html"
            };
        }
        var cnt = $templateCache.get(opt.tpl).replace(new RegExp("node", "g"), key);

        return cnt;
    };
    
    var OnlyWin = null;
    
    var makeMark = function(node, markerOpt, cb, opt) {
        var marker;
        if (markerOpt) {
            marker = new BMap.Marker(new BMap.Point(node.lng, node.lat), markerOpt);
        } else {
            marker = new BMap.Marker(new BMap.Point(node.lng, node.lat));
        }
        marker._dev = node;
        
        if (opt && opt.popup === false) return marker;
        if (opt)
            node._marker_opt = opt;
        
        node.popup = function () {
            var me = this;
            if ('undefined' !== typeof me.infowin)
                return;
            if (node._marker_opt && node._marker_opt.popup_opt) {
                var cnt = buildContent(node._marker_opt.popup_opt.key, null, {
                    tpl: node._marker_opt.popup_opt.tpl
                });
            } else {
                var cnt = buildContent("nodes['" + me.macAddr + "']");
            }
            var iw = new BMapLib.InfoBox(marker.getMap(), cnt, node._marker_opt.popWinOpt?node._marker_opt.popWinOpt:{
                boxStyle:{
                    width: "320px",
                    height: "210px"
                },
                closeIconMargin: "13px 13px 0 0",
                closeIconUrl: 'app/img/close.png',
                enableAutoPan: true,
                align: INFOBOX_AT_TOP
            });
            iw.addEventListener("open", function () {
                cb(me.macAddr);
            });
            iw.open(marker);
            iw.addEventListener("close", function () {
                delete me.refresh;
                delete me.infowin;
                if (OnlyWin === me.infowin) {
                    OnlyWin = null;
                }
            });
            if (null !== OnlyWin) {
                OnlyWin.close();
            }
            OnlyWin = iw;
            this.infowin = iw;
            this.refresh = function () {
            };
        };
        marker.addEventListener('click', function () {
            node.popup();
        });
        return marker;
    };
    
    /**
     * 
     * @param {Array} data Array of object with mapFrom/lat/lng properties.
     * @param {Function} cb Callback after convert done, with arg of converted object array. rlat and rlng properties as converted coordinations.
     * @returns {undefined}
     */
    var convert = function (data, cb) {
        var types = [], map = {}, finalPoints = [], limit = 100;
        data.forEach(function(e, index) {
            if (types.indexOf(e.mapFrom) >= 0) {
                map[e.mapFrom].push(e);
            } else {
                types.push(e.mapFrom);
                map[e.mapFrom] = [e];
            }
        });

        var recuit = function (index, type) {
            var points = map[type];
            if (points.length <= index) {
                if (types.length < 1) {
                    cb(finalPoints);
                    return;
                }
                type = types.pop();
                points = map[type];
                index = 0;
            }
            
            if (type == 5) {
            	finalPoints = finalPoints.concat(points);
                if (types.length < 1)
                    return cb(finalPoints);
                recuit(0, types.pop());
                return;
            }
            
            var curPoints = [];
            if (points.length >= index + limit) {
                curPoints = points.slice(index, index + limit);
            } else {
                curPoints = points.slice(index);
            }
            var coos = '';
            var realPoint = [];
            for (var i in curPoints) {
                var pt = curPoints[i];
                if(pt.lng != 0 && pt.lat != 0)
            	{
                	coos += pt.lng + ',' + pt.lat + ';';
                	realPoint.push(pt);
            	}
                else
            	{
                	finalPoints.push(pt);
            	}
            }

            $http.jsonp('http://api.map.baidu.com/geoconv/v1/?' + [
                'ak=' + Global.MAP_KEY,
                'from='+type,
                'to=5',
                'output=json',
                'coords='+ coos.substr(0, coos.length - 1),
                'callback=JSON_CALLBACK'
            ].join("&")).success(function (ret) {
                for (var i in realPoint) {
                    var nd = realPoint[i];
                    if (ret.status === 0 && ret.result && ret.result.length > i) {
                        nd.rlng = ret.result[i].x;
                        nd.rlat = ret.result[i].y;
                    }
                }
                finalPoints = finalPoints.concat(realPoint);
                if (finalPoints.length >= data.length) {
                    cb(finalPoints);
                } else {
                    recuit(index + limit, type);
                }
            }).error(function (data) {
                finalPoints = finalPoints.concat(curPoints);
                if (finalPoints.length >= points.length) {
                    cb(finalPoints);
                } else {
                    recuit(index + limit, type);
                }
            });
        };
        recuit(0, types.pop());
    };
    
    /**
     * option参数解释：
     *   center和zoom： 可选，用于设置地图默认的显示中心， center为地名字符串或BMap.Point类型， zoom为数字；
     *   city： 可选，同样用于设置显示中心， 优先级次之。没有地图中心参数的时候默认显示少华山，缩放级别为19.
     *   
     *   marker： 可选，显示于地图的标记，类型为BMap.Marker，用于自定义marker行为。内部逻辑仅仅将其addOverlay。
     *   point： 可选，显示于地图的标记，在有marker参数的情况下忽略。内部包含坐标转换逻辑，同convert函数；包含自定义标记和地图弹框逻辑，同makeMark。
     *   markerOptions和markerClickOptions: 可选，作为point的自定义标记和弹框参数。
     *   
     *   polygon： 可选，用于显示多边形，类型为object，其中的points为BMap.Point的Array， options为绘制多边形的参数，详见百度API。
     * @param {type} opt
     * @param {type} cb
     * @returns {modal}
     */
    var popMap = function (opt, cb) {
        return $uibModal.open({
            templateUrl: 'app/views/template/map.modal.html',
            controller: 'mapModalCtrl',
            windowClass: "modal-ssmp",
            showClose: true,
            backdrop: 'static',
            size: 'md', //大小配置
            resolve: {
                data: function () {
                    return opt;
                }
            }
        });
    };
    
    return {
        buildContent: buildContent,
        makeMark: makeMark,
        convert: convert,
        popMap: popMap
    };
});