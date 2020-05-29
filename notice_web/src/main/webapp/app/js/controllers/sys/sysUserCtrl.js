/**
 * Created by Administrator on 2017/7/11.
 */
App.controller("sysUserCtrl", function ($scope, $rootScope, $uibModal, $translate, Client, PageFactory, MapUtil, $localStorage, Global, $compile, $localStorage, $filter) {

    $scope.pageNo = 1;
    $scope.total = 0;
    $scope.filter = {};
    $scope.pager = PageFactory.page(Global.BASEURL + "general/get",{api:"account/query",filter:{}});

    $scope.$watch('$viewContentLoaded', function () {
        $scope.goPage(1);
    });

    $scope.search = function () {
        if ($scope.filter.account) {
            if(!/^[a-zA-Z0-9\_\@]+$/.test($scope.filter.name)){
                $rootScope.alert({
                    msg: '账号不能使用汉字和特殊字符',
                    type: "alert"
                });
                return;
            }
        }
        $scope.uname = $scope.filter.account;
        $scope.pager = PageFactory.page(Global.BASEURL + "general/get",{api:"account/query",filter:{uname:$scope.uname}});
        $scope.goPage(1);

    };

    $scope.goPage = function (no) {
        var loading = $rootScope.alert({
            msg: '<span class="icon-spinner icon-spin"></span>加载中......',
            hideBtn: true
        });
        $scope.pager.query(no).then(function (data) {
            $scope.nodes = data.data.data;
            $scope.total = data.data.count;
            $scope.page = no;
            loading.close();
        }, function (err) {
            loading.close();
            $rootScope.alert({
                msg: '操作失败！',
                type: 'alert'
            });
        });
    };

    $scope.detail = function (node) {
        $uibModal.open({
            templateUrl: 'user_detail.html',
            controller: 'sysUserDetailCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'sm', //大小配置
            windowClass: 'modal-ssmp',
            resolve: {
                data: function () {
                    return node;
                }
            }
        }).result.then(function () {
            $scope.goPage(1);
        });
    };

    $scope.add = function () {
        $uibModal.open({
            templateUrl: 'app/views/sys/user/user.add.html',
            controller: 'sysUserAddCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'sm', //大小配置
            windowClass: 'modal-smp',
            resolve: {
                sysUserCtrl: function () {
                    return $scope;
                }
            }
        }).result.then(function (flag) {
            if(flag){
                $scope.goPage(1);
            }
        });
    };


    $scope.edit = function (node) {
        $uibModal.open({
            templateUrl: 'app/views/sys/user/user.edit.html',
            controller: 'sysUserEditCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'sm', //大小配置
            windowClass: 'modal-smp',
            resolve: {
                data: function () {
                    return node;
                },
                sysUserCtrl: function () {
                    return $scope;
                }
            }
        }).result.then(function (flag) {
            if(flag){
                $scope.goPage();
            }
        });
    };


    $scope.delete = function (node) {
        var alert = $rootScope.alert({
            msg: '确认删除？'
        });
        alert.result.then(function (data) {
            if (data) {
                var loading = $rootScope.alert({
                    msg: '<span class="fa fa-spinner fa-spin"></span>加载中......',
                    hideBtn: true
                });
                Client.json(Global.BASEURL + "general/post",{api:"account/delete",data:{filter:{uid:node.id},userID:$localStorage.user.id}}).then(function (data) {
                    loading.close();
                    if (data.code == 200) {
                        $rootScope.alert({
                            msg: '删除成功！',
                            buttons: [{
                                label: "关闭"
                            }]
                        }, function () {
                            $scope.goPage($scope.pageNo);
                        });
                    } else {
                        $rootScope.alert({
                            msg: '删除失败！',
                            buttons: [{
                                label: "关闭"
                            }]
                        });
                    }

                }, function () {
                    loading.close();
                    $rootScope.alert({
                        msg: '操作失败！',
                        buttons: [{
                            label: "关闭"
                        }]
                    });
                });
            }
        }, function (err) {
            alert.dismiss();
        });

    };
    //input框的回车监听事件
    $scope.doEnter = function ($event) {
        if ($event.keyCode == 13) {//回车
            $scope.search();
        }
    };
});

//详情
App.controller('sysUserDetailCtrl', function ($scope, $uibModalInstance,$localStorage, $compile, $timeout, $filter, $translate, $http, data) {
    $scope.model = data;
    $scope.$watch('$viewContentLoaded', function () {

    });

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    }

});


