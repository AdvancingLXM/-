app.controller("baseController", function ($scope) {
    // 初始化分页参数
    $scope.paginationConf = {
        currentPage:1,// 当前页号
        totalItems:10,// 总记录数
        itemsPerPage:10,// 页大小
        perPageOptions:[10, 20, 30, 40, 50],// 可选择的每页大小
        onChange: function () {// 当上述的参数发生变化了后触发
            $scope.reloadList();
        }
    };

    //加载列表数据
    $scope.reloadList = function () {

        //调用分页方法查询分页数据
        //$scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //已选择了的id集合
    $scope.selectedIds = [];

    //点击复选框
    $scope.updateSelection = function ($event, id) {
        if($event.target.checked){
            //选中；则将id加入到数组
            $scope.selectedIds.push(id);
        } else {
            //反选；则将id从数组中删除
            var index = $scope.selectedIds.indexOf(id);
            //splice是删除数组元素；参数1：起始索引号，参数2：要删除的个数
            $scope.selectedIds.splice(index, 1);
        }

    };

    //从一个json列表中获取某个属性的值的拼接
    $scope.jsonToString = function (listJsonStr, key) {
        var str = "";

        //将json格式字符串转换为一个json
        var list = JSON.parse(listJsonStr);
        for (var i = 0; i < list.length; i++) {
            var obj = list[i];

            if(str.length > 0){
                str += "," + obj[key];
            } else {
                str = obj[key];
            }
        }

        return str;
    }
});