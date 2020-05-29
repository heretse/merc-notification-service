/*!
 * Bootstrap Admin App + AngularJS
 * 
 * Author: @bopin
 * 
 */
if (typeof $ === 'undefined') {
    throw new Error('This application\'s JavaScript requires jQuery');
}
App = angular.module('notice', [
    'ui.bootstrap',
    'ngRoute',
    'ngAnimate',
    'ngStorage',
    'ngCookies',
    'pascalprecht.translate',
    'ui.router',
    'oc.lazyLoad',
    'ngSanitize'
]);

App.run(["$rootScope", "$state", "$stateParams", '$window', '$templateCache', '$localStorage', function ($rootScope, $state, $stateParams, $window, $templateCache, $localStorage) {
    // Set reference to access them from any scope
    $rootScope.$state = $state;
    $rootScope.$stateParams = $stateParams;
    $rootScope.$storage = $window.localStorage;
    // Scope Globals
    $rootScope.app = {
        name: '通知服务',
        description: '博频（上海）科技股份有限公司版权所有',
        year: ((new Date()).getFullYear()),
        layout: {
            isFixed: true,
            isCollapsed: false,
            isBoxed: false,
            isRTL: false,
            horizontal: false,
            isFloat: false,
            asideHover: false,
            theme: null
        },
        useFullLayout: false,
        hiddenFooter: false,
        viewAnimation: 'ng-fadeInUp'
    };
    $rootScope.user = {
        guest: true,
        name: 'admin',
        job: '',
        picture: 'app/img/user/user.jpg',
        configure: {}
    };
    if ($localStorage.user) {
        $rootScope.user.name = $localStorage.user.name;
        $rootScope.user.guest = false;
    }
    else
	{
    	$localStorage.user = {};
	}
    if (!$localStorage.configure) {
        $localStorage.configure = {
            "terminalConfig": {
                "id": 12, "terminalUpperLimit": 500, " defaultLimit ": 100, "timeScope": 120, "refreshCycle": 60
            },
            "rssiGradeConfig": [{"id": 3, "grade": 5, "value": 54}],
            "ThermodynamicDiagramConfig": {
                "id": 2, "step": 10, "toleranceTime": 120,
                "transparency": 0.5, "radius": 10, "gradientInterval": 0
            }
        };

    }
}]);

/**=========================================================
 * Module: config.js
 * App routes and resources configuration
 =========================================================*/

App.config(['$stateProvider', '$locationProvider', '$urlRouterProvider', 'RouteHelpersProvider',
    function ($stateProvider, $locationProvider, $urlRouterProvider, helper) {
        'use strict';
        // Set the following to true to enable the HTML5 Mode
        // You may have to set <base> tag in index and a routing configuration in your server
        $locationProvider.html5Mode(false);
        // defaults to dashboard
        $urlRouterProvider.otherwise('/page/login');
        // Application Routes
        $stateProvider
            .state('app', {
                url: '/app',
                abstract: true,
                templateUrl: helper.basepath('app.html'),
                controller: 'AppController',
                resolve: helper.resolveFor('icons', 'md5', 'RestClient', 'ngTagsInput')
            })
            .state("app.dev",{
                url: '/dev',
                templateUrl: helper.basepath("op.html")
            })
            .state('app.dev.terminal', {
                url: '/ter',
                title: '终端管理',
                controller: "devTermCtrl",
                templateUrl: helper.basepath("dev/terminal/terminal.index.html"),
                resolve: helper.resolveFor('devTermCtrl', 'datetimepicker','xlsx-model')
            })
            .state('app.dev.terminalgroup', {
                url: '/terg',
                title: '终端组管理',
                controller: "devTermGroupCtrl",
                templateUrl: helper.basepath("dev/terminalgroup/terminalgroup.index.html"),
                resolve: helper.resolveFor('devTermGroupCtrl', 'datetimepicker')
            })
            .state("app.inf",{
                url: '/inf',
                templateUrl: helper.basepath("op.html")
            })
            .state('app.inf.person', {
                url: '/person',
                title: '通报对象管理',
                controller: "infPersonCtrl",
                templateUrl: helper.basepath("inf/person/person.index.html"),
                resolve: helper.resolveFor('infPersonCtrl', 'datetimepicker')
            })
            .state('app.inf.group', {
                url: '/group',
                title: '通报组管理',
                controller: "infGroupCtrl",
                templateUrl: helper.basepath("inf/group/group.index.html"),
                resolve: helper.resolveFor('infGroupCtrl', 'datetimepicker')
            })
            /**
             * 系统管理
             */
            .state('app.sys', {
                url: '/sys',
                templateUrl: helper.basepath("op.html")
            })
            .state('app.sys.user', {
                url: '/user',
                title: '账号管理',
                controller: "sysUserCtrl",
                templateUrl: helper.basepath("sys/user/user.index.html"),
                resolve: helper.resolveFor('sysUserCtrl')
            })
            // -----------------------------------
            .state('page', {
                url: '/page',
                templateUrl: 'app/pages/page.html',
                resolve: helper.resolveFor('icons'),
                controller: ["$rootScope", function ($rootScope) {
                    $rootScope.app.layout.isBoxed = false;
                }]
            })
            .state('page.login', {
                url: '/login',
                title: "登录",
                templateUrl: 'app/pages/login.html',
                controller: 'LoginPageController',
                resolve: helper.resolveFor('RestClient', 'md5')
            })
            .state('page.404', {
                url: '/404',
                title: "Not Found",
                templateUrl: 'app/pages/404.html'
            });
    }]).config(['$ocLazyLoadProvider', 'APP_REQUIRES', function ($ocLazyLoadProvider, APP_REQUIRES) {
    'use strict';
    // Lazy Load modules configuration
    $ocLazyLoadProvider.config({
        debug: false,
        events: true,
        modules: APP_REQUIRES.modules
    });

}]).config(['$controllerProvider', '$compileProvider', '$filterProvider', '$provide',
    function ($controllerProvider, $compileProvider, $filterProvider, $provide) {
        'use strict';
        // registering components after bootstrap
        App.controller = $controllerProvider.register;
        App.directive = $compileProvider.directive;
        App.filter = $filterProvider.register;
        App.factory = $provide.factory;
        App.service = $provide.service;
        App.constant = $provide.constant;
        App.value = $provide.value;

}]).config(['$translateProvider', function ($translateProvider) {
//    $translateProvider.useLocalStorage();
    $translateProvider.useStaticFilesLoader({
        prefix: 'app/i18n/',
        suffix: '.json'
    });
    $translateProvider.preferredLanguage('zh');
    $translateProvider.usePostCompiling(true);
}]).config(['$tooltipProvider', function ($tooltipProvider) {
    $tooltipProvider.options({appendToBody: true});
}]);

/**=========================================================
 * Module: constants.js
 * Define constants to inject across the application
 =========================================================*/
