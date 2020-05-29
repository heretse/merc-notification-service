/**
 * Created by Administrator on 2018/2/5.
 */
App.controller('devTermGroupCtrl', devTermGroupCtrl);
App.controller('devnoticeDetailCtrl',devnoticeDetailCtrl);
App.controller('devgroupDetailCtrl',devgroupDetailCtrl);
App.controller('devTermGroupAddCtrl', devTermGroupAddCtrl);
App.controller('devTermGroupEditCtrl', devTermGroupEditCtrl);
App.controller('devTermGroupRelatedCtrl', devTermGroupRelatedCtrl);

function devTermGroupCtrl($scope, $rootScope,$localStorage, $uibModal, PageFactory, Global, Client,$timeout) {

    $scope.pageNo = 1;
    $scope.total = 0;
    $scope.filter ={};
    $scope.key = "";
    $scope.list_show = false;
    $scope.pager = PageFactory.page(Global.BASEURL + "general/get",{api:"groupTerminal/query",filter:{}});

    $scope.$watch('$viewContentLoaded', function () {
        Client.json(Global.BASEURL + "general/post", {api:"group/query",data:{filter:{},page:null,size:null,userID:$localStorage.user.id}}).then(function (data) {
            if (data.code == 200) {
                $scope.noticeList = data.data.data;
            } else {
            }
        }, function () {
            $rootScope.alert({
                msg: '操作失败！',
                buttons: [{
                    label: "关闭"
                }]
            });
        });
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

    $scope.toggleList = function () {
        $scope.key = "";
        $scope.list_show = !$scope.list_show;
    };

    $scope.showList = function () {
        $scope.list_show = true;
    };

    $scope.focus = function (item) {
        $scope.key = item.gname;
        $scope.gid = item.id
        $scope.list_show = false;
    };

    $scope.search = function () {
        var data={};
        if($scope.filter.gtname){
            data.gtname = $scope.filter.gtname;
        }
        if($scope.gid && $scope.key != ""){
            data.gId = $scope.gid;
        }
        $scope.pager = PageFactory.page(Global.BASEURL + "general/get",{api:"groupTerminal/query",filter:data});
        $scope.goPage(1);
    };

    $scope.noticedetail = function (node) {
        $uibModal.open({
            templateUrl: 'noticegroups_detail.html',
            controller: 'devnoticeDetailCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'md', //大小配置
            windowClass: 'modal-ssmp',
            resolve: {
                data: function () {
                    return node;
                }
            }
        }).result.then(function (flag) {
            if(flag){
                $scope.goPage();
            }
        });
    };

    $scope.detail = function (node) {
        $uibModal.open({
            templateUrl: 'groupTerminal_detail.html',
            controller: 'devgroupDetailCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'md', //大小配置smmd
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
            templateUrl: 'app/views/dev/terminalgroup/terminalgroup.related.html',
            controller: 'devTermGroupRelatedCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'smmd', //大小配置
            windowClass:'modal-ssmp',
            resolve: {
                data: function () {
                    return node;
                },
                devTermGroupCtrl: function () {
                    return $scope;
                }
            }
        }).result.then(function (flag) {
            if(flag){
                $scope.goPage();
            }
        });
    };

    $scope.edit = function (node) {
        $uibModal.open({
            templateUrl: 'app/views/dev/terminalgroup/terminalgroup.edit.html',
            controller: 'devTermGroupEditCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'sm', //大小配置
            windowClass:'modal-ssmp',
            resolve: {
                data: function () {
                    return node;
                },
                devTermGroupCtrl: function () {
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
            templateUrl: 'app/views/dev/terminalgroup/terminalgroup.add.html',
            controller: 'devTermGroupAddCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'sm', //大小配置
            windowClass: 'modal-ssmp',
            resolve: {
                devTermGroupCtrl: function () {
                    return $scope;
                }
            }
        }).result.then(function (flag) {
            if(flag){
                $scope.goPage(1);
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
                Client.json(Global.BASEURL + "general/post", {api:"groupTerminal/delete",data:{filter:{gtId:node.id},userID:$localStorage.user.id}}).then(function (data) {
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
//终端详情
function devgroupDetailCtrl($scope, $uibModalInstance, PageFactory,Global,$rootScope,$compile, $timeout, $filter, $translate, $http, data) {
    $scope.model = data;
    $scope.pageNo = 1;
    $scope.total = 0;
    $scope.$watch('$viewContentLoaded', function () {
        $scope.gtname=data.gtname;
        $scope.pager = PageFactory.page(Global.BASEURL + "general/get",{api:"groupTerminal/queryTerminalBygtID",filter:{gtId:data.id}});
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
//通报详情
function devnoticeDetailCtrl($scope, $uibModalInstance,PageFactory,Global,$rootScope,$compile, $timeout, $filter, $translate, $http, data) {
    $scope.model = data;

    $scope.$watch('$viewContentLoaded', function () {
        $scope.model.gname =data.gname;
        $scope.pager = PageFactory.page(Global.BASEURL + "general/get",{api:"groupTerminal/queryContactByGID",filter:{gid:data.gid}});
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

function devTermGroupAddCtrl($scope, $uibModalInstance,$rootScope,$localStorage, $translate,$uibModal, PageFactory, Global,devTermGroupCtrl,Client) {
    $scope.model = {};

    $scope.$watch('$viewContentLoaded', function () {
    });

    $scope.exit = function () {
        $uibModalInstance.dismiss();
    };

    $scope.confirm = function () {
        var nd = {};
        if (!$scope.model.gtname) {
            $rootScope.alert({
                msg: '请填写完必填项',
                type: "alert"
            });
            return;
        }
        if ($scope.model.gtname.length<5) {
            $rootScope.alert({
                msg: '终端组名称长度不能小于5位',
                type: "alert"
            });
            return;
        }
        nd.gtname = $scope.model.gtname;
        if($scope.model.remark && $scope.model.remark.length > 64){
            $rootScope.alert({
                msg: '备注信息应小于64位',
                type: "alert"
            });
            return;
        }
        nd.remark = $scope.model.remark;
        Client.json(Global.BASEURL + "general/post", {api:"groupTerminal/add",data:{filter:nd,userID: $localStorage.user.id}}).then(function (data) {
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
                        msg: "添加失败：" + $translate.instant(data.desc),
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
}

function devTermGroupEditCtrl($scope, $uibModalInstance,$rootScope,$localStorage, $translate,$uibModal, PageFactory, Global,data,devTermGroupCtrl,Client) {
    $scope.data = data;
    $scope.model = {};
    $scope.$watch('$viewContentLoaded', function () {
        if(data){
            $scope.model.id = data.id ;
            if(data.gtname){
                $scope.model.gtname =data.gtname;
            }
            if(data.remark){
                $scope.model.remark =data.remark;
            }
        }
    });
    $scope.confirm = function () {
        var nd = {};
        nd.gtId = $scope.model.id;
        if (!$scope.model.gtname) {
            $rootScope.alert({
                msg: '请填写完必填项',
                type: "alert"
            });
            return;
        }
        if ($scope.model.gtname.length<5) {
            $rootScope.alert({
                msg: '终端组名称长度不能小于5位',
                type: "alert"
            });
            return;
        }
        nd.gtname = $scope.model.gtname;
        if($scope.model.remark && $scope.model.remark.length >64){
            $rootScope.alert({
                msg: '备注信息应小于64位',
                type: "alert"
            });
            return;
        }
        nd.remark = $scope.model.remark;
        Client.json(Global.BASEURL + "general/post", {api:"groupTerminal/edit",data:{filter:nd,userID: $localStorage.user.id}}).then(function (data) {
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
                        msg: "修改失败：" + $translate.instant(data.desc),
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

function devTermGroupRelatedCtrl($scope, $uibModalInstance,$rootScope,$localStorage,$timeout,$translate,$uibModal, PageFactory, Global,data,devTermGroupCtrl,Client) {
    $scope.data = data;
    $scope.model = {};
    $scope.unbindmacs = [];
    $scope.$watch('$viewContentLoaded', function () {
        Client.json(Global.BASEURL + "general/post", {api:"groupTerminal/queryRelationMac",data:{filter:{gtId:data.id},userID: $localStorage.user.id}}).then(function (data) {
            if (data.code == 200) {
                $scope.bindmacs = data.data.data[0].bindMacs;
                $scope.unbind = data.data.data[0].unbindMacs;
                for(var i in $scope.unbind){
                    var b = $scope.unbind[i];
                    var c={};
                    c.id = i;
                    c.mac = b;
                    $scope.unbindmacs.push(c) ;
                }
            } else {
                $rootScope.alert({
                    msg: "操作失败!",
                    type: "alert"
                });
            }
        }, function () {
            $rootScope.alert({
                msg: '操作失败！',
                buttons: [{
                    label: "关闭"
                }]
            });
        });

        if( $scope.data){
            $scope.model.gtname =  $scope.data.gtname;
        }
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
        var m = [];
        var m1 = [];
        var str = "";
        if(!$("#select1 option").is(":selected")){
            $rootScope.alert({
                msg: '请选择需要移动的选项',
                type: "alert"
            });
            return
        } else{
          /* $('#select1 option:selected').appendTo('#select2');
            $('#select1 option:selected').remove();*/
            for(var i =0;i<$('#select2 option').length;i++){
                str = $('#select2 option')[i].text;
                m.push(str);
            }
            for(var i=0;i< $('#select1 option:selected').length;i++){
                str = $('#select1 option:selected')[i].text;
                m.push(str);
                m1.push(str);
            }
            $scope.bindmacs = [];
            $timeout(function () {
                $scope.bindmacs = m;
                for(var j =0;j<$scope.unbindmacs.length;j++ ){
                    var jj = $scope.unbindmacs[j];
                    for(var k  =0;k<m1.length;k++ ){
                        var kk = m1[k];
                        if(jj.mac == kk ){
                            delete $scope.unbindmacs[j];
                           // $scope.unbindmacs.splice(j,1);
                        }
                    }
                }
                $scope.unbindmacs = $scope.replaceEmptyItem($scope.unbindmacs);
            },100);
        }
    };
    $scope.remove = function(){
        var m = [];
        var str = "";
        if(!$("#select2 option").is(":selected")){
            $rootScope.alert({
                msg: '请选择需要移动的选项',
                type: "alert"
            });
            return;
        }else{
            for(var i=0;i< $('#select2 option:selected').length;i++){
                str = $('#select2 option:selected')[i].text;
                m.push(str);
            }
           /* console.log($('#select2 option:selected')[0].text);
            var b = $('#select2 option:selected')[0].text;*/
            $('#select2 option:selected').remove();
            for(var j =0;j<$scope.bindmacs.length;j++ ){
                var jj = $scope.bindmacs[j];
                for(var k  =0;k<m.length;k++ ){
                    var kk = m[k];
                    if(jj == kk ){
                        delete $scope.bindmacs[j];
                        // $scope.unbindmacs.splice(j,1);
                    }
                }
            }
            $scope.bindmacs = $scope.replaceEmptyItem($scope.bindmacs);
            for(var i=0;i< m.length;i++){
                var c={};
                c.id = $('#select1 option').length+1;
                c.mac = m[i];
                $scope.unbindmacs.push(c);
            }
        }
    }
    $scope.addAll = function() {
        var m = [];
        var m1 = [];
        var str = "";
        for(var i =0;i<$('#select2 option').length;i++){
            str = $('#select2 option')[i].text;
            m.push(str);
        }
        $('#select2 option').remove();
        for(var i =0;i<$('#select1 option').length;i++){
            str = $('#select1 option')[i].text;
            m.push(str);
            m1.push(str);
        };
        $('#select1 option').remove();
        $scope.bindmacs = [];
        $timeout(function () {
            $scope.bindmacs = m;
            for(var i =0;i<$scope.unbindmacs.length;i++){
                var ii = $scope.unbindmacs[i].mac;
                for(var j in m1){
                    var jj = m1[j];
                    if(ii == jj ){
                        delete $scope.unbindmacs[i];
                    }
                }
            }
            $scope.unbindmacs = $scope.replaceEmptyItem($scope.unbindmacs);
        },100);
    };
    $scope.removeAll = function() {
        var m = [];
       // $('#select2 option').remove().appendTo('#select1');
       /* for(var i =0;i<$('#select1 option').length;i++){
            var c = {};
            c.id = i;
            c.mac = $('#select1 option')[i].text;
            m.push(c);
        }*/
        $('#select1 option').remove();
        for(var i =0;i<$('#select2 option').length;i++){
            var c = {};
            c.id = i;
            c.mac = $('#select2 option')[i].text;
            m.push(c);
            $scope.unbindmacs.push(c);
        }
        $('#select2 option').remove();
        $scope.bindmacs = [];
    }
    $scope.confirm = function () {
        var nd = {};
        /*if (!$scope.model.gtname) {
            $rootScope.alert({
                msg: '请填写完必填项',
                type: "alert"
            });
            return;
        }*/
        nd.gtId = $scope.data.id;
        var mac = [];
        var str = "";
        var select = document.getElementById("select2");
        for(var i=0;i<select.length;i++){
            if(select.options[i]){
                str = select[i].text;
            }
           /* if(select.options[i].selected){
                str = select[i].text;
            }*/
            mac.push(str);
        }

        nd.macs = mac;

        Client.json(Global.BASEURL + "general/post", {api:"groupTerminal/bind",data:{filter:nd,userID: $localStorage.user.id}}).then(function (data) {
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