//新增
App.controller('sysUserAddCtrl', function ($scope, $rootScope,$localStorage, Client, Global, $uibModalInstance, $compile, $timeout, $filter, $translate, $http, sysUserCtrl) {
    $scope.model = {};
    $scope.error = {};

    $scope.$watch('$viewContentLoaded', function () {
    });

    $scope.exit = function () {
        $uibModalInstance.dismiss();
    };

    $scope.confirm = function (model) {
        var data = {};
        if ( !$scope.model.account || !$scope.model.pwd || !$scope.model.confirm) {
            $rootScope.alert({
                msg: '请填写所有必填项',
                type: "alert"
            });
            return;
        }
        if ($scope.model.account) {
            if (!/^[0-9a-zA-Z]{5,16}$/.test($scope.model.account)) {
                $rootScope.alert({
                    msg: '账号输入内容必须为数字或字母，长度为5到16',
                    type: "alert"
                });
                return;
            /* $scope.error.name = "账号必须为数字或字母，长度为5到16";
             return;*/
            }
            data.uname = $scope.model.account;
        }
        /*if($scope.model.account.length<5){
         $rootScope.alert({
         msg: '账号长度不能小于5位',
         type: "alert"
         });
         return;
         }*/
        if($scope.model.pwd.length<5 ||$scope.model.pwd.length>16){
            $rootScope.alert({
                msg: '密码输入内容不小于5位且不大于16位',
                type: "alert"
            });
            return;
           /* $scope.error.pwd = "密码长度不能小于5位,大于16位";
            return;*/

        }
        if ($scope.model.pwd != $scope.model.confirm) {
            $rootScope.alert({
                msg: '两次输入的密码不一致',
                type: "alert"
            });
            return;
           /* $scope.error.confirm = "两次输入的密码不一致";
            return;*/
        }
        data.pwd=$scope.model.pwd;

        var loading = $rootScope.alert({
            msg: '<span class="fa fa-spinner fa-spin"></span>加载中......',
            hideBtn: true
        });
        Client.json(Global.BASEURL + "general/post", {api:"account/add", data:{filter:data,userID:$localStorage.user.id}}).then(function (data) {
            loading.close();
            if (data.code == 200) {
                $rootScope.alert({
                    msg: "添加成功!",
                    type: "alert"
                }, function () {
                    $uibModalInstance.close(true);
                });
            } else {
                if(data.code == 500){
                    $rootScope.alert({
                        msg: "添加失败：" + $translate.instant( data.desc),
                        type: "alert"
                    });
                }else{
                    $rootScope.alert({
                        msg: "添加失败：" + $translate.instant("common.errorCode." + data.code),
                        type: "alert"
                    });
                }
            }
        }, function (err) {
            loading.close();
            $rootScope.alert({
                msg: "操作失败!",
                type: "alert"
            });
        });
    };


});

//编辑
App.controller('sysUserEditCtrl', function ($scope, $rootScope,$localStorage, Global, Client, $uibModalInstance, $compile, $timeout, $filter, $translate, $http, sysUserCtrl, data) {

    $scope.model = {};

    $scope.$watch('$viewContentLoaded', function () {
        if (data) {
            $scope.model.id = data.id;
            $scope.model.account = data.uname;
            $scope.model.pwd = data.pwd;
            $scope.model.confirm = data.pwd;
            if($scope.model.account == 'admin'){
                $("#account").attr('disabled',true);
            }
        }
    });
    $scope.exit = function () {
        $uibModalInstance.dismiss();
    };
    //提交对数据进行校验
    $scope.confirm = function (model) {
        var data = {};
        data.uid = $scope.model.id;

        if ( !$scope.model.account || !$scope.model.pwd || !$scope.model.confirm) {
            $rootScope.alert({
                msg: '请填写所有必填项',
                type: "alert"
            });
            return;
        }
        if ($scope.model.account) {
            if (!/^[0-9a-zA-Z]{5,16}$/.test($scope.model.account)) {
                $rootScope.alert({
                    msg: '账号输入内容必须为数字或字母，长度为5到16',
                    type: "alert"
                });
                return;
            }
        }
        if($scope.model.pwd.length<5 ||$scope.model.pwd.length>16){
            $rootScope.alert({
                msg: '密码输入内容不小于5位且不大于16位',
                type: "alert"
            });
            return;
         }

        if ($scope.model.pwd != $scope.model.confirm) {
            $rootScope.alert({
                msg: '两次输入的密码不一致',
                type: "alert"
            });
            return;
        }

        if ($scope.model.account) {
            data.uname = $scope.model.account;
        }
        if ($scope.model.pwd && $scope.model.confirm) {
            data.pwd = $scope.model.pwd;
        }
        var loading = $rootScope.alert({
            msg: '<span class="fa fa-spinner fa-spin"></span>加载中......',
            hideBtn: true
        });
        Client.json(Global.BASEURL + "general/post", {api:"account/edit",data:{filter:data,userID:$localStorage.user.id}}).then(function (data) {
            loading.close();
            if (data.code == 200) {
                $rootScope.alert({
                    msg: "修改成功!",
                    type: "alert"
                }, function () {
                    $uibModalInstance.close(true);
                });
            } else {
                if(data.code == 1004){
                    $rootScope.alert({
                        msg: "修改失败：账号重复" ,
                        type: "alert"
                    });
                }else{
                    $rootScope.alert({
                        msg: "修改失败：" + $translate.instant("common.errorCode." + data.code),
                        type: "alert"
                    });
                }
            }
        }, function (err) {
            loading.close();
            $rootScope.alert({
                msg: "操作失败!",
                type: "alert"
            });
        });
    };
    $scope.keyup = function(){
        if($scope.model.account =='admin'){
            $scope.model.account = '';
            $("#account").attr('placeholder','admin账户已锁定,请重新输入');
        }else{
            $("#account").attr('placeholder','请输入5~16位账号（仅支持数字字母）');
        }

    }
});