App.constant('APP_COLORS', {
    'primary': '#5d9cec',
    'success': '#27c24c',
    'info': '#23b7e5',
    'warning': '#ff902b',
    'danger': '#f05050',
    'inverse': '#131e26',
    'green': '#37bc9b',
    'pink': '#f532e5',
    'purple': '#7266ba',
    'dark': '#3a3f51',
    'yellow': '#fad732',
    'gray-darker': '#232735',
    'gray-dark': '#3a3f51',
    'gray': '#dde6e9',
    'gray-light': '#e4eaec',
    'gray-lighter': '#edf1f2'
}).constant('APP_MEDIAQUERY', {
    'desktopLG': 1200,
    'desktop': 992,
    'tablet': 768,
    'mobile': 480
}).constant('APP_REQUIRES', {
    // jQuery based and standalone scripts
    scripts: {
        'RestClient': ['app/js/services/service.js'],
        'echarts': ['app/lib/echarts.js'],
        'heatmap': ['app/lib/Heatmap_min.js'],
        'highchart': ['app/lib/highcharts.js', 'app/lib/highcharts-ng.js'],
        'devTermGroupCtrl':['app/js/controllers/dev/devTermGroupCtrl.js'],
        'devTermCtrl':['app/js/controllers/dev/devTermCtrl.js'],
        'infPersonCtrl': ['app/js/controllers/inf/infPersonCtrl.js'],
        'infGroupCtrl': ['app/js/controllers/inf/infGroupCtrl.js'],
        'bizCustomerCtrl': ['app/js/controllers/biz/bizCustomerCtrl.js'],
        'bizRegisterCtrl': ['app/js/controllers/biz/bizRegisterCtrl.js'],
        'sysUserCtrl': ['app/js/controllers/sys/sysUserCtrl.js'],
        'mapModal': ['app/js/controllers/mapModalCtrl.js'],
        'md5': ['app/lib/md5.js'],
        'animo': ['vendor/animo.js/animo.js'],
        'animate': ['vendor/animate.css/animate.min.css'],
        'icons': ['vendor/skycons/skycons.js','vendor/fontawesome/css/font-awesome.min.css','vendor/simple-line-icons/css/simple-line-icons.css','vendor/weather-icons/css/weather-icons.min.css'],
        'slimscroll': ['vendor/slimScroll/jquery.slimscroll.min.js'],
        'moment': ['vendor/moment/min/moment-with-locales.min.js'],
        'inputmask': ['vendor/jquery.inputmask/dist/jquery.inputmask.bundle.min.js'],
        'taginput': ['vendor/bootstrap-tagsinput/dist/bootstrap-tagsinput.css','vendor/bootstrap-tagsinput/dist/bootstrap-tagsinput.min.js'],
        'gcal': ['vendor/fullcalendar/dist/gcal.js'],
        'loaders.css': ['vendor/loaders.css/loaders.css'],
        'spinkit': ['vendor/spinkit/css/spinkit.css']
    },
    // Angular based script (use the right module name)
    modules: [
        {
            name: 'ngTagsInput', files: ['vendor/ngTagsInput/ng-tags-input.css', 'vendor/ngTagsInput/ng-tags-input.bootstrap.css',
            'vendor/ngTagsInput/ng-tags-input.js']
        },
        {name: 'xlsx-model', files: ['vendor/angular-xlsx-model/xlsx.full.min.js',
            'vendor/angular-xlsx-model/xlsx-model.js']},
        {
            name: 'toaster', files: ['vendor/angularjs-toaster/toaster.js',
            'vendor/angularjs-toaster/toaster.css']
        },
        {
            name: 'localytics.directives', files: ['vendor/chosen_v1.2.0/chosen.jquery.min.js',
            'vendor/chosen_v1.2.0/chosen.min.css',
            'vendor/angular-chosen-localytics/chosen.js']
        },
        {	name: 'ngWig', files: ['vendor/ngWig/dist/ng-wig.min.js']
		},
        {
            name: 'angularBootstrapNavTree', files: ['vendor/angular-bootstrap-nav-tree/dist/abn_tree_directive.js',
            'vendor/angular-bootstrap-nav-tree/dist/abn_tree.css']
        },
        {
            name: 'ui.bootstrap-slider', files: ['vendor/seiyria-bootstrap-slider/dist/bootstrap-slider.min.js',
            'vendor/seiyria-bootstrap-slider/dist/css/bootstrap-slider.min.css',
            'vendor/angular-bootstrap-slider/slider.js']
        },
        {   name: 'ui.select', files: ['vendor/angular-ui-select/dist/select.min.js',
            'vendor/angular-ui-select/dist/select.min.css']
        },
        {   name: 'datetimepicker', files: ['vendor/bootstrap-datetimepicker/bootstrap-datetimepicker.js',
            'vendor/bootstrap-datetimepicker/datetimepicker.js',
            'vendor/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css']
        }
    ]
});
App.constant("Global", {
    PAGESIZE: 10,
    BASEURL: 'api/',
    PREFIX_FILTER: '',
    PREFIX_SORT: 'sort.',
    QUERY_INTERVAL: 5000,
    MAP_KEY: "18QHVbujVNyF8IGu4uto4gL6",
    MAP_MARKER_HEIGHT: 35,
	MAP_MARKER_WIDTH: 25,
	MAP_ZOOM: 19,
    MAP_CENTER: "少华山"
});

/**=========================================================
 * Module: access-login.js
 * Demo for login api
 =========================================================*/
App.controller('LoginPageController', function ($scope, $rootScope, $localStorage, $timeout, Global, Client, $state, $translate) {

    $("#loginHeight").height(document.body.clientHeight - 197 - 226);
    // bind here all data from the form
    $scope.account = {};
    // place the message if something goes wrong
    $scope.authMsg = '';
    
    $scope.press = function (e) {
        if (e.keyCode === 13 || e.charCode === 13) {
            $scope.login();
        }
    };
    
    function addCookie(name,value,days,path){   /**添加设置cookie**/  
        var name = escape(name);  
        var value = escape(value);  
        var expires = new Date();  
        expires.setTime(expires.getTime() + days * 3600000 * 24);  
        //path=/，表示cookie能在整个网站下使用，path=/temp，表示cookie只能在temp目录下使用  
        path = path == "" ? "" : ";path=" + path;                 
        var _expires = (typeof days) == "string" ? "" : ";expires=" + expires.toUTCString();  //参数days只能是数字型 
        document.cookie = name + "=" + value + _expires + path;  
    }  
    function getCookieValue(name){  /**获取cookie的值，根据cookie的键获取值**/  
        var name = escape(name);  
        var allcookies = document.cookie;         
        name += "=";  
        var pos = allcookies.indexOf(name);      
        if (pos != -1){  //如果pos值为-1则说明搜索"version="失败  
            var start = pos + name.length;               
            var end = allcookies.indexOf(";",start);        
            if (end == -1) end = allcookies.length;       
            var value = allcookies.substring(start,end); //提取cookie的值  
            return (value);                             
        }else{ 
            return "";  
        }  
    }  
    function deleteCookie(name,path){   /**根据cookie的键，删除cookie，其实就是设置其失效**/  
        var name = escape(name);  
        var expires = new Date(0);  
        path = path == "" ? "" : ";path=" + path;  
        document.cookie = name + "="+ ";expires=" + expires.toUTCString() + path;  
    }  
    
    $scope.$watch('$viewContentEnter', function () {
        if ($localStorage.user) {
            $scope.account.email = $localStorage.user.name;
            $scope.loginForm.account_email.$setDirty(true);
        }               
      /*  if($localStorage.check.ck == true){
        	$scope.account.remember = true;
            var userNameValue = getCookieValue("userName"); 
            var userPassValue = getCookieValue("userPass");           
            if (userNameValue||userPassValue) {
            	$scope.account.email = userNameValue;  
            	$scope.loginForm.account_email.$setDirty(true);
            	$scope.account.password = userPassValue;  
            	$scope.loginForm.account_password.$setDirty(true);                
            }
        }*/
    });
    
    $scope.login = function () { 
        if ($scope.loginForm.$valid) {
            var loading = $rootScope.alert({
                msg: '<span class="fa fa-spinner fa-spin"></span>' + $translate.instant("common.loading"),
                hideBtn: true
            });
            Client.login(/*Global.BASEURL + */"login", {username: $scope.account.email,password: $scope.account.password}, true).then(function (data, status, header, config) {
            	 if (data.code !== 200) {
                     loading.close();
                      /*登录错误*/
                     $rootScope.alert({
                         msg: $translate.instant("common.loginFailed"),
                         type: "alert"
                     });
                 } else {
                	 $localStorage.user.name = data.data.uname;
                	 $localStorage.user.id = data.data.id;
                     loading.close();
                     $localStorage.bearer = data.Authorization;
                     $state.go('app.dev.terminal');
                     $rootScope.$broadcast('after_login', data.data);
                 }
            }, function (err) {
                loading.close();
                $rootScope.alert({
                    msg: $translate.instant("common.loginFailed"),
                    type: "alert"
                });
            });
        }else {
            $scope.loginForm.account_email.$dirty = true;
            $scope.loginForm.account_password.$dirty = true;
            if ('undefined' === typeof $scope.account.email && 'undefined' === typeof $scope.account.password) {
                $rootScope.alert({
                       msg: '请输入账号和密码！',
                       type: "alert"
                   });
                   return;
               }
            if ('undefined' === typeof $scope.account.email) {
            	$rootScope.alert({
            		msg: '请输入账号！',
                    type: "alert"
            	});
            	return;
            }
            if ('undefined' === typeof $scope.account.password) {
            	$rootScope.alert({
            		msg: '请输入密码！',
                    type: "alert"
            	});
            	return;
            }
        }
    };
});

