/**
 * Created by Administrator on 2018/2/5.
 */
App.controller('infPersonCtrl', infPersonCtrl);
App.controller('infPersonAddCtrl', infPersonAddCtrl);
App.controller('infPersonEditCtrl', infPersonEditCtrl);

function infPersonCtrl($scope, $rootScope,$localStorage, $uibModal, PageFactory, Global, Client,$timeout,$compile) {

    $scope.pageNo = 1;
    $scope.total = 0;
    $scope.filter ={};
    $scope.keyWd = "";
    $scope.list_show = false;
    // 初始化查询
    $scope.pager = PageFactory.page(Global.BASEURL + "general/get", {api:"contact/query",filter:{}});
    // Loaded
    $scope.toggleList = function () {
        $scope.keyWd = "";
        $scope.list_show = !$scope.list_show;
    };

    $scope.showList = function () {
        $scope.list_show = true;
    };

    $scope.focus = function (item) {
        $scope.keyWd = item.methodName;
        $scope.mid = item.mid;
        $scope.list_show = false;
    };
    $scope.$watch('$viewContentLoaded', function () {
        Client.json(Global.BASEURL + "general/post", {api:"contact/queryMethods",data:{filter:{},userID:$localStorage.user.id}}).then(function (data) {
            if (data.code == 200) {
                $scope.methodList = data.data.data;
            }else {
                $rootScope.alert({
                    msg: '操作失败！',
                    type: "alert"
                });
            }
        }, function () {
            $rootScope.alert({
                msg: '操作失败！',
                type: "alert"
            });
        });
        $scope.goPage(1);

    });

    $scope.doEnter = function ($event) {
        if ($event.keyCode == 13) {// 回车
            $scope.search();
        }
    };

    $scope.goPage = function (no) {
        var loading = $rootScope.alert({
            msg: '<span class="icon-spinner icon-spin"></span>加载中......',
            // type:'alert'
            hideBtn: true
        });
        $scope.pager.query(no).then(function (data){
            $scope.nodes = data.data.data;
            $scope.total = data.data.count;
            $scope.page = no;
            loading.close();
        },function (err) {
            $timeout(function () {
                loading.close();
            },100);
            $rootScope.alert({
                msg: '操作失败！',
                type: 'alert'
            });
        });
    };

    $scope.search = function () {
        var nd = {};
        if($scope.filter.name){
            if(!/^[a-zA-Z0-9\u4e00-\u9fa5\·]{1,10}$/.test($scope.filter.name)){
                $rootScope.alert({
                    msg: '不能用特殊字符',
                    type: "alert"
                });
                return;
            }
            nd.cname = $scope.filter.name;
        }
        if($scope.mid && $scope.keyWd != ""){
            nd.mid = $scope.mid;
        }
        $scope.pager = PageFactory.page(Global.BASEURL + "general/get", {api:"contact/query",filter:nd});
        $scope.goPage(1);
    };

    $scope.add = function () {
        $uibModal.open({
            templateUrl: 'app/views/inf/person/person.add.html',
            controller: 'infPersonAddCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'sm', // 大小配置
            windowClass:'modal-ssmp',
            resolve: {
                infPersonCtrl: function () {
                    return $scope;
                }
            }
        }).result.then(function (flag) {
            if(flag){
                $scope.goPage(1);
            }
        });
    };

    $scope.edit = function (data) {
        $uibModal.open({
            templateUrl: 'app/views/inf/person/person.edit.html',
            controller: 'infPersonEditCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'sm', // 大小配置
            windowClass:'modal-ssmp',
            resolve: {
                data: function () {
                    return data;
                },
                infPersonCtrl: function () {
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
                Client.json(Global.BASEURL + "general/post", {api:"contact/delete",data:{filter:{cid:node.id},userID:$localStorage.user.id}}).then(function (data) {
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
}

function infPersonAddCtrl($scope, $uibModalInstance,$rootScope,$localStorage, $translate,$uibModal, PageFactory, Global,infPersonCtrl,Client,$compile) {
    $scope.model = {};

    $scope.$watch('$viewContentLoaded', function () {
        Client.json(Global.BASEURL + "general/post", {api:"contact/queryMethods",data:{filter:{},userID:$localStorage.user.id}}).then(function (data) {
            if (data.code == 200) {
                $scope.methodList = data.data.data;
            }
        }, function () {
            $rootScope.alert({
                msg: '操作失败！',
                buttons: [{
                    label: "关闭"
                }]
            });
        });
        $scope.model.methodName = "1";
    });

    $scope.exit = function () {
        $uibModalInstance.dismiss();
    };

    $scope.confirm = function () {
        var nd = {};
        var cm = [];
        if (!$scope.model.name) {
            $rootScope.alert({
                msg: '请填写完必填项',
                type: "alert"
            });
            return;
        }
        if ($scope.model.name.length<2)  {
            $rootScope.alert({
                msg: '通报对象名称长度不能小于2位',
                type: "alert"
            });
            return;
        }
        if($scope.model.remark && $scope.model.remark.length > 32){
            $rootScope.alert({
                msg: '备注信息应小于32位',
                type: "alert"
            });
            return;
        }
        if($("input[type='radio'][name='radio']:checked").length == 0 ){
            $rootScope.alert({
                msg: '没有选中通报方式',
                type: "alert"
            });
            return;
        }
        for(var i = 0;i<$("#tab div.pt").length ;i++){
            var ii = $("#tab div.pt")[i];
            var mid;
            for(var j in $scope.methodList){
                var jj = $scope.methodList[j];
                if($("#type" + i).text() == jj.methodName){
                        mid=jj.mid;
                }
            }
            var cmethods = {};
            cmethods.mid = parseInt(mid);
            cmethods.caccount = $("#name" + i).val();
            cmethods.isEnabled = $("#radio" + i).is(":checked") == true ? 1 : 0;
            if(cmethods.caccount == ""&&cmethods.isEnabled){
                $rootScope.alert({
                    msg: '通报方式未填写',
                    type: "alert"
                });
                return;
            }
            if (cmethods.mid == 1 && cmethods.caccount != "") {
                if (!/^(([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5}){1,25})$/.test(cmethods.caccount)) {
                    $rootScope.alert({
                        msg: '邮件格式错误',
                        type: "alert"
                    });
                    return;
                }

            }
           /* else if (cmethods.mid == 2 && cmethods.caccount != "") {
                if (cmethods.caccount.length<6||cmethods.caccount.length>20) {
                    $rootScope.alert({
                        msg: '微信长度应为6-20',
                        type: "alert"
                    });
                    return;
                }
                if (!/^[a-zA-Z][a-zA-Z0-9_-]{5,19}$/.test(cmethods.caccount)) {
                    $rootScope.alert({
                        msg: '微信格式错误',
                        type: "alert"
                    });
                    return;
                }
            }*/
            if(cmethods.caccount != ""){
                if (cmethods.caccount.length>64) {
                    $rootScope.alert({
                        msg: '通报方式账号过长',
                        type: "alert"
                    });
                    return;
                }
                cm.push(cmethods);
            }
        }

        nd.cmethods = cm;
        nd.cname = $scope.model.name;
        nd.remark = $scope.model.remark;
        Client.json(Global.BASEURL + "general/post", {api:"contact/add",data:{filter:nd,userID:$localStorage.user.id}}).then(function (data) {
            if (data.code == 200) {
                $rootScope.alert({
                    msg: "添加成功!",
                    type: "alert"
                }, function () {
                    $uibModalInstance.close(true);
                });
            }
            else if(data.code == 1004){
                $rootScope.alert({
                    msg: "添加失败：" + "通报方式重复",
                    type: "alert"
                });
            } else {
                $rootScope.alert({
                    msg: "添加失败：" + $translate.instant("common.errorCode." + data.code),
                    type: "alert"
                });
            }
        }, function (err) {
            $rootScope.alert({
                msg: "操作失败!",
                type: "alert"
            });
        });
    };
}

function infPersonEditCtrl($scope, $uibModalInstance,$rootScope,$localStorage, $timeout,$translate,$uibModal, PageFactory, Global,data,infPersonCtrl,Client,$compile) {
    $scope.data = data;
    $scope.model = {};
    $scope.cmethodList ={};
    $scope.$watch('$viewContentLoaded', function () {
        Client.json(Global.BASEURL + "general/post", {api:"contact/queryMethods",data:{filter:{},userID:$localStorage.user.id}}).then(function (data) {
            if (data.code == 200) {
                $scope.mlist = data.data.data;
            }
        }, function () {
            $rootScope.alert({
                msg: '操作失败！',
                buttons: [{
                    label: "关闭"
                }]
            });
        });
        $timeout(function(){
            if(data){
                $scope.model.id =data.id;
                if(data.contactName){
                    $scope.model.name =data.contactName;
                }
                if(data.remark){
                    $scope.model.remark =data.remark;
                }
                if(data.cmethods){
                    $scope.cmethodList = data.cmethods;
                    for(var i in $scope.cmethodList){
                        var c1 =  $scope.cmethodList[i];
                        for(var j in $scope.mlist){
                            var c2 = $scope.mlist[j];
                            if(c1.mid == c2.mid){
                                c2.check = c1.isEnabled;
                                c2.typename = c1.caccount;
                            }
                        }
                    }
                }
            }
        },500);

    });

    $scope.exit = function () {
        $uibModalInstance.dismiss();
    };

    $scope.confirm = function () {
        var nd = {};
        if (!$scope.model.name) {
            $rootScope.alert({
                msg: '请填写完必填项',
                type: "alert"
            });
            return;
        }
        if ($scope.model.name.length<2)  {
            $rootScope.alert({
                msg: '通报对象名称长度不能小于2位',
                type: "alert"
            });
            return;
        }
        if($scope.model.remark && $scope.model.remark.length > 32){
            $rootScope.alert({
                msg: '备注信息应小于32位',
                type: "alert"
            });
            return;
        }
        if($("input[type='radio'][name='radio']:checked").length == 0 ){
            $rootScope.alert({
                msg: '没有选中通报方式',
                type: "alert"
            });
            return;
        }
        var cms = [];
        for(var j = 0;j<$("#first div.pt").length ;j++){
            var cc = {};
            var mid;
            for(var k in $scope.mlist){
                var kk = $scope.mlist[k];
                if($("#type" + j).text() == kk.methodName){
                    mid=kk.mid;
                }
            }
            cc.mid = parseInt(mid);
            cc.caccount = $("#name" + j).val();
            cc.isEnabled = $("#radio" + j).is(":checked") == true ? 1 : 0;
            if(cc.caccount == ""&&cc.isEnabled){
                $rootScope.alert({
                    msg: '通报方式未填写',
                    type: "alert"
                });
                return;
            }
            if (cc.mid == 1 &&cc.caccount != "") {
                if (!/^(([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5}){1,25})$/.test(cc.caccount)) {
                    $rootScope.alert({
                        msg: '邮件格式错误',
                        type: "alert"
                    });
                    return;
                }

            }
           /* else if (cc.mid == 2 && cc.caccount != "") {
                if (cc.caccount.length<6||cc.caccount.length>20) {
                    $rootScope.alert({
                        msg: '微信长度应为6-20',
                        type: "alert"
                    });
                    return;
                }
                if (!/^[a-zA-Z][a-zA-Z0-9_-]{5,19}$/.test(cc.caccount)) {
                    $rootScope.alert({
                        msg: '微信格式错误',
                        type: "alert"
                    });
                    return;
                }
            }*/
            if (cc.caccount != "") {
                if (cc.caccount.length>64) {
                    $rootScope.alert({
                        msg: '通报方式账号过长',
                        type: "alert"
                    });
                    return;
                }
                cms.push(cc);
            }
        }

        nd.cid =  $scope.model.id;
        nd.cname =  $scope.model.name;
        nd.cmethods = cms;
        nd.remark = $scope.model.remark;
        Client.json(Global.BASEURL + "general/post", {api:"contact/edit",data:{filter:nd,userID:$localStorage.user.id}}).then(function (data) {
            if (data.code == 200) {
                $rootScope.alert({
                    msg: "修改成功!",
                    type: "alert"
                }, function () {
                    $uibModalInstance.close(true);
                });
            } else if(data.code == 1004){
                    $rootScope.alert({
                        msg: "修改失败：" +"通报方式重复",
                        type: "alert"
                    });
                } else {
                    $rootScope.alert({
                        msg: "修改失败：" + $translate.instant("common.errorCode." + data.code),
                        type: "alert"
                    });
                }
        }, function (err) {
            $rootScope.alert({
                msg: "操作失败!",
                type: "alert"
            });
        });
    };
}
