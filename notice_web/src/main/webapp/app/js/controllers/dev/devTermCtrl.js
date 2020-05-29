/**
 * Created by Administrator on 2018/2/5.
 */
App.controller('devTermCtrl', devTermCtrl);
App.controller('devTermAddCtrl', devTermAddCtrl);
App.controller('devDetailCtrl',devDetailCtrl);

function devTermCtrl($scope, $rootScope, $localStorage,$uibModal, PageFactory, Global, Client,$timeout,$compile) {

    $scope.pageNo = 1;
    $scope.total = 0;
    $scope.filter ={};
    $scope.pager = PageFactory.page(Global.BASEURL + "general/get",{api:"terminal/query",filter:{}});

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
        var data = {};
        if($scope.filter.mac)
        {
            data.mac =  $scope.filter.mac ;
        }
        if($scope.filter.to !=null&&$scope.filter.from!=null){
            var interv = $scope.filter.to.valueOf() - $scope.filter.from.valueOf();
            if(interv <= 0){
                $rootScope.alert({
                    msg: "请正确选择起止时间",
                    type: "alert"
                });
                return;
            }
            /*if (interv == 0 ) {
                $rootScope.alert({
                    msg: "请正确选择起止时间",
                    type: "alert"
                });
                return;
            }*/
        }
        if($scope.filter.to ==null&&$scope.filter.from!=null){
            $rootScope.alert({
                msg: "起止时间不能为空",
                type: "alert"
            });
            return;
        }
        if($scope.filter.to !=null&&$scope.filter.from==null){
            $rootScope.alert({
                msg: "起止时间不能为空",
                type: "alert"
            });
            return;
        }

        if($scope.filter.from)
        {
            data.start = Date.parse($scope.filter.from);
        }
        if($scope.filter.to)
        {
            data.end = Date.parse($scope.filter.to);
        }

        $scope.pager = PageFactory.page(Global.BASEURL + "general/get",{api:"terminal/query",filter:data});
        $scope.goPage(1);
    };

    /*
     * Excel批量导入
     */
    var wb;
    $scope.imp = function () {
        var json;
        var data ="";
        var h = document.getElementById("H1");
      /*  console.log(h);
        console.log($('input[type="file"]')[0].files[0]);
        console.log(h.files[0]);*/
        if(angular.isUndefined(h.files[0]))
        {
            $rootScope.alert({
                msg: '请参照模板文件，录入相关数据！',
                type: "alert",
            });
            return;
        }
        var suffix =h.files[0].name.split(".")[1];
        if(suffix != 'xls' && suffix !='xlsx'){
            $rootScope.alert({
                msg: '导入的文件格式不正确!',
                type: "alert",
            });
            return;
        }
        var f = h.files[0];
        var reader = new FileReader();
        reader.readAsBinaryString(f);
        reader.onload = function (e) {
            var datas = e.target.result;
            wb = XLSX.read(datas, {
                type: 'binary'
            });
            //console.log(JSON.stringify(XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]])));
            json = JSON.stringify(XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]]));
        }
       /* if(angular.isUndefined($scope.excel) || $scope.excel==null || $scope.excel=="")
        {
            $rootScope.alert({
                msg: '请参照模板文件，录入相关数据！',
                type: "alert",
            });
            return;
        }
        var json = angular.fromJson(angular.toJson($scope.excel).replace(".xlsx",""));
        var data = eval(angular.toJson(json.Sheet1));*/
        $timeout(function () {
            data = eval(json);
            if(data !=""){
                if(!data[0].mac){
                    $rootScope.alert({
                        msg: '数据错误！',
                        buttons: [{
                            label: "关闭"
                        }]
                    });
                    return;
                    }
            }else{
                $rootScope.alert({
                    msg: '导入数据不能为空！',
                    buttons: [{
                        label: "关闭"
                    }]
                });
                return;
            }
            var loading = $rootScope.alert({
                msg: '请稍后...',
                hideBtn: true
            });
            Client.json(Global.BASEURL + "general/post", {api:"terminal/addBatch",data:{filter:{terminals:data},userID:$localStorage.user.id}}).then(function (data, status, header, config) {
                loading.close();
                if (data.code == 200) {
                    if(data.data.data.length>0){
                        //console.log(data.data.data);
                        $uibModal.open({
                            templateUrl: 'terminal_detail.html',
                            controller: 'devDetailCtrl',
                            showClose: true,
                            backdrop: 'static',
                            size: 'sm', //大小配置smmd
                            windowClass: 'modal-ssmp',
                            resolve: {
                                data: function () {
                                    return data.data.data;
                                }
                            }
                        }).result.then(function (flag) {
                            if(flag){
                                $scope.goPage(1);
                            }
                        });
                    }else{
                        $rootScope.alert({
                            msg: '操作成功！',
                            type: "alert",
                        });
                        $scope.goPage(1);
                    }
                    var h = document.getElementById("H1");
                    h.value = "";
                }else{
                    $rootScope.alert({
                        msg: '操作失败！',
                        buttons: [{
                            label: "关闭"
                        }]
                    });
                    var h = document.getElementById("H1");
                    h.value = "";
                }
                //$scope.goPage(1);
            }, function (err, status) {
                loading.close();
                $rootScope.alert({
                    msg: '操作失败！',
                    buttons: [{
                        label: "关闭"
                    }]
                });
                var h = document.getElementById("H1");
                h.value = "";
            });
        },100);
    };

    $scope.add = function () {
        $uibModal.open({
            templateUrl: 'app/views/dev/terminal/terminal.add.html',
            controller: 'devTermAddCtrl',
            showClose: true,
            backdrop: 'static',
            size: 'sm', //大小配置
            windowClass:'modal-ssmp',
            resolve: {
                devTermCtrl: function () {
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
                Client.json(Global.BASEURL + "general/post", {api:"terminal/delete",data:{filter:{id: node.id},userID:$localStorage.user.id}}).then(function (data) {
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

function devTermAddCtrl($scope, $uibModalInstance,$rootScope,$localStorage, $translate,$uibModal, PageFactory, Global,devTermCtrl,Client) {

    $scope.model = {};
    $scope.$watch('$viewContentLoaded', function () {

    });

    $scope.exit = function () {
        $uibModalInstance.dismiss();
    };

    $scope.confirm = function () {
        var nd = {};
        if (!$scope.model.mac) {
            $rootScope.alert({
                msg: '请填写完必填项',
                type: "alert"
            });
            return;
        }
        nd.mac = $scope.model.mac;
        if ($scope.model.mac.length<16) {
            $rootScope.alert({
                msg: 'MAC长度不能小于16位',
                type: "alert"
            });
            return;
        }
        if (!(/^[0-9a-fA-F]{16}$/.test($scope.model.mac))) {
            $rootScope.alert({
                msg: 'MAC格式错误',
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
        if($scope.model.remark){
            nd.remark = $scope.model.remark;
        }
        Client.json(Global.BASEURL + "general/post", {api:"terminal/add",data:{filter:nd,userID:$localStorage.user.id}}).then(function (data) {
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
                        msg: "添加失败：" + "数据重复",
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


//终端详情
function devDetailCtrl($scope, $uibModalInstance, PageFactory,Global,$rootScope,$compile, $timeout, $filter, $translate, $http, data) {
    $scope.nodes = data;
    $scope.$watch('$viewContentLoaded', function () {
    });
    $scope.cancel = function () {
        $uibModalInstance.close(true);
    }
};