/**
 * AngularJS default filter with the following expression:
 * "person in people | filter: {name: $select.search, age: $select.search}"
 * performs a AND between 'name: $select.search' and 'age: $select.search'.
 * We want to perform a OR.
 */
App.filter('propsFilter', function () {
    return function (items, props) {
        var out = [];

        if (angular.isArray(items)) {
            items.forEach(function (item) {
                var itemMatches = false;

                var keys = Object.keys(props);
                for (var i = 0; i < keys.length; i++) {
                    var prop = keys[i];
                    var text = props[prop].toLowerCase();
                    if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
                        itemMatches = true;
                        break;
                    }
                }

                if (itemMatches) {
                    out.push(item);
                }
            });
        } else {
            // Let the output be the input untouched
            out = items;
        }

        return out;
    };
});

/**
 * 自动截取字符串长度，
 * 切字方式 (布尔) - 如果是 true，只切单字。
 * 长度 (整數) - 要保留的最大字数。
 * 后缀 (字串，默认：'…') - 接在字字的后面。
 */
App.filter('cut', function () {
    return function (value, wordwise, max, tail) {
        if (!value) return '';
        max = parseInt(max, 10);
        if (!max) return value;
        if (value.length <= max) return value;
        value = value.substr(0, max);
        if (wordwise) {
            var lastspace = value.lastIndexOf(' ');
            if (lastspace != -1) {
                value = value.substr(0, lastspace);
            }
        }
        return value + (tail || ' …');
    };
});

App.controller("RootCtrl", function ($scope, $rootScope, $translate, $uibModal) {
    /**
     * General Alert Modal Function
     * @param config object|boolean false to close last modal；
     * @param callback function
     * @returns {undefined | modalInstance}
     */
    $rootScope.alert = function (config, callback) {
        if (config === false) {
            if (this.modal)
                this.modal.close();
            return;
        }
        if (this.modal) {
            this.modal.close(false);
        }
        this.modal = $uibModal.open({
            templateUrl: 'app/views/template/alert.modal.html',
            controller: 'AlertModalController',
            closeByDocument: false,
            showClose: false,
            backdrop: 'static',
            closeByEscape: false,
            size: 'sm',
            windowClass: "modal-ssmp",
            resolve: {
                config: function () {
                    return config;
                }
            }
        });
        this.modal.tmid = new Date().valueOf();

        this.modal.result.then(function (selectedItem) {
            if (callback) {
                callback(selectedItem);
            }
        }, function () {
            if (callback) {
                callback(false);
            }
        });
        return this.modal;
    };
    
    $rootScope.bread = function (state) {
    	var path = state.name;
        var paths = path.split(".");
    	$rootScope.breads = [];
        if (path.indexOf("dev") >= 0) {
            $rootScope.breads.push({title: $translate.instant("sidebar.nav.MENU_LOG"), active: true});
        } else {
            $rootScope.breads.push({title: $translate.instant("sidebar.nav.MENU_USER"), active: true});
        }
        if (path.indexOf("index") >= 0) {
            $rootScope.breads.push({title: $translate.instant("sidebar.bread."+path), active: true});
        } else {
            var tmp = path.substr(0, path.lastIndexOf("."));
            $rootScope.breads.push({title: $translate.instant("sidebar.bread."+tmp+".index"), active: false, url: tmp+".index"});
            $rootScope.breads.push({title: $translate.instant("sidebar.bread."+path), active: true});
        }
    };
    
    $rootScope.codeToMap = function (type) {
        return ["--", "GPS坐标", "GPS米制坐标", "国测局坐标", "国测局米制坐标", "百度坐标", "百度米制坐标", "mapbar坐标", "51地图坐标"][type] || "--";
    };
    
    $rootScope.codeToMapterminal = function (type) {
    	return ["--", "智能门票", "山体滑坡"][type] || "--";
    };
});


/**=========================================================
 * Module: main.js
 * Main Application Controller
 =========================================================*/
