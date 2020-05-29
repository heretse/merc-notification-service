/**
 * Created by Administrator on 2018/2/5.
 */
App.controller('infGroupCtrl', infGroupCtrl);
App.controller('infNoticeDetailCtrl', infNoticeDetailCtrl);
App.controller('infGroupDetailCtrl', infGroupDetailCtrl);
App.controller('infGroupAddCtrl', infGroupAddCtrl);
App.controller('infGroupEditCtrl', infGroupEditCtrl);
App.controller('infGroupRelatedCtrl', infGroupRelatedCtrl);

function infGroupCtrl($scope, $rootScope, $localStorage,$uibModal, PageFactory, Global, Client,$timeout) {

    $scope.pageNo = 1;
    $scope.total = 0;
    $scope.filter ={};
    $scope.pager = PageFactory.page(Global.BASEURL + "general/get", {api:"group/query",filter:{}});

    $scope.doEnter = function ($event) {
        if ($event.keyCode == 13) {//回车
            $scope.search();
        }
    };
    // Loaded
    $scope.$watch('$viewContentLoaded', function () {
        $scope.goPage(1);

    });

    $scope.goPage = function (no) {
        var loading = $rootScope.alert({
            msg: '<span class="icon-spinner icon-spin"></span>加载中......',
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
        if($scope.filter.gname){
            $scope.name =$scope.filter.gname;
        }
        $scope.pager = PageFactory.page(Global.BASEURL + "general/get", {api:"group/query",filter:{gname:$scope.name}});
        $scope.goPage(1);
    };


    $scope.noticedetail = function (node) {
        $uibModal.open({
            templateUrl: 'inf_noticegroups_detail.html',
            controller: 'infNoticeDetailCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'md', //大小配置
            windowClass: 'modal-ssmp',
            resolve: {
                data: function () {
                    return node;
                }
            }
        }).result.then(function () {
            $scope.goPage();
        });
    };

    $scope.detail = function (node) {
        $uibModal.open({
            templateUrl: 'inf_groupTerminal_detail.html',
            controller: 'infGroupDetailCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'md', //大小配置
            windowClass: 'modal-ssmp',
            resolve: {
                data: function () {
                    return node;
                }
            }
        }).result.then(function () {
            $scope.goPage();
        });
    };

    $scope.related = function (node) {
        $uibModal.open({
            templateUrl: 'app/views/inf/group/group.related.html',
            controller: 'infGroupRelatedCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'smmd', //大小配置
            windowClass:'modal-ssmp',
            resolve: {
                data: function () {
                    return node;
                },
                infGroupCtrl: function () {
                    return $scope;
                }
            }
        }).result.then(function (flag) {
            if(flag){
                $scope.goPage();
            }
        });
    };

    $scope.add = function () {
        $uibModal.open({
            templateUrl: 'app/views/inf/group/group.add.html',
            controller: 'infGroupAddCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'sm', //大小配置
            windowClass:'modal-ssmp',
            resolve: {
                infGroupCtrl: function () {
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
            templateUrl: 'app/views/inf/group/group.edit.html',
            controller: 'infGroupEditCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'sm', //大小配置
            windowClass:'modal-ssmp',
            resolve: {
                data: function () {
                    return data;
                },
                infGroupCtrl: function () {
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
                Client.json(Global.BASEURL + "general/post",{api:"group/delete",data:{filter:{gid: node.id},userID:$localStorage.user.id}}).then(function (data) {
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
//通报详情
function infNoticeDetailCtrl($scope, $uibModalInstance,PageFactory,Global,$rootScope,$compile, $timeout, $filter, $translate, $http, data) {
    $scope.model = data;

    $scope.$watch('$viewContentLoaded', function () {
        $scope.model.gname = data.gname;
        $scope.pager = PageFactory.page(Global.BASEURL + "general/get",{api:"groupTerminal/queryContactByGID",filter:{gid:data.id}});
        $scope.goPage(1);
    });

    $scope.goPage = function (no) {
        var loading = $rootScope.alert({
            msg: '<span class="icon-spinner icon-spin"></span>加载中......',
            hideBtn: true
        });
        $scope.pager.query(no).then(function (data){
            $scope.nodes =data.data.data;
            $scope.total = data.data.count;
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

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    }
};
//终端组详情
function infGroupDetailCtrl($scope, $uibModalInstance,PageFactory,Global,$rootScope, $compile, $timeout, $filter, $translate, $http, data) {
    $scope.model = data;

    $scope.$watch('$viewContentLoaded', function () {
        $scope.model.gname = data.gname;
        $scope.pager = PageFactory.page(Global.BASEURL + "general/get",{api:"group/queryGTByID",filter:{gid:data.id}});
        $scope.goPage(1);
    });

    $scope.goPage = function (no) {
        var loading = $rootScope.alert({
            msg: '<span class="icon-spinner icon-spin"></span>加载中......',
            hideBtn: true
        });
        $scope.pager.query(no).then(function (data){
            $scope.nodes =data.data.data;
            $scope.total = data.data.count;
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

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    }

};

function infGroupAddCtrl($scope, $uibModalInstance,$rootScope, $localStorage,$translate,$uibModal, PageFactory, Global,infGroupCtrl,Client) {
    $scope.model = {};
    $scope.$watch('$viewContentLoaded', function () {
        $scope.model.delaytime ="0" ;
    });
    $scope.confirm = function () {
        var nd = {};
        if (!$scope.model.name)  {
            $rootScope.alert({
                msg: '请填写完必填项',
                type: "alert"
            });
            return;
        }
        if ($scope.model.name.length<5)  {
            $rootScope.alert({
                msg: '通报组名称长度不能小于5位',
                type: "alert"
            });
            return;
        }
        nd.gname = $scope.model.name;
        nd.delaytime =parseInt($scope.model.delaytime);

        Client.json(Global.BASEURL + "general/post", {api:"group/add",data:{filter:nd,userID:$localStorage.user.id}}).then(function (data) {
            if (data.code == 200) {
                $rootScope.alert({
                    msg: "添加成功!",
                    type: "alert"
                }, function () {
                    $uibModalInstance.close(true);
                });
            } else {
                if(data.code == 1004){
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
            $rootScope.alert({
                msg: "操作失败!",
                type: "alert"
            });
        });
    };

    $scope.exit = function () {
        $uibModalInstance.dismiss();
    };

}

function infGroupEditCtrl($scope, $uibModalInstance,$rootScope, $localStorage,$translate,$uibModal, PageFactory, Global,data,infGroupCtrl,Client) {
    $scope.data = data;
    $scope.model = {};
    $scope.$watch('$viewContentLoaded', function () {
        if(data) {
            $scope.model.id = data.id;
            if (data.gname) {
                $scope.model.name = data.gname;
            }
           /* if (data.delayTime) {
            $scope.model.delaytime =  data.delayTime.toString();
            }*/
            if(data.remark){
                $scope.model.remark = data.remark;
            }
            $scope.model.delaytime =  data.delayTime.toString();
        }
    });
    $scope.confirm = function () {
        var nd = {};
        if (!$scope.model.name)  {
            $rootScope.alert({
                msg: '请填写完必填项',
                type: "alert"
            });
            return;
        }
        if ($scope.model.name.length<5)  {
            $rootScope.alert({
                msg: '通报组名称长度不能小于5位',
                type: "alert"
            });
            return;
        }
        nd.gname = $scope.model.name;
        nd.delaytime =parseInt($scope.model.delaytime);
        nd.gid = $scope.model.id;

        Client.json(Global.BASEURL + "general/post", {api:"group/edit",data:{filter:nd,userID:$localStorage.user.id}}).then(function (data) {
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
            $rootScope.alert({
                msg: "操作失败!",
                type: "alert"
            });
        });

    }

    $scope.exit = function () {
        $uibModalInstance.dismiss();
    };

}

function infGroupRelatedCtrl($scope, $uibModalInstance,$rootScope, $localStorage,$translate,$uibModal, PageFactory, Global,data,infGroupCtrl,Client,$timeout) {
    $scope.gid = data.id;
    $scope.model = {};
    $scope.contacts = [];
    $scope.groupterminals = [];
    $scope.$watch('$viewContentLoaded', function () {
        if(data){
            $scope.model.gname=data.gname;
            $scope.model.delaytime = data.delayTime;
        }
        Client.json(Global.BASEURL + "general/post", {api:"group/queryBindDatas",data:{filter:{gid:data.id,userID:$localStorage.user.id}}}).then(function (data) {
            if (data.code == 200) {
                $scope.unbindGroupTerminals = data.data.data[0].unbindGroupTerminals;
                $scope.bindGroupTerminals = data.data.data[0].bindGroupTerminals;
                $scope.unbindContacts = data.data.data[0].unbindContacts;
                $scope.bindContacts = data.data.data[0].bindContacts;
            } else {
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
    });
    $scope.replaceEmptyItem = function(array){
        for(var i = 0 ;i<array.length;i++) {
            if(array[i] == "" || typeof(array[i]) == "undefined")
            {
                array.splice(i,1);
                i= i-1;
            }
        }
        return array;
    }
    $scope.add = function (){
        var s =[];
        var s1=[];
        var str ='';
        if(!$("#select1 option").is(":selected")){
            $rootScope.alert({
                msg: '请选择需要移动的选项',
                type: "alert"
            });
            return
        } else{
            //$('#select1 option:selected').appendTo('#select2');
           /* $('#select1 option:selected').clone().appendTo('#select2');
            $('#select1 option:selected').remove();*/
            for(var i =0;i<$('#select2 option').length;i++){
                var gtname = $('#select2 option')[i].text;
                var gtid = $('#select2 option')[i].value;
                var c = {};
                c.gtname = gtname;
                c.gtId = gtid;
                s.push(c);
            }
            for(i=0;i< $('#select1 option:selected').length;i++){
                var gtname = $('#select1 option:selected')[i].text;
                var gtid = $('#select1 option:selected')[i].value;
                var c = {};
                c.gtname = gtname;
                c.gtId = gtid;
                s.push(c);
                s1.push(c);
            }
            $scope.bindGroupTerminals = s;
            for(var j in $scope.unbindGroupTerminals ){
                var jj = $scope.unbindGroupTerminals[j].gtname;
                for(var k in s1){
                    var kk = s1[k].gtname;
                    if(jj == kk ){
                        //$scope.unbindGroupTerminals.splice(j,1);
                        delete $scope.unbindGroupTerminals[j];
                    }
                }
            }
            $scope.unbindGroupTerminals = $scope.replaceEmptyItem($scope.unbindGroupTerminals);
        }

    };
    $scope.remove = function(){
        var s =[];
        var str ='';
        var m = [];
        //$('#select2 option:selected').appendTo('#select1');
        if(!$("#select2 option").is(":selected")){
            $rootScope.alert({
                msg: '请选择需要移动的选项',
                type: "alert"
            });
            return;
        }else {
            for(var i=0;i< $('#select2 option:selected').length;i++){
                var gtname = $('#select2 option:selected')[i].text;
                var gtid = $('#select2 option:selected')[i].value;
                var c = {};
                c.gtname = gtname;
                c.gtId = gtid;
                $scope.unbindGroupTerminals.push(c);
            }
            $('#select2 option:selected').remove();
            for(var i =0;i<$scope.bindGroupTerminals.length;i++){
                var ii = $scope.bindGroupTerminals[i].gtId;
                for(var j in m){
                    var jj = m[j].gtId;
                    if(ii == jj ){
                        delete $scope.bindGroupTerminals[i];
                    }

                }
            }
            $scope.bindGroupTerminals = $scope.replaceEmptyItem($scope.bindGroupTerminals);
        }
    }
    $scope.addAll = function() {
        //$('#select1 option').appendTo('#select2');
        var m1 = [];
        var m = [];
        for(var i =0;i<$('#select2 option').length;i++){
            var c ={};
            c.gtname = $('#select2 option')[i].text;
            c.gtId = $('#select2 option')[i].value;
            m.push(c);
        }
        $('#select2 option').remove();
        for(var i =0;i<$('#select1 option').length;i++){
            var d ={};
            d.gtname = $('#select1 option')[i].text;
            d.gtId = $('#select1 option')[i].value;
            m.push(d);
            m1.push(d);
        }
        $('#select1 option').remove();
        $scope.bindGroupTerminals = [];
        $timeout(function () {
            $scope.bindGroupTerminals = m;
            for(var i =0;i<$scope.unbindGroupTerminals.length;i++){
                var ii = $scope.unbindGroupTerminals[i].gtId;
                for(var j in m1){
                    var jj = m1[j].gtId;
                    if(ii == jj ){
                        delete $scope.unbindGroupTerminals[i];
                    }

                }
            }
            $scope.unbindGroupTerminals = $scope.replaceEmptyItem($scope.unbindGroupTerminals);
        },100);
    };
    $scope.removeAll = function() {
        //$('#select2 option').appendTo('#select1');
        var m = [];
       /* for(var i =0;i<$('#select1 option').length;i++){
            var c ={};
            c.gtname = $('#select1 option')[i].text;
            c.gtId = $('#select1 option')[i].value;
            m.push(c);
        }*/
        $('#select1 option').remove();
        for(var i =0;i<$('#select2 option').length;i++){
            var c ={};
            c.gtname = $('#select2 option')[i].text;
            c.gtId = $('#select2 option')[i].value;
            m.push(c);
            $scope.unbindGroupTerminals.push(c);
        }
        $('#select2 option').remove();
        $scope.bindGroupTerminals = [];
    }

    $scope.add1 = function () {
        if(!$("#select3 option").is(":selected")){
            $rootScope.alert({
                msg: '请选择需要移动的选项',
                type: "alert"
            });
            return
        } else {
            $('#select3 option:selected').appendTo('#select4');
        }
    }
    $scope.remove1 = function(){
        if(!$("#select4 option").is(":selected")){
            $rootScope.alert({
                msg: '请选择需要移动的选项',
                type: "alert"
            });
            return;
        }else{
            $('#select4 option:selected').appendTo('#select3');
        }
    }

    $scope.addAll1 = function() {
        $('#select3 option').appendTo('#select4');
    };
    $scope.removeAll1 = function() {
        $('#select4 option').appendTo('#select3');
    }

    $scope.confirm = function () {
        var nd = {};
        nd.gid = $scope.gid;
        var str = "";
        var select = document.getElementById("select2");
        for(var i=0;i<select.length;i++){
            /*if(select.options[i].selected){
                $scope.groupterminals.push(select[i].value);
            }*/
            if(select.options[i]){
                $scope.groupterminals.push(select[i].value);
            }
        }

        var select2 = document.getElementById("select4");
        for(var i=0;i<select2.length;i++){
            /*if(select2.options[i].selected){
                $scope.contacts.push(select2[i].value);
            }*/
            if(select2.options[i]){
                $scope.contacts.push(select2[i].value);
            }
        }
        /*if ($scope.contacts.length == 0 || $scope.groupterminals.length == 0) {
            $rootScope.alert({
                msg: '请填写完必填项',
                type: "alert"
            });
            return;
        }*/

        nd.cids = $scope.contacts;
        nd.gtids = $scope.groupterminals;

        Client.json(Global.BASEURL + "general/post", {api:"group/bindData",data:{filter:nd,userID:$localStorage.user.id}}).then(function (data) {
            if (data.code == 200) {
                $rootScope.alert({
                    msg: "操作成功!",
                    type: "alert"
                }, function () {
                    $uibModalInstance.close(true);
                });
            } else {
                $rootScope.alert({
                    msg: "操作失败：" + $translate.instant("common.errorCode." + data.code),
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

    $scope.exit = function () {
        $uibModalInstance.dismiss();
    };

}