App.controller('AppController', ['$rootScope', '$scope', '$state', '$timeout', '$translate', '$window', '$localStorage', 'toggleStateService', 'colors', 'Client', 'Global','$uibModal',
    function ($rootScope, $scope, $state, $timeout, $translate, $window, $localStorage, toggle, colors, Client, Global) {
        "use strict";
        //Loaded
/*        $scope.$watch('$viewContentLoaded', function () {
            $scope.loop();
        });
        /!**
         * 定时刷新
         *!/
        $scope.loop = function()
        {
            var time;
            time = $timeout(function(){
                //console.log('*************************refresh broadcast');
                $rootScope.$broadcast('refresh');
                $scope.loop();
            }, Global.QUERY_INTERVAL);

            $rootScope.$on('logout',function(event){
                $timeout.cancel(time);
            });

        };*/
        // -----------------------------------
        $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
            if (!!window.ActiveXObject || "ActiveXObject" in window) {
                //ie browser 6-11, 剔除掉ie 是因为会导致闪退
            } else {
                $(document).ready(function () {
                    //chrome browser
                    if (window.history && window.history.pushState) {
                        $(window).on('popstate', function () {
                            // 当点击浏览器的 后退和前进按钮 时才会被触发
                            window.history.pushState('forward', null, '');
                            window.history.forward(1);
                        });
                    }
                    //firefox browser
                    window.history.pushState('forward', null, '');
                    window.history.forward(1);
                });
            }
/*            $rootScope.alert(false);
            if ($rootScope.timer && -1 !== $rootScope.timer) {
                clearInterval($rootScope.timer);
            }*/
        });
        // Hook not found
        $rootScope.$on('$stateNotFound',
            function (event, unfoundState, fromState, fromParams) {
                console.log(unfoundState.to); // "lazy.state"
                console.log(unfoundState.toParams); // {a:1, b:2}
                console.log(unfoundState.options); // {inherit:false} + default options
            });
        // Hook error
        $rootScope.$on('$stateChangeError',
            function (event, toState, toParams, fromState, fromParams, error) {
                console.log(error);
            });
        // Hook success
        $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
            $rootScope.clientHeight = $("body").height() - 135 + "px";
            // display new view from top
            $window.scrollTo(0, 0);
            $rootScope.bread(toState);
            // Save the route title
            $rootScope.currTitle = $state.current.title;
            $rootScope.pageTitle = $rootScope.app.name + '  ' + ($rootScope.currTitle || $rootScope.app.description);
        });
        $rootScope.$on("err", function (event, data) {
            if (data === 403) {
                if ($rootScope.timer && -1 !== $rootScope.timer) {
                    clearInterval($rootScope.timer);
                }
                $rootScope.alert(false);
                $rootScope.showLogin();
            }
        });

        if (!$localStorage.bearer) {
            $state.go('page.login');
        }



        $rootScope.onError = function (event, data) {

            if (data.code === 401) {
                if ($rootScope.timer && -1 !== $rootScope.timer) {
                    clearInterval($rootScope.timer);
                }
                $rootScope.alert(false);
                $("div.modal").remove();
                $("div.modal-backdrop").remove();
                if ($localStorage.bearer) {
                    $rootScope.alert({
                        msg: '登录超时，系统将于5秒后跳转到登陆页',
                        hideBtn: true
                    }).result.then(function (data) {
                        $rootScope.showLogin();
                    });

                    $timeout(function () {
                        $("div.modal").remove();
                        $("div.modal-backdrop").remove();
                        $rootScope.showLogin();
                    }, 5000);
                }
            }
        };

        $rootScope.$on('after_login', function () {
        });

        $rootScope.currTitle = $state.current.title;
        $rootScope.pageTitle = $rootScope.app.name + '  ' + ($rootScope.currTitle || $rootScope.app.description);

        // Close submenu when sidebar change from collapsed to normal
        $rootScope.$watch('app.layout.isCollapsed', function (newValue, oldValue) {
            if (newValue === false)
                $rootScope.$broadcast('closeSidebarMenu');
        });

        // Restore layout settings
        if (angular.isDefined($localStorage.layout))
            $scope.app.layout = $localStorage.layout;
        else
            $localStorage.layout = $scope.app.layout;

        $rootScope.$watch("app.layout", function () {
            $localStorage.layout = $scope.app.layout;
        }, true);

        // Allows to use branding color with interpolation
        // {{ colorByName('primary') }}
        $scope.colorByName = colors.byName;

        // Hides/show user avatar on sidebar
        $scope.toggleUserBlock = function () {
            $scope.$broadcast('toggleUserBlock');
        };

        // Internationalization
        $scope.language = {
            // Handles language dropdown
            listIsOpen: false,
            // list of available languages
            available: {
                'zh': '简体中文',
                'zh-tw': '繁体中文',
                'en': 'English'
            },
            // display always the current ui language
            init: function () {
                var proposedLanguage = $translate.proposedLanguage() || $translate.use();
                var preferredLanguage = $translate.preferredLanguage(); // we know we have set a preferred one in app.config
                $scope.language.selected = $scope.language.available[(proposedLanguage || preferredLanguage)];
            },
            set: function (localeId, ev) {
                // Set the new idiom
                $translate.use(localeId);
                // save a reference for the current language
                $scope.language.selected = $scope.language.available[localeId];
                // finally toggle dropdown
                $scope.language.listIsOpen = !$scope.language.listIsOpen;
            }
        };

        $scope.language.init();
        $scope.user.name = $localStorage.user.name;

        $scope.lang = 'zh';

        // Restore application classes state
        toggle.restoreState($(document.body));

        // cancel click event easily
        $rootScope.cancel = function ($event) {
            $event.stopPropagation();
        };

        $rootScope.showLogin = function () {
        	$rootScope.alert(false);
            $state.go('page.login');
        };

        $rootScope.logout = function () {
            $localStorage.bearer = null;
/*            Client.form(Global.BASEURL + "logout").then(function () {
                //退出登录
                $rootScope.$broadcast('logout');
                $rootScope.showLogin();
            }, function () {
                $rootScope.showLogin();
            });*/
            $rootScope.$broadcast('logout');
            $rootScope.showLogin();
        };
        $scope.$watch("$viewContentEnter", function () {
            $rootScope.clientHeight = $("body").height() - 135 + "px";
        });
    }]);

App.controller("TopNavBarCtrl", function ($scope, $rootScope, $timeout, $interval, Client, Global) {
    $scope.alarmSum = {
        fence: 0,
        slide: 0
    };

    $scope.$watch("$viewContentEnter", function () {
     //   $scope.query();
    });
    //定时刷新
    $rootScope.$on('refresh', function (event){
        $scope.query();
    });
    //监听智能告警加载
    $scope.$on("alarmLoad", function () {
    	 $scope.query();
    });
    //监听山体滑坡关闭
    $scope.$on("slideAlarm", function () {
   	 	$scope.query();
    });
    //监听退出系统
    $scope.$on("logout", function () {
        $("#alarm_logo").removeClass("fa-shake");
    });

    $scope.query = function () {
        Client.get("rest/web/alarm/list").then(function (data) {
            if (data && data.code == 1000 && data.data && data.data.length > 0) {
                var alarmSum = {
                    fence: 0,
                    slide: 0
                };
                for (var i in data.data) {
                    var it = data.data[i];
                    if (it.type == 1) {         //1电子围栏
                        alarmSum.fence ++;
                    } else if (it.type == 2) {   //2山体滑坡
                        alarmSum.slide ++;
                    }
                }
                $scope.alarmSum.fence = alarmSum.fence;
                $scope.alarmSum.slide = alarmSum.slide;
                $scope.alarmCount = $scope.alarmSum.fence + $scope.alarmSum.slide;
                $("#alarm_logo").addClass("fa-shake");
            } else {
                $scope.alarmSum.fence = 0;
                $scope.alarmSum.slide = 0;
                $scope.alarmCount = $scope.alarmSum.fence + $scope.alarmSum.slide;
                $("#alarm_logo").removeClass("fa-shake");
            }
        },function (err) {
            $scope.alarmSum.fence = 0;
            $scope.alarmSum.slide = 0;
            $scope.alarmCount = $scope.alarmSum.fence + $scope.alarmSum.slide;
            $("#alarm_logo").removeClass("fa-shake");
        });
    };
});

App.controller("AlertModalController", function ($scope, $sce, config, $uibModalInstance, $translate) {
    //var config = $scope.ngDialogData.config;
    $scope.title = config && config.title ? config.title : $translate.instant('common.info');
    $scope.msg = $sce.trustAsHtml(config && config.msg ? config.msg : '提示信息。');
    $scope.hideBtn = config && config.hideBtn;
    $scope.type = config.type ? config.type : "confirm";
    $scope.buttons = config && config.buttons ? config.buttons : config.type === "confirm" ? [{
        label: $translate.instant("common.confirm"),
        action: function () {
            return true;
        }
    }, {
        label: $translate.instant("common.cancel"),
        action: function () {
            return false;
        }
    }] : config.type === "alert" ? [{
        label: $translate.instant("common.close")
    }] : [{
        label: $translate.instant("common.confirm"),
        action: function () {
            return true;
        }
    }, {
        label: $translate.instant("common.cancel"),
        action: function () {
            return false;
        }
    }];

    $scope.callback = function (btn) {
        if (btn.action) {
            $uibModalInstance.close(btn.action());
            return;
        }
        if (btn.value) {
            $uibModalInstance.close(btn.value);
        } else {
            $uibModalInstance.close(btn.label);
        }
    };
});

/**=========================================================
 * Module: sidebar-menu.js
 * Handle sidebar collapsible elements
 =========================================================*/

App.controller('SidebarController', ['$rootScope', '$scope', '$state', '$http', '$timeout', 'Utils', 'Global', '$localStorage',
    function ($rootScope, $scope, $state, $http, $timeout, Utils, Global, $localStorage) {

        var collapseList = [];

        // demo: when switch from collapse to hover, close all items
        $rootScope.$watch('app.layout.asideHover', function (oldVal, newVal) {
            if (newVal === false && oldVal === true) {
                closeAllBut(-1);
            }
        });
        
        $scope.$watch("$viewContentEnter", function () {
        	$rootScope.bread($state.current);
        });

        // Check item and children active state
        var isActive = function (item) {

            if (!item) return;

            if (!item.sref || item.sref == '#') {
                var foundActive = false;
                angular.forEach(item.submenu, function (value, key) {
                    if (isActive(value)) foundActive = true;
                });
                return foundActive;
            } else {
                if (item.checkActive) {
                    return eval(item.checkActive);
                }
                if ($state.is(item.sref) || $state.includes(item.sref))
                    return true;
                if (item.inc) {
                    for (var i in item.inc) {
                        if ($state.is(item.inc[i]) || $state.includes(item.inc[i])) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };

        // Load menu from json file
        $scope.getMenuItemPropClasses = function (item) {
            return (item.heading ? 'nav-heading' : '') +
                (isActive(item) ? ' active' : '');
        };

        $scope.loadSidebarMenu = function () {
            if (!($rootScope.user.name)) {
                $rootScope.logout();
            }
            $scope.menuItems = [
            {
                "text": "设施管理",
                "sref": "#",
                "icon": "mi mi-dev",
                "label": "label label-info",
                "submenu": [
                    {"text": "终端管理", "sref": "app.dev.terminal", "translate": "sidebar.nav.MENU_TERMINAL"},
                    {"text": "终端组管理", "sref": "app.dev.terminalgroup", "translate": "sidebar.nav.MENU_TERMINALGROUP"}
                ],
                "translate": "sidebar.nav.MENU_FACILITY"
            }, {
                "text": "通报管理",
                "sref": "#",
                "icon": "mi mi-inf",
                "label": "label label-info",
                "submenu": [
                    {"text": "通报对象管理", "sref": "app.inf.person", "translate": "sidebar.nav.MENU_PERSON"},
                    {"text": "通报组", "sref": "app.inf.group", "translate": "sidebar.nav.MENU_GROUP"}
                ],
                "translate": "sidebar.nav.MENU_NOTICE"
            }, {
                "text": "系统管理",
                "sref": "#",
                "icon": "mi mi-sys",
                "label": "label label-info",
                "submenu": [
                    {"text": "终端管理", "sref": "app.sys.user", "translate": "sidebar.nav.MENU_USER"},
                ],
                "translate": "sidebar.nav.MENU_SYS"
            }];
        };
        
        $scope.$on('after_login', function () {
            $scope.loadSidebarMenu();
        });

        $scope.loadSidebarMenu();

        // Handle sidebar collapse items
        $scope.addCollapse = function ($index, item) {
            collapseList[$index] = $rootScope.app.layout.asideHover ? true : !isActive(item);
        };

        $scope.isCollapse = function ($index) {
            return (collapseList[$index]);
        };

        $scope.toggleCollapse = function ($index, isParentItem) {
            // collapsed sidebar doesn't toggle drodopwn
            if (Utils.isSidebarCollapsed() || $rootScope.app.layout.asideHover) return true;

            // make sure the item index exists
            if (angular.isDefined(collapseList[$index])) {
                if (!$scope.lastEventFromChild) {
                    collapseList[$index] = !collapseList[$index];
                    closeAllBut($index);
                }
            }
            else if (isParentItem) {
                closeAllBut(-1);
            }

            $scope.lastEventFromChild = isChild($index);

            return true;

        };
      
        function closeAllBut(index) {
            index += '';
            for (var i in collapseList) {
                if (index < 0 || index.indexOf(i) < 0)
                    collapseList[i] = true;
            }
        }

        function isChild($index) {
            return (typeof $index === 'string') && !($index.indexOf('-') < 0);
        }

    }
]);

App.controller('UserBlockController', function ($scope) {
    $scope.userBlockVisible = false;
    $scope.$on('toggleUserBlock', function (event, args) {
        $scope.userBlockVisible = !$scope.userBlockVisible;
    });
});

/**=========================================================
 * Module: anchor.js
 * Disables null anchor behavior
 =========================================================*/

App.directive('href', function () {
    return {
        restrict: 'A',
        compile: function (element, attr) {
            return function (scope, element) {
                if (attr.ngClick || attr.href === '' || attr.href === '#') {
                    if (!element.hasClass('dropdown-toggle'))
                        element.on('click', function (e) {
                            e.preventDefault();
                            e.stopPropagation();
                        });
                }
            };
        }
    };
});

/**=========================================================
 * Module: animate-enabled.js
 * Enable or disables ngAnimate for element with directive
 * @param {object} $animate description
 =========================================================*/
App.directive("animateEnabled", ["$animate", function ($animate) {
    return {
        link: function (scope, element, attrs) {
            scope.$watch(function () {
                return scope.$eval(attrs.animateEnabled, scope);
            }, function (newValue) {
                $animate.enabled(!!newValue, element);
            });
        }
    };
}]);


/**=========================================================
 * Module: load-css.js
 * Request and load into the current page a css file
 =========================================================*/
App.directive('loadCss', function () {
    'use strict';
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.on('click', function (e) {
                if (element.is('a')) e.preventDefault();
                var uri = attrs.loadCss, link;
                if (uri) {
                    link = createLink(uri);
                    if (!link) {
                        $.error('Error creating stylesheet link element.');
                    }
                }
                else {
                    $.error('No stylesheet location defined.');
                }
            });
        }
    };

    function createLink(uri) {
        var linkId = 'autoloaded-stylesheet', oldLink = $('#' + linkId).attr('id', linkId + '-old');

        $('head').append($('<link/>').attr({
            'id': linkId,
            'rel': 'stylesheet',
            'href': uri
        }));
        if (oldLink.length) {
            oldLink.remove();
        }
        return $('#' + linkId);
    }
});
/**=========================================================
 * Module: masked,js
 * Initializes the masked inputs
 =========================================================*/
App.directive('masked', function () {
    return {
        restrict: 'A',
        controller: ["$scope", "$element", function ($scope, $element) {
            var $elem = $($element);
            if ($.fn.inputmask)
                $elem.inputmask();
        }]
    };
});

/**=========================================================
 * Module: notify.js
 * Directive for notify plugin
 =========================================================*/
App.directive('notify', ["$window", "Notify", function ($window, Notify) {

    return {
        restrict: 'A',
        scope: {
            options: '=',
            message: '='
        },
        link: function (scope, element, attrs) {

            element.on('click', function (e) {
                e.preventDefault();
                Notify.alert(scope.message, scope.options);
            });

        }
    };

}]);


/**=========================================================
 * Module: play-animation.js
 * Provides a simple way to run animation with a trigger
 * Requires animo.js
 =========================================================*/
App.directive('animate', ["$window", "Utils", function ($window, Utils) {
    'use strict';
    var $scroller = $(window).add('body, .wrapper');
    return {
        restrict: 'A',
        link: function (scope, elem, attrs) {
            // Parse animations params and attach trigger to scroll
            var $elem = $(elem),
                offset = $elem.data('offset'),
                delay = $elem.data('delay') || 100, // milliseconds
                animation = $elem.data('play') || 'bounce';
            if (typeof offset !== 'undefined') {
                // test if the element starts visible
                testAnimation($elem);
                // test on scroll
                $scroller.scroll(function () {
                    testAnimation($elem);
                });
            }

            // Test an element visibilty and trigger the given animation
            function testAnimation(element) {
                if (!element.hasClass('anim-running') &&
                    Utils.isInView(element, {topoffset: offset})) {
                    element
                        .addClass('anim-running');

                    setTimeout(function () {
                        element
                            .addClass('anim-done')
                            .animo({animation: animation, duration: 0.7});
                    }, delay);
                }
            }

            // Run click triggered animations
            $elem.on('click', function () {
                var $elem = $(this),
                    targetSel = $elem.data('target'),
                    animation = $elem.data('play') || 'bounce',
                    target = $(targetSel);

                if (target && target.length) {
                    target.animo({animation: animation});
                }
            });
        }
    };
}]);

/**=========================================================
 * Module: sidebar.js
 * Wraps the sidebar and handles collapsed state
 =========================================================*/
App.directive('sidebar', ['$rootScope', '$window', 'Utils', function ($rootScope, $window, Utils) {

    var $win = $($window);
    var $body = $('body');
    var $scope;
    var $sidebar;
    var currentState = $rootScope.$state.current.name;

    return {
        restrict: 'EA',
        template: '<nav class="sidebar" ng-transclude></nav>',
        transclude: true,
        replace: true,
        link: function (scope, element, attrs) {
            $scope = scope;
            $sidebar = element;
            var eventName = Utils.isTouch() ? 'click' : 'mouseenter';
            var subNav = $();
            $sidebar.on(eventName, '.nav > li', function () {
                if (Utils.isSidebarCollapsed() || $rootScope.app.layout.asideHover) {
                    subNav.trigger('mouseleave');
                    subNav = toggleMenuItem($(this));
                    // Used to detect click and touch events outside the sidebar
                    sidebarAddBackdrop();
                }
            });

            scope.$on('closeSidebarMenu', function () {
                removeFloatingNav();
            });

            // Normalize state when resize to mobile
            $win.on('resize', function () {
                if (!Utils.isMobile())
                    $body.removeClass('aside-toggled');
            });

            // Adjustment on route changes
            $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
                currentState = toState.name;
                // Hide sidebar automatically on mobile
                $('body.aside-toggled').removeClass('aside-toggled');
                $rootScope.$broadcast('closeSidebarMenu');
            });

            // Allows to close
            if (angular.isDefined(attrs.sidebarAnyclickClose)) {
                $('.wrapper').on('click.sidebar', function (e) {
                    // don't check if sidebar not visible
                    if (!$body.hasClass('aside-toggled')) return;
                    // if not child of sidebar
                    if (!$(e.target).parents('.aside').length) {
                        $body.removeClass('aside-toggled');
                    }
                });
            }
        }
    };

    function sidebarAddBackdrop() {
        var $backdrop = $('<div/>', {'class': 'dropdown-backdrop'});
        $backdrop.insertAfter('.aside-inner').on("click mouseenter", function () {
            removeFloatingNav();
        });
    }

    // Open the collapse sidebar submenu items when on touch devices
    // - desktop only opens on hover
    function toggleTouchItem($element) {
        $element
            .siblings('li')
            .removeClass('open')
            .end()
            .toggleClass('open');
    }

    // Handles hover to open items under collapsed menu
    // -----------------------------------
    function toggleMenuItem($listItem) {
        removeFloatingNav();
        var ul = $listItem.children('ul');

        if (!ul.length) return $();
        if ($listItem.hasClass('open')) {
            toggleTouchItem($listItem);
            return $();
        }

        var $aside = $('.aside');
        var $asideInner = $('.aside-inner'); // for top offset calculation
        // float aside uses extra padding on aside
        var mar = parseInt($asideInner.css('padding-top'), 0) + parseInt($aside.css('padding-top'), 0);
        var subNav = ul.clone().appendTo($aside);

        toggleTouchItem($listItem);

        var itemTop = ($listItem.position().top + mar) - $sidebar.scrollTop();
        var vwHeight = $win.height();

        subNav
            .addClass('nav-floating')
            .css({
                position: $scope.app.layout.isFixed ? 'fixed' : 'absolute',
                top: itemTop,
                bottom: (subNav.outerHeight(true) + itemTop > vwHeight) ? 0 : 'auto'
            });

        subNav.on('mouseleave', function () {
            toggleTouchItem($listItem);
            subNav.remove();
        });
        return subNav;
    }

    function removeFloatingNav() {
        $('.dropdown-backdrop').remove();
        $('.sidebar-subnav.nav-floating').remove();
        $('.sidebar li.open').removeClass('open');
    }
}]);

/**=========================================================
 * Module: toggle-state.js
 * Toggle a classname from the BODY Useful to change a state that
 * affects globally the entire layout or more than one item
 * Targeted elements must have [toggle-state="CLASS-NAME-TO-TOGGLE"]
 * User no-persist to avoid saving the sate in browser storage
 =========================================================*/
App.directive('toggleState', ['toggleStateService', function (toggle) {
    'use strict';
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {

            var $body = $('body');

            $(element)
                .on('click', function (e) {
                    e.preventDefault();
                    var classname = attrs.toggleState;
                    if (classname) {
                        if ($body.hasClass(classname)) {
                            $body.removeClass(classname);
                            if (!attrs.noPersist)
                                toggle.removeState(classname);
                        }
                        else {
                            $body.addClass(classname);
                            if (!attrs.noPersist)
                                toggle.addState(classname);
                        }

                    }

                });
        }
    };

}]);

/**=========================================================
 * Module: validate-form.js
 * Initializes the validation plugin Parsley
 =========================================================*/
App.directive('validateForm', function () {
    return {
        restrict: 'A',
        controller: ["$scope", "$element", function ($scope, $element) {
            var $elem = $($element);
            if ($.fn.parsley)
                $elem.parsley();
        }]
    };
});

/**=========================================================
 * Module: browser.js
 * Browser detection
 =========================================================*/

App.service('browser', function () {
    "use strict";

    var matched, browser;

    var uaMatch = function (ua) {
        ua = ua.toLowerCase();

        var match = /(opr)[\/]([\w.]+)/.exec(ua) ||
            /(chrome)[ \/]([\w.]+)/.exec(ua) ||
            /(version)[ \/]([\w.]+).*(safari)[ \/]([\w.]+)/.exec(ua) ||
            /(webkit)[ \/]([\w.]+)/.exec(ua) ||
            /(opera)(?:.*version|)[ \/]([\w.]+)/.exec(ua) ||
            /(msie) ([\w.]+)/.exec(ua) ||
            ua.indexOf("trident") >= 0 && /(rv)(?::| )([\w.]+)/.exec(ua) ||
            ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(ua) ||
            [];

        var platform_match = /(ipad)/.exec(ua) ||
            /(iphone)/.exec(ua) ||
            /(android)/.exec(ua) ||
            /(windows phone)/.exec(ua) ||
            /(win)/.exec(ua) ||
            /(mac)/.exec(ua) ||
            /(linux)/.exec(ua) ||
            /(cros)/i.exec(ua) ||
            [];

        return {
            browser: match[3] || match[1] || "",
            version: match[2] || "0",
            platform: platform_match[0] || ""
        };
    };

    matched = uaMatch(window.navigator.userAgent);
    browser = {};

    if (matched.browser) {
        browser[matched.browser] = true;
        browser.version = matched.version;
        browser.versionNumber = parseInt(matched.version);
    }

    if (matched.platform) {
        browser[matched.platform] = true;
    }

    // These are all considered mobile platforms, meaning they run a mobile browser
    if (browser.android || browser.ipad || browser.iphone || browser["windows phone"]) {
        browser.mobile = true;
    }

    // These are all considered desktop platforms, meaning they run a desktop browser
    if (browser.cros || browser.mac || browser.linux || browser.win) {
        browser.desktop = true;
    }

    // Chrome, Opera 15+ and Safari are webkit based browsers
    if (browser.chrome || browser.opr || browser.safari) {
        browser.webkit = true;
    }

    // IE11 has a new token so we will assign it msie to avoid breaking changes
    if (browser.rv) {
        var ie = "msie";

        matched.browser = ie;
        browser[ie] = true;
    }

    // Opera 15+ are identified as opr
    if (browser.opr) {
        var opera = "opera";

        matched.browser = opera;
        browser[opera] = true;
    }

    // Stock Android browsers are marked as Safari on Android.
    if (browser.safari && browser.android) {
        var android = "android";

        matched.browser = android;
        browser[android] = true;
    }

    // Assign the name and platform variable
    browser.name = matched.browser;
    browser.platform = matched.platform;


    return browser;

});
/**=========================================================
 * Module: colors.js
 * Services to retrieve global colors
 =========================================================*/

App.factory('colors', ['APP_COLORS', function (colors) {

    return {
        byName: function (name) {
            return (colors[name] || '#fff');
        }
    };

}]);

/**=========================================================
 * Module: notify.js
 * Create a notifications that fade out automatically.
 * Based on Notify addon from UIKit (http://getuikit.com/docs/addons_notify.html)
 =========================================================*/

App.service('Notify', ["$timeout", function ($timeout) {
    this.alert = alert;

    ////////////////

    function alert(msg, opts) {
        if (msg) {
            $timeout(function () {
                $.notify(msg, opts || {});
            });
        }
    }

}]);


/**
 * Notify Addon definition as jQuery plugin
 * Adapted version to work with Bootstrap classes
 * More information http://getuikit.com/docs/addons_notify.html
 */

(function ($, window, document) {

    var containers = {},
        messages = {},

        notify = function (options) {

            if ($.type(options) == 'string') {
                options = {message: options};
            }

            if (arguments[1]) {
                options = $.extend(options, $.type(arguments[1]) == 'string' ? {status: arguments[1]} : arguments[1]);
            }

            return (new Message(options)).show();
        },
        closeAll = function (group, instantly) {
            if (group) {
                for (var id in messages) {
                    if (group === messages[id].group) messages[id].close(instantly);
                }
            } else {
                for (var id in messages) {
                    messages[id].close(instantly);
                }
            }
        };

    var Message = function (options) {

        var $this = this;

        this.options = $.extend({}, Message.defaults, options);

        this.uuid = "ID" + (new Date().getTime()) + "RAND" + (Math.ceil(Math.random() * 100000));
        this.element = $([
            // @geedmo: alert-dismissable enables bs close icon
            '<div class="uk-notify-message alert-dismissable">',
            '<a class="close">&times;</a>',
            '<div>' + this.options.message + '</div>',
            '</div>'

        ].join('')).data("notifyMessage", this);

        // status
        if (this.options.status) {
            this.element.addClass('alert alert-' + this.options.status);
            this.currentstatus = this.options.status;
        }

        this.group = this.options.group;

        messages[this.uuid] = this;

        if (!containers[this.options.pos]) {
            containers[this.options.pos] = $('<div class="uk-notify uk-notify-' + this.options.pos + '"></div>').appendTo('body').on("click", ".uk-notify-message", function () {
                $(this).data("notifyMessage").close();
            });
        }
    };


    $.extend(Message.prototype, {

        uuid: false,
        element: false,
        timout: false,
        currentstatus: "",
        group: false,

        show: function () {

            if (this.element.is(":visible")) return;

            var $this = this;

            containers[this.options.pos].show().prepend(this.element);

            var marginbottom = parseInt(this.element.css("margin-bottom"), 10);

            this.element.css({
                "opacity": 0,
                "margin-top": -1 * this.element.outerHeight(),
                "margin-bottom": 0
            }).animate({"opacity": 1, "margin-top": 0, "margin-bottom": marginbottom}, function () {

                if ($this.options.timeout) {

                    var closefn = function () {
                        $this.close();
                    };

                    $this.timeout = setTimeout(closefn, $this.options.timeout);

                    $this.element.hover(
                        function () {
                            clearTimeout($this.timeout);
                        },
                        function () {
                            $this.timeout = setTimeout(closefn, $this.options.timeout);
                        }
                    );
                }

            });

            return this;
        },

        close: function (instantly) {

            var $this = this,
                finalize = function () {
                    $this.element.remove();

                    if (!containers[$this.options.pos].children().length) {
                        containers[$this.options.pos].hide();
                    }

                    delete messages[$this.uuid];
                };

            if (this.timeout) clearTimeout(this.timeout);

            if (instantly) {
                finalize();
            } else {
                this.element.animate({
                    "opacity": 0,
                    "margin-top": -1 * this.element.outerHeight(),
                    "margin-bottom": 0
                }, function () {
                    finalize();
                });
            }
        },

        content: function (html) {

            var container = this.element.find(">div");

            if (!html) {
                return container.html();
            }

            container.html(html);

            return this;
        },

        status: function (status) {

            if (!status) {
                return this.currentstatus;
            }

            this.element.removeClass('alert alert-' + this.currentstatus).addClass('alert alert-' + status);

            this.currentstatus = status;

            return this;
        }
    });

    Message.defaults = {
        message: "",
        status: "normal",
        timeout: 5000,
        group: null,
        pos: 'top-center'
    };


    $["notify"] = notify;
    $["notify"].message = Message;
    $["notify"].closeAll = closeAll;

    return notify;

}(jQuery, window, document));

/**=========================================================
 * Module: helpers.js
 * Provides helper functions for routes definition
 =========================================================*/

App.provider('RouteHelpers', ['APP_REQUIRES', function (appRequires) {
    "use strict";

    // Set here the base of the relative path
    // for all app views
    this.basepath = function (uri) {
        return 'app/views/' + uri;
    };

    // Generates a resolve object by passing script names
    // previously configured in constant.APP_REQUIRES
    this.resolveFor = function () {
        var _args = arguments;
        return {
            deps: ['$ocLazyLoad', '$q', function ($ocLL, $q) {
                // Creates a promise chain for each argument
                var promise = $q.when(1); // empty promise
                for (var i = 0, len = _args.length; i < len; i++) {
                    promise = andThen(_args[i]);
                }
                return promise;

                // creates promise to chain dynamically
                function andThen(_arg) {
                    // also support a function that returns a promise
                    if (typeof _arg == 'function')
                        return promise.then(_arg);
                    else
                        return promise.then(function () {
                            // if is a module, pass the name. If not, pass the array
                            var whatToLoad = getRequired(_arg);
                            // simple error check
                            if (!whatToLoad) return $.error('Route resolve: Bad resource name [' + _arg + ']');
                            // finally, return a promise
                            return $ocLL.load(whatToLoad);
                        });
                }

                // check and returns required data
                // analyze module items with the form [name: '', files: []]
                // and also simple array of script files (for not angular js)
                function getRequired(name) {
                    if (appRequires.modules)
                        for (var m in appRequires.modules)
                            if (appRequires.modules[m].name && appRequires.modules[m].name === name)
                                return appRequires.modules[m];
                    return appRequires.scripts && appRequires.scripts[name];
                }

            }]
        };
    }; // resolveFor

    // not necessary, only used in config block for routes
    this.$get = function () {
        return {
            basepath: this.basepath
        };
    };

}]);


/**=========================================================
 * Module: toggle-state.js
 * Services to share toggle state functionality
 =========================================================*/

App.service('toggleStateService', ['$rootScope', function ($rootScope) {

    var storageKeyName = 'toggleState';

    // Helper object to check for words in a phrase //
    var WordChecker = {
        hasWord: function (phrase, word) {
            return new RegExp('(^|\\s)' + word + '(\\s|$)').test(phrase);
        },
        addWord: function (phrase, word) {
            if (!this.hasWord(phrase, word)) {
                return (phrase + (phrase ? ' ' : '') + word);
            }
        },
        removeWord: function (phrase, word) {
            if (this.hasWord(phrase, word)) {
                return phrase.replace(new RegExp('(^|\\s)*' + word + '(\\s|$)*', 'g'), '');
            }
        }
    };

    // Return service public methods
    return {
        // Add a state to the browser storage to be restored later
        addState: function (classname) {
            var data = angular.fromJson($rootScope.$storage[storageKeyName]);

            if (!data) {
                data = classname;
            }
            else {
                data = WordChecker.addWord(data, classname);
            }

            $rootScope.$storage[storageKeyName] = angular.toJson(data);
        },

        // Remove a state from the browser storage
        removeState: function (classname) {
            var data = $rootScope.$storage[storageKeyName];
            // nothing to remove
            if (!data) return;

            data = WordChecker.removeWord(data, classname);

            $rootScope.$storage[storageKeyName] = angular.toJson(data);
        },

        // Load the state string and restore the classlist
        restoreState: function ($elem) {
            var data = angular.fromJson($rootScope.$storage[storageKeyName]);

            // nothing to restore
            if (!data) return;
            $elem.addClass(data);
        }

    };

}]);

/**=========================================================
 * Module: utils.js
 * Utility library to use across the theme
 =========================================================*/

App.service('Utils', ["$window", "APP_MEDIAQUERY", function ($window, APP_MEDIAQUERY) {
    'use strict';

    var $html = angular.element("html"),
        $win = angular.element($window),
        $body = angular.element('body');

    return {
        // DETECTION
        support: {
            transition: (function () {
                var transitionEnd = (function () {

                    var element = document.body || document.documentElement,
                        transEndEventNames = {
                            WebkitTransition: 'webkitTransitionEnd',
                            MozTransition: 'transitionend',
                            OTransition: 'oTransitionEnd otransitionend',
                            transition: 'transitionend'
                        }, name;

                    for (name in transEndEventNames) {
                        if (element.style[name] !== undefined) return transEndEventNames[name];
                    }
                }());

                return transitionEnd && {end: transitionEnd};
            })(),
            animation: (function () {

                var animationEnd = (function () {

                    var element = document.body || document.documentElement,
                        animEndEventNames = {
                            WebkitAnimation: 'webkitAnimationEnd',
                            MozAnimation: 'animationend',
                            OAnimation: 'oAnimationEnd oanimationend',
                            animation: 'animationend'
                        }, name;

                    for (name in animEndEventNames) {
                        if (element.style[name] !== undefined) return animEndEventNames[name];
                    }
                }());

                return animationEnd && {end: animationEnd};
            })(),
            requestAnimationFrame: window.requestAnimationFrame ||
            window.webkitRequestAnimationFrame ||
            window.mozRequestAnimationFrame ||
            window.msRequestAnimationFrame ||
            window.oRequestAnimationFrame ||
            function (callback) {
                window.setTimeout(callback, 1000 / 60);
            },
            touch: (
                ('ontouchstart' in window && navigator.userAgent.toLowerCase().match(/mobile|tablet/)) ||
                (window.DocumentTouch && document instanceof window.DocumentTouch) ||
                (window.navigator['msPointerEnabled'] && window.navigator['msMaxTouchPoints'] > 0) || //IE 10
                (window.navigator['pointerEnabled'] && window.navigator['maxTouchPoints'] > 0) || //IE >=11
                false
            ),
            mutationobserver: (window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver || null)
        },
        // UTILITIES
        isInView: function (element, options) {

            var $element = $(element);

            if (!$element.is(':visible')) {
                return false;
            }

            var window_left = $win.scrollLeft(),
                window_top = $win.scrollTop(),
                offset = $element.offset(),
                left = offset.left,
                top = offset.top;

            options = $.extend({topoffset: 0, leftoffset: 0}, options);

            if (top + $element.height() >= window_top && top - options.topoffset <= window_top + $win.height() &&
                left + $element.width() >= window_left && left - options.leftoffset <= window_left + $win.width()) {
                return true;
            } else {
                return false;
            }
        },
        langdirection: $html.attr("dir") == "rtl" ? "right" : "left",
        isTouch: function () {
            return $html.hasClass('touch');
        },
        isSidebarCollapsed: function () {
            return $body.hasClass('aside-collapsed');
        },
        isSidebarToggled: function () {
            return $body.hasClass('aside-toggled');
        },
        isMobile: function () {
            return $win.width() < APP_MEDIAQUERY.tablet;
        }
    };
}]